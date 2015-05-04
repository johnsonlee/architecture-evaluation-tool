package de.cau.cs.se.evaluation.architecture.transformation.java;

import com.google.common.base.Objects;
import de.cau.cs.se.evaluation.architecture.hypergraph.Edge;
import de.cau.cs.se.evaluation.architecture.hypergraph.MethodTrace;
import de.cau.cs.se.evaluation.architecture.hypergraph.ModularHypergraph;
import de.cau.cs.se.evaluation.architecture.hypergraph.Module;
import de.cau.cs.se.evaluation.architecture.hypergraph.ModuleReference;
import de.cau.cs.se.evaluation.architecture.hypergraph.Node;
import de.cau.cs.se.evaluation.architecture.hypergraph.NodeReference;
import de.cau.cs.se.evaluation.architecture.hypergraph.TypeTrace;
import de.cau.cs.se.evaluation.architecture.transformation.java.JavaHypergraphElementFactory;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class JavaASTEvaluation {
  /**
   * Scan the AST of a method body for property access and method calls.
   * 
   * @param graph the hypergraph holding node, edges, and modules
   * @param node the corresponding node to the method
   * @param clazz declaring class of the given method
   * @param method the method to be evaluated for property access and method calls
   */
  public static void evaluteMethod(final ModularHypergraph graph, final Node node, final TypeDeclaration clazz, final MethodDeclaration method) {
    Block _body = method.getBody();
    List _statements = _body.statements();
    boolean _notEquals = (!Objects.equal(_statements, null));
    if (_notEquals) {
      Block _body_1 = method.getBody();
      List _statements_1 = _body_1.statements();
      final Consumer<Object> _function = new Consumer<Object>() {
        public void accept(final Object statement) {
          JavaASTEvaluation.evaluate(((Statement) statement), graph, node, clazz, method);
        }
      };
      _statements_1.forEach(_function);
    }
  }
  
  private static void evaluate(final Statement statement, final ModularHypergraph graph, final Node node, final TypeDeclaration clazz, final MethodDeclaration method) {
    boolean _matched = false;
    if (!_matched) {
      if (statement instanceof AssertStatement) {
        _matched=true;
        Expression _expression = ((AssertStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof Block) {
        _matched=true;
        List _statements = ((Block)statement).statements();
        final Consumer<Object> _function = new Consumer<Object>() {
          public void accept(final Object it) {
            JavaASTEvaluation.evaluate(((Statement) it), graph, node, clazz, method);
          }
        };
        _statements.forEach(_function);
      }
    }
    if (!_matched) {
      if (statement instanceof ConstructorInvocation) {
        _matched=true;
        JavaASTEvaluation.handleConstructorInvocation(((ConstructorInvocation)statement), graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof DoStatement) {
        _matched=true;
        Expression _expression = ((DoStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        Statement _body = ((DoStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof EnhancedForStatement) {
        _matched=true;
        Expression _expression = ((EnhancedForStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        Statement _body = ((EnhancedForStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof ExpressionStatement) {
        _matched=true;
        Expression _expression = ((ExpressionStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof ForStatement) {
        _matched=true;
        Expression _expression = ((ForStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        List _initializers = ((ForStatement)statement).initializers();
        final Consumer<Object> _function = new Consumer<Object>() {
          public void accept(final Object it) {
            JavaASTEvaluation.evaluate(((Expression) it), graph, node, clazz, method);
          }
        };
        _initializers.forEach(_function);
        List _updaters = ((ForStatement)statement).updaters();
        final Consumer<Object> _function_1 = new Consumer<Object>() {
          public void accept(final Object it) {
            JavaASTEvaluation.evaluate(((Expression) it), graph, node, clazz, method);
          }
        };
        _updaters.forEach(_function_1);
        Statement _body = ((ForStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof IfStatement) {
        _matched=true;
        Expression _expression = ((IfStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        Statement _thenStatement = ((IfStatement)statement).getThenStatement();
        JavaASTEvaluation.evaluate(_thenStatement, graph, node, clazz, method);
        Statement _elseStatement = ((IfStatement)statement).getElseStatement();
        JavaASTEvaluation.evaluate(_elseStatement, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof LabeledStatement) {
        _matched=true;
        Statement _body = ((LabeledStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof ReturnStatement) {
        _matched=true;
        Expression _expression = ((ReturnStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof SuperConstructorInvocation) {
        _matched=true;
        JavaASTEvaluation.handleSuperConstructorInvocation(((SuperConstructorInvocation)statement), graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof SwitchCase) {
        _matched=true;
        Expression _expression = ((SwitchCase)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof SwitchStatement) {
        _matched=true;
        Expression _expression = ((SwitchStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        List _statements = ((SwitchStatement)statement).statements();
        final Consumer<Object> _function = new Consumer<Object>() {
          public void accept(final Object it) {
            JavaASTEvaluation.evaluate(((Statement) it), graph, node, clazz, method);
          }
        };
        _statements.forEach(_function);
      }
    }
    if (!_matched) {
      if (statement instanceof SynchronizedStatement) {
        _matched=true;
        Expression _expression = ((SynchronizedStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        Block _body = ((SynchronizedStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof ThrowStatement) {
        _matched=true;
        Expression _expression = ((ThrowStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof TryStatement) {
        _matched=true;
        Block _body = ((TryStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
        List _catchClauses = ((TryStatement)statement).catchClauses();
        final Consumer<Object> _function = new Consumer<Object>() {
          public void accept(final Object it) {
            Block _body = ((CatchClause) it).getBody();
            JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
          }
        };
        _catchClauses.forEach(_function);
        Block _finally = ((TryStatement)statement).getFinally();
        JavaASTEvaluation.evaluate(_finally, graph, node, clazz, method);
      }
    }
    if (!_matched) {
      if (statement instanceof VariableDeclarationStatement) {
        _matched=true;
        List _fragments = ((VariableDeclarationStatement)statement).fragments();
        final Consumer<Object> _function = new Consumer<Object>() {
          public void accept(final Object it) {
            Expression _initializer = ((VariableDeclarationFragment) it).getInitializer();
            JavaASTEvaluation.evaluate(_initializer, graph, node, clazz, method);
          }
        };
        _fragments.forEach(_function);
      }
    }
    if (!_matched) {
      if (statement instanceof WhileStatement) {
        _matched=true;
        Expression _expression = ((WhileStatement)statement).getExpression();
        JavaASTEvaluation.evaluate(_expression, graph, node, clazz, method);
        Statement _body = ((WhileStatement)statement).getBody();
        JavaASTEvaluation.evaluate(_body, graph, node, clazz, method);
      }
    }
  }
  
  /**
   * Handle an constructor call to the super class. This could be a method which is part of the framework.
   */
  public static boolean handleSuperConstructorInvocation(final SuperConstructorInvocation invocation, final ModularHypergraph graph, final Node node, final TypeDeclaration clazz, final MethodDeclaration method) {
    boolean _xblockexpression = false;
    {
      final IMethodBinding methodBinding = invocation.resolveConstructorBinding();
      EList<Module> _modules = graph.getModules();
      final Function1<Module, Boolean> _function = new Function1<Module, Boolean>() {
        public Boolean apply(final Module it) {
          boolean _xblockexpression = false;
          {
            ModuleReference _derivedFrom = it.getDerivedFrom();
            final Object type = ((TypeTrace) _derivedFrom).getType();
            boolean _switchResult = false;
            boolean _matched = false;
            if (!_matched) {
              if (type instanceof TypeDeclaration) {
                _matched=true;
                ITypeBinding _resolveBinding = ((TypeDeclaration)type).resolveBinding();
                ITypeBinding _declaringClass = methodBinding.getDeclaringClass();
                _switchResult = _resolveBinding.isSubTypeCompatible(_declaringClass);
              }
            }
            if (!_matched) {
              if (type instanceof ITypeBinding) {
                _matched=true;
                ITypeBinding _declaringClass = methodBinding.getDeclaringClass();
                _switchResult = ((ITypeBinding)type).isSubTypeCompatible(_declaringClass);
              }
            }
            if (!_matched) {
              Class<?> _class = type.getClass();
              String _plus = (_class + " is not supported as a source for module.");
              throw new UnsupportedOperationException(_plus);
            }
            _xblockexpression = _switchResult;
          }
          return Boolean.valueOf(_xblockexpression);
        }
      };
      Module module = IterableExtensions.<Module>findFirst(_modules, _function);
      boolean _equals = Objects.equal(module, null);
      if (_equals) {
        IMethodBinding _resolveConstructorBinding = invocation.resolveConstructorBinding();
        ITypeBinding _declaringClass = _resolveConstructorBinding.getDeclaringClass();
        Module _createModuleForTypeBinding = JavaHypergraphElementFactory.createModuleForTypeBinding(_declaringClass);
        module = _createModuleForTypeBinding;
        EList<Module> _modules_1 = graph.getModules();
        _modules_1.add(module);
      }
      EList<Node> _nodes = module.getNodes();
      final Function1<Node, Boolean> _function_1 = new Function1<Node, Boolean>() {
        public Boolean apply(final Node it) {
          boolean _xblockexpression = false;
          {
            NodeReference _derivedFrom = it.getDerivedFrom();
            final Object localMethod = ((MethodTrace) _derivedFrom).getMethod();
            boolean _switchResult = false;
            boolean _matched = false;
            if (!_matched) {
              if (localMethod instanceof MethodDeclaration) {
                _matched=true;
                IMethodBinding _resolveBinding = ((MethodDeclaration)localMethod).resolveBinding();
                _switchResult = _resolveBinding.isSubsignature(methodBinding);
              }
            }
            if (!_matched) {
              if (localMethod instanceof IMethodBinding) {
                _matched=true;
                _switchResult = ((IMethodBinding)localMethod).isSubsignature(methodBinding);
              }
            }
            if (!_matched) {
              _switchResult = false;
            }
            _xblockexpression = _switchResult;
          }
          return Boolean.valueOf(_xblockexpression);
        }
      };
      Node targetNode = IterableExtensions.<Node>findFirst(_nodes, _function_1);
      boolean _equals_1 = Objects.equal(targetNode, null);
      if (_equals_1) {
        Node _createNodeForSuperConstructorInvocation = JavaHypergraphElementFactory.createNodeForSuperConstructorInvocation(methodBinding);
        targetNode = _createNodeForSuperConstructorInvocation;
        EList<Node> _nodes_1 = module.getNodes();
        _nodes_1.add(targetNode);
        EList<Node> _nodes_2 = graph.getNodes();
        _nodes_2.add(targetNode);
      }
      final Edge edge = JavaHypergraphElementFactory.createCallEdge(clazz, method, methodBinding);
      EList<Edge> _edges = targetNode.getEdges();
      _edges.add(edge);
      EList<Edge> _edges_1 = node.getEdges();
      _xblockexpression = _edges_1.add(edge);
    }
    return _xblockexpression;
  }
  
  /**
   * Handle an constructor 'this' invocation. This requires (a) an call edge from
   * this method to the called constructor and (b) an evaluation of all parameters.
   */
  private static void handleConstructorInvocation(final ConstructorInvocation invocation, final ModularHypergraph graph, final Node node, final TypeDeclaration clazz, final MethodDeclaration method) {
    final Edge edge = JavaHypergraphElementFactory.createCallEdge(clazz, method, clazz, invocation);
    EList<Edge> _edges = graph.getEdges();
    final Function1<Edge, Boolean> _function = new Function1<Edge, Boolean>() {
      public Boolean apply(final Edge it) {
        String _name = it.getName();
        String _name_1 = edge.getName();
        return Boolean.valueOf(_name.equals(_name_1));
      }
    };
    boolean _exists = IterableExtensions.<Edge>exists(_edges, _function);
    boolean _not = (!_exists);
    if (_not) {
      EList<Edge> _edges_1 = graph.getEdges();
      _edges_1.add(edge);
      EList<Node> _nodes = graph.getNodes();
      final Node targetNode = JavaHypergraphElementFactory.findNodeForConstructoreInvocation(_nodes, invocation);
      EList<Edge> _edges_2 = targetNode.getEdges();
      _edges_2.add(edge);
      EList<Edge> _edges_3 = node.getEdges();
      _edges_3.add(edge);
    }
    List _arguments = invocation.arguments();
    final Consumer<Object> _function_1 = new Consumer<Object>() {
      public void accept(final Object it) {
        JavaASTEvaluation.evaluate(((Expression) it), graph, node, clazz, method);
      }
    };
    _arguments.forEach(_function_1);
  }
  
  /**
   * Expression evaluation hook.
   */
  private static void evaluate(final Expression expression, final ModularHypergraph graph, final Node node, final TypeDeclaration clazz, final MethodDeclaration method) {
  }
}
