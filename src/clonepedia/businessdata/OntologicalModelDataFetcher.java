package clonepedia.businessdata;

import java.util.HashMap;
import java.util.HashSet;

import org.carrot2.util.preprocessor.shaded.apache.velocity.runtime.parser.node.GetExecutor;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;

public class OntologicalModelDataFetcher implements OntologicalDataFetcher{

	/**
	 * 
	 */
	private static final long serialVersionUID = -832729195696857226L;

	private CloneSets sets = new CloneSets();
	
	private HashMap<String, Class> classMap = new HashMap<String, Class>();
	private HashMap<String, Interface> interfaceMap = new HashMap<String, Interface>();
	private HashMap<String, Method> methodMap = new HashMap<String, Method>();
	private HashMap<String, Field> fieldMap = new HashMap<String, Field>(); 
	
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
	
}
