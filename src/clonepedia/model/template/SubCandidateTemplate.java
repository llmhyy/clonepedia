package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class SubCandidateTemplate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366461236737697331L;

	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();
	
	private HashSet<SubCandidateTemplate> callerTFG = new HashSet<SubCandidateTemplate>();
	private HashSet<SubCandidateTemplate> calleeTFG = new HashSet<SubCandidateTemplate>();
	
	private HashSet<SubCandidateTemplate> reachableList = new HashSet<SubCandidateTemplate>();
	
	private boolean visited = false;
	
	public SubCandidateTemplate(){};
	
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
	
	public String toString(){
		return this.methodGroupList.toString();
	}

	public HashSet<SubCandidateTemplate> getCallerTFG() {
		return callerTFG;
	}

	public void setCallerTFG(HashSet<SubCandidateTemplate> callerTFG) {
		this.callerTFG = callerTFG;
	}
	
	public void addCallerTFG(SubCandidateTemplate callerTFG){
		this.callerTFG.add(callerTFG);
	}

	public HashSet<SubCandidateTemplate> getCalleeTFG() {
		return calleeTFG;
	}

	public void setCalleeTFG(HashSet<SubCandidateTemplate> calleeTFG) {
		this.calleeTFG = calleeTFG;
	}
	
	public void addCalleeTFG(SubCandidateTemplate calleeTFG){
		this.calleeTFG.add(calleeTFG);
	}
	
	public void mark(){
		this.visited = true;
	}
	
	public boolean isVisited(){
		return this.visited;
	}

	public HashSet<SubCandidateTemplate> getReachableList() {
		return reachableList;
	}

	public void setReachableList(HashSet<SubCandidateTemplate> reachableList) {
		this.reachableList = reachableList;
	}
}
