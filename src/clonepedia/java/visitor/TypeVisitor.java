package clonepedia.java.visitor;

//import java.util.ArrayList;
import java.util.List;


import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
//import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import clonepedia.db.DBOperator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.VarType;



public class TypeVisitor extends ASTVisitor {
	
	private DBOperator op = new DBOperator();
	private Project project;
	
	@SuppressWarnings("rawtypes")
	public boolean visit(TypeDeclaration type){
		
		//DBOperation op = new DBOperation();
		
		SimpleName simpleName = type.getName();
		ITypeBinding binding = simpleName.resolveTypeBinding();
		
		/*if(simpleName.getIdentifier().length() == 0)
			System.out.print("");*/
		
		/**
		 * The type to deal with is a class
		 */
		if(!type.isInterface()){
			
			String classFullName;
			clonepedia.model.ontology.Class clas = null;
			/**
			 * Deal with the case of inner class
			 */
			if(!(type.getParent() instanceof CompilationUnit)){
				CompilationUnit root = (CompilationUnit)type.getRoot();
				TypeDeclaration outer = (TypeDeclaration)root.types().get(0);
				
				String outerClassFullName = outer.resolveBinding().getPackage().getName() + "." + outer.getName().getIdentifier();
				
				classFullName = binding.getPackage().getName() + "." + 
						/*outer.getName().getIdentifier() + "." + */simpleName.getIdentifier();
				clas = op.getTheExistingClassorCreateOne(classFullName, project);
				
				clonepedia.model.ontology.Class outerClass = op.getTheExistingClassorCreateOne(outerClassFullName, project);
				clas.setOuterClass(outerClass);
				
				System.out.print("");
			} 
			else {
				classFullName = binding.getPackage().getName() + "." + simpleName.getIdentifier();
				clas = op.getTheExistingClassorCreateOne(classFullName, project);
			}
			
			
			Type superClassType = type.getSuperclassType();
			if(null != superClassType){					
				clonepedia.model.ontology.Class superClass = (Class) MinerUtilforJava.transferTypeToComplexType(superClassType, project);
				clas.setSuperClass(superClass);
			}
			
			List interfaceList = type.superInterfaceTypes();
			for(int i=0; i<interfaceList.size(); i++){
				Type interfaceType = (Type)interfaceList.get(i);
				
				clonepedia.model.ontology.Interface interf = (Interface) MinerUtilforJava.transferTypeToComplexType(interfaceType, project);
				clas.addInterface(interf);
				
			}
			
			op.storeClasswithDependency(clas);
			
			try {
				parseandStoreFieldInformation(type, clas);
				parseandStoreMethodInformation(type, clas);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println();
		}
		/**
		 * It's an interface
		 */
		else{
			String interfaceFullName = binding.getPackage().getName() + "." + simpleName.getIdentifier();
			
			clonepedia.model.ontology.Interface interf = op.getTheExistingInterfaceorCreateOne(interfaceFullName, project);
			
			List interfaceList = type.superInterfaceTypes();
			for(int i=0; i<interfaceList.size(); i++){
				SimpleType interfaceType = (SimpleType)interfaceList.get(i);	
				clonepedia.model.ontology.Interface in = (Interface) MinerUtilforJava.transferTypeToComplexType(interfaceType, project);
				interf.addSuperInterface(in);		
			}
			
			op.storeInterfaceWithDependency(interf);
			
			try {
				parseandStoreFieldInformation(type, interf);
				parseandStoreMethodInformation(type, interf);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println();
		}
		
		//System.out.println();
		return true;
	}
	
	private void parseandStoreMethodInformation(TypeDeclaration type, ComplexType owner) throws Exception {
		
		//MinerUtilforJava mufj = new MinerUtilforJava();
		
		MethodDeclaration[] md = type.getMethods();
		for(int i=0; i<md.length; i++){
			Method method = MinerUtilforJava.getMethodfromASTNode(md[i], project);
			
			/*if(method.getOwner().getSimpleName().length() == 0)
				System.out.print("");*/
			
			op.getTheExistingMethodorCreateOne(method);
			System.out.print("");
		}
	}
	
	

	@SuppressWarnings("rawtypes")
	private void parseandStoreFieldInformation(TypeDeclaration type, ComplexType complexType) throws Exception {
		FieldDeclaration[] fd = type.getFields();
		for(int i=0; i<fd.length; i++){
			Type t  = fd[i].getType();
			
			VarType varType = MinerUtilforJava.getVariableType(t, project);
			
			if(null != varType){
				List fieList = fd[i].fragments();
				
				for(int j=0; j<fieList.size(); j++){
					VariableDeclarationFragment fragment = (VariableDeclarationFragment)fieList.get(j);
					SimpleName sName = fragment.getName();
					
					Field field = new Field(sName.getIdentifier(), complexType, varType);
					op.getTheExistingFieldorCreateOne(field);
					//System.out.print("");
				}
			}
		}
	}
	
	
	
	public void setProject(Project project){
		this.project = project;
	}
}
