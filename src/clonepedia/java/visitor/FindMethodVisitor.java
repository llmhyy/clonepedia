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
		String returnTypeString = returnType.toString();
		
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
			System.out.println();
		}
		
		Arrays.sort(tobeComparedList);
		Arrays.sort(this.params);
		
		for(int i=0; i<paramList.size(); i++){
			if(!this.params[i].contains(tobeComparedList[i])){
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