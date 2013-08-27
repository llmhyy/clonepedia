package clonepedia.model.template;

import java.util.ArrayList;

import clonepedia.model.ontology.CloneSets;

public class TotalTFGs extends ArrayList<TFGList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3770890406948695349L;

	private CloneSets sets;
	
	public TotalTFGs(){}
	
	public TotalTFGs(CloneSets sets) {
		super();
		this.sets = sets;
	}
	
	public CloneSets getSets() {
		return sets;
	}
	
	public void setSets(CloneSets sets) {
		this.sets = sets;
	}
	
	public Template findTemplate(String templateID){
		for(TFGList tfgList: this){
			Template template = tfgList.getTemplate();
			if(template.getTemplateID().toString().equals(templateID)){
				return template;
			}
		}
		
		return null;
	}
}
