package clonepedia.model.template;

import java.util.ArrayList;

public class TemplateFeature {
	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();
	
	public TemplateFeature(){};
	
	public void addTemplateMethodGroup(TemplateMethodGroup group){
		if(!methodGroupList.contains(group)){
			this.methodGroupList.add(group);
		}
	}
	
	public boolean contains(TemplateMethodGroup group){
		return this.methodGroupList.contains(group);
	}
	
	public ArrayList<TemplateMethodGroup> getTemplateMethodGroupList(){
		return this.methodGroupList;
	}
}
