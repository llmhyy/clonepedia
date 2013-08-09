package clonepedia.featuretemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.ontology.Method;
import clonepedia.model.template.TemplateFeature;
import clonepedia.model.template.TemplateMethodGroup;

public class TemplateFeatureBuilder {

	private ArrayList<TemplateMethodGroup> methodGroupList;

	public TemplateFeatureBuilder(ArrayList<TemplateMethodGroup> methodGroupList) {
		this.methodGroupList = methodGroupList;
	}

	public void generateTemplateFeatures() {
		ArrayList<TemplateFeature> features = clusterTMGByLocation();
		
		buildCallingRelationsForTemplateFeatures(features);
	}
	
	private ArrayList<TemplateFeature> clusterTMGByLocation(){
		
		ArrayList<TemplateFeature> features = new ArrayList<TemplateFeature>();
		
		TemplateMethodGroup[] groupList = this.methodGroupList.toArray(new TemplateMethodGroup[0]);
		
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(groupList);
		try {
			ArrayList<NormalCluster> clusters = algorithm.doClustering();
			for(NormalCluster cluster: clusters){
				TemplateFeature feature = new TemplateFeature();
				
				for(IClusterable clusterable: cluster){
					TemplateMethodGroup group = (TemplateMethodGroup)clusterable;
					feature.addTemplateMethodGroup(group);
				}
				
				features.add(feature);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return features;
	}
	
	private void buildCallingRelationsForTemplateFeatures(ArrayList<TemplateFeature> features){
		
	}

	private ArrayList<TemplateFeature> generateTemplateFeatureByDirectlyCallingRelation() {
		// contructCallGraph();

		ArrayList<TemplateFeature> templateFeatures = new ArrayList<TemplateFeature>();

		TemplateMethodGroup[] groupList = methodGroupList
				.toArray(new TemplateMethodGroup[0]);

		for (int i = 0; i < groupList.length; i++) {
			TemplateMethodGroup tmg = groupList[i];
			// the group has not been visited
			if (!tmg.isVisisted()) {
				TemplateFeature feature = new TemplateFeature();
				feature.addTemplateMethodGroup(tmg);

				gatherCalleeGroups(feature, tmg);
				gatherCallerGroups(feature, tmg);

				templateFeatures.add(feature);
			}
		}

		return templateFeatures;
	}

	private void gatherCalleeGroups(TemplateFeature feature,
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

	private void gatherCallerGroups(TemplateFeature feature,
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

}
