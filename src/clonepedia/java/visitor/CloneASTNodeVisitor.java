package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class CloneASTNodeVisitor extends CloneVisitor {
	
	protected ArrayList<ASTNode> cloneASTNodeList;
	
	public CloneASTNodeVisitor(int startLine, int endLine, ArrayList<ASTNode> list, CompilationUnit unit){
		super(startLine, endLine, unit);
		this.cloneASTNodeList = list;
	}

	public void preVisit(ASTNode node){
		int startPosition = node.getStartPosition();
		if(startPosition >= unit.getPosition(startLine, 0)
				&& startPosition < unit.getPosition(endLine+1, 0)){
			cloneASTNodeList.add(node);
		}
	}
	
	public ArrayList<ASTNode> getCloneASTNodeList() {
		return cloneASTNodeList;
	}

}
