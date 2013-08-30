package clonepedia.featuretemplate;

import java.util.ArrayList;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.template.CandidateTemplate;
import clonepedia.model.template.SubCandidateTemplate;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.model.template.TotalCandidateTemplates;
import clonepedia.util.Settings;

public class CandidateTemplateBuilder {

	private ArrayList<TemplateMethodGroup> methodGroupList;
	private TotalCandidateTemplates featureGroups = new TotalCandidateTemplates();

	public CandidateTemplateBuilder(ArrayList<TemplateMethodGroup> methodGroupList) {
		this.methodGroupList = methodGroupList;
	}

	public void generateTemplateFeatures() {
		CandidateTemplate features = clusterTMGByLocation();
		
		buildCallingRelationsForTemplateFeatures(features);
		
		connectTFGsByCallingRelation(features);
	}
	
	private CandidateTemplate clusterTMGByLocation(){
		
		CandidateTemplate features = new CandidateTemplate();
		
		TemplateMethodGroup[] groupList = this.methodGroupList.toArray(new TemplateMethodGroup[0]);
		
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(groupList, 
				Settings.thresholdDistanceForTMGLocationClustering, HierarchyClusterAlgorithm.SingleLinkage);
		try {
			ArrayList<NormalCluster> clusters = algorithm.doClustering();
			
			//int count = 0;
			for(NormalCluster cluster: clusters){
				SubCandidateTemplate featureGroup = new SubCandidateTemplate();
				
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
	
	private void buildCallingRelationsForTemplateFeatures(ArrayList<SubCandidateTemplate> features){
		for(SubCandidateTemplate callerTFG: features){
			for(TemplateMethodGroup callerTMG: callerTFG.getTemplateMethodGroupList()){
				for(TemplateMethodGroup calleeTMG: callerTMG.getCalleeGroup()){
					SubCandidateTemplate calleeTFG = calleeTMG.getTFG();
					
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
	
	/**
	 * Just think of such a problem as graph traversing. Given one vertex v in graph G, we can get a set of
	 * nodes V by DFS. Let us call such a vertex set as depth set (Not that depth set can only be achieved by
	 * traversing only one vertex in G). If graph G has n vertexes, thus, there are at most n depth sets for G. 
	 * We call a depth set independent if and only if it will no longer be a depth set when we add one other 
	 * vertex into the set. Such a method is written for compute all the independent depth set in a G.
	 * 
	 * Tempalte Feature Group (TFG) is for node.
	 * Calling Relation is for edge.
	 * @param tfgList
	 */
	private void connectTFGsByCallingRelation(CandidateTemplate tfgList){

		SubCandidateTemplate[] groupList = tfgList.toArray(new SubCandidateTemplate[0]);

		for (int i = 0; i < groupList.length; i++) {
			SubCandidateTemplate tfg = groupList[i];
			// the group has not been visited
			if (!tfg.isVisited()) {
				CandidateTemplate features = new CandidateTemplate();
				features.add(tfg);

				gatherNeighbourTMGs(features, tfg);
				//gatherCallerTMGs(features, tfg);

				this.featureGroups.add(features);
			}
		}
	}
	
	private void gatherNeighbourTMGs(CandidateTemplate features,
			SubCandidateTemplate group) {

		if (group.getCalleeTFG().size() == 0 && group.getCallerTFG().size() == 0) {
			return;
		} else {
			for (SubCandidateTemplate calleeGroup : group.getCalleeTFG()) {
				calleeGroup.mark();
				if (!features.contains(calleeGroup)) {
					features.add(calleeGroup);
					gatherNeighbourTMGs(features, calleeGroup);
				}
			}
			
			/*for (TemplateFeatureGroup callerGroup : group.getCallerTFG()) {
				callerGroup.mark();
				if (!features.contains(callerGroup)) {
					features.add(callerGroup);
					gatherNeighbourTMGs(features, callerGroup);
				}
			}*/
		}
	}
	
	/*private void gatherCalleeTMGs(TFGList features, TemplateFeatureGroup group) {

		if (features.size() == 0) {
			return;
		} else {
			for (TemplateFeatureGroup callerGroup : group.getCallerTFG()) {
				callerGroup.mark();
				if (!features.contains(callerGroup)) {
					features.add(callerGroup);
					gatherCalleeTMGs(features, callerGroup);
				}
			}
		}
	}*/
	
	/*private void gatherCallerTMGs(TFGList features,
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
	
	private void gatherCalleeGroups(SubCandidateTemplate feature,
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

	private void gatherCallerGroups(SubCandidateTemplate feature,
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

	private ArrayList<SubCandidateTemplate> generateTemplateFeatureByDirectlyCallingRelation() {
		// contructCallGraph();

		ArrayList<SubCandidateTemplate> templateFeatures = new ArrayList<SubCandidateTemplate>();

		TemplateMethodGroup[] groupList = methodGroupList
				.toArray(new TemplateMethodGroup[0]);

		for (int i = 0; i < groupList.length; i++) {
			TemplateMethodGroup tmg = groupList[i];
			// the group has not been visited
			if (!tmg.isVisited()) {
				SubCandidateTemplate feature = new SubCandidateTemplate();
				feature.addTemplateMethodGroup(tmg);

				gatherCalleeGroups(feature, tmg);
				gatherCallerGroups(feature, tmg);

				templateFeatures.add(feature);
			}
		}

		return templateFeatures;
	}

	

	public TotalCandidateTemplates getFeatureGroups() {
		return featureGroups;
	}

	public void setFeatureGroups(TotalCandidateTemplates featureGroups) {
		this.featureGroups = featureGroups;
	}
}
