package clonepedia.java.visitor;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.VarType;


/**
 * This class extract program structure into database so that the result information
 * is able to be validated.
 * @author linyun
 *
 */
public class GlobalTypeVisitor extends ASTVisitor {
	
	private OntologicalDataFetcher fetcher;
	private Project project;
	private CompilationUnit compilationUnit;
	
	public GlobalTypeVisitor(CompilationUnit compilationUnit, OntologicalDataFetcher fetcher){
		this.compilationUnit = compilationUnit;
		this.fetcher = fetcher;
	}
	
	public boolean visit(AnonymousClassDeclaration acd){
		
		ITypeBinding binding = acd.resolveBinding();
		
		Class anonymousClass = extractAndStoreClassInformation(binding, true, 
				fetcher, compilationUnit, project);
		
		try {
			parseAndStoreFieldInformation(binding, anonymousClass, this.compilationUnit);
			parseAndStoreMethodInformation(binding, anonymousClass, this.compilationUnit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean visit(TypeDeclaration type){
		ITypeBinding binding = type.getName().resolveTypeBinding();
		/**
		 * The type to deal with is a class
		 */
		if(!type.isInterface()){
			Class clas = extractAndStoreClassInformation(binding, false, fetcher, compilationUnit, project);
			try {
				parseAndStoreFieldInformation(binding, clas, this.compilationUnit);
				parseAndStoreMethodInformation(binding, clas, this.compilationUnit);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * It's an interface
		 */
		else{
			Interface interf = extractAndStoreInterfaceInformation(binding, fetcher, compilationUnit, project);
			
			try {
				parseAndStoreFieldInformation(binding, interf, this.compilationUnit);
				parseAndStoreMethodInformation(binding, interf, this.compilationUnit);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * The information extracted: its interface name, project, super interface.
	 * @param binding
	 * @param fetcher
	 * @param compilationUnit
	 * @param project
	 * @return
	 */
	private Interface extractAndStoreInterfaceInformation(ITypeBinding binding, OntologicalDataFetcher fetcher, 
			CompilationUnit compilationUnit, Project project){
		String interfaceFullName = binding.getPackage().getName() + "." + binding.getName();
		
		Interface interf = fetcher.getTheExistingInterfaceorCreateOne(interfaceFullName, project);
		
		for(ITypeBinding interfaceBinding: binding.getInterfaces()){
			Interface superInterface = (Interface) MinerUtilforJava.transferTypeToComplexType(interfaceBinding, project, compilationUnit, fetcher);
			interf.addSuperInterface(superInterface);
		}
		
		fetcher.storeInterfaceWithDependency(interf);
		
		return interf;
	}
	
	/**
	 * The information extracted: its class name, project, superclass, outer class
	 * and interface.
	 * @param binding
	 * @param isAnonymous
	 * @param fetcher
	 * @param compilationUnit
	 * @param project
	 * @return
	 */
	private Class extractAndStoreClassInformation(ITypeBinding binding, boolean isAnonymous, 
			OntologicalDataFetcher fetcher, CompilationUnit compilationUnit, Project project){
		
		String classFullName;
		clonepedia.model.ontology.Class clas = null;
			
		ITypeBinding declaringClassBinding = binding.getDeclaringClass();
		/**
		 * Deal with the case of inner class
		 */
		if(declaringClassBinding != null){
			//String outerClassFullName = declaringClassBinding.getPackage().getName() + "." + declaringClassBinding.getName();
			String outerClassFullName = MinerUtilforJava.getTheLongestPrefixOfFullName(binding, compilationUnit);
			
			classFullName = outerClassFullName + "." + MinerUtilforJava.getSimpleClassNameFromTypeBinding(binding, compilationUnit);
			
			clas = fetcher.getTheExistingClassorCreateOne(classFullName, project);
			
			clonepedia.model.ontology.Class outerClass = fetcher.getTheExistingClassorCreateOne(outerClassFullName, project);
			clas.setOuterClass(outerClass);
			outerClass.getInnerClassList().add(clas);
			
		} 
		else {
			classFullName = binding.getPackage().getName() + "." + binding.getName()/*simpleName.getIdentifier()*/;
			clas = fetcher.getTheExistingClassorCreateOne(classFullName, project);
		}
		
		ITypeBinding superClassBinding = binding.getSuperclass();
		if(null != superClassBinding && !superClassBinding.getName().equals("Object")){
			Class superClass = (Class) MinerUtilforJava.transferTypeToComplexType(superClassBinding, project, compilationUnit, fetcher);
			clas.setSuperClass(superClass);
		}
		
		for(ITypeBinding interfaceBinding: binding.getInterfaces()){
			Interface interf = (Interface)MinerUtilforJava.transferTypeToComplexType(interfaceBinding, project, compilationUnit, fetcher);
			clas.addInterface(interf);
		}
		
		fetcher.storeClasswithDependency(clas);
		return clas;
	}
	
	/**
	 * Information extracted: the method's owner, name, return type, parameters. 
	 * @param binding
	 * @param owner
	 * @param cu
	 * @throws Exception
	 */
	private void parseAndStoreMethodInformation(ITypeBinding binding, ComplexType owner, CompilationUnit cu) throws Exception {
		for(IMethodBinding methodBinding: binding.getDeclaredMethods()){
			Method method = MinerUtilforJava.getMethodfromBinding(methodBinding, project, cu, fetcher);
			
			method = fetcher.getTheExistingMethodorCreateOne(method);
			
			MethodDeclaration md = (MethodDeclaration)cu.findDeclaringNode(methodBinding);
			if(null != md){
				FindInvokingMethodsVistor visitor = new FindInvokingMethodsVistor();	
				md.accept(visitor);
				HashSet<IMethodBinding> invokedMethodBindings = visitor.getInvokedMethodBindings();
				for(IMethodBinding imb: invokedMethodBindings){
					Method invokedMethod = MinerUtilforJava.getMethodfromBinding(imb, project, cu, fetcher);
					invokedMethod = fetcher.getTheExistingMethodorCreateOne(invokedMethod);
					
					invokedMethod.addCallerMethod(method);
					method.addCalleeMethod(invokedMethod);
				}
			}
		}
	}

	/**
	 * Information extracted: the field's owner, type, name.
	 * @param binding
	 * @param complexType
	 * @param cu
	 * @throws Exception
	 */
	private void parseAndStoreFieldInformation(ITypeBinding binding, ComplexType complexType, CompilationUnit cu) throws Exception {
		for(IVariableBinding fieldBinding: binding.getDeclaredFields()){
			VarType varType = MinerUtilforJava.getVariableType(fieldBinding.getType(), project, cu, fetcher);
			
			if(null != varType){
				Field field = new Field(fieldBinding.getName(), complexType, varType);
				fetcher.getTheExistingFieldorCreateOne(field);
			}
		}
	}
	
	public void setProject(Project project){
		this.project = project;
	}
	
	public OntologicalDataFetcher getFetcher(){
		return this.fetcher;
	}
}
