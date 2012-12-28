package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;

public class CloneASTStatementNodeVisitor extends CloneASTNodeVisitor {
	
	private Statement root;
	
	public CloneASTStatementNodeVisitor(int startLine, int endLine, ArrayList<ASTNode> list, CompilationUnit unit, Statement root){
		super(startLine, endLine, list, unit);
		this.root = root;
	}
	
	@Override
	public boolean preVisit2(ASTNode node){
		if( (node != root) && (node instanceof Statement)){
			return false;			
		}
		else{
			this.cloneASTNodeList.add(node);
			return true;
		}
	}

	public Statement getRoot() {
		return root;
	}

	public void setRoot(Statement root) {
		this.root = root;
	}
}
