package clonepedia.java;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import clonepedia.util.BoolComparator;

public class ASTStatementBoolComparator implements BoolComparator {

	@Override
	public boolean isMatch(Object obj1, Object obj2) {
		ASTMatcher matcher = new ASTMatcher();
		
		if((obj1 instanceof Statement) && (obj2 instanceof Statement)){
			Statement stat1 = (Statement)obj1;
			Statement stat2 = (Statement)obj2;
			
			if(stat1.getNodeType() == stat2.getNodeType()){
				switch(stat1.getNodeType()){
				case ASTNode.ASSERT_STATEMENT:
					return matcher.match((AssertStatement)stat1, stat2);
				case ASTNode.BLOCK:
					return true;
				case ASTNode.BREAK_STATEMENT:
					matcher.match((BreakStatement)stat1, stat2);
				case ASTNode.CONSTRUCTOR_INVOCATION:
					matcher.match((ConstructorInvocation)stat1, stat2);
				case ASTNode.CONTINUE_STATEMENT:
					return matcher.match((ContinueStatement)stat1, stat2);
				case ASTNode.DO_STATEMENT:
					return matcher.match((DoStatement)stat1, stat2);
				case ASTNode.EMPTY_STATEMENT:
					return true;
				case ASTNode.ENHANCED_FOR_STATEMENT:
					return matcher.match((EnhancedForStatement)stat1, stat2);
				case ASTNode.EXPRESSION_STATEMENT:
					return matcher.match((ExpressionStatement)stat1, stat2);
				case ASTNode.FOR_STATEMENT:
					return matcher.match((ForStatement)stat1, stat2);
				case ASTNode.IF_STATEMENT:
					return matcher.match((IfStatement)stat1, stat2);
				case ASTNode.LABELED_STATEMENT:
					return matcher.match((LabeledStatement)stat1, stat2);
				case ASTNode.RETURN_STATEMENT:
					return matcher.match((ReturnStatement)stat1, stat2);
				case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
					return matcher.match((SuperConstructorInvocation)stat1, stat2);
				case ASTNode.SWITCH_CASE:
					return matcher.match((SwitchCase)stat1, stat2);
				case ASTNode.SWITCH_STATEMENT:
					return matcher.match((SwitchStatement)stat1, stat2);
				case ASTNode.SYNCHRONIZED_STATEMENT:
					return matcher.match((SynchronizedStatement)stat1, stat2);
				case ASTNode.THROW_STATEMENT:
					return matcher.match((ThrowStatement)stat1, stat2);
				case ASTNode.TRY_STATEMENT:
					return matcher.match((TryStatement)stat1, stat2);
				case ASTNode.TYPE_DECLARATION_STATEMENT:
					return matcher.match((TypeDeclarationStatement)stat1, stat2);
				case ASTNode.VARIABLE_DECLARATION_STATEMENT:
					return matcher.match((VariableDeclarationStatement)stat1, stat2);
				case ASTNode.WHILE_STATEMENT:
					return matcher.match((WhileStatement)stat1, stat2);
				}
			}
		}
		
		return false;
	}

}
