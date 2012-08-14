package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.HashMap;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.MergeableSimpleConcreteElement;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Variable;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.programmingelement.ClassWrapper;
import clonepedia.model.viewer.programmingelement.FieldWrapper;
import clonepedia.model.viewer.programmingelement.InterfaceWrapper;
import clonepedia.model.viewer.programmingelement.MergeableSimpleElementWrapper;
import clonepedia.model.viewer.programmingelement.MethodWrapper;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapperPool;
import clonepedia.model.viewer.programmingelement.VariableWrapper;


public class ClonePatternGroupCategory {
	
	private boolean programmingHierachicalModel = false;
	
	private String name;
	private ClonePatternGroupWrapperList patterns = new ClonePatternGroupWrapperList();
	
	public ClonePatternGroupCategory(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ClonePatternGroupWrapperList getPatterns() {
		return patterns;
	}
	public void setPatterns(ClonePatternGroupWrapperList patterns) {
		this.patterns = patterns;
	}
	
	public void addClonePatternGroupWrapper(ClonePatternGroupWrapper clonePatternWrapper){
		patterns.add(clonePatternWrapper);
	}
	
	
	
	/*private ArrayList<ProgrammingElementWrapper> getTopNodesInHierarchy(ProgrammingElementWrapperPool pool){
		ArrayList<ProgrammingElementWrapper> topList = new ArrayList<ProgrammingElementWrapper>();
		for(String key: pool.keySet()){
			ProgrammingElementWrapper wrapper = pool.get(key);
			if(wrapper.getParent() == null)
				topList.add(wrapper);
		}
		return topList;
	}*/
	
	public HashMap<String, ProgrammingElementWrapper> getProgrammingHierachicalRoots(){
		return this.getPatterns().getProgrammingHierachicalRoots();
	}
	
	public void constructProgrammingElementHierarchy(){
		this.getPatterns().constructProgrammingElementHierarchy();
	}

	public boolean isProgrammingHierachicalModel() {
		return programmingHierachicalModel;
	}

	public void setProgrammingHierachicalModel(boolean programmingHierachicalModel) {
		this.programmingHierachicalModel = programmingHierachicalModel;
	}
}
