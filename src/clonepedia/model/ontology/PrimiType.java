package clonepedia.model.ontology;

import java.util.ArrayList;

public class PrimiType extends ProgrammingElement implements VarType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3176680308112333471L;
	
	
	public String toString(){
		return fullName;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof VarType){
			VarType vt = (VarType)obj;
			return vt.getFullName().equals(this.fullName);
		}
		return false;
	}
	
	public PrimiType(String typeName) {
		this.fullName = typeName;
	}

	public String getTypeName(){
		return this.fullName;
	}

	@Override
	public int getConcreteType() {
		return VarType.PrimiType;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		// TODO Auto-generated method stub
		return new ArrayList<OntologicalElement>();
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof PrimiType){
			return this.getFullName().equals(((PrimiType) element).getFullName());
		}
		return false;
	}

	@Override
	public String getSimpleElementName() {
		// TODO Auto-generated method stub
		return this.fullName;
	}

	@Override
	public ArrayList<ProgrammingElement> getSupportingElements() {
		// TODO Auto-generated method stub
		return null;
	}
}
