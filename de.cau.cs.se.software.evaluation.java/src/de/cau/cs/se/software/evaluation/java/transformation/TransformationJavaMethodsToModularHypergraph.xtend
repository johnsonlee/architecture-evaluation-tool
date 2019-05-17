/***************************************************************************
 * Copyright (C) 2016 Reiner Jung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package de.cau.cs.se.software.evaluation.java.transformation

import de.cau.cs.se.software.evaluation.hypergraph.Edge
import de.cau.cs.se.software.evaluation.hypergraph.HypergraphFactory
import de.cau.cs.se.software.evaluation.hypergraph.MethodTrace
import de.cau.cs.se.software.evaluation.hypergraph.ModularHypergraph
import de.cau.cs.se.software.evaluation.hypergraph.Node
import de.cau.cs.se.software.evaluation.hypergraph.TypeTrace
import java.util.List
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.emf.common.util.EList
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration
import org.eclipse.jdt.core.dom.ArrayType
import org.eclipse.jdt.core.dom.IMethodBinding
import org.eclipse.jdt.core.dom.ITypeBinding
import org.eclipse.jdt.core.dom.Modifier
import org.eclipse.jdt.core.dom.ParameterizedType
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.QualifiedType
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.Type
import org.eclipse.jdt.core.dom.TypeDeclaration
import org.eclipse.jdt.core.dom.UnionType
import org.eclipse.jdt.core.dom.WildcardType

import static de.cau.cs.se.software.evaluation.java.transformation.JavaASTEvaluation.*
import static de.cau.cs.se.software.evaluation.java.transformation.JavaHypergraphElementFactory.*

import static extension de.cau.cs.se.software.evaluation.java.transformation.NameResolutionHelper.*
import org.eclipse.jdt.core.dom.VariableDeclarationFragment
import de.cau.cs.se.software.evaluation.hypergraph.EModuleKind
import de.cau.cs.se.software.evaluation.transformation.AbstractTransformation
import de.cau.cs.se.software.evaluation.jobs.IOutputHandler

class TransformationJavaMethodsToModularHypergraph extends AbstractTransformation<List<AbstractTypeDeclaration>,ModularHypergraph> {
	
	val IJavaProject project
	val List<String> dataTypePatterns
	val List<String> observedSystemPatterns
	
	val IOutputHandler handler
		
	/**
	 * Create a new hypergraph generator utilizing a specific hypergraph set for a specific set of java resources.
	 *  
	 * @param hypergraphSet the hypergraph set where the generated hypergraph will be added to
	 * @param scope the global scoper used to resolve classes during transformation
	 * @param eclipse progress monitor
	 */
	new(IJavaProject project, List<String> dataTypePatterns, List<String> observedSystemPatterns, IProgressMonitor monitor, IOutputHandler handler) {
		super(monitor)
		this.project = project
		this.dataTypePatterns = dataTypePatterns
		this.observedSystemPatterns = observedSystemPatterns
		this.handler = handler
	}
		
	/**
	 * Main transformation routine.
	 */
	override generate(List<AbstractTypeDeclaration> input) {
		result = HypergraphFactory.eINSTANCE.createModularHypergraph
		/** create modules for all classes */
		input.forEach[clazz | result.modules.add(createModuleForTypeBinding(clazz.resolveBinding, EModuleKind.SYSTEM))]
		monitor.worked(input.size)

		/** define edges for all internal variables of a class */
		input.forEach[clazz | result.edges.createEdgesForClassProperties(clazz, dataTypePatterns)]
		monitor.worked(input.size)
		
		/** find all method declarations and create nodes for it, grouped by class as modules */
		input.forEach[clazz | result.nodes.createNodesForMethods(clazz)]
		monitor.worked(input.size)
		input.filter[clazz | clazz.hasImplicitConstructor].
			forEach[clazz |
				val node = createNodeForImplicitConstructor(clazz.resolveBinding)
				val module = result.modules.findFirst[((it.derivedFrom as TypeTrace).type as ITypeBinding).isSubTypeCompatible(clazz.resolveBinding)]
				module.nodes.add(node) 
				result.nodes.add(node)
			]
		monitor.worked(input.size)
		
		/** resolve edges */
		input.forEach[clazz | resolveEdges(result, dataTypePatterns, clazz)]
		monitor.worked(input.size)
		
		return result
	}
	
	/**
	 * Create edges for each method found in the observed system.
	 * 
	 * @param graph the hypergraph where the edges will be added to
	 * @param type the type which is processed for edges
	 */
	private def void resolveEdges(ModularHypergraph graph, List<String> dataTypePatterns, AbstractTypeDeclaration type) {
		if (type instanceof TypeDeclaration) {
			val clazz = type as TypeDeclaration
			clazz.methods.forEach[method |
				val node = graph.nodes.findFirst[
					((it.derivedFrom as MethodTrace).method as IMethodBinding).
						isEqualTo(method.resolveBinding)
				]
				evaluteMethod(graph, dataTypePatterns, node, clazz, method, handler)
			]
		}
	}
	
	/**
	 * Determine if a given class has no explicit constructors which
	 * implies an implicit constructor.
	 * 
	 * @param type a class, enum or interface type from the selection
	 * 
	 * @return returns true if the given type is a normal class with no constructor
	 */
	private def boolean hasImplicitConstructor(AbstractTypeDeclaration type) {
		if (type instanceof TypeDeclaration) {
			val clazz = type as TypeDeclaration
			if (!clazz.interface && !Modifier.isAbstract(clazz.getModifiers())) {
				val isImplicit = !clazz.methods.exists[method | method.constructor]
				return isImplicit
			}
		}
		
		return false
	}

	/**
	 * Create all nodes for a class.
	 * 
	 * @param nodes list where the nodes will be inserted
	 * @param type the class where the methods come from
	 */
	private def void createNodesForMethods(EList<Node> nodes, AbstractTypeDeclaration type) {
		if (type instanceof TypeDeclaration) {
			val module = result.modules.findFirst[((it.derivedFrom as TypeTrace).type as ITypeBinding).isSubTypeCompatible(type.resolveBinding)]
			type.methods.forEach[method |
				val node = createNodeForMethod(method.resolveBinding)
				nodes.add(node)
				module.nodes.add(node)
			]
		}
	}
	
	/**
	 * Create edges for all properties in the given type which are data type properties.
	 */
	private def void createEdgesForClassProperties(EList<Edge> edges, AbstractTypeDeclaration type, List<String> dataTypePatterns) {
		if (type instanceof TypeDeclaration) {
			type.fields.forEach[field |
				if (field.type.isDataType(dataTypePatterns))
					field.fragments.forEach[fragment | edges.add(createDataEdge((fragment as VariableDeclarationFragment).resolveBinding))]
			]
		}
	}
	
	/**
	 * Determine if the given type is a data type.
	 * 
	 * @param type the type to be evaluated
	 * @param dataTypes a list of data types
	 * 
	 * @return returns true if the given type is a data type and not a behavior type.
	 */
	private def boolean isDataType(Type type, List<String> dataTypePatterns) {
		switch(type) {
			ArrayType:
				return type.elementType.isDataType(dataTypePatterns)
			ParameterizedType:
				return type.type.isDataType(dataTypePatterns)
			PrimitiveType: /** primitive types are all data types */
				return true
			QualifiedType:
				return type.qualifier.isDataType(dataTypePatterns) 
			SimpleType:
				return dataTypePatterns.exists[dataTypePattern | type.resolveBinding.determineFullyQualifiedName.matches(dataTypePattern)]
			UnionType:
				return type.types.forall[it.isDataType(dataTypePatterns)]
			WildcardType:
				return if (type.bound !== null)
					type.bound.isDataType(dataTypePatterns)
				else
					true	
			default:
				return false
		}
	}
	
	override workEstimate(List<AbstractTypeDeclaration> input) {
		input.size + // modules
		input.size + // properties for each class
		input.size + // methods
		input.size + // implicit constructors
		input.size // edges
	}
	
	
		
}