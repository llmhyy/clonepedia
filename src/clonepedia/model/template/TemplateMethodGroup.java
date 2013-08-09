package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.Method;
/**
 * 
 * @author linyun
 *
 */
public class TemplateMethodGroup implements Serializable, Comparable<TemplateMethodGroup>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -652499753497315887L;
	private TreeSet<Method> methods = new TreeSet<Method>();
	private HashSet<CloneSet> relatedCloneSets = new HashSet<CloneSet>();

	private ArrayList<TemplateMethodGroup> calleeGroup = new ArrayList<TemplateMethodGroup>();
	private ArrayList<TemplateMethodGroup> callerGroup = new ArrayList<TemplateMethodGroup>();
	
	private int featureId;
	private boolean visited;
	
	public TemplateMethodGroup(){
		this.visited = false;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(Method m: methods){
			buf.append(m.toString() + "\n");
		}
		
		for(CloneSet set: this.relatedCloneSets){
			buf.append(set.getId() + " ");
		}
		
		return buf.toString();
	}
	
	@Override
	public int compareTo(TemplateMethodGroup tmg) {
		return this.toString().compareTo(tmg.toString());
	}
	
	@Override
	public int hashCode(){
		
		int product = 0;
		for(Method m: this.methods){
			product += m.toString().hashCode();
		}
		
		return product;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof TemplateMethodGroup){
			TemplateMethodGroup tmg = (TemplateMethodGroup)obj;
			/*if(tmg.toString().contains("findContains") && this.toString().contains("findContains")){
				System.out.print("");
			}*/
			if(tmg.getMethods().size() == this.getMethods().size()){
				for(Method m: tmg.getMethods()){
					if(!this.getMethods().contains(m)){
						return false;
					}
				}
				
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
	public CloneSet findSmallestCloneSet(){
		CloneSet[] sets = this.relatedCloneSets.toArray(new CloneSet[0]);
		CloneSet tmpSet = sets[0];
		
		for(int i=1; i<sets.length; i++){
			if(sets[i].size() < tmpSet.size()){
				tmpSet = sets[i];
			}
		}
		
		return tmpSet;
	}
	
	public void addMethod(Method m){
		this.methods.add(m);
	}
	
	public TreeSet<Method> getMethods() {
		return methods;
	}

	public void setMethods(TreeSet<Method> methods) {
		this.methods = methods;
	}

	public HashSet<CloneSet> getRelatedCloneSets() {
		return relatedCloneSets;
	}

	public void setRelatedCloneSets(HashSet<CloneSet> relatedCloneSets) {
		this.relatedCloneSets = relatedCloneSets;
	}

	public ArrayList<TemplateMethodGroup> getCalleeGroup() {
		return calleeGroup;
	}
	
	public void addCalleeGroup(TemplateMethodGroup group){
		if(!this.calleeGroup.contains(group) && !group.equals(this)){
			this.calleeGroup.add(group);			
		}
	}

	public void setCalleeGroup(ArrayList<TemplateMethodGroup> calleeGroup) {
		this.calleeGroup = calleeGroup;
	}

	public ArrayList<TemplateMethodGroup> getCallerGroup() {
		return callerGroup;
	}

	public void setCallerGroup(ArrayList<TemplateMethodGroup> callerGroup) {
		this.callerGroup = callerGroup;
	}
	
	public void addCallerGroup(TemplateMethodGroup group){
		if(!this.callerGroup.contains(group) && !group.equals(this)){
			this.callerGroup.add(group);			
		}
	}

	public void setFeaureId(int index){
		this.featureId = index;
	}
	
	public boolean isVisisted(){
		return visited;
	}
	
	public void mark(){
		this.visited = true;
	}
	
	public void resetVisitTag(){
		this.visited = false;
	}
	
	public void addRelatedCloneSet(CloneSet set){
		this.relatedCloneSets.add(set);
	}

	
}
