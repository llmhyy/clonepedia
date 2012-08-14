package clonepedia.syntactic.util.comparator;

import java.io.Serializable;

import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.OntologicalElement;

public class OntologicalComparingPair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1332932578479807493L;
	
	private OntologicalElement element1;
	private OntologicalElement element2;
	
	public OntologicalComparingPair(OntologicalElement element1,
			OntologicalElement element2) {
		super();
		this.element1 = element1;
		this.element2 = element2;
	}
	
	public String toString(){
		return "[" + element1 + "," + element2 + "]";
	}
	
	public boolean isComplexTypePair(){
		if((element1 instanceof ComplexType)
				&& (element2 instanceof ComplexType))
			return true;
		else return false;
	}

	public boolean equals(Object obj){
		if(obj instanceof OntologicalComparingPair){
			OntologicalComparingPair pair = (OntologicalComparingPair)obj;
			if((pair.getElement1().equals(this.element1) && pair.getElement2().equals(this.element2))
					|| (pair.getElement2().equals(this.element1) && pair.getElement1().equals(this.element2)))
				return true;
			else return false;
		}
		else return false;
	}
	
	/*public int hashCode(){
		String eleUniqueString = element1.toString() + element2.toString();
		return eleUniqueString.hashCode();
	}*/
	
	

	public OntologicalElement getElement1() {
		return element1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((element1 == null) ? 0 : element1.hashCode());
		result = prime * result
				+ ((element2 == null) ? 0 : element2.hashCode());
		return result;
	}

	public void setElement1(OntologicalElement element1) {
		this.element1 = element1;
	}

	public OntologicalElement getElement2() {
		return element2;
	}

	public void setElement2(OntologicalElement element2) {
		this.element2 = element2;
	}
	
	
}
