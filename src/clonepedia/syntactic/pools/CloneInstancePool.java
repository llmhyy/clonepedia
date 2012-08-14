package clonepedia.syntactic.pools;

import java.util.HashSet;

import clonepedia.model.ontology.CloneInstance;

public class CloneInstancePool extends HashSet<CloneInstance> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2602923729256222266L;

	public CloneInstance getCloneInstance(String cloneInstanceId){
		for(CloneInstance instance: this){
			if(instance.getId().equals(cloneInstanceId))
				return instance;
		}
		
		return null;
	}
}
