package clonepedia.businessdata;

import java.io.Serializable;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.Project;

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
public interface OntologicalDataFetcher extends Serializable{
	
	/**
	 * Find a existent class in ontology, if there is no such class, this method will create a new corresponding
	 * one in ontology.
	 * @param classFullName
	 * @param project
	 * @return
	 */
	public clonepedia.model.ontology.Class getTheExistingClassorCreateOne(String classFullName, Project project);
	
	public clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(String interfaceFullName, Project project);
	
	public Method getTheExistingMethodorCreateOne(Method m) throws Exception;
	
	public Field getTheExistingFieldorCreateOne(Field f) throws Exception;
	
	public void storeInterfaceWithDependency(clonepedia.model.ontology.Interface interf);
	
	public void storeClasswithDependency(clonepedia.model.ontology.Class clas);
}
