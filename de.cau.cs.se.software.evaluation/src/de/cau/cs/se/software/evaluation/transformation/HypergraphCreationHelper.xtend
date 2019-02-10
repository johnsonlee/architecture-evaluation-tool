/***************************************************************************
 * Copyright (C) 2015 Reiner Jung
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
package de.cau.cs.se.software.evaluation.transformation

import de.cau.cs.se.software.evaluation.hypergraph.Edge
import de.cau.cs.se.software.evaluation.hypergraph.HypergraphFactory
import de.cau.cs.se.software.evaluation.hypergraph.ModularHypergraph
import de.cau.cs.se.software.evaluation.hypergraph.Module
import de.cau.cs.se.software.evaluation.hypergraph.Node
import de.cau.cs.se.software.evaluation.hypergraph.Hypergraph
import org.eclipse.emf.ecore.EObject

/**
 * Special factory for hypergraphs.
 * 
 * @author Reiner Jung
 */
class HypergraphCreationHelper {
	
	/**
	 * Create a node for a hypergraph and set the derived from reference to the
	 * specified element.
	 * 
	 * @param hypergraph
	 * @param name the name of the node
	 * @param element a model element related to that node
	 */			
	def static createNode(Hypergraph hypergraph, String name, EObject element) {
		val node = HypergraphFactory.eINSTANCE.createNode
		node.name = name
		
		if (element !== null) {
			val reference = HypergraphFactory.eINSTANCE.createModelElementTrace
			reference.element = element
			node.derivedFrom = reference
		} else
			node.derivedFrom = null

		hypergraph.nodes.add(node)
		
		return node
	}
	
	/**
	 * Create a node for a modular hypergraph and set the derived from reference to the
	 * specified element.
	 * 
	 * @param hypergraph the modular hypergraph
	 * @param module a module of that modular hypergraph
	 * @param name the name of the node
	 * @param element a model element related to that node
	 */			
	def static createNode(ModularHypergraph hypergraph, Module module, String name, EObject element) {
		val node = hypergraph.createNode(name, element)
		
		module.nodes.add(node)
		
		return node
	}
	
	/**
	 * Create an edge for a hypergraph between the two specified nodes.
	 * 
	 * @param hypergraph
	 * @param source the first node
	 * @param target the second node
	 * @param name the name of the edge
	 * @param element a model element related to that edge
	 */
	def static createEdge(Hypergraph hypergraph, Node source, Node target, String name, EObject element) {
		val edge = HypergraphFactory.eINSTANCE.createEdge
		edge.name = name
		
		if (element !== null) {
			val reference = HypergraphFactory.eINSTANCE.createModelElementTrace
			reference.element = element
			edge.derivedFrom = reference
		} else
			edge.derivedFrom = null
		
		hypergraph.edges.add(edge)
		source.edges.add(edge)
		target.edges.add(edge)
		
		return edge
	}
	
	/**
	 * Create a module for a modular hypergraph and set the derived from reference to the
	 * specified element.
	 * 
	 * @param hypergraph
	 * @param name the name of the module
	 * @param element a model element related to that module
	 */			
	def static createModule(ModularHypergraph hypergraph, String name, EObject element) {
		val module = HypergraphFactory.eINSTANCE.createModule
		module.name = name
		
		if (element !== null) {
			val reference = HypergraphFactory.eINSTANCE.createModelElementTrace
			reference.element = element
			module.derivedFrom = reference
		} else
			module.derivedFrom = null

		hypergraph.modules.add(module)
		
		return module
	}	
	
	/**
	 * Derive a new node from an old node and set the appropriate trace information.
	 * 
	 * @param node the old node
	 * 
	 * @returns the new node
	 */
	def static deriveNode(Node node) {
		val resultNode = HypergraphFactory.eINSTANCE.createNode
		resultNode.name = node.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createNodeTrace
		derivedFrom.node = node
		resultNode.derivedFrom = derivedFrom
		
		return resultNode
	}
	
	/**
	 * Derive a new edge from an old edge and set the appropriate trace information.
	 * 
	 * @param edge the old edge
	 * 
	 * @returns the new edge
	 */
	def static deriveEdge(Edge edge) {
		val resultEdge = HypergraphFactory.eINSTANCE.createEdge
		resultEdge.name = edge.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createEdgeTrace
		derivedFrom.edge = edge
		resultEdge.derivedFrom = derivedFrom
		
		return resultEdge
	}
	
	/**
	 * Derive a new module from an old module and set the appropriate trace information.
	 * 
	 * @param module the old module
	 * 
	 * @returns the new module
	 */
	def static deriveModule(Module module) {
		val resultModule = HypergraphFactory.eINSTANCE.createModule
		resultModule.name = module.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createModuleTrace
		derivedFrom.module = module
		resultModule.derivedFrom = derivedFrom
		
		return resultModule
	}
	
	/**
	 * Create an edge between two nodes. Despite the fact that our hypergraph
	 * edges do not have a direction, we use the terms source and target here for
	 * nodes to increase the understandability of the reoutine.
	 * 
	 * @param hypergraph a modular hypergraph
	 * @param source a node of that hypergraph
	 * @param target a second node of that hypergraph
	 * 
	 * @return an edge between these two nodes which is also added to the source and target node, as
	 * well as the hypergraph 
	 */
	def static createUniqueEdge(ModularHypergraph hypergraph, Node source, Node target) {
		val edgeSubset = source.edges.filter[sourceEdge | target.edges.exists[targetEdge | targetEdge == sourceEdge]]
		val edgeName = source.name + "::" + target.name
		val existingEdge = edgeSubset.findFirst[edge | edge.name.equals(edgeName)]
		if (existingEdge === null) {			
			return createEdge(hypergraph, source, target, edgeName, null)
		} else
			return existingEdge
	}
	
}
