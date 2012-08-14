package clonepedia.model.syntactic;

import java.util.ArrayList;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.Variable;
import clonepedia.syntactic.util.SyntacticUtil;

public class Path extends ArrayList<OntologicalElement> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4536797398596366824L;
	
	public Path(){}
	
	public Path(OntologicalElement element){
		this.add(element);
	}
	
	public void addNode(OntologicalElement element){
		this.add(element);
	}
	
	public OntologicalElement getLastElement(){
		return this.get(this.size()-1);
	}
	
	public Path copyPath(){
		Path copyPath = new Path();
		for(OntologicalElement element: this){
			copyPath.add(element);
		}
		return copyPath;
	}
	
	public int getContainingOntologicalRelationNumber(OntologicalRelationType type) throws Exception{
		OntologicalElement[] list = this.toArray(new OntologicalElement[0]);
		OntologicalElement pre = list[0];
		
		int count = 0;
		for(int i=1; i<list.length; i++){
			OntologicalRelationType relation = SyntacticUtil.identifyOntologicalRelation(pre, list[i]);
			if(relation.equals(type))
				count++;
			pre = list[i];
		}
		return count;
	}
	
	public int getContaningOntologicalElementNumber(OntologicalElement element){
		int count = 0;
		for(OntologicalElement e: this){
			try{
				if(e.equals(element))
					count++;
			}
			catch(NullPointerException ex){
				ex.printStackTrace();
			}
		}
		return count;
	}
	
	public int getContainingClassNode(){
		int count = 0;
		for(OntologicalElement e: this){
			if(e instanceof Class)
				count++;
		}
		return count;
	}
	
	public int getContainingInterfaceNode(){
		int count = 0;
		for(OntologicalElement e: this){
			if(e instanceof Interface)
				count++;
		}
		return count;
	}
	
	public boolean isDisposable(){
		if(this.size() == 2){
			Object lastElement = getLastElement();
			if(lastElement instanceof Constant)
				return true;
			if(lastElement instanceof ComplexType){
				if(((ComplexType)lastElement).isGeneralType())
					return true;
			}
		}
		else if(this.size() == 3){
			Object subjectElement = this.get(1);
			if((subjectElement instanceof Method) || (subjectElement instanceof Field) || (subjectElement instanceof Variable)){
				Object typeElement = getLastElement();
				if(typeElement instanceof PrimiType)
					return true;
				else if(typeElement instanceof ComplexType)
					if(((ComplexType)typeElement).isGeneralType())
						return true;
				
			}
		}
		
		return false;
	}
	
	public String toString(){
		if(this.size() == 0)
			return "";
		else if(this.size() == 1)
			return this.get(0).toString();
		else{
			
			OntologicalElement[] list = this.toArray(new OntologicalElement[0]);
			OntologicalElement pre = list[0];
			String pathString = pre.toString();
			
			//OntologicalElement suc = list[1];
			for(int i=1; i<list.length; i++){
				try {
					OntologicalRelationType relation = SyntacticUtil.identifyOntologicalRelation(pre, list[i]);
					pathString += " " + relation + " " + list[i];
					pre = list[i];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return pathString;
		}
		
	}
	
	public PathSequence transferToSequence() throws Exception {
		PathSequence seq = new PathSequence();
		OntologicalElement[] elements = this.toArray(new OntologicalElement[0]);
		OntologicalElement pre = elements[0];
		seq.add(pre);
		
		for(int i=1; i<elements.length; i++){
			OntologicalElement element = elements[i];
			OntologicalRelationType relationType = SyntacticUtil.identifyOntologicalRelation(pre, element);
			seq.add(relationType);
			seq.add(element);
			
			pre = element;
		}
		return seq;
	}
}
