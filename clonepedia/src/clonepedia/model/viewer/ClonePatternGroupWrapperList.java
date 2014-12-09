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

public class ClonePatternGroupWrapperList extends PatternGroupWrapperList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4515354709738994995L;
	
	public ClonePatternGroupWrapper searchSpecificClonePattern(String uniqueId){
		for(PatternGroupWrapper pattern: this)
			if(pattern instanceof ClonePatternGroupWrapper){
				ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)pattern;
				if(clonePattern.getClonePattern().getUniqueId().equals(uniqueId))
					return clonePattern;
			}
			
		return null;
	}
	
	public ClonePatternGroupWrapperList searchContent(String content){
		ClonePatternGroupWrapperList list = new ClonePatternGroupWrapperList();
		
		content = content.toLowerCase();
		String[] keywordList = content.split(" ");
		
		for(PatternGroupWrapper pattern: this){
			if(pattern instanceof ClonePatternGroupWrapper){
				ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)pattern;
				if(isRelevant(clonePattern, keywordList))
					list.add(clonePattern);
			}
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
	
	
	
	
}
