package ccdemon.model.rule;

public class TemplateMatch {
	private int[] templateMatch;
	
	public TemplateMatch(int size){
		this.templateMatch = new int[size];
		for(int i=0; i<templateMatch.length; i++){
			templateMatch[i] = -1;
		}
	}
	
	public void setValue(int index, int value){
		templateMatch[index] = value;
	}

	public int getValue(int index) {
		return templateMatch[index];
	}
}
