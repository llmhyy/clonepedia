package clonepedia.java.visitor;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;

public class StatementContaingCompleteCloneVisitor extends CloneVisitor {
	
	private boolean isMostInnerStatement = false;
	
	public StatementContaingCompleteCloneVisitor(int startLine, int endLine,
			CompilationUnit unit) {
		super(startLine, endLine, unit);
		// TODO Auto-generated constructor stub
	}

	public boolean visit(Statement stat){
		int startPosition = stat.getStartPosition();
		int endPosition = startPosition + stat.getLength();
		
		if(startPosition <= unit.getPosition(this.startLine, 0) &&
				endPosition >= unit.getPosition(this.endLine+1, 0)){
			this.isMostInnerStatement = true;
			return false;
		}
		
		return true;
	}
	
	public boolean mostInnerStatementIsFound(){
		return this.isMostInnerStatement;
	}
}
