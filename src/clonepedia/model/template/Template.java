package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Method;

public class Template implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -69586874579958250L;

	private ArrayList<Class> abstractClassList = new ArrayList<Class>();
	
	private ArrayList<Method> abstractMethodList = new ArrayList<Method>();

	public Template(ArrayList<Class> abstractClassList,
			ArrayList<Method> abstractMethodList) {
		super();
		this.abstractClassList = abstractClassList;
		this.abstractMethodList = abstractMethodList;
	}

	public ArrayList<Class> getAbstractClassList() {
		return abstractClassList;
	}

	public void setAbstractClassList(ArrayList<Class> abstractClassList) {
		this.abstractClassList = abstractClassList;
	}

	public ArrayList<Method> getAbstractMethodList() {
		return abstractMethodList;
	}

	public void setAbstractMethodList(ArrayList<Method> abstractMethodList) {
		this.abstractMethodList = abstractMethodList;
	}
	
	
}
