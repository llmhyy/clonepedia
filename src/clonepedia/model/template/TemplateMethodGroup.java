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
public class TemplateMethodGroup implements Serializable{

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
	
	public int hasCode(){
		int product = 1;
		for(Method m: this.methods){
			product *= m.toString().hashCode();
		}
		
		return product;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(Method m: methods){
			buf.append(m.toString() + "\n");
		}
		
		return buf.toString();
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
}
