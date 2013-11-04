package clonepedia.views.codesnippet;

import clonepedia.model.ontology.CloneInstance;

public class SnippetInstanceRelation{
	public CloneInstance instance;
	public String snippet;
	public int upperDistant;
	public int lowerDistant;
	
	public SnippetInstanceRelation(CloneInstance instance, String snippet,
			int upperDistant, int lowerDistant){
		this.instance = instance;
		this.snippet = snippet;
		this.upperDistant = upperDistant;
		this.lowerDistant = lowerDistant;
	}
}