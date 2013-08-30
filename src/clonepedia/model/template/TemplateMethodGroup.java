package clonepedia.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Method;
import clonepedia.util.MinerUtil;
/**
 * 
 * renaming, the concept is as follows:
 * A TMG contains several corresponding methods
 * A SubCandidateTemplate contains TMGs with similar locations
 * A CandidateTemplate contains several SubCaididateTemplate with call relations
 * 
 * @author linyun
 *
 */
public class TemplateMethodGroup implements Serializable, Comparable<TemplateMethodGroup>, IClusterable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -652499753497315887L;
	
	private String name;
	
	private TreeSet<Method> methods = new TreeSet<Method>();
	
	private TreeSet<CloneSet> relatedCloneSets = new TreeSet<CloneSet>();
	private ArrayList<TemplateMethodGroup> containedTMGs = new ArrayList<TemplateMethodGroup>();

	private ArrayList<TemplateMethodGroup> calleeGroup = new ArrayList<TemplateMethodGroup>();
	private ArrayList<TemplateMethodGroup> callerGroup = new ArrayList<TemplateMethodGroup>();
	
	private SubCandidateTemplate subCandidateTemplate;
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
	
	public boolean contains(TemplateMethodGroup tmg){
		if(tmg.getMethods().size() > this.getMethods().size())return false;
		
		for(Method m: tmg.getMethods()){
			if(!this.getMethods().contains(m)){
				return false;
			}
		}
		return true;
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

	public TreeSet<CloneSet> getRelatedCloneSets() {
		return relatedCloneSets;
	}

	public void setRelatedCloneSets(TreeSet<CloneSet> relatedCloneSets) {
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
			
			if(tmg.toString().contains("alignFigure") && this.toString().contains("alignFigure")){
				System.out.print("");
			}
			
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
			ComplexType type = m.getOwner(); 
			types.add(type);
			
			for(ComplexType superType: type.getAllParents()){
				if(!types.contains(superType)){
					types.add(superType);
				}
			}
			
			if(type.isClass()){
				Class clazz = (Class)type;
				Class outerClass = clazz.getOuterClass();
				while(outerClass != null){
					if(!types.contains(outerClass)){
						types.add(outerClass);
					}
					outerClass = outerClass.getOuterClass();
				}
			}
		}
		
		return types;
	}

	public SubCandidateTemplate getSubCandiateTemplate() {
		return subCandidateTemplate;
	}

	public void setSubCandidateTemplate(SubCandidateTemplate templateFeatureGroup) {
		this.subCandidateTemplate = templateFeatureGroup;
	}

	public ArrayList<TemplateMethodGroup> getContainedTMGs() {
		return containedTMGs;
	}

	public void setContainedTMGs(ArrayList<TemplateMethodGroup> containedTMGs) {
		this.containedTMGs = containedTMGs;
	}

	public void addContainedTMG(TemplateMethodGroup tmg){
		this.containedTMGs.add(tmg);
	}

	public String getName() {
		ArrayList<String> list = new ArrayList<String>();
		if(this.name == null){
			for(Method m: getMethods()){
				list.add(m.getMethodName());
			}
			
			this.name = MinerUtil.generateAbstractString(list, MinerUtil.CamelSplitting);
			
		}
		return name;	
		
	}

	public void setName(String name) {
		this.name = name;
	}
}
