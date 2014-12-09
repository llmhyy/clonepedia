package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;

public class CloneASTStatementVisitor extends CloneVisitor{
	
	private ArrayList<Statement> cloneStatementList;
	
	public CloneASTStatementVisitor(int startLine, int endLine, ArrayList<Statement> list, CompilationUnit unit){
		super(startLine, endLine, unit);
		this.cloneStatementList = list;
	}

	public void preVisit(ASTNode node){
		if(node instanceof Statement){
			int startPosition = node.getStartPosition();
			if(startPosition >= unit.getPosition(startLine, 0)
					&& startPosition < unit.getPosition(endLine+1, 0)){
				this.cloneStatementList.add((Statement)node);
			}
		}
	}
	
	public ArrayList<Statement> getCloneStatementList(){
		return this.cloneStatementList;
	}
}
