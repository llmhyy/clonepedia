package clonepedia.businessdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import org.carrot2.util.preprocessor.shaded.apache.velocity.runtime.parser.node.GetExecutor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

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
import clonepedia.util.MinerProperties;

/**
 * An ontological data fetcher is a delegate interacting with ontology or an
 * entity accessing ontology. It aims to add, query and store information into
 * ontology.
 * 
 * Currently (19/4/2013), there are two data fetchers: DB data fetcher
 * (OntologicalDBDataFetcher) and model data fetcher
 * (OntologicalModelDataFetcher). The ontology for the former is stored in
 * database while the one for latter us stored in memory.
 * 
 * @author linyun
 * 
 */
public abstract class OntologicalDataFetcher implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8187961528385695524L;

	/**
	 * Find a existent class in ontology, if there is no such class, this method
	 * will create a new corresponding one in ontology.
	 * 
	 * @param classFullName
	 * @param project
	 * @return
	 */
	public abstract clonepedia.model.ontology.Class getTheExistingClassorCreateOne(
			String classFullName, Project project);

	public abstract clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(
			String interfaceFullName, Project project);

	public abstract Method getTheExistingMethodorCreateOne(Method m)
			throws Exception;

	public abstract Field getTheExistingFieldorCreateOne(Field f)
			throws Exception;

	public abstract void storeInterfaceWithDependency(
			clonepedia.model.ontology.Interface interf);

	public abstract void storeClasswithDependency(
			clonepedia.model.ontology.Class clas);

	protected abstract void storeCloneInstanceWithDependency(
			CloneInstance instance) throws Exception;

	protected abstract void storeCommonRelation(CloneSet set,
			ProgrammingElement element) throws Exception;

	public abstract CloneSet storeDiffRelation(CloneSetWrapper setWrapper)
			throws Exception;

	public void storeCloneInformation(ArrayList<CloneSetWrapper> setWrapperList, Project project) {

		for (CloneSetWrapper setWrapper : setWrapperList) {
			CloneSet set = setWrapper.getCloneSet();
			try {
				set.setProject(project);

				for (CloneInstanceWrapper instanceWrapper : setWrapper) {
					CloneInstance instance = instanceWrapper.getCloneInstance();
					Method residingMethod = MinerUtilforJava.
							getMethodfromASTNode(instanceWrapper.getMethodDeclaration(), project, this);
					
					residingMethod = this.getTheExistingMethodorCreateOne(residingMethod);
					//appendCallerMethod(instanceWrapper.getMethodDeclaration(), residingMethod);
					
					instance.setResidingMethod(residingMethod);
					residingMethod.addCloneInstance(instance);

					storeCloneInstanceWithDependency(instance);
				}

				if (setWrapper.getCommonASTNodeList() != null) {
					for (ASTNode node : setWrapper.getCommonASTNodeList()) {
						if (MinerUtilforJava.isConcernedType(node)) {
							ProgrammingElement element = transferASTNodesToProgrammingElement(
									node, set, project);
							if (null != element) {
								storeCommonRelation(set, element);
							}
							System.out.print("");
						}
					}
				}

				storeDiffRelation(setWrapper);
			} catch (Exception e) {
				System.out.println(setWrapper.getId());
				e.printStackTrace();
			}

		}
	}

	/*@SuppressWarnings("restriction")
	protected void appendCallerMethod(MethodDeclaration md, Method residingMethod) {
		CompilationUnit unit = MinerUtilforJava.getCompilationUnit(md);
		
		IMethod methodElement = (IMethod) md.resolveBinding().getJavaElement();
		
		HashSet<IMethod> callerMethodSet = MinerUtilforJava.getCallerForMethod(methodElement);
		IJavaElement[] list = new IJavaElement[callerMethodSet.size()];
		int count = 0;
		for(IMethod m: callerMethodSet){
			list[count++] = m;
		}


		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(residingMethod.getOwner().getProject().getProjectName());
		try {
			if (project.isNatureEnabled(MinerProperties.javaNatureName)) {
				IJavaProject javaProject = JavaCore.create(project);
				
				
			}
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			ICompilationUnit iunit = (ICompilationUnit) methodElement.
					getAncestor(IJavaElement.COMPILATION_UNIT);
			parser.setSource(iunit);
			//parser.setProject(javaProject);
			parser.setResolveBindings(true);
			
			//parser.setSource(doc.get().toCharArray());
			IBinding[] bindingList = parser.createBindings(list, null);
			
			for(IBinding binding: bindingList){
				if(binding instanceof IMethodBinding){
					IMethodBinding mb = (IMethodBinding)binding;
					Method callerMethod = MinerUtilforJava.getMethodfromBinding(mb, 
							residingMethod.getOwner().getProject(), unit, this);
					
					callerMethod = getTheExistingMethodorCreateOne(callerMethod);
					
					callerMethod.addCalleeMethod(residingMethod);
					residingMethod.addCallerMethod(callerMethod);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}*/
	
	

	public ProgrammingElement transferASTNodesToProgrammingElement(
			ASTNode node, RegionalOwner owner, Project project)
			throws Exception {

		if (MinerUtilforJava.isConcernedType(node)) {
			if (node.getNodeType() == ASTNode.PRIMITIVE_TYPE) {
				PrimitiveType primitiveType = (PrimitiveType) node;
				PrimiType primiType = new PrimiType(primitiveType.toString());
				return primiType;
			} else if (node.getNodeType() == ASTNode.STRING_LITERAL
					|| node.getNodeType() == ASTNode.NUMBER_LITERAL
					|| node.getNodeType() == ASTNode.CHARACTER_LITERAL
					|| node.getNodeType() == ASTNode.BOOLEAN_LITERAL) {

				String typeName = ASTNode.nodeClassForType(node.getNodeType())
						.getSimpleName();
				typeName = typeName.substring(0, typeName.length() - 7);
				Constant constant = new Constant(
						MinerUtilforJava.getConcernedASTNodeName(node),
						new PrimiType(typeName), false);
				return constant;
			} else if (node.getNodeType() == ASTNode.TYPE_LITERAL) {
				TypeLiteral type = (TypeLiteral) node;
				if (type.getType().resolveBinding().isClass()
						|| type.getType().resolveBinding().isInterface()) {
					return MinerUtilforJava.transferTypeToComplexType(
							type.getType(), project,
							(CompilationUnit) node.getRoot(), this);
				} else
					return null;
			} else {
				SimpleName name = (SimpleName) node;

				if (name.resolveBinding() == null)
					return null;

				if (name.resolveBinding().getKind() == IBinding.TYPE) {
					
					ASTNode parentNode = name.getParent();
					while(!((parentNode instanceof Statement) || (parentNode instanceof ClassInstanceCreation))){
						parentNode = parentNode.getParent();
					}
					
					ITypeBinding typeBinding = (ITypeBinding) name.resolveBinding();
					
					if(parentNode instanceof ClassInstanceCreation){
						ClassInstanceCreation creation = (ClassInstanceCreation)parentNode;
						IMethodBinding constructorBinding = creation.resolveConstructorBinding();
						if(constructorBinding.getDeclaringClass().equals(typeBinding)){
							Method m = MinerUtilforJava.getMethodfromBinding(constructorBinding, project, (CompilationUnit) node.getRoot(), this);
							Method method = getTheExistingMethodorCreateOne(m);
							return method;
						}
					}
					
					if (typeBinding.isClass() || typeBinding.isInterface()) {
						return MinerUtilforJava.transferTypeToComplexType(typeBinding, project, (CompilationUnit) node.getRoot(), this);
					} else {						
						return null;
					}
				} else if (name.resolveBinding().getKind() == IBinding.METHOD) {
					IMethodBinding methodBinding = (IMethodBinding) name.resolveBinding();
					Method m = MinerUtilforJava.getMethodfromBinding(methodBinding, project, (CompilationUnit) node.getRoot(), this);
					Method method = getTheExistingMethodorCreateOne(m);
					return method;
				} else if (name.resolveBinding().getKind() == IBinding.VARIABLE) {
					IVariableBinding variableBinding = (IVariableBinding) name
							.resolveBinding();
					if (variableBinding.isField()) {
						Field f = MinerUtilforJava.getFieldfromBinding(
								variableBinding, project,
								(CompilationUnit) node.getRoot(), this);
						if (f.getOwnerType() != null) {
							Field field = getTheExistingFieldorCreateOne(f);
							return field;
						} else {
							Variable v = MinerUtilforJava
									.getVariablefromBinding(name,
											variableBinding, project,
											(CompilationUnit) node.getRoot(),
											this);
							v.setOwner(owner);

							return v;
						}
					} else {
						Variable v = MinerUtilforJava.getVariablefromBinding(
								name, variableBinding, project,
								(CompilationUnit) node.getRoot(), this);
						v.setOwner(owner);

						return v;
					}
				}
				return null;
			}
		} else
			return null;
	}
}
