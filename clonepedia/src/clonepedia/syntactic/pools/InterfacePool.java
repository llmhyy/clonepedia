package clonepedia.syntactic.pools;

import java.util.ArrayList;

import clonepedia.model.ontology.Interface;

public class InterfacePool extends ArrayList<Interface> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7751503974643857886L;
	
	public Interface getInterface(String interfaceId){
		for(Interface interf: this){
			if(interf.getId().equals(interfaceId))
				return interf;
		}
		return null;
	}
}
