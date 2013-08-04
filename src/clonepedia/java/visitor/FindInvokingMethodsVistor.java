package clonepedia.java.visitor;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class FindInvokingMethodsVistor extends ASTVisitor {
	
	private HashSet<IMethodBinding> invokedMethodBindings = new HashSet<IMethodBinding>();
	
	public boolean visit(MethodInvocation invoke){
		IMethodBinding methodBinding = invoke.resolveMethodBinding();
		invokedMethodBindings.add(methodBinding);
		
		return true;
	}
	
	public boolean visit(ClassInstanceCreation invoke){
		IMethodBinding methodBinding = invoke.resolveConstructorBinding();
		invokedMethodBindings.add(methodBinding);
		
		return true;
	}
	
	public HashSet<IMethodBinding> getInvokedMethodBindings(){
		return this.invokedMethodBindings;
	}
}
