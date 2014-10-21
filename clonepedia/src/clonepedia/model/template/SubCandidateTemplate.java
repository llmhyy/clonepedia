package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.util.MinerUtil;

/**
 * 
 * renaming, the concept is as follows: A TMG contains several corresponding
 * methods A SubCandidateTemplate contains TMGs with similar locations A
 * CandidateTemplate contains several SubCaididateTemplate with call relations
 * 
 * @author linyun
 * 
 */
public class SubCandidateTemplate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366461236737697331L;

	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();

	private HashSet<SubCandidateTemplate> callerSubCT = new HashSet<SubCandidateTemplate>();
	private HashSet<SubCandidateTemplate> calleeSubCT = new HashSet<SubCandidateTemplate>();

	private HashSet<SubCandidateTemplate> reachableList = new HashSet<SubCandidateTemplate>();
	private HashSet<SubCandidateTemplate> depthSet = new HashSet<SubCandidateTemplate>();

	private boolean visited = false;

	public SubCandidateTemplate() {
	};

	public void addTemplateMethodGroup(TemplateMethodGroup group) {
		if (!methodGroupList.contains(group)) {
			this.methodGroupList.add(group);
		}
	}

	public boolean contains(TemplateMethodGroup group) {
		return this.methodGroupList.contains(group);
	}
	
	public boolean isValidateNode(){
		if(this.reachableList.size() == 0){
			return true;
		}
		/**
		 * check if the start unit is a pure circle: all the nodes in this.reachableList
		 * has exactly the same reachableList.
		 */
		else{
			if(this.reachableList.contains(this)){
				 ArrayList<HashSet<SubCandidateTemplate>> comparingList = new ArrayList<HashSet<SubCandidateTemplate>>();
				 for(SubCandidateTemplate sct: this.reachableList){
					 comparingList.add(sct.getReachableList());
				 }
				 
				 boolean flag = isComparingListContainExactlySameSet(comparingList);
				 
				 if(flag){
					 for(SubCandidateTemplate sct: this.reachableList){
						 sct.mark();
					 }
				 }
				 
				 return flag;
			}
		}
		
		return false;
	}
	
	private boolean isComparingListContainExactlySameSet(ArrayList<HashSet<SubCandidateTemplate>> comparingList){
		HashSet<SubCandidateTemplate> initialSet = comparingList.get(0);
		
		for(int i=1; i<comparingList.size(); i++){
			if(!MinerUtil.isTwoSetSame(initialSet, comparingList.get(i))){
				return false;
			}
		}
		
		return true;
	}
	
	public void buildDepthSet(){
		this.depthSet.add(this);
		recursiveBuildDepthSet(this);
	}
	
	private void recursiveBuildDepthSet(SubCandidateTemplate subTemplate){
		if (subTemplate.calleeSubCT.size() > 0) {
			for(SubCandidateTemplate sct: subTemplate.getCalleeSubCT()){
				if(!this.depthSet.contains(sct)){
					this.depthSet.add(sct);
					recursiveBuildDepthSet(sct);					
				}
			}
		}
	}

	public void buildReachableList() {
		recursiveBuildReachableList(this);
		//System.out.println();
	}

	private void recursiveBuildReachableList(SubCandidateTemplate subTemplate) {
		if (subTemplate.callerSubCT.size() > 0) {
			for(SubCandidateTemplate sct: subTemplate.getCallerSubCT()){
				if(!this.reachableList.contains(sct)){
					this.reachableList.add(sct);
					recursiveBuildReachableList(sct);					
				}
			}
		}
	}

	public ArrayList<TemplateMethodGroup> getTemplateMethodGroupList() {
		return this.methodGroupList;
	}

	public String toString() {
		return this.methodGroupList.toString();
	}

	public HashSet<SubCandidateTemplate> getCallerSubCT() {
		return callerSubCT;
	}

	public void setCallerSubCT(HashSet<SubCandidateTemplate> callerTFG) {
		this.callerSubCT = callerTFG;
	}

	public void addCallerTFG(SubCandidateTemplate callerTFG) {
		this.callerSubCT.add(callerTFG);
	}

	public HashSet<SubCandidateTemplate> getCalleeSubCT() {
		return calleeSubCT;
	}

	public void setCalleeSubCT(HashSet<SubCandidateTemplate> calleeTFG) {
		this.calleeSubCT = calleeTFG;
	}

	public void addCalleeTFG(SubCandidateTemplate calleeTFG) {
		this.calleeSubCT.add(calleeTFG);
	}

	public void mark() {
		this.visited = true;
	}

	public boolean isVisited() {
		return this.visited;
	}

	public HashSet<SubCandidateTemplate> getReachableList() {
		return reachableList;
	}

	public void setReachableList(HashSet<SubCandidateTemplate> reachableList) {
		this.reachableList = reachableList;
	}

	public HashSet<SubCandidateTemplate> getDepthSet() {
		return depthSet;
	}

	public void setDepthSet(HashSet<SubCandidateTemplate> depthSet) {
		this.depthSet = depthSet;
	}
}
