package clonepedia.featuretemplate;

import java.util.ArrayList;

import clonepedia.model.ontology.Method;
import clonepedia.model.template.TemplateFeature;
import clonepedia.model.template.TemplateMethodGroup;

public class TemplateFeatureBuilder {

	private ArrayList<TemplateMethodGroup> methodGroupList;
	
	public TemplateFeatureBuilder(ArrayList<TemplateMethodGroup> methodGroupList){
		this.methodGroupList = methodGroupList;
	}
	
	public ArrayList<TemplateFeature> generateTemplateFeatures(){
		contructCallGraph();
		
		ArrayList<TemplateFeature> templateFeatures = new ArrayList<TemplateFeature>();
		
		TemplateMethodGroup[] groupList = methodGroupList.toArray(new TemplateMethodGroup[0]);
		
		for(int i=0; i<groupList.length; i++){
			TemplateMethodGroup tmg = groupList[i];
			//the group has not been visited
			if(!tmg.isVisisted()){
				TemplateFeature feature = new TemplateFeature();
				feature.addTemplateMethodGroup(tmg);
				
				gatherCalleeGroups(feature, tmg);
				gatherCallerGroups(feature, tmg);
				
				templateFeatures.add(feature);
			}
		}
		
		return templateFeatures;
	}
	
	private void gatherCalleeGroups(TemplateFeature feature, TemplateMethodGroup group){
		
		if(group.getCalleeGroup().size() == 0
				|| feature.contains(group)){
			return;
		}
		else{
			for(TemplateMethodGroup calleeGroup: group.getCalleeGroup()){
				calleeGroup.mark();
				feature.addTemplateMethodGroup(calleeGroup);
				gatherCalleeGroups(feature, calleeGroup);
			}
		}
	}
	
	private void gatherCallerGroups(TemplateFeature feature, TemplateMethodGroup group){
		if(group.getCallerGroup().size() == 0
				|| feature.contains(group)){
			return;
		}
		else{
			for(TemplateMethodGroup callerGroup: group.getCallerGroup()){
				callerGroup.mark();
				feature.addTemplateMethodGroup(callerGroup);
				gatherCallerGroups(feature, callerGroup);
			}
		}
	}
	
	private void contructCallGraph(){
		for(TemplateMethodGroup group: this.methodGroupList){
			for(Method m: group.getMethods()){
				for(TemplateMethodGroup calleeGroup: m.getTemplateGroupList()){
					group.addCalleeGroup(calleeGroup);
					calleeGroup.addCallerGroup(group);
				}
			}
		}
	}
}
