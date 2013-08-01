package clonepedia.java.model;

import java.util.HashSet;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.Method;

public class TemplateMethodGroup {

	private HashSet<Method> methods = new HashSet<Method>();
	private HashSet<CloneSet> relatedCloneSets = new HashSet<CloneSet>();

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

}
