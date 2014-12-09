package clonepedia.java.visitor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class FindMethodVisitor extends ASTVisitor {
	
	private String returnType;
	private String methodName;
	private String[] params;
	
	private MethodDeclaration methodDeclaration;
	
	public FindMethodVisitor(String returnType, String methodName, String[] params){
		this.returnType = returnType;
		this.methodName = methodName;
		this.params = params;
	}
	
	public boolean visit(MethodDeclaration md){
		Type returnType = md.getReturnType2();
		String returnTypeString;
		if(returnType != null){
			returnTypeString = returnType.toString();
		}
		else{
			returnTypeString = "";
		}
		
		if(!this.returnType.contains(returnTypeString))return true;
		
		String methodName = md.getName().getIdentifier();
		
		if(!methodName.equals(this.methodName))return true;

		List paramList = md.parameters();
		if(paramList.size() != this.params.length)return true;
		
		String[] tobeComparedList = new String[paramList.size()];
		for (int j = 0; j < paramList.size(); j++) {
			SingleVariableDeclaration svd = (SingleVariableDeclaration) paramList.get(j);

			String paramType = svd.getType().toString();
			
			tobeComparedList[j] = paramType;
			
			/**
			 * Simplified way to handle inner class
			 */
			if(paramType.contains(".")){
				tobeComparedList[j] = tobeComparedList[j].substring(tobeComparedList[j].lastIndexOf(".")+1, tobeComparedList[j].length());				
			}
			//System.out.println();
		}
		
		String[] simpleList = new String[this.params.length];
		for(int i=0; i<this.params.length; i++){
			simpleList[i] = this.params[i].substring(this.params[i].lastIndexOf(".")+1, this.params[i].length());
		}
		
		Arrays.sort(tobeComparedList);
		Arrays.sort(simpleList);
		
		for(int i=0; i<paramList.size(); i++){
			if(!simpleList[i].equals(tobeComparedList[i])){
				return true;
			}
		}
		
		this.methodDeclaration = md;
		
		return false;
	}
	
	public MethodDeclaration getMethodDeclaration(){
		return this.methodDeclaration;
	}
}
