package clonepedia.businessdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;

import org.carrot2.util.preprocessor.shaded.apache.velocity.runtime.parser.node.GetExecutor;
import org.eclipse.jdt.core.dom.ASTNode;

import clonepedia.db.DBOperator;
import clonepedia.db.schema.Entity;
import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.CloneSetWrapper;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.Variable;
import clonepedia.model.ontology.VariableUseType;

public class OntologicalModelDataFetcher extends OntologicalDataFetcher{

	/**
	 * 
	 */
	private static final long serialVersionUID = -832729195696857226L;

	private HashMap<String, CloneSet> cloneSetMap = new HashMap<String, CloneSet>();
	private HashMap<String, CloneInstance> cloneInstanceMap = new HashMap<String, CloneInstance>();
	
	private HashMap<String, Class> classMap = new HashMap<String, Class>();
	private HashMap<String, Interface> interfaceMap = new HashMap<String, Interface>();
	private HashMap<String, Method> methodMap = new HashMap<String, Method>();
	private HashMap<String, Field> fieldMap = new HashMap<String, Field>(); 

	public HashMap<String, CloneSet> getCloneSetMap(){
		return this.cloneSetMap;
	}
	
	public HashMap<String, Method> getMethodMap(){
		return this.methodMap;
	}
	
	@Override
	public Class getTheExistingClassorCreateOne(String classFullName,
			Project project) {
		Class clazz = this.classMap.get(classFullName);
		if(null == clazz){
			clazz = new Class(classFullName, project, classFullName);
			this.classMap.put(classFullName, clazz);
		}
		
		return clazz;
	}

	@Override
	public Interface getTheExistingInterfaceorCreateOne(
			String interfaceFullName, Project project) {
		Interface interf = this.interfaceMap.get(interfaceFullName);
		if(null == interf){
			interf = new Interface(interfaceFullName, project, interfaceFullName);
			this.interfaceMap.put(interfaceFullName, interf);
		}
		return interf;
	}

	@Override
	public Method getTheExistingMethodorCreateOne(Method m) throws Exception {
		Method method = this.methodMap.get(m.getMethodId());
		if(null == method){
			String methodId = m.getFullName();
			m.setMethodId(methodId);
			this.methodMap.put(methodId, m);
			method = m;
		}
		
		ComplexType owner = method.getOwner();
		if(owner.isClass()){
			Class clazz = (Class)owner;
			clazz = getTheExistingClassorCreateOne(clazz.getFullName(), clazz.getProject());
			clazz.addDistinctMethod(method);
		}
		else{
			Interface interf = (Interface)owner;
			interf = getTheExistingInterfaceorCreateOne(interf.getFullName(), interf.getProject());
			interf.addDistinctMethod(method);
		}
		
		return method;
	}

	@Override
	public Field getTheExistingFieldorCreateOne(Field f) throws Exception {
		Field field = this.fieldMap.get(f.getFullName());
		if(null == field){
			this.fieldMap.put(f.getFullName(), f);
			field = f;
		}
		
		ComplexType owner = field.getOwnerType();
		if(owner.isClass()){
			Class clazz = (Class)owner;
			clazz = getTheExistingClassorCreateOne(clazz.getFullName(), clazz.getProject());
			clazz.addDistinctField(field);
		}
		else{
			Interface interf = (Interface)owner;
			interf = getTheExistingInterfaceorCreateOne(interf.getFullName(), interf.getProject());
			interf.addDistinctField(field);
		}
		
		return field;
	}
	
	@Override
	public void storeClasswithDependency(Class clas) {
		this.classMap.put(clas.getFullName(), clas);
	}
	
	@Override
	public void storeInterfaceWithDependency(Interface interf) {
		this.interfaceMap.put(interf.getFullName(), interf);
	}
	
	private CloneSet getExistingCloneSetorCreateOne(CloneSet set){
		CloneSet cloneSet = this.cloneSetMap.get(set.getId());
		if(null == cloneSet){
			this.cloneSetMap.put(set.getId(), set);
		}
		return set;
	}
	
	private CloneInstance getExistingCloneInstanceorCreateOne(CloneInstance instance){
		CloneInstance cloneInstance = this.cloneInstanceMap.get(instance.getFullName());
		if(null == cloneInstance){
			this.cloneInstanceMap.put(instance.getFullName(), instance);
		}
		return instance;
	}

	@Override
	protected void storeCloneInstanceWithDependency(CloneInstance instance)
			throws Exception {
		
		instance.setId(instance.getFullName());
		getExistingCloneSetorCreateOne(instance.getCloneSet());
		getExistingCloneInstanceorCreateOne(instance);
		
	}

	@Override
	protected void storeCommonRelation(CloneSet set, ProgrammingElement element)
			throws Exception {
		CloneSet cloneSet = getExistingCloneSetorCreateOne(set);
		if (element instanceof Method) {
			Method method = (Method) element;
			cloneSet.addDistinctCallingMethod(method);
		} else if (element instanceof Field) {
			Field field = (Field) element;
			cloneSet.addDistinctAccessingField(field);
		} else if (element instanceof Variable) {
			Variable variable = (Variable) element;
			if(variable.getUseType().equals(VariableUseType.DEFINE)){
				cloneSet.addDistinctDefiningVariable(variable);
			}
			else{
				cloneSet.addDistinctReferingVariable(variable);
			}
			variable.setOwner(cloneSet);
		} else if (element instanceof Constant) {
			Constant constant = (Constant) element;
			cloneSet.addDistinctUsingConstant(constant);
			constant.setOwner(cloneSet);
		} else if (element instanceof ComplexType) {
			ComplexType type = (ComplexType) element;
			if(type.isClass()){
				cloneSet.addDistinctUsingClass((Class)type);
			}
			else{
				cloneSet.addDistinctUsingInterface((Interface)type);
			}
		} else if (element instanceof PrimiType){
			/**
			 * do nothing
			 */
		} else
			throw new Exception(
					"unexpected programming element is transfered into"
							+ " method storeCommonRelation");
		
	}

	@Override
	public CloneSet storeDiffRelation(CloneSetWrapper setWrapper) throws Exception {
		
		CloneSet set = getExistingCloneSetorCreateOne(setWrapper.getCloneSet());
		
		int i=0;
		for(DiffCounterRelationGroupEmulator group: setWrapper.getRelationGroups()){
			CounterRelationGroup crp = new CounterRelationGroup(new Integer(i++).toString());
			for(DiffInstanceElementRelationEmulator relation: group.getElements()){
				
				ASTNode node = relation.getNode();
				if(MinerUtilforJava.isConcernedType(node) /*|| node.getNodeType() == ASTNode.PRIMITIVE_TYPE*/){
					ProgrammingElement element = transferASTNodesToProgrammingElement(node, set, set.getProject());
					CloneInstanceWrapper instanceWrapper = relation.getInstanceWrapper();
					CloneInstance instance = getExistingCloneInstanceorCreateOne(instanceWrapper.getCloneInstance());
					
					Method residingMethod = instance.getResidingMethod();
					if(null == residingMethod){
						residingMethod = MinerUtilforJava.getMethodfromASTNode(instanceWrapper.getMethodDeclaration(), set.getProject(), this);
					}
					instance.setResidingMethod(residingMethod);
					if(null != element){
						InstanceElementRelation ier = new InstanceElementRelation(instance, element);
						crp.add(ier);
					}
					//System.out.print("");
				}
			}
			set.addCounterRelationGroup(crp);
		}
		
		return set;
	}
	
}
