package clonepedia.featuretemplate;

import java.util.ArrayList;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.template.TemplateFeatureGroup;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.util.Settings;

public class TFGBuilder {

	private ArrayList<TemplateMethodGroup> methodGroupList;
	private ArrayList<ArrayList<TemplateFeatureGroup>> featureGroups = new ArrayList<ArrayList<TemplateFeatureGroup>>();

	public TFGBuilder(ArrayList<TemplateMethodGroup> methodGroupList) {
		this.methodGroupList = methodGroupList;
	}

	public void generateTemplateFeatures() {
		ArrayList<TemplateFeatureGroup> features = clusterTMGByLocation();
		
		buildCallingRelationsForTemplateFeatures(features);
		
		connectTFGsByCallingRelation(features);
	}
	
	private ArrayList<TemplateFeatureGroup> clusterTMGByLocation(){
		
		ArrayList<TemplateFeatureGroup> features = new ArrayList<TemplateFeatureGroup>();
		
		TemplateMethodGroup[] groupList = this.methodGroupList.toArray(new TemplateMethodGroup[0]);
		
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(groupList, Settings.thresholdDistanceForTMGFilteringAndSplitting);
		try {
			ArrayList<NormalCluster> clusters = algorithm.doClustering();
			
			//int count = 0;
			for(NormalCluster cluster: clusters){
				TemplateFeatureGroup featureGroup = new TemplateFeatureGroup();
				
				for(IClusterable clusterable: cluster){
					TemplateMethodGroup tmg = (TemplateMethodGroup)clusterable;
					featureGroup.addTemplateMethodGroup(tmg);
					tmg.setTFG(featureGroup);
					//tmg.mark();
				}
				
				features.add(featureGroup);
			}
			
			//System.out.println(count);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return features;
	}
	
	private void buildCallingRelationsForTemplateFeatures(ArrayList<TemplateFeatureGroup> features){
		for(TemplateFeatureGroup callerTFG: features){
			for(TemplateMethodGroup callerTMG: callerTFG.getTemplateMethodGroupList()){
				for(TemplateMethodGroup calleeTMG: callerTMG.getCalleeGroup()){
					TemplateFeatureGroup calleeTFG = calleeTMG.getTFG();
					
					if(calleeTFG == null){
						System.out.print("");
					}
					
					if(calleeTFG != null && !calleeTFG.equals(callerTFG)){
						callerTFG.addCalleeTFG(calleeTFG);
						calleeTFG.addCallerTFG(callerTFG);
					}
				}
			}
		}
	}
	
	private void connectTFGsByCallingRelation(ArrayList<TemplateFeatureGroup> TFGList){

		TemplateFeatureGroup[] groupList = TFGList.toArray(new TemplateFeatureGroup[0]);

		for (int i = 0; i < groupList.length; i++) {
			TemplateFeatureGroup tfg = groupList[i];
			// the group has not been visited
			if (!tfg.isVisited()) {
				ArrayList<TemplateFeatureGroup> features = new ArrayList<TemplateFeatureGroup>();
				features.add(tfg);

				gatherNeighbourTMGs(features, tfg);
				//gatherCallerTMGs(features, tfg);

				this.featureGroups.add(features);
			}
		}
	}
	
	private void gatherNeighbourTMGs(ArrayList<TemplateFeatureGroup> features,
			TemplateFeatureGroup group) {

		if (features.size() == 0) {
			return;
		} else {
			for (TemplateFeatureGroup calleeGroup : group.getCalleeTFG()) {
				calleeGroup.mark();
				if (!features.contains(calleeGroup)) {
					features.add(calleeGroup);
					gatherNeighbourTMGs(features, calleeGroup);
				}
			}
			
			for (TemplateFeatureGroup callerGroup : group.getCallerTFG()) {
				callerGroup.mark();
				if (!features.contains(callerGroup)) {
					features.add(callerGroup);
					gatherNeighbourTMGs(features, callerGroup);
				}
			}
		}
	}
	
	/*private void gatherCallerTMGs(ArrayList<TemplateFeatureGroup> features,
			TemplateFeatureGroup group) {

		if (features.size() == 0) {
			return;
		} else {
			for (TemplateFeatureGroup callerGroup : group.getCallerTFG()) {
				callerGroup.mark();
				if (!features.contains(callerGroup)) {
					features.add(callerGroup);
					gatherCallerTMGs(features, callerGroup);
				}
			}
		}
	}*/
	
	private void gatherCalleeGroups(TemplateFeatureGroup feature,
			TemplateMethodGroup group) {

		if (group.getCalleeGroup().size() == 0) {
			return;
		} else {
			for (TemplateMethodGroup calleeGroup : group.getCalleeGroup()) {
				calleeGroup.mark();
				if (!feature.contains(calleeGroup)) {
					feature.addTemplateMethodGroup(calleeGroup);
					gatherCalleeGroups(feature, calleeGroup);
				}
			}
		}
	}

	private void gatherCallerGroups(TemplateFeatureGroup feature,
			TemplateMethodGroup group) {
		if (group.getCallerGroup().size() == 0) {
			return;
		} else {
			for (TemplateMethodGroup callerGroup : group.getCallerGroup()) {
				callerGroup.mark();
				if (!feature.contains(group)) {
					feature.addTemplateMethodGroup(callerGroup);
					gatherCallerGroups(feature, callerGroup);
				}
			}
		}
	}

	private ArrayList<TemplateFeatureGroup> generateTemplateFeatureByDirectlyCallingRelation() {
		// contructCallGraph();

		ArrayList<TemplateFeatureGroup> templateFeatures = new ArrayList<TemplateFeatureGroup>();

		TemplateMethodGroup[] groupList = methodGroupList
				.toArray(new TemplateMethodGroup[0]);

		for (int i = 0; i < groupList.length; i++) {
			TemplateMethodGroup tmg = groupList[i];
			// the group has not been visited
			if (!tmg.isVisited()) {
				TemplateFeatureGroup feature = new TemplateFeatureGroup();
				feature.addTemplateMethodGroup(tmg);

				gatherCalleeGroups(feature, tmg);
				gatherCallerGroups(feature, tmg);

				templateFeatures.add(feature);
			}
		}

		return templateFeatures;
	}

	

	public ArrayList<ArrayList<TemplateFeatureGroup>> getFeatureGroups() {
		return featureGroups;
	}

	public void setFeatureGroups(ArrayList<ArrayList<TemplateFeatureGroup>> featureGroups) {
		this.featureGroups = featureGroups;
	}
}
