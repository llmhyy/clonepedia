package clonepedia.featuretemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;

import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Method;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.util.Settings;

public class TMGBuilder2 {
	private CloneSets sets;
	private HashSet<TemplateMethodGroup> methodGroupList = new HashSet<TemplateMethodGroup>();

	public TMGBuilder2(CloneSets sets) {
		this.sets = sets;
	}

	public void build(IProgressMonitor monitor) {

		init();

		monitor.worked(20);
		
		ArrayList<TemplateMethodGroup> list = new ArrayList<TemplateMethodGroup>();
		for (TemplateMethodGroup tmg : methodGroupList) {
			list.add(tmg);
		}

		buildCloneRelations();

		list = new ArrayList<TemplateMethodGroup>();
		for (TemplateMethodGroup tmg : methodGroupList) {
			list.add(tmg);
		}

		buildCallingGraph();
		
		monitor.worked(10);
	}

	private void init() {
		ArrayList<Method> methodList = new ArrayList<Method>();
		
		OntologicalModelDataFetcher fetcher = (OntologicalModelDataFetcher)this.sets.getDataFetcher();
		for(String key: fetcher.getMethodMap().keySet()){
			Method method = fetcher.getMethodMap().get(key);
			if(method.getOwner().isClass()){
				methodList.add(method);				
			}
		}
		
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(methodList.toArray(new Method[0]), 
				Settings.thresholdDistanceForTMGFilteringAndSplitting, HierarchyClusterAlgorithm.CompleteLinkage);
		
		try {
			ArrayList<NormalCluster> clusters = algorithm.doClustering();
			
			for(NormalCluster cluster: clusters){
				
				if(cluster.size() > 1){
					TemplateMethodGroup tmg = new TemplateMethodGroup();
					for(IClusterable clusterable: cluster){
						tmg.addMethod((Method)clusterable);
					}
					
					this.methodGroupList.add(tmg);					
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void buildCloneRelations() {
		// TODO Auto-generated method stub

	}

	private void buildCallingGraph() {
		for (TemplateMethodGroup group : this.methodGroupList) {
			for (Method m : group.getMethods()) {
				for (Method calleeMethod : m.getCalleeMethod()) {
					for (TemplateMethodGroup calleeGroup : calleeMethod
							.getTemplateGroupList()) {

						if (existValidatedCallingRelations(group, calleeGroup)) {

							group.addCalleeGroup(calleeGroup);
							calleeGroup.addCallerGroup(group);
						}
					}
				}
			}
		}

	}

	private boolean existValidatedCallingRelations(TemplateMethodGroup callerGroup, TemplateMethodGroup calleeGroup) {

		int count = 0;

		TreeSet<Method> examinedMethods = new TreeSet<Method>();
		for (Method methodInCallerGroup : callerGroup.getMethods()) {
			if (examinedMethods.contains(methodInCallerGroup))
				continue;

			for (Method calleeMethod : methodInCallerGroup.getCalleeMethod()) {

				if (examinedMethods.contains(calleeMethod))
					continue;

				if (calleeGroup.getMethods().contains(calleeMethod)) {
					examinedMethods.add(methodInCallerGroup);
					examinedMethods.add(calleeMethod);

					count++;
					break;
				}
			}
		}

		return count >= Settings.templateMethodGroupCallingStrength;
	}
	
	public HashSet<TemplateMethodGroup> getMethodGroupList() {
		return this.methodGroupList;
	}
}
