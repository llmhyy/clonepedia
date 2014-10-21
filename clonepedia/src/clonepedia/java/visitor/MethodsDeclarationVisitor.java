package clonepedia.java.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodsDeclarationVisitor extends ASTVisitor {
	
	private MethodDeclaration methodDeclaration;
	private CompilationUnit unit;
	private int startLine;
	private int endLine;
	
	public MethodsDeclarationVisitor(int start, int end, CompilationUnit unit){
		startLine = start;
		endLine = end;
		this.unit = unit;
	}
	
	
	public boolean visit(MethodDeclaration m){
		int tolerence = 3;
		int start = m.getStartPosition();
		//unit.getPosition(line, column)
		if(unit.getLineNumber(start) <= startLine+tolerence
				&& unit.getLineNumber(start+m.getLength()) >= endLine-tolerence){
			
			methodDeclaration = m;
		}
		return false;
	}
	
	public MethodDeclaration getCloneResidingMethod(){
		return this.methodDeclaration;
	}
}
