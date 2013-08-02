package clonepedia.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

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
	private HashSet<Method> methods = new HashSet<Method>();
	private HashSet<CloneSet> relatedCloneSets = new HashSet<CloneSet>();

	private ArrayList<TemplateMethodGroup> calleeGroup = new ArrayList<TemplateMethodGroup>();
	private ArrayList<TemplateMethodGroup> callerGroup = new ArrayList<TemplateMethodGroup>();
	
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
	
	public HashSet<Method> getMethods() {
		return methods;
	}

	public void setMethods(HashSet<Method> methods) {
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
		this.calleeGroup.add(group);
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
		this.callerGroup.add(group);
	}

	
}
