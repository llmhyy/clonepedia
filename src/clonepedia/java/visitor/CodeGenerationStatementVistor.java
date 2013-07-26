package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

/**
 * This class is used for find specific statement for automatically generating codes based on
 * code clones.
 * 
 * @author linyun
 *
 */
public class CodeGenerationStatementVistor extends CloneVisitor {
	
	private ArrayList<Statement> cloneStatList = new ArrayList<Statement>();
	
	
	public CodeGenerationStatementVistor(int startLine, int endLine, CompilationUnit unit){
		super(startLine, endLine, unit);
	}
	
	@Override
	public boolean preVisit2(ASTNode node){
		
		if(node instanceof Statement){
			
			Statement stat = (Statement)node; 
				
			if(isBlockofMethod(stat)){
				return true;
			}
			
			int startPosition = stat.getStartPosition();
			
			int endPosition = startPosition + stat.getLength();
			
			if(statementIsCompletelyIncluded(stat, startPosition, endPosition)){
				this.cloneStatList.add(stat);
				return false;
			}
			else if(statementIsPartialCompletelyIncluded(stat, startPosition, endPosition)){
				this.cloneStatList.add(stat);
				return false;
			}
			else if(statementContainCompleteClone(stat, startPosition, endPosition)){
				StatementContaingCompleteCloneVisitor visitor = 
						new StatementContaingCompleteCloneVisitor(startPosition, endPosition, unit);
				if(!visitor.mostInnerStatementIsFound()){
					this.cloneStatList.add(stat);
					return false;
				}
				else{
					return true;
				}
			}
			else{
				return false;
			}
			
		}
		
		return true;
		
	}
	
	protected boolean isBlockofMethod(Statement stat){
		if(stat instanceof Block){
			Block block = (Block)stat;
			return( block.getParent() instanceof MethodDeclaration);
		}
		
		return false;
	}
	
	protected boolean statementIsCompletelyIncluded(Statement stat, int startPosition, int endPosition){
		
		return startPosition >= unit.getPosition(startLine, 0)
				&& endPosition < unit.getPosition(endLine+1, 0);
	} 
	
	protected boolean statementIsPartialCompletelyIncluded(Statement stat, int startPosition, int endPosition){
		return (startPosition < unit.getPosition(startLine, 0)
				&& endPosition < unit.getPosition(endLine+1, 0)) ||
				(startPosition >= unit.getPosition(startLine, 0)
						&& endPosition > unit.getPosition(endLine+1, 0));
	}
	
	protected boolean statementContainNoClone(Statement stat, int startPosition, int endPosition){
		return endPosition < unit.getPosition(startLine, 0)
				|| startPosition > unit.getPosition(endLine+1, 0);
	}
	
	protected boolean statementContainCompleteClone(Statement stat, int startPosition, int endPosition){
		return startPosition < unit.getPosition(startLine, 0)
				&& endPosition > unit.getPosition(endLine+1, 0);
	}
	
	public ArrayList<Statement> getCloneStatementList(){
		return cloneStatList;
	}
	
}
