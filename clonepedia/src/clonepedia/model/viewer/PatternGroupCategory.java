package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;


public class PatternGroupCategory {
	
	private boolean programmingHierachicalModel = false;
	
	private String name;
	private PatternGroupWrapperList patterns;
	
	public PatternGroupCategory(String name, PatternGroupWrapperList patterns) {
		super();
		this.name = name;
		this.patterns = patterns;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PatternGroupWrapperList getPatterns() {
		return patterns;
	}
	
	public List getPatternList(){
		ArrayList<PatternGroupWrapper> list =
				new ArrayList<PatternGroupWrapper>();
		for(PatternGroupWrapper pgw: patterns){
			list.add(pgw);
		}
		return list;
	}
	
	public void setPatterns(PatternGroupWrapperList patterns) {
		this.patterns = patterns;
	}
	
	public void addPatternGroupWrapper(PatternGroupWrapper patternWrapper){
		patterns.add(patternWrapper);
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
