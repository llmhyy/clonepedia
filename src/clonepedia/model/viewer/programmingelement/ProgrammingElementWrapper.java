package clonepedia.model.viewer.programmingelement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.viewer.PatternGroupWrapperList;

public abstract class ProgrammingElementWrapper {
	
	protected ProgrammingElement element;
	protected ProgrammingElementWrapper parent;
	protected HashMap<String, ProgrammingElementWrapper> children = new HashMap<String, ProgrammingElementWrapper>();
	protected PatternGroupWrapperList patterns = new PatternGroupWrapperList(); 
	protected String name;
	protected int containedPatternNumber = -1;
	
	public ProgrammingElementWrapper(ProgrammingElement element){
		this.element = element;
		this.name = element.getSimpleElementName();
	}
	
	public ProgrammingElementWrapper getParent(){
		return parent;
	}
	
	public void setParent(ProgrammingElementWrapper parent){
		this.parent = parent;
	}
	
	
	public ProgrammingElement getElement() {
		return element;
	}

	public void setElement(ProgrammingElement element) {
		this.element = element;
	}

	public HashMap<String, ProgrammingElementWrapper> getChildren() {
		return children;
	}
	
	public ArrayList<ProgrammingElementWrapper> getChildren(Comparator<ProgrammingElementWrapper> comparator){
		ArrayList<ProgrammingElementWrapper> list = new ArrayList<ProgrammingElementWrapper>();
		for(String key: children.keySet())
			list.add(children.get(key));
		
		Collections.sort(list, comparator);
		return list;
	}

	public void setChildren(HashMap<String, ProgrammingElementWrapper> children) {
		this.children = children;
	}
	
	public void addChild(ProgrammingElementWrapper element){
		ProgrammingElementWrapper e = this.children.get(element.getName());
		if(null == e)
			this.children.put(element.getName(), element);
	}

	public PatternGroupWrapperList getPatterns() {
		return patterns;
	}

	public void setPatterns(PatternGroupWrapperList patterns) {
		this.patterns = patterns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode()*element.getOntologicalType().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ProgrammingElementWrapper){
			ProgrammingElementWrapper wrapper = (ProgrammingElementWrapper)obj;
			return wrapper.getName().equals(this.name) && wrapper.getElement().getOntologicalType().equals(this.element.getOntologicalType());
		}
		
		return false;
	}
	
	public int getContainedClonePatternNumber(){
		if(-1 == this.containedPatternNumber){
			if(this.children.size() == 0)
				return this.patterns.size();
			else{
				int sum = this.patterns.size();
				for(String key: children.keySet()){
					ProgrammingElementWrapper element = children.get(key);
					sum += element.getContainedClonePatternNumber();
				}
				return sum;
			}
		}
		else
			return this.containedPatternNumber;
	}
}
