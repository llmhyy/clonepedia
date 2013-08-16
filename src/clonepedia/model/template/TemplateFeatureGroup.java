package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class TemplateFeatureGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366461236737697331L;

	private ArrayList<TemplateMethodGroup> methodGroupList = new ArrayList<TemplateMethodGroup>();
	
	private HashSet<TemplateFeatureGroup> callerTFG = new HashSet<TemplateFeatureGroup>();
	private HashSet<TemplateFeatureGroup> calleeTFG = new HashSet<TemplateFeatureGroup>();
	
	private boolean visited = false;
	
	public TemplateFeatureGroup(){};
	
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

	public HashSet<TemplateFeatureGroup> getCallerTFG() {
		return callerTFG;
	}

	public void setCallerTFG(HashSet<TemplateFeatureGroup> callerTFG) {
		this.callerTFG = callerTFG;
	}
	
	public void addCallerTFG(TemplateFeatureGroup callerTFG){
		this.callerTFG.add(callerTFG);
	}

	public HashSet<TemplateFeatureGroup> getCalleeTFG() {
		return calleeTFG;
	}

	public void setCalleeTFG(HashSet<TemplateFeatureGroup> calleeTFG) {
		this.calleeTFG = calleeTFG;
	}
	
	public void addCalleeTFG(TemplateFeatureGroup calleeTFG){
		this.calleeTFG.add(calleeTFG);
	}
	
	public void mark(){
		this.visited = true;
	}
	
	public boolean isVisited(){
		return this.visited;
	}
}
