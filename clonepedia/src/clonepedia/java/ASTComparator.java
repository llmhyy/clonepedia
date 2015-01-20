package clonepedia.java;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
/*import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;*/
import org.eclipse.jdt.core.dom.CharacterLiteral;
/*import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;*/
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
/*import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;*/
import org.eclipse.jdt.core.dom.SimpleName;
/*import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;*/
import org.eclipse.jdt.core.dom.StringLiteral;
/*import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;*/
import org.eclipse.jdt.core.dom.TypeLiteral;
/*import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;*/

import clonepedia.util.BoolComparator;

public class ASTComparator implements BoolComparator {
	
	/**
	 * As some key words(while, if, for, instanceof, return, switch, this, throw, try, catch, 
	 * continue, do) should be reserved in clone fragments so that they can be used as 
	 * "landmark" to distinguish the different part of clone code fragments. The more "context"
	 * in code code, the better effect my diff-algorithm has.
	 */
	public boolean isMatch(Object obj1, Object obj2) {

		ASTNode node1 = (ASTNode)obj1;
		ASTNode node2 = (ASTNode)obj2;
		
		ASTMatcher matcher = new ASTMatcher();

		if (node1.getNodeType() != node2.getNodeType())
			return false;

		switch (node1.getNodeType()){
		case ASTNode.SIMPLE_NAME:{
			SimpleName name1 = (SimpleName) node1;
			SimpleName name2 = (SimpleName) node2;
			
			return name1.getIdentifier().equals(name2.getIdentifier());
			
//			IBinding b1 = name1.resolveBinding();
//			IBinding b2 = name2.resolveBinding();
//			
//			if((b1 instanceof IMethodBinding) && (b2 instanceof IMethodBinding)){
//				IMethodBinding mb1 = (IMethodBinding)b1;
//				IMethodBinding mb2 = (IMethodBinding)b2;
//				
//				boolean cond1 = matcher.match((SimpleName) node1, node2);
//				boolean cond2 = mb1.getDeclaringClass().getBinaryName().equals(mb2.getDeclaringClass().getBinaryName());
//				
//				String returnTypeName1 = mb1.getReturnType().getBinaryName();
//				String returnTypeName2 = mb2.getReturnType().getBinaryName();
//				boolean cond3 = (null != returnTypeName1) && (null != returnTypeName2) 
//						&& returnTypeName1.equals(returnTypeName2);
//				
//				return cond1 && cond2 && cond3;
//			}
//			
//			boolean isName1Null = name1.resolveTypeBinding() == null;
//			boolean isName2Null = name2.resolveTypeBinding() == null;
//			
//			if(isName1Null ^ isName2Null){
//				return false;
//			}
//			else if(isName1Null && isName2Null){
//				return /*(name1.resolveTypeBinding().getBinaryName().equals(name2.resolveTypeBinding().getBinaryName())) && */
//						matcher.match((SimpleName) node1, node2);
//			}
//			else{
//				String binaryName1 = name1.resolveTypeBinding().getBinaryName();
//				String binaryName2 = name2.resolveTypeBinding().getBinaryName();
//				
//				return (null != binaryName1) && (null != binaryName2) && binaryName1.equals(binaryName2) && 
//						matcher.match((SimpleName) node1, node2);				
//			}
		}
		case ASTNode.CHARACTER_LITERAL:
			return matcher.match((CharacterLiteral) node1, node2);
		case ASTNode.NULL_LITERAL:
			return matcher.match((NullLiteral) node1, node2);
		case ASTNode.NUMBER_LITERAL:
			return matcher.match((NumberLiteral) node1, node2);
		case ASTNode.STRING_LITERAL:
			return matcher.match((StringLiteral) node1, node2);
		case ASTNode.TYPE_LITERAL:
			return matcher.match((TypeLiteral) node1, node2);
		case ASTNode.PRIMITIVE_TYPE:
			return matcher.match((PrimitiveType) node1, node2);
		/*case ASTNode.BLOCK:
			return matcher.match((Block)node1, node2);*/
		default:
			return true;
		}
		/*switch (node1.getNodeType()) {
		case ASTNode.ANNOTATION_TYPE_DECLARATION:
			return matcher.match((AnnotationTypeDeclaration) node1, node2);
		case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
			return matcher
					.match((AnnotationTypeMemberDeclaration) node1, node2);
		case ASTNode.ANONYMOUS_CLASS_DECLARATION:
			return matcher.match((AnonymousClassDeclaration) node1, node2);
		case ASTNode.ARRAY_ACCESS:
			return matcher.match((ArrayAccess) node1, node2);
		case ASTNode.ARRAY_CREATION:
			return matcher.match((ArrayCreation) node1, node2);
		case ASTNode.ARRAY_INITIALIZER:
			return matcher.match((ArrayInitializer) node1, node2);
		case ASTNode.ARRAY_TYPE:
			return matcher.match((ArrayType) node1, node2);
		case ASTNode.ASSERT_STATEMENT:
			return matcher.match((AssertStatement) node1, node2);
		case ASTNode.ASSIGNMENT:
			return matcher.match((Assignment) node1, node2);
		case ASTNode.BLOCK:
			return matcher.match((Block) node1, node2);
		case ASTNode.BLOCK_COMMENT:
			return matcher.match((BlockComment) node1, node2);
		case ASTNode.BOOLEAN_LITERAL:
			return matcher.match((BooleanLiteral) node1, node2);
		case ASTNode.BREAK_STATEMENT:
			return matcher.match((BreakStatement) node1, node2);
		case ASTNode.CAST_EXPRESSION:
			return matcher.match((CastExpression) node1, node2);
		case ASTNode.CATCH_CLAUSE:
			//return matcher.match((CatchClause) node1, node2);
			return true;
		case ASTNode.CHARACTER_LITERAL:
			return matcher.match((CharacterLiteral) node1, node2);
		case ASTNode.CLASS_INSTANCE_CREATION:
			return matcher.match((ClassInstanceCreation) node1, node2);
		case ASTNode.COMPILATION_UNIT:
			return matcher.match((CompilationUnit) node1, node2);
		case ASTNode.CONDITIONAL_EXPRESSION:
			return matcher.match((ConditionalExpression) node1, node2);
		case ASTNode.CONSTRUCTOR_INVOCATION:
			return matcher.match((ConstructorInvocation) node1, node2);
		case ASTNode.CONTINUE_STATEMENT:
			//return matcher.match((ContinueStatement) node1, node2);
			return true;
		case ASTNode.UNION_TYPE:
			return matcher.match((UnionType) node1, node2);
		case ASTNode.DO_STATEMENT:
			//return matcher.match((DoStatement) node1, node2);
			return true;
		case ASTNode.EMPTY_STATEMENT:
			return matcher.match((EmptyStatement) node1, node2);
		case ASTNode.ENHANCED_FOR_STATEMENT:
			return matcher.match((EnhancedForStatement) node1, node2);
		case ASTNode.ENUM_CONSTANT_DECLARATION:
			return matcher.match((EnumConstantDeclaration) node1, node2);
		case ASTNode.ENUM_DECLARATION:
			return matcher.match((EnumDeclaration) node1, node2);
		case ASTNode.EXPRESSION_STATEMENT:
			return matcher.match((ExpressionStatement) node1, node2);
		case ASTNode.FIELD_ACCESS:
			return matcher.match((FieldAccess) node1, node2);
		case ASTNode.FIELD_DECLARATION:
			return matcher.match((FieldDeclaration) node1, node2);
		case ASTNode.FOR_STATEMENT:
			//return matcher.match((ForStatement) node1, node2);
			return true;
		case ASTNode.IF_STATEMENT:
			//return matcher.match((IfStatement) node1, node2);
			return true;
		case ASTNode.IMPORT_DECLARATION:
			return matcher.match((ImportDeclaration) node1, node2);
		case ASTNode.INFIX_EXPRESSION:
			return matcher.match((InfixExpression) node1, node2);
		case ASTNode.INITIALIZER:
			return matcher.match((Initializer) node1, node2);
		case ASTNode.INSTANCEOF_EXPRESSION:
			//return matcher.match((InstanceofExpression) node1, node2);
			return true;
		case ASTNode.JAVADOC:
			return matcher.match((Javadoc) node1, node2);
		case ASTNode.LABELED_STATEMENT:
			return matcher.match((LabeledStatement) node1, node2);
		case ASTNode.LINE_COMMENT:
			return matcher.match((LineComment) node1, node2);
		case ASTNode.MARKER_ANNOTATION:
			return matcher.match((MarkerAnnotation) node1, node2);
		case ASTNode.MEMBER_REF:
			return matcher.match((MemberRef) node1, node2);
		case ASTNode.MEMBER_VALUE_PAIR:
			return matcher.match((MemberValuePair) node1, node2);
		case ASTNode.METHOD_DECLARATION:
			return matcher.match((MethodDeclaration) node1, node2);
		case ASTNode.METHOD_INVOCATION:
			return matcher.match((MethodInvocation) node1, node2);
		case ASTNode.METHOD_REF:
			return matcher.match((MethodRef) node1, node2);
		case ASTNode.METHOD_REF_PARAMETER:
			return matcher.match((MethodRefParameter) node1, node2);
		case ASTNode.MODIFIER:
			return matcher.match((Modifier) node1, node2);
		case ASTNode.NORMAL_ANNOTATION:
			return matcher.match((NormalAnnotation) node1, node2);
		case ASTNode.NULL_LITERAL:
			return matcher.match((NullLiteral) node1, node2);
		case ASTNode.NUMBER_LITERAL:
			return matcher.match((NumberLiteral) node1, node2);
		case ASTNode.PACKAGE_DECLARATION:
			return matcher.match((PackageDeclaration) node1, node2);
		case ASTNode.PARAMETERIZED_TYPE:
			return matcher.match((ParameterizedType) node1, node2);
		case ASTNode.PARENTHESIZED_EXPRESSION:
			return matcher.match((ParenthesizedExpression) node1, node2);
		case ASTNode.POSTFIX_EXPRESSION:
			return matcher.match((PostfixExpression) node1, node2);
		case ASTNode.PREFIX_EXPRESSION:
			return matcher.match((PrefixExpression) node1, node2);
		case ASTNode.PRIMITIVE_TYPE:
			return matcher.match((PrimitiveType) node1, node2);
		case ASTNode.QUALIFIED_NAME:
			return matcher.match((QualifiedName) node1, node2);
		case ASTNode.QUALIFIED_TYPE:
			return matcher.match((QualifiedType) node1, node2);
		case ASTNode.RETURN_STATEMENT:
			//return matcher.match((ReturnStatement) node1, node2);
			return true;
		case ASTNode.SIMPLE_NAME:
			return matcher.match((SimpleName) node1, node2);
		case ASTNode.SIMPLE_TYPE:
			return matcher.match((SimpleType) node1, node2);
		case ASTNode.SINGLE_MEMBER_ANNOTATION:
			return matcher.match((SingleMemberAnnotation) node1, node2);
		case ASTNode.SINGLE_VARIABLE_DECLARATION:
			return matcher.match((SingleVariableDeclaration) node1, node2);
		case ASTNode.STRING_LITERAL:
			return matcher.match((StringLiteral) node1, node2);
		case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
			return matcher.match((SuperConstructorInvocation) node1, node2);
		case ASTNode.SUPER_FIELD_ACCESS:
			return matcher.match((SuperFieldAccess) node1, node2);
		case ASTNode.SUPER_METHOD_INVOCATION:
			return matcher.match((SuperMethodInvocation) node1, node2);
		case ASTNode.SWITCH_CASE:
			return matcher.match((SwitchCase) node1, node2);
		case ASTNode.SWITCH_STATEMENT:
			//return matcher.match((SwitchStatement) node1, node2);
			return true;
		case ASTNode.SYNCHRONIZED_STATEMENT:
			return matcher.match((SynchronizedStatement) node1, node2);
		case ASTNode.TAG_ELEMENT:
			return matcher.match((TagElement) node1, node2);
		case ASTNode.TEXT_ELEMENT:
			return matcher.match((TextElement) node1, node2);
		case ASTNode.THIS_EXPRESSION:
			//return matcher.match((ThisExpression) node1, node2);
			return true;
		case ASTNode.THROW_STATEMENT:
			//return matcher.match((ThrowStatement) node1, node2);
			return true;
		case ASTNode.TRY_STATEMENT:
			//return matcher.match((TryStatement) node1, node2);
			return true;
		case ASTNode.TYPE_DECLARATION:
			return matcher.match((TypeDeclaration) node1, node2);
		case ASTNode.TYPE_DECLARATION_STATEMENT:
			return matcher.match((TypeDeclarationStatement) node1, node2);
		case ASTNode.TYPE_LITERAL:
			return matcher.match((TypeLiteral) node1, node2);
		case ASTNode.TYPE_PARAMETER:
			return matcher.match((TypeParameter) node1, node2);
		case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
			return matcher.match((VariableDeclarationExpression) node1, node2);
		case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
			return matcher.match((VariableDeclarationFragment) node1, node2);
		case ASTNode.VARIABLE_DECLARATION_STATEMENT:
			return matcher.match((VariableDeclarationStatement) node1, node2);
		case ASTNode.WHILE_STATEMENT:
			//return matcher.match((WhileStatement) node1, node2);
			return true;
		case ASTNode.WILDCARD_TYPE:
			return matcher.match((WildcardType) node1, node2);

		}

		return false;*/
	}
}
