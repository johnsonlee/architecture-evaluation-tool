package de.cau.cs.se.evaluation.architecture.transformation.java.old;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class VariableDeclarationResolver {
  public static Type findVariableDeclaration(final SimpleName name, final AbstractTypeDeclaration type) {
    ASTNode _parent = name.getParent();
    String _fullyQualifiedName = name.getFullyQualifiedName();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, _fullyQualifiedName, type);
  }
  
  private static CompilationUnit getCompilationUnit(final AbstractTypeDeclaration type) {
    ASTNode _parent = type.getParent();
    return VariableDeclarationResolver.getCompilationUnit(_parent);
  }
  
  private static CompilationUnit getCompilationUnit(final ASTNode astNode) {
    if ((astNode instanceof CompilationUnit)) {
      return ((CompilationUnit) astNode);
    } else {
      ASTNode _parent = astNode.getParent();
      return VariableDeclarationResolver.getCompilationUnit(_parent);
    }
  }
  
  /**
   * -- Expressions --
   */
  private static Type _findVariableDeclaration(final MethodInvocation astNode, final String variableName, final AbstractTypeDeclaration type) {
    ASTNode _parent = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
  }
  
  private static Type _findVariableDeclaration(final Expression astNode, final String variableName, final AbstractTypeDeclaration type) {
    ASTNode _parent = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
  }
  
  /**
   * -- Statements --
   */
  private static Type _findVariableDeclaration(final IfStatement astNode, final String variableName, final AbstractTypeDeclaration type) {
    ASTNode _parent = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
  }
  
  private static Type _findVariableDeclaration(final ForStatement astNode, final String variableName, final AbstractTypeDeclaration type) {
    ASTNode _parent = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
  }
  
  private static Type _findVariableDeclaration(final EnhancedForStatement astNode, final String variableName, final AbstractTypeDeclaration type) {
    Type _xifexpression = null;
    SingleVariableDeclaration _parameter = astNode.getParameter();
    SimpleName _name = _parameter.getName();
    String _fullyQualifiedName = _name.getFullyQualifiedName();
    boolean _equals = _fullyQualifiedName.equals(variableName);
    if (_equals) {
      SingleVariableDeclaration _parameter_1 = astNode.getParameter();
      _xifexpression = _parameter_1.getType();
    } else {
      ASTNode _parent = astNode.getParent();
      _xifexpression = VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
    }
    return _xifexpression;
  }
  
  private static Type _findVariableDeclaration(final Block astNode, final String variableName, final AbstractTypeDeclaration type) {
    List _statements = astNode.statements();
    Iterable<VariableDeclarationStatement> _filter = Iterables.<VariableDeclarationStatement>filter(_statements, VariableDeclarationStatement.class);
    final Function1<VariableDeclarationStatement, Boolean> _function = new Function1<VariableDeclarationStatement, Boolean>() {
      public Boolean apply(final VariableDeclarationStatement declaration) {
        List _fragments = declaration.fragments();
        final Function1<Object, Boolean> _function = new Function1<Object, Boolean>() {
          public Boolean apply(final Object it) {
            SimpleName _name = ((VariableDeclarationFragment) it).getName();
            String _fullyQualifiedName = _name.getFullyQualifiedName();
            return Boolean.valueOf(_fullyQualifiedName.equals(variableName));
          }
        };
        return Boolean.valueOf(IterableExtensions.<Object>exists(_fragments, _function));
      }
    };
    final VariableDeclarationStatement declaration = IterableExtensions.<VariableDeclarationStatement>findFirst(_filter, _function);
    boolean _equals = Objects.equal(declaration, null);
    if (_equals) {
      ASTNode _parent = astNode.getParent();
      return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
    } else {
      return declaration.getType();
    }
  }
  
  private static Type _findVariableDeclaration(final Statement astNode, final String variableName, final AbstractTypeDeclaration type) {
    ASTNode _parent = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
  }
  
  /**
   * -- Other node types --
   */
  private static Type _findVariableDeclaration(final MethodDeclaration astNode, final String variableName, final AbstractTypeDeclaration type) {
    List _parameters = astNode.parameters();
    final Function1<Object, Boolean> _function = new Function1<Object, Boolean>() {
      public Boolean apply(final Object it) {
        SimpleName _name = ((SingleVariableDeclaration) it).getName();
        String _fullyQualifiedName = _name.getFullyQualifiedName();
        return Boolean.valueOf(_fullyQualifiedName.equals(variableName));
      }
    };
    final Object variableDeclaration = IterableExtensions.<Object>findFirst(_parameters, _function);
    boolean _notEquals = (!Objects.equal(variableDeclaration, null));
    if (_notEquals) {
      return ((SingleVariableDeclaration) variableDeclaration).getType();
    } else {
      ASTNode _parent = astNode.getParent();
      return VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
    }
  }
  
  private static Type _findVariableDeclaration(final VariableDeclarationFragment astNode, final String variableName, final AbstractTypeDeclaration type) {
    SimpleName _name = astNode.getName();
    String _fullyQualifiedName = _name.getFullyQualifiedName();
    boolean _equals = _fullyQualifiedName.equals(variableName);
    if (_equals) {
      ASTNode _parent = astNode.getParent();
      boolean _matched = false;
      if (!_matched) {
        if (_parent instanceof VariableDeclarationStatement) {
          _matched=true;
          ASTNode _parent_1 = astNode.getParent();
          return ((VariableDeclarationStatement) _parent_1).getType();
        }
      }
      Class<? extends VariableDeclarationFragment> _class = astNode.getClass();
      String _plus = ((("unhandled parent for variable declaration fragment " + astNode) + " ") + _class);
      new Exception(_plus);
    }
    ASTNode _parent_1 = astNode.getParent();
    return VariableDeclarationResolver.findVariableDeclaration(_parent_1, variableName, type);
  }
  
  private static Type _findVariableDeclaration(final CatchClause astNode, final String variableName, final AbstractTypeDeclaration type) {
    Type _xblockexpression = null;
    {
      SingleVariableDeclaration _exception = astNode.getException();
      boolean _notEquals = (!Objects.equal(_exception, null));
      if (_notEquals) {
        SingleVariableDeclaration _exception_1 = astNode.getException();
        SimpleName _name = _exception_1.getName();
        String _fullyQualifiedName = _name.getFullyQualifiedName();
        boolean _equals = _fullyQualifiedName.equals(variableName);
        if (_equals) {
          SingleVariableDeclaration _exception_2 = astNode.getException();
          return _exception_2.getType();
        }
      }
      ASTNode _parent = astNode.getParent();
      _xblockexpression = VariableDeclarationResolver.findVariableDeclaration(_parent, variableName, type);
    }
    return _xblockexpression;
  }
  
  private static Type _findVariableDeclaration(final TypeDeclaration astNode, final String variableName, final AbstractTypeDeclaration type) {
    FieldDeclaration[] _fields = astNode.getFields();
    final Function1<FieldDeclaration, Boolean> _function = new Function1<FieldDeclaration, Boolean>() {
      public Boolean apply(final FieldDeclaration field) {
        List _fragments = field.fragments();
        final Function1<Object, Boolean> _function = new Function1<Object, Boolean>() {
          public Boolean apply(final Object variable) {
            SimpleName _name = ((VariableDeclarationFragment) variable).getName();
            String _fullyQualifiedName = _name.getFullyQualifiedName();
            return Boolean.valueOf(_fullyQualifiedName.equals(variableName));
          }
        };
        return Boolean.valueOf(IterableExtensions.<Object>exists(_fragments, _function));
      }
    };
    final FieldDeclaration field = IterableExtensions.<FieldDeclaration>findFirst(((Iterable<FieldDeclaration>)Conversions.doWrapArray(_fields)), _function);
    boolean _notEquals = (!Objects.equal(field, null));
    if (_notEquals) {
      return field.getType();
    } else {
      return null;
    }
  }
  
  private static Type _findVariableDeclaration(final AnonymousClassDeclaration astNode, final String variableName, final AbstractTypeDeclaration type) {
    List _bodyDeclarations = astNode.bodyDeclarations();
    Iterable<FieldDeclaration> _filter = Iterables.<FieldDeclaration>filter(_bodyDeclarations, FieldDeclaration.class);
    final Function1<FieldDeclaration, Boolean> _function = new Function1<FieldDeclaration, Boolean>() {
      public Boolean apply(final FieldDeclaration field) {
        List _fragments = field.fragments();
        final Function1<Object, Boolean> _function = new Function1<Object, Boolean>() {
          public Boolean apply(final Object variable) {
            SimpleName _name = ((VariableDeclarationFragment) variable).getName();
            String _fullyQualifiedName = _name.getFullyQualifiedName();
            return Boolean.valueOf(_fullyQualifiedName.equals(variableName));
          }
        };
        return Boolean.valueOf(IterableExtensions.<Object>exists(_fragments, _function));
      }
    };
    final FieldDeclaration field = IterableExtensions.<FieldDeclaration>findFirst(_filter, _function);
    boolean _notEquals = (!Objects.equal(field, null));
    if (_notEquals) {
      return field.getType();
    } else {
      return null;
    }
  }
  
  private static Type _findVariableDeclaration(final ASTNode astNode, final String variableName, final AbstractTypeDeclaration type) {
    try {
      Class<? extends ASTNode> _class = astNode.getClass();
      String _plus = ((("unhandled AST node type " + astNode) + " ") + _class);
      throw new Exception(_plus);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private static Type findVariableDeclaration(final ASTNode astNode, final String variableName, final AbstractTypeDeclaration type) {
    if (astNode instanceof TypeDeclaration) {
      return _findVariableDeclaration((TypeDeclaration)astNode, variableName, type);
    } else if (astNode instanceof Block) {
      return _findVariableDeclaration((Block)astNode, variableName, type);
    } else if (astNode instanceof EnhancedForStatement) {
      return _findVariableDeclaration((EnhancedForStatement)astNode, variableName, type);
    } else if (astNode instanceof ForStatement) {
      return _findVariableDeclaration((ForStatement)astNode, variableName, type);
    } else if (astNode instanceof IfStatement) {
      return _findVariableDeclaration((IfStatement)astNode, variableName, type);
    } else if (astNode instanceof MethodDeclaration) {
      return _findVariableDeclaration((MethodDeclaration)astNode, variableName, type);
    } else if (astNode instanceof MethodInvocation) {
      return _findVariableDeclaration((MethodInvocation)astNode, variableName, type);
    } else if (astNode instanceof VariableDeclarationFragment) {
      return _findVariableDeclaration((VariableDeclarationFragment)astNode, variableName, type);
    } else if (astNode instanceof AnonymousClassDeclaration) {
      return _findVariableDeclaration((AnonymousClassDeclaration)astNode, variableName, type);
    } else if (astNode instanceof CatchClause) {
      return _findVariableDeclaration((CatchClause)astNode, variableName, type);
    } else if (astNode instanceof Expression) {
      return _findVariableDeclaration((Expression)astNode, variableName, type);
    } else if (astNode instanceof Statement) {
      return _findVariableDeclaration((Statement)astNode, variableName, type);
    } else if (astNode != null) {
      return _findVariableDeclaration(astNode, variableName, type);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(astNode, variableName, type).toString());
    }
  }
}
