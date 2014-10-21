package clonepedia.syntactic.pools;

import java.util.ArrayList;

import clonepedia.model.ontology.Class;

public class ClassPool extends ArrayList<Class>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725554464119745644L;
	
	public Class getClass(String classId){
		for(Class clas: this){
			if(clas.getId().equals(classId))
				return clas;
		}
		return null;
	}
}
