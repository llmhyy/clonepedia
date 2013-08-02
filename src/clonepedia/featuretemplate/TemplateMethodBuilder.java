package clonepedia.featuretemplate;

import java.util.ArrayList;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Method;
import clonepedia.model.template.TemplateMethodGroup;

/**
 * 
 * @author linyun
 *
 */
public class TemplateMethodBuilder {
	
	private CloneSets sets;
	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();
	
	public TemplateMethodBuilder(CloneSets sets){
		this.sets = sets;
	}
	
	public void build(){
		for(CloneSet set: this.sets.getCloneList()){
			TemplateMethodGroup methodGroup = new TemplateMethodGroup();
			for(CloneInstance instance: set){
				methodGroup.addMethod(instance.getResidingMethod());
			}
			
			if(methodGroup.getMethods().size() > 1){
				verifyTemplateMethodGroup(methodGroup);
			}
		}
		
		sanitize();
	}
	
	/**
	 * Case I: The cloned code ranges of a small-size template method group (TMG) will be overlapped,
	 * or even proper contained in a larger-size template method. 
	 * 
	 * Solution: In this case, I remove small-size TMG and keep large-size TMG
	 * 
	 * Case II: The TMG of the same size may involve more than one clone set
	 * 
	 * Solution: Merge them.
	 */
	private void sanitize(){
		//TODO 
	}
	
	/**
	 * The methods in template method group should be similar in four perspectives:
	 * 
	 * 1. package location
	 * 2. class location
	 * 3. return type
	 * 4. parameter list
	 * 
	 * only if the above perspectives are similar, the method are regarded as a candidate
	 * to form template.
	 * @param methodGroup
	 */
	private void verifyTemplateMethodGroup(TemplateMethodGroup methodGroup){
		IClusterable[] elements = new IClusterable[methodGroup.getMethods().size()];
		int count = 0;
		for(Method m: methodGroup.getMethods()){
			elements[count++] = m;
		}
		
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(elements);
		try {
			ArrayList<NormalCluster> clusters = algorithm.doClustering();
			
			for(NormalCluster cluster: clusters){
				if(cluster.size() > 1){
					TemplateMethodGroup newGroup = new TemplateMethodGroup();
					for(IClusterable ele: cluster){
						Method tMethod = (Method)ele;
						newGroup.addMethod(tMethod);
						tMethod.addTemplateGroup(newGroup);
						
					}
					this.methodGroupList.add(newGroup);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<TemplateMethodGroup> getMethodGroupList(){
		return this.methodGroupList;
	}
}
