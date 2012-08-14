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
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.programmingelement.ClassWrapper;
import clonepedia.model.viewer.programmingelement.FieldWrapper;
import clonepedia.model.viewer.programmingelement.InterfaceWrapper;
import clonepedia.model.viewer.programmingelement.MergeableSimpleElementWrapper;
import clonepedia.model.viewer.programmingelement.MethodWrapper;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;
import clonepedia.model.viewer.programmingelement.VariableWrapper;

public class ClonePatternGroupWrapperList extends ArrayList<ClonePatternGroupWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4515354709738994995L;
	HashMap<String, ProgrammingElementWrapper> programmingHierachicalRoots = new HashMap<String, ProgrammingElementWrapper>();
	
	
	public ClonePatternGroupWrapper searchSpecificClonePattern(String uniqueId){
		for(ClonePatternGroupWrapper clonePattern: this)
			if(clonePattern.getClonePattern().getUniqueId().equals(uniqueId))
				return clonePattern;
		return null;
	}
	
	public ClonePatternGroupWrapperList searchContent(String content){
		ClonePatternGroupWrapperList list = new ClonePatternGroupWrapperList();
		
		content = content.toLowerCase();
		String[] keywordList = content.split(" ");
		
		
		
		for(ClonePatternGroupWrapper clonePattern: this){
			if(isRelevant(clonePattern, keywordList))
				list.add(clonePattern);
		}
		
		return list;
	}
	
	private boolean isRelevant(ClonePatternGroupWrapper clonePattern, String[] keywordList){
		String epitomise = "";
		try {
			epitomise = clonePattern.getEpitomise();
		} catch (Exception e) {
			e.printStackTrace();
		}
		epitomise = epitomise.toLowerCase();
		
		for(String keyword: keywordList)
			if(epitomise.contains(keyword) || isRelatedToSupportingPathPattern(clonePattern, keyword))
				return true;
		
		return false;	
	}
	
	private boolean isRelatedToSupportingPathPattern(ClonePatternGroupWrapper clonePattern, String content){
		for(PathPatternGroup pathPattern: clonePattern.getClonePattern()){
			String absPathString = pathPattern.getAbstractPathSequence().toString().toLowerCase();
			if(absPathString.contains(content))
				return true;
		}
		return false;
	}
	
	public void constructProgrammingElementHierarchy(){
		
		if(programmingHierachicalRoots.size() != 0)
			return;
		
		for(ClonePatternGroupWrapper clonePattern: this){
			
			PathSequence seq = clonePattern.getClonePattern().getAbstractPathSequence();
			HashMap<String, ProgrammingElementWrapper> parentPool = programmingHierachicalRoots;
			
			for(int i=seq.size()-1; i>=4; i=i-2){
				ProgrammingElement parentElement = (ProgrammingElement)seq.get(i);
				OntologicalRelationType relation = (OntologicalRelationType)seq.get(i-1);
				ProgrammingElement childElement = (ProgrammingElement)seq.get(i-2);
				
				if(isValidLocationRelation(relation, clonePattern.getClonePattern().getStyle())){
					ProgrammingElementWrapper parentWrapper = 
							getOrCreateProgrammingElementWrapperFromPool(parentElement, parentPool);
					ProgrammingElementWrapper childWrapper = 
							getOrCreateProgrammingElementWrapperFromPool(childElement, parentWrapper.getChildren());
					
					parentWrapper.addChild(childWrapper);
					childWrapper.setParent(parentWrapper);
					
					if(i == 4){
						childWrapper.getClonePatterns().add(clonePattern);
					}
					
					parentPool = parentWrapper.getChildren();
				}
				else
					break;
				
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
			if(!clonePatternStyle.equals(ClonePatternGroup.LOCATION))
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
