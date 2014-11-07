package ccdemon.model;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;

public class CodeTemplateMaterial {
	private CloneSet cloneSet;
	private CloneInstance referredCloneInstance;
	
	
	/**
	 * @param cloneSet
	 * @param referredCloneInstance
	 */
	public CodeTemplateMaterial(CloneSet cloneSet,
			CloneInstance referredCloneInstance) {
		super();
		this.cloneSet = cloneSet;
		this.referredCloneInstance = referredCloneInstance;
	}
	
	/**
	 * @return the cloneSet
	 */
	public CloneSet getCloneSet() {
		return cloneSet;
	}
	/**
	 * @param cloneSet the cloneSet to set
	 */
	public void setCloneSet(CloneSet cloneSet) {
		this.cloneSet = cloneSet;
	}
	/**
	 * @return the referredCloneInstance
	 */
	public CloneInstance getReferredCloneInstance() {
		return referredCloneInstance;
	}
	/**
	 * @param referredCloneInstance the referredCloneInstance to set
	 */
	public void setReferredCloneInstance(CloneInstance referredCloneInstance) {
		this.referredCloneInstance = referredCloneInstance;
	}
	
	
}
