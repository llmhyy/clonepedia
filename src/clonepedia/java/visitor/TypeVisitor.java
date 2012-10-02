package clonepedia.java.visitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.VarType;



public class TypeVisitor extends ASTVisitor {
	
	private OntologicalDataFetcher fetcher = new OntologicalDataFetcher();
	private Project project;
	private CompilationUnit compilationUnit;
	
	public TypeVisitor(CompilationUnit compilationUnit){
		this.compilationUnit = compilationUnit;
	}
	
	public boolean visit(AnonymousClassDeclaration acd){
		
		ITypeBinding binding = acd.resolveBinding();
		
		Class anonymousClass = MinerUtilforJava.extractAndStoreClassInformation(binding, true, 
				fetcher, compilationUnit, project);
		
		try {
			parseAndStoreFieldInformation(binding, anonymousClass);
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
			Class clas = MinerUtilforJava.extractAndStoreClassInformation(binding, false, fetcher, compilationUnit, project);
			try {
				parseAndStoreFieldInformation(binding, clas);
				parseAndStoreMethodInformation(binding, clas, this.compilationUnit);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * It's an interface
		 */
		else{
			Interface interf = MinerUtilforJava.extractAndStoreInterfaceInformation(binding, fetcher, compilationUnit, project);
			
			try {
				parseAndStoreFieldInformation(binding, interf);
				parseAndStoreMethodInformation(binding, interf, this.compilationUnit);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	
	
	private void parseAndStoreMethodInformation(ITypeBinding binding, ComplexType owner, CompilationUnit cu) throws Exception {
		for(IMethodBinding methodBinding: binding.getDeclaredMethods()){
			Method method = MinerUtilforJava.getMethodfromBinding(methodBinding, project, cu);
			fetcher.getTheExistingMethodorCreateOne(method);
		}
	}

	private void parseAndStoreFieldInformation(ITypeBinding binding, ComplexType complexType) throws Exception {
		for(IVariableBinding fieldBinding: binding.getDeclaredFields()){
			VarType varType = MinerUtilforJava.getVariableType(fieldBinding.getType(), project);
			
			if(null != varType){
				Field field = new Field(fieldBinding.getName(), complexType, varType);
				fetcher.getTheExistingFieldorCreateOne(field);
			}
		}
	}
	
	public void setProject(Project project){
		this.project = project;
	}
}
