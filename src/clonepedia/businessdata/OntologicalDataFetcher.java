package clonepedia.businessdata;

import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeLiteral;

import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.CloneSetWrapper;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.RegionalOwner;
import clonepedia.model.ontology.Variable;

/**
 * An ontological data fetcher is a delegate interacting with ontology or an entity accessing ontology. It aims
 * to add, query and store information into ontology.
 * 
 * Currently (19/4/2013), there are two data fetchers: DB data fetcher (OntologicalDBDataFetcher) and model data
 * fetcher (OntologicalModelDataFetcher). The ontology for the former is stored in database while the one for latter
 * us stored in memory.
 * @author linyun
 *
 */
public abstract class OntologicalDataFetcher implements Serializable{
	
	/**
	 * Find a existent class in ontology, if there is no such class, this method will create a new corresponding
	 * one in ontology.
	 * @param classFullName
	 * @param project
	 * @return
	 */
	public abstract clonepedia.model.ontology.Class getTheExistingClassorCreateOne(String classFullName, Project project);
	
	public abstract clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(String interfaceFullName, Project project);
	
	public abstract Method getTheExistingMethodorCreateOne(Method m) throws Exception;
	
	public abstract Field getTheExistingFieldorCreateOne(Field f) throws Exception;
	
	public abstract void storeInterfaceWithDependency(clonepedia.model.ontology.Interface interf);
	
	public abstract void storeClasswithDependency(clonepedia.model.ontology.Class clas);
	
	protected abstract void storeCloneInstanceWithDependency(CloneInstance instance) throws Exception;
	
	protected abstract void storeCommonRelation(CloneSet set, ProgrammingElement element) throws Exception;
	
	protected abstract void storeDiffRelation(CloneSetWrapper setWrapper) throws Exception;

	public void storeCloneInformation(ArrayList<CloneSetWrapper> setWrapperList, Project project){
		
		for(CloneSetWrapper setWrapper: setWrapperList){
			CloneSet set = setWrapper.getCloneSet();
			try{
				set.setProject(project);
				
				for(CloneInstanceWrapper instanceWrapper: setWrapper){
					CloneInstance instance = instanceWrapper.getCloneInstance();
					Method residingMethod = MinerUtilforJava.getMethodfromASTNode(instanceWrapper.getMethodDeclaration(), project, this);
					instance.setResidingMethod(residingMethod);
					storeCloneInstanceWithDependency(instance);
				}
				
				if(setWrapper.getCommonASTNodeList() != null){					
					for(ASTNode node: setWrapper.getCommonASTNodeList()){
						if(MinerUtilforJava.isConcernedType(node)){
							ProgrammingElement element = transferASTNodesToProgrammingElement(node, set, project);
							if(null != element){
								storeCommonRelation(set, element);								
							}
							System.out.print("");
						}
					}
				}
				
				storeDiffRelation(setWrapper);
			}
			catch(Exception e){
				System.out.println(setWrapper.getId());
				e.printStackTrace();
			}
			
		}
	}

	public ProgrammingElement transferASTNodesToProgrammingElement(ASTNode node, RegionalOwner owner, Project project) throws Exception{
		
		if(MinerUtilforJava.isConcernedType(node)){
			if(node.getNodeType() == ASTNode.PRIMITIVE_TYPE){
				PrimitiveType primitiveType = (PrimitiveType)node;
				PrimiType primiType = new PrimiType(primitiveType.toString());
				return primiType;
			}
			else if(node.getNodeType() == ASTNode.STRING_LITERAL ||
					node.getNodeType() == ASTNode.NUMBER_LITERAL ||
					node.getNodeType() == ASTNode.CHARACTER_LITERAL ||
					node.getNodeType() == ASTNode.BOOLEAN_LITERAL){
				
				String typeName = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
				typeName = typeName.substring(0, typeName.length()-7);
				Constant constant = new Constant(MinerUtilforJava.getConcernedASTNodeName(node), new PrimiType(typeName), false);
				return constant;
			}
			else if(node.getNodeType() == ASTNode.TYPE_LITERAL){
				TypeLiteral type = (TypeLiteral)node;
				if(type.getType().resolveBinding().isClass() || type.getType().resolveBinding().isInterface()){
					return MinerUtilforJava.transferTypeToComplexType(type.getType(), project, (CompilationUnit)node.getRoot(), this);	
				}
				else return null;
			}
			else {
				SimpleName name = (SimpleName)node;
				
				if(name.resolveBinding() == null)
					return null;
				
				if(name.resolveBinding().getKind() == IBinding.TYPE){
					ITypeBinding typeBinding = (ITypeBinding)name.resolveBinding();
					if(typeBinding.isClass() || typeBinding.isInterface()){
						return MinerUtilforJava.transferTypeToComplexType(typeBinding, project, (CompilationUnit)node.getRoot(), this);	
					}
					else return null;
				}
				else if(name.resolveBinding().getKind() == IBinding.METHOD){
					IMethodBinding methodBinding = (IMethodBinding) name.resolveBinding();
					Method m = MinerUtilforJava.getMethodfromBinding(methodBinding, project, (CompilationUnit)node.getRoot(), this);
					Method method = getTheExistingMethodorCreateOne(m);
					return method;
				}
				else if(name.resolveBinding().getKind() == IBinding.VARIABLE){
					IVariableBinding variableBinding = (IVariableBinding) name.resolveBinding();
					if(variableBinding.isField()){
						Field f = MinerUtilforJava.getFieldfromBinding(variableBinding, project, (CompilationUnit)node.getRoot(), this);
						if(f.getOwnerType() != null){
							Field field = getTheExistingFieldorCreateOne(f);
							return field;
						}
						else{
							Variable v = MinerUtilforJava.getVariablefromBinding(name, variableBinding, project, (CompilationUnit)node.getRoot(), this);
							v.setOwner(owner);
							
							return v;
						}
					}
					else {
						Variable v = MinerUtilforJava.getVariablefromBinding(name, variableBinding, project, (CompilationUnit)node.getRoot(), this);
						v.setOwner(owner);
						
						return v;
					}
				}
				return null;
			}
		}
		else return null;
	}
}
