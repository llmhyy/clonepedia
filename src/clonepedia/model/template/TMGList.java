package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.model.ontology.CloneSets;

public class TMGList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4131258151559067855L;
	
	private CloneSets sets;
	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();
	
	public TMGList(CloneSets sets,
			ArrayList<TemplateMethodGroup> methodGroupList) {
		super();
		this.sets = sets;
		this.methodGroupList = methodGroupList;
	}
	
	
	public CloneSets getSets() {
		return sets;
	}
	public void setSets(CloneSets sets) {
		this.sets = sets;
	}
	public ArrayList<TemplateMethodGroup> getMethodGroupList() {
		return methodGroupList;
	}
	public void setMethodGroupList(ArrayList<TemplateMethodGroup> methodGroupList) {
		this.methodGroupList = methodGroupList;
	}
}
