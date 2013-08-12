package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Method;
/**
 * 
 * @author linyun
 *
 */
public class TemplateMethodGroup implements Serializable, Comparable<TemplateMethodGroup>, IClusterable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -652499753497315887L;
	private TreeSet<Method> methods = new TreeSet<Method>();
	private HashSet<CloneSet> relatedCloneSets = new HashSet<CloneSet>();

	private ArrayList<TemplateMethodGroup> calleeGroup = new ArrayList<TemplateMethodGroup>();
	private ArrayList<TemplateMethodGroup> callerGroup = new ArrayList<TemplateMethodGroup>();
	
	private TemplateFeatureGroup templateFeatureGroup;
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
		
		buf.append('\n');
		
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
	
	public boolean isVisited(){
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

	
	
	/**
	 * This distance is used to compute similarity of the declaring class of each method
	 * in template method group. The distance is 1 - Jaccard coefficient.
	 */
	@Override
	public double computeDistanceWith(IClusterable cluster) {
		if(cluster instanceof TemplateMethodGroup){
			TemplateMethodGroup tmg = (TemplateMethodGroup)cluster;
			
			ArrayList<ComplexType> types1 = getDeclaringClassFromTemplateMethodGroup(this);
			ArrayList<ComplexType> types2 = getDeclaringClassFromTemplateMethodGroup(tmg);
			
			double count = 0;
			for(ComplexType type: types1){
				if(types2.contains(type)){
					count++;
				}
			}
			
			return 1 - count/(types1.size()+types2.size()-count);
		}
		
		return 1;
	}

	private ArrayList<ComplexType> getDeclaringClassFromTemplateMethodGroup(TemplateMethodGroup tmg){
		ArrayList<ComplexType> types = new ArrayList<ComplexType>();
		for(Method m: tmg.getMethods()){
			types.add(m.getOwner());
		}
		
		return types;
	}

	public TemplateFeatureGroup getTFG() {
		return templateFeatureGroup;
	}

	public void setTFG(TemplateFeatureGroup templateFeatureGroup) {
		this.templateFeatureGroup = templateFeatureGroup;
	}
}
