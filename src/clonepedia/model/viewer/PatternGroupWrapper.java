package clonepedia.model.viewer;

import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.util.MinerUtil;
import clonepedia.views.util.ViewUtil;

/**
 * The subclasses of PatternWrapper are {@link PathPatternGroupWrapper} and
 * {@link ClonePatternGroupWrapper}, both of its subclasses should initialize
 * {@link PatternGroupWrapper#pathSequence} field.
 */
public class PatternGroupWrapper{
	
	protected PathSequence pathSequence;
	
	public String toString(){
		try {
			return getEpitomise3(/*10*/);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.toString();
	}
	
	public String getEpitomise3() throws Exception {
		//PathSequence sequence = this.getClonePattern().getAbstractPathSequence();
		if(this.pathSequence.isLocationPath())
			return getFullyQulifiedFormStartByIndex(2, this.pathSequence);
		else{
			if(this.pathSequence.size() > 3){
				
				if(this.pathSequence.get(2) instanceof ComplexType)
					return getFullyQulifiedFormStartByIndex(2, this.pathSequence);
				else{
					OntologicalRelationType relation = (OntologicalRelationType) this.pathSequence.get(3);
					if(relation.equals(OntologicalRelationType.declaredIn))
						return getFullyQulifiedFormStartByIndex(2, this.pathSequence);
					else{
						StringBuffer buffer = new StringBuffer();
						
						OntologicalElement element = (OntologicalElement) this.pathSequence.get(2);
						buffer.append(getElementString(element));
						if(relation.equals(OntologicalRelationType.hasType))
							buffer.append(":");
						else if(relation.equals(OntologicalRelationType.hasParameterType))
							buffer.append("(");
						buffer.append(getFullyQulifiedFormStartByIndex(4, this.pathSequence));
						
						if(relation.equals(OntologicalRelationType.hasParameterType))
							buffer.append(")");
						
						return buffer.toString();
					}
				}
				
			}
			else
				return ((OntologicalElement)this.pathSequence.get(2)).getSimpleElementName();
		}
	}
	
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
		buffer.append(ViewUtil.getSimplifiedNote((OntologicalRelationType)this.pathSequence.get(index)));
		index++;
		OntologicalElement element = (OntologicalElement)this.pathSequence.get(index);
		index++;
		
		if(index >= this.pathSequence.size()){
			if(element instanceof Constant)
				buffer.append("\"" + element.getSimpleElementName() + "\"");
			else 
				buffer.append(element.getSimpleElementName());
			return buffer.toString();
		}
			
		
		OntologicalRelationType relation = (OntologicalRelationType)this.pathSequence.get(index);
		index++;
		
		boolean isNonComplexType = false;
		if(element instanceof Method){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.declaredIn)){
				OntologicalElement complexType = (OntologicalElement)this.pathSequence.get(index);
				buffer.append(complexType.getSimpleElementName() + "." + element.getSimpleElementName()
						+ "()");
			}
			else if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)this.pathSequence.get(index);
				buffer.append(element.getSimpleElementName() + "()<" + returnType.getSimpleElementName() + ">");
			}
			else if(relation.equals(OntologicalRelationType.hasParameterType)){
				OntologicalElement paramType = (OntologicalElement)this.pathSequence.get(index);
				buffer.append(element.getSimpleElementName() + "(" + paramType.getSimpleElementName() + ", *)");
				return buffer.toString();
			}
		}
		else if(element instanceof Field){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.declaredIn)){
				OntologicalElement complexType = (OntologicalElement)this.pathSequence.get(index);
				buffer.append(complexType.getSimpleElementName() + "." + element.getSimpleElementName());
			}
			else if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)this.pathSequence.get(index);
				buffer.append(element.getSimpleElementName() + "<" + returnType.getSimpleElementName() + ">");
			}
		}
		else if(element instanceof Variable){
			isNonComplexType = true;
			if(relation.equals(OntologicalRelationType.hasType)){
				OntologicalElement returnType = (OntologicalElement)this.pathSequence.get(index);
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
		
		for(; i<this.pathSequence.size() && i<(index + epitomizingNumber - 1); i++){
			Object obj = this.pathSequence.get(i);
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
		//PathSequence abSeq = clonePattern.getAbstractPathSequence();
		String epitomise = "";
		
		int index = 1;
		/*if(clonePattern.isLocationPattern() && !showRelationForLocation)
			index++;*/
		
		for(int i=index; i<this.pathSequence.size() /*&& i<(index + epitomizingNumber - 1)*/; i++){
			Object element = this.pathSequence.get(i);
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
		//PathSequence abSeq = clonePattern.getAbstractPathSequence();
		String epitomise = "";
		
		int index = this.pathSequence.size() - 1;
		
		for(int i=index; i>1; i--){
			Object element = this.pathSequence.get(i);
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
}
