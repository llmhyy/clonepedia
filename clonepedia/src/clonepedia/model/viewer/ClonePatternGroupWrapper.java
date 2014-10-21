package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.summary.SummaryUtil;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.MinerUtil;
import clonepedia.views.util.ViewUtil;

public class ClonePatternGroupWrapper extends PatternGroupWrapper implements IContent, IContainer{
	private ClonePatternGroup clonePattern;
	private ArrayList<IContent> content = new ArrayList<IContent>();
	private IContainer container;
	private int cloneSetSize = 0;
	private int diversity = 0;
	
	public ClonePatternGroupWrapper(ClonePatternGroup pc){
		this.clonePattern = pc;
		this.pathSequence = pc.getAbstractPathSequence();
	}
	
	public String toString(){
		try {
			//return getEpitomise3(/*10*/);
			return getEpitomise(/*10*/);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.toString();
	}
	
	/*public String getEpitomise3() {
		PathSequence sequence = this.getClonePattern().getAbstractPathSequence();
		if(this.getClonePattern().isLocationPattern())
			return getFullyQulifiedFormStartByIndex(2, sequence);
		else{
			if(sequence.size() > 3){
				
				if(sequence.get(2) instanceof ComplexType)
					return getFullyQulifiedFormStartByIndex(2, sequence);
				else{
					OntologicalRelationType relation = (OntologicalRelationType) sequence.get(3);
					if(relation.equals(OntologicalRelationType.declaredIn))
						return getFullyQulifiedFormStartByIndex(2, sequence);
					else{
						StringBuffer buffer = new StringBuffer();
						
						OntologicalElement element = (OntologicalElement) sequence.get(2);
						buffer.append(getElementString(element));
						if(relation.equals(OntologicalRelationType.hasType))
							buffer.append(":");
						else if(relation.equals(OntologicalRelationType.hasParameterType))
							buffer.append("(");
						buffer.append(getFullyQulifiedFormStartByIndex(4, sequence));
						
						if(relation.equals(OntologicalRelationType.hasParameterType))
							buffer.append(")");
						
						return buffer.toString();
					}
				}
				
			}
			else
				return ((OntologicalElement)sequence.get(2)).getSimpleElementName();
		}
	}*/
	
	private String getFullyQulifiedFormStartByIndex(int index, PathSequence sequence){
		
		StringBuffer buffer = new StringBuffer();
		
		if(index == sequence.size()-1)
			return getElementString((OntologicalElement)sequence.get(index));
		
		if(sequence.get(index) instanceof OntologicalElement){
			for(int i=sequence.size()-1; i>=index; i=i-2){
				OntologicalElement preElement = (OntologicalElement)sequence.get(i);
				OntologicalRelationType relation = (OntologicalRelationType)sequence.get(i-1);
				//OntologicalElement postElement = (OntologicalElement)sequence.get(i-2);
				
				String preElementString = getElementString(preElement);
				//String postElementString = getElementString(postElement);
				
				String relationSymbol = "";
				
				buffer.append(preElementString);
				if(relation.equals(OntologicalRelationType.extendClass) ||
						relation.equals(OntologicalRelationType.extendInterface) ||
						relation.equals(OntologicalRelationType.implement))
					relationSymbol = "~";
				else if(relation.equals(OntologicalRelationType.isInnerClassOf) ||
						relation.equals(OntologicalRelationType.declaredIn))
					relationSymbol = ".";
				
				if(index != i)
					buffer.append(relationSymbol);
				//buffer.append(postElementString);
			}
			
			return buffer.toString();
		}
		else
			return "";
	}
	
	private String getElementString(OntologicalElement element){
		if(element instanceof Method){
			String nameString = element.getSimpleElementName();
			StringBuffer buffer = new StringBuffer(nameString);
			char ch = buffer.charAt(0);
			if(Character.isLetter(ch))
				ch = Character.toLowerCase(ch);
			buffer.replace(0, 1, String.valueOf(ch));
			return buffer.toString() + "(...)";
		}
		else 
			return element.getSimpleElementName();
	}
	
	public String getEpitomise2(int epitomizingNumber) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int index = 1;
		PathSequence abSeq = clonePattern.getAbstractPathSequence();
		buffer.append(ViewUtil.getSimplifiedNote((OntologicalRelationType)abSeq.get(index)));
		index++;
		OntologicalElement element = (OntologicalElement)abSeq.get(index);
		index++;
		
		if(index >= abSeq.size()){
			if(element instanceof Constant)
				buffer.append("\"" + element.getSimpleElementName() + "\"");
			else 
				buffer.append(element.getSimpleElementName());
			return buffer.toString();
		}
			
		
		OntologicalRelationType relation = (OntologicalRelationType)abSeq.get(index);
		index++;
		
		boolean isNonComplexType = false;
		if(element instanceof Method){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.declaredIn)){
				OntologicalElement complexType = (OntologicalElement)abSeq.get(index);
				buffer.append(complexType.getSimpleElementName() + "." + element.getSimpleElementName()
						+ "()");
			}
			else if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)abSeq.get(index);
				buffer.append(element.getSimpleElementName() + "()<" + returnType.getSimpleElementName() + ">");
			}
			else if(relation.equals(OntologicalRelationType.hasParameterType)){
				OntologicalElement paramType = (OntologicalElement)abSeq.get(index);
				buffer.append(element.getSimpleElementName() + "(" + paramType.getSimpleElementName() + ", *)");
				return buffer.toString();
			}
		}
		else if(element instanceof Field){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.declaredIn)){
				OntologicalElement complexType = (OntologicalElement)abSeq.get(index);
				buffer.append(complexType.getSimpleElementName() + "." + element.getSimpleElementName());
			}
			else if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)abSeq.get(index);
				buffer.append(element.getSimpleElementName() + "<" + returnType.getSimpleElementName() + ">");
			}
		}
		else if(element instanceof Variable){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)abSeq.get(index);
				buffer.append(element.getSimpleElementName() + "<" + returnType.getSimpleElementName() + ">");
			}
		}
		else if(element instanceof Constant){
			isNonComplexType = true;
			buffer.append("\"" + element.getSimpleElementName() + "\"");
			return buffer.toString();
		}
		index++;
		
		int i=index;
		if(!isNonComplexType)
			i -= 3;
		
		for(; i<abSeq.size() && i<(index + epitomizingNumber - 1); i++){
			Object obj = abSeq.get(i);
			if(obj instanceof OntologicalElement){
				OntologicalElement ontoEle = (OntologicalElement)obj; 
				buffer.append(ontoEle.getSimpleElementName());
			}	
			else if(obj instanceof OntologicalRelationType){
				OntologicalRelationType rel = (OntologicalRelationType)obj;
				buffer.append(ViewUtil.getSimplifiedNote(rel));
			}
		}
		
		return buffer.toString();
	}
	
	public String getEpitomise(/*int epitomizingNumber*/) throws Exception{
		PathSequence abSeq = clonePattern.getAbstractPathSequence();
		StringBuffer epitomise = new StringBuffer();
		epitomise.append("clones ");
		
		int index = 1;
		/*if(clonePattern.isLocationPattern() && !showRelationForLocation)
			index++;*/
		
		for(int i=index; i<abSeq.size() /*&& i<(index + epitomizingNumber - 1)*/; i++){
			Object element = abSeq.get(i);
			if(element instanceof OntologicalElement){
				OntologicalElement ontoEle = (OntologicalElement)element; 
				String type = ontoEle.getOntologicalType().toString();
				epitomise.append(ontoEle.getSimpleElementName() + " ");
			}	
			else if(element instanceof OntologicalRelationType){
				OntologicalRelationType relation = (OntologicalRelationType)element;
				epitomise.append(ViewUtil.getString(relation) + " ");
			}
		}
		
		//epitomise += "...";
		/*epitomise = epitomise.replace("<", "[");
		epitomise = epitomise.replace(">", "]");*/
		String label = MinerUtil.xmlStringProcess(epitomise.toString());
		return label;
	}
	
	public String getEpitomiseBak(/*int epitomizingNumber*/) throws Exception{
		PathSequence abSeq = clonePattern.getAbstractPathSequence();
		String epitomise = "";
		
		int index = 1;
		/*if(clonePattern.isLocationPattern() && !showRelationForLocation)
			index++;*/
		
		for(int i=index; i<abSeq.size() /*&& i<(index + epitomizingNumber - 1)*/; i++){
			Object element = abSeq.get(i);
			if(element instanceof OntologicalElement){
				OntologicalElement ontoEle = (OntologicalElement)element; 
				String type = ontoEle.getOntologicalType().toString();
				epitomise += type.substring(0, 4) + "[" + ontoEle.getSimpleElementName() +"]";
			}	
			else if(element instanceof OntologicalRelationType){
				OntologicalRelationType relation = (OntologicalRelationType)element;
				epitomise += ViewUtil.getSimplifiedNote(relation);
			}
				
		}
		
		//epitomise += "...";
		/*epitomise = epitomise.replace("<", "[");
		epitomise = epitomise.replace(">", "]");*/
		epitomise = MinerUtil.xmlStringProcess(epitomise);
		return epitomise;
	}
	
	public String getReverseEpitomise() throws Exception{
		PathSequence abSeq = clonePattern.getAbstractPathSequence();
		String epitomise = "";
		
		int index = abSeq.size() - 1;
		
		for(int i=index; i>1; i--){
			Object element = abSeq.get(i);
			if(element instanceof OntologicalElement){
				OntologicalElement ontoEle = (OntologicalElement)element; 
				String type = ontoEle.getOntologicalType().toString();
				epitomise += type.substring(0, 4) + "[" + ontoEle.getSimpleElementName() +"]";
			}	
			else if(element instanceof OntologicalRelationType){
				OntologicalRelationType relation = (OntologicalRelationType)element;
				epitomise += ViewUtil.getSimplifiedNote(relation);
			}
				
		}
		
		epitomise = MinerUtil.xmlStringProcess(epitomise);
		return epitomise;
	}

	public ClonePatternGroup getClonePattern() {
		return clonePattern;
	}

	public void setClonePattern(ClonePatternGroup clonePattern) {
		this.clonePattern = clonePattern;
	}

	public ArrayList<IContent> getContent() {
		return content;
	}

	public void setContent(ArrayList<IContent> content) {
		this.content = content;
	}

	public IContainer getContainer() {
		return container;
	}

	public void setContainer(IContainer container) {
		this.container = container;
	}

	@Override
	public HashSet<CloneSetWrapper> getAllContainedCloneSet() {
		HashSet<CloneSetWrapper> set = new HashSet<CloneSetWrapper>();
		recursivelyFindCloneSets(set, this);
		
		return set;
	}
	
	private void recursivelyFindCloneSets(HashSet<CloneSetWrapper> basicWrapperSet, IContainer root) {
		for(IContent content: root.getContent())
			if(content instanceof IContainer)
				recursivelyFindCloneSets(basicWrapperSet, (IContainer)content);
			else if(content instanceof CloneSetWrapper)
				basicWrapperSet.add((CloneSetWrapper)content);
	}

	@Override
	public HashSet<CloneSetWrapper> getIntersectionWith(IContainer container) {
		HashSet<CloneSetWrapper> intersectionSet = new HashSet<CloneSetWrapper>();
		for(CloneSetWrapper cloneSetWrapper1: this.getAllContainedCloneSet())
			for(CloneSetWrapper cloneSetWrapper2: container.getAllContainedCloneSet())
				if(cloneSetWrapper1.equals(cloneSetWrapper2))
					intersectionSet.add(cloneSetWrapper1);
		return intersectionSet;
	}
	
	public double value(){
		double value = 0d;
		for(Object obj: clonePattern.getAbstractPathSequence()){
			if(obj instanceof OntologicalElement){
				OntologicalElement element = (OntologicalElement)obj;
				if(!element.getSimpleElementName().equals("*"))
					value++;
			}
			else if(obj instanceof OntologicalRelationType){
				OntologicalRelationType relationType = (OntologicalRelationType)obj;
				if(!(relationType.equals(OntologicalRelationType.declaredIn) || relationType.equals(OntologicalRelationType.resideIn)))
					value++;
			}
		}
		
		return value*size()/diversity();
	}
	
	public int size(){
		if(this.cloneSetSize == 0)
			this.cloneSetSize = getAllContainedCloneSet().size();
		
		return this.cloneSetSize;
	}
	
	public int diversity(){
		if(this.diversity == 0){
			ArrayList<CloneSet> sets = new ArrayList<CloneSet>();
			for(CloneSetWrapper setWrapper: getAllContainedCloneSet())
				sets.add(setWrapper.getCloneSet());
			
			this.diversity = SummaryUtil.generateTopicOrientedSimpleTree(sets).size();
		}
		return this.diversity;
	}

	public int getCloneSetSize() {
		return cloneSetSize;
	}

	public void setCloneSetSize(int cloneSetSize) {
		this.cloneSetSize = cloneSetSize;
	}

	public int getDiversity() {
		return diversity;
	}

	public void setDiversity(int diversity) {
		this.diversity = diversity;
	}
}
