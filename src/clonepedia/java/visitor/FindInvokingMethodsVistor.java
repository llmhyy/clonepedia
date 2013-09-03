package clonepedia.java.visitor;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class FindInvokingMethodsVistor extends ASTVisitor {
	
	private HashMap<IMethodBinding, CompilationUnit> invokedMethodBindingMap = new HashMap<IMethodBinding, CompilationUnit>();
	
	public boolean visit(MethodInvocation invoke){
		IMethodBinding methodBinding = invoke.resolveMethodBinding();
		
		CompilationUnit cu = getCompilationUnit(invoke);
		
		if(null != cu){
			invokedMethodBindingMap.put(methodBinding, cu);			
		}
		
		return true;
	}
	
	public boolean visit(ClassInstanceCreation invoke){
		IMethodBinding methodBinding = invoke.resolveConstructorBinding();
		
		CompilationUnit cu = getCompilationUnit(invoke);
		
		if(null != cu){
			invokedMethodBindingMap.put(methodBinding, cu);	
			//methodBinding.getDeclaringClass().
		}
		
		return true;
	}
	
	private CompilationUnit getCompilationUnit(ASTNode node){
		while((node != null) && !(node instanceof CompilationUnit)){
			node = node.getParent();
		}
		
		return (node == null)? null : (CompilationUnit)node;
	}

	public HashMap<IMethodBinding, CompilationUnit> getInvokedMethodBindingMap() {
		return invokedMethodBindingMap;
	}
}
