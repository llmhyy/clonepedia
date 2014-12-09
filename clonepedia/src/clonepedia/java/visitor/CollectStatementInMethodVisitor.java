package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

public class CollectStatementInMethodVisitor extends ASTVisitor {
	
	private ArrayList<Statement> totalStatList = new ArrayList<Statement>();
	
	@Override
	public boolean preVisit2(ASTNode node){
		if(node instanceof Statement){
			Statement stat = (Statement)node;
			if(!(stat instanceof Block)){				
				totalStatList.add(stat);
				return false;
			}
			else{
				return true;
			}
		}
		else{
			return true;			
		}
		
	}

	public ArrayList<Statement> getTotalStatementList(){
		return totalStatList;
	}
}
