package clonepedia.java.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleName;


public class CollectImportedTypeVisitor extends ASTVisitor {
	private ArrayList<String> importList = new ArrayList<String>();
	
	@Override
	public void preVisit(ASTNode node){
		if(node instanceof SimpleName){
			SimpleName name = (SimpleName)node;
			
			ITypeBinding binding = name.resolveTypeBinding();
			
			if(null != binding){
				String qualifiedName = binding.getQualifiedName();
				if(qualifiedName.contains(".")){
					importList.add(qualifiedName);					
				}
				
			}
		}
	}
	
	public ArrayList<String> getImportList(){
		return this.importList;
	}
}
