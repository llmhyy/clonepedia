package clonepedia.java.visitor;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.VarType;

/**
 * This class extract the program directly into model, which takes much less time
 * than debugging mode. 
 * @author linyun
 *
 */
public class GlobalTypeIntoModelVisitor extends ASTVisitor{
	private Project project;
	private CompilationUnit compilationUnit;
	private OntologicalModelDataFetcher fetcher;
	
	public GlobalTypeIntoModelVisitor(CompilationUnit compilationUnit){
		this.compilationUnit = compilationUnit;
	}
	
	public GlobalTypeIntoModelVisitor(CompilationUnit compilationUnit, Project project){
		this.compilationUnit = compilationUnit;
		this.project = project;
	}
	
	public boolean visit(AnonymousClassDeclaration acd){
		
		ITypeBinding binding = acd.resolveBinding();
		
		Class anonymousClass = MinerUtilforJava.extractAndStoreClassInformation(binding, true, 
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
			Class clas = MinerUtilforJava.extractAndStoreClassInformation(binding, false, fetcher, compilationUnit, project);
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
			Interface interf = MinerUtilforJava.extractAndStoreInterfaceInformation(binding, fetcher, compilationUnit, project);
			
			try {
				parseAndStoreFieldInformation(binding, interf, this.compilationUnit);
				parseAndStoreMethodInformation(binding, interf, this.compilationUnit);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	
	
	private void parseAndStoreMethodInformation(ITypeBinding binding, ComplexType owner, CompilationUnit cu) throws Exception {
		for(IMethodBinding methodBinding: binding.getDeclaredMethods()){
			Method method = MinerUtilforJava.getMethodfromBinding(methodBinding, project, cu, fetcher);
			fetcher.getTheExistingMethodorCreateOne(method);
		}
	}

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
}
