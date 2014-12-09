package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import clonepedia.model.viewer.programmingelement.VariableWrapper;

public class PatternGroupWrapperList extends ArrayList<PatternGroupWrapper>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5504914536385278570L;
	protected HashMap<String, ProgrammingElementWrapper> programmingHierachicalRoots = new HashMap<String, ProgrammingElementWrapper>();
	
	public void constructProgrammingElementHierarchy(){
		
		if(programmingHierachicalRoots.size() != 0)
			return;
		
		for(PatternGroupWrapper pattern: this){
			
			PathSequence seq = pattern.pathSequence;
			HashMap<String, ProgrammingElementWrapper> parentPool = programmingHierachicalRoots;
			
			for(int i=seq.size()-1; i>=4; i=i-2){
				ProgrammingElement parentElement = (ProgrammingElement)seq.get(i);
				OntologicalRelationType relation = (OntologicalRelationType)seq.get(i-1);
				ProgrammingElement childElement = (ProgrammingElement)seq.get(i-2);
				
				try {
					if(isValidLocationRelation(relation, seq.getStyle())){
						ProgrammingElementWrapper parentWrapper = 
								getOrCreateProgrammingElementWrapperFromPool(parentElement, parentPool);
						ProgrammingElementWrapper childWrapper = 
								getOrCreateProgrammingElementWrapperFromPool(childElement, parentWrapper.getChildren());
						
						parentWrapper.addChild(childWrapper);
						childWrapper.setParent(parentWrapper);
						
						if(i == 4){
							childWrapper.getPatterns().add(pattern);
						}
						
						parentPool = parentWrapper.getChildren();
					}
					else
						break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		//this.setTopListInProgrammingHierarchy(getTopNodesInHierarchy(pool));
	}
	
	private ProgrammingElementWrapper getOrCreateProgrammingElementWrapperFromPool(ProgrammingElement element, 
			HashMap<String, ProgrammingElementWrapper> map){
		ProgrammingElementWrapper elementWrapper = map.get(element.getSimpleElementName());
		if(null == elementWrapper){
			if(element instanceof Method)
				elementWrapper = new MethodWrapper(element);
			else if(element instanceof Field)
				elementWrapper = new FieldWrapper(element);
			else if(element instanceof Variable)
				elementWrapper = new VariableWrapper(element);
			else if(element instanceof Class)
				elementWrapper = new ClassWrapper(element);
			else if(element instanceof Interface)
				elementWrapper = new InterfaceWrapper(element);
			else if(element instanceof MergeableSimpleConcreteElement)
				elementWrapper = new MergeableSimpleElementWrapper(element);
		}
		
		map.put(element.getSimpleElementName(), elementWrapper);
		return elementWrapper;
	}
	
	public boolean isValidLocationRelation(OntologicalRelationType relation, String clonePatternStyle){
		if(relation.equals(OntologicalRelationType.extendInterface) ||
				relation.equals(OntologicalRelationType.extendClass) ||
				relation.equals(OntologicalRelationType.implement) ||
				relation.equals(OntologicalRelationType.isInnerClassOf) ||
				relation.equals(OntologicalRelationType.declaredIn))
			return true;
		else if(relation.equals(OntologicalRelationType.hasType)){
			if(!clonePatternStyle.equals(PathSequence.LOCATION))
				return true;
			else return false;
		}
		else 
			return false;
	}
	
	public ArrayList<ProgrammingElementWrapper> getSortedRootProgrammingElements(Comparator<ProgrammingElementWrapper> comparator){
		ArrayList<ProgrammingElementWrapper> list = new ArrayList<ProgrammingElementWrapper>();
		for(String key: programmingHierachicalRoots.keySet())
			list.add(programmingHierachicalRoots.get(key));
		
		Collections.sort(list, comparator);
		return list;
	}
	
	public HashMap<String, ProgrammingElementWrapper> getProgrammingHierachicalRoots() {
		return programmingHierachicalRoots;
	}

	public void setProgrammingHierachicalRoots(
			HashMap<String, ProgrammingElementWrapper> programmingHierachicalRoots) {
		this.programmingHierachicalRoots = programmingHierachicalRoots;
	}
}
