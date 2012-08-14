package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class CloneASTNodeVisitor extends ASTVisitor {

	private int startLine;
	private int endLine;
	private ArrayList<ASTNode> cloneASTNodeList;
	private CompilationUnit unit;
	
	public CloneASTNodeVisitor(int startLine, int endLine, ArrayList<ASTNode> list, CompilationUnit unit){
		this.startLine = startLine;
		this.endLine = endLine;
		this.cloneASTNodeList = list;
		this.unit = unit;
	}
	
	public ArrayList<ASTNode> getCloneASTNodeList() {
		return cloneASTNodeList;
	}

	public void preVisit(ASTNode node){
		int startPosition = node.getStartPosition();
		if(startPosition >= unit.getPosition(startLine, 0)
				&& startPosition < unit.getPosition(endLine+1, 0)){
			cloneASTNodeList.add(node);
		}
	}

}
