package clonepedia.businessdata;

import java.util.HashSet;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneSets;
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
	
	private HashSet<Method> methodSet = new HashSet<Method>();
	private HashSet<Class> classSet = new HashSet<Class>();
	private HashSet<Interface> interfaceSet = new HashSet<Interface>();
	private HashSet<Field> fieldSet = new HashSet<Field>(); 
	
	@Override
	public Class getTheExistingClassorCreateOne(String classFullName,
			Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Interface getTheExistingInterfaceorCreateOne(
			String interfaceFullName, Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getTheExistingMethodorCreateOne(Method m) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field getTheExistingFieldorCreateOne(Field f) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeInterfaceWithDependency(Interface interf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeClasswithDependency(Class clas) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
