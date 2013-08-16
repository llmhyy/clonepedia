package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.model.ontology.CloneSets;

public class TFGList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 806907071451200161L;

	private CloneSets sets;
	private ArrayList<ArrayList<TemplateFeatureGroup>> featureGroupList = new ArrayList<ArrayList<TemplateFeatureGroup>>();

	public TFGList(CloneSets sets,
			ArrayList<ArrayList<TemplateFeatureGroup>> featureGroupList) {
		super();
		this.sets = sets;
		this.featureGroupList = featureGroupList;
	}

	public CloneSets getSets() {
		return sets;
	}

	public void setSets(CloneSets sets) {
		this.sets = sets;
	}

	public ArrayList<ArrayList<TemplateFeatureGroup>> getFeatureGroupList() {
		return featureGroupList;
	}

	public void setFeatureGroupList(
			ArrayList<ArrayList<TemplateFeatureGroup>> featureGroupList) {
		this.featureGroupList = featureGroupList;
	}

}
