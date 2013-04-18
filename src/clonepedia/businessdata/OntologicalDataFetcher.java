package clonepedia.businessdata;

import java.io.Serializable;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;

public interface OntologicalDataFetcher extends Serializable{
	
	public clonepedia.model.ontology.Class getTheExistingClassorCreateOne(String classFullName, Project project);
	
	public clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(String interfaceFullName, Project project);
	
	public Method getTheExistingMethodorCreateOne(Method m) throws Exception;
	
	public Field getTheExistingFieldorCreateOne(Field f) throws Exception;
	
	public void storeInterfaceWithDependency(clonepedia.model.ontology.Interface interf);
	
	public void storeClasswithDependency(clonepedia.model.ontology.Class clas);
}
