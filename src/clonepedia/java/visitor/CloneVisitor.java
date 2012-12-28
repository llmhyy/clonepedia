package clonepedia.java.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class CloneVisitor extends ASTVisitor {
	protected int startLine;
	protected int endLine;
	protected CompilationUnit unit;
	
	public CloneVisitor(int startLine, int endLine, CompilationUnit unit){
		this.startLine = startLine;
		this.endLine = endLine;
		this.unit = unit;
	}
	
}
