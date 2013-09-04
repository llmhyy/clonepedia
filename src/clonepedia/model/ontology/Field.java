package clonepedia.model.ontology;

import java.util.ArrayList;

public class Field extends MergeableSimpleOntologicalElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7812303279925050807L;
	private String fieldName;
	private ComplexType ownerType;
	private VarType fieldType;

	public Field(String fieldName, ComplexType ownerType, VarType fieldType) {
		super();
		this.fieldName = fieldName;
		this.ownerType = ownerType;
		this.fieldType = fieldType;
	}
	
	public Field(String fieldName, VarType fieldType, boolean isMerged){
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.isMerged = isMerged;
	}

	public String toString(){
		return ownerType + "." + fieldName + "(" + fieldType + ")";
	}
	
	public String getFullName(){
		return toString();
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public ComplexType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(ComplexType ownerType) {
		this.ownerType = ownerType;
	}

	public VarType getFieldType() {
		return fieldType;
	}

	public void setFieldType(VarType fieldType) {
		this.fieldType = fieldType;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Field;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		successors.add(ownerType);
		
		if(fieldType.getPureVarType() instanceof ComplexType)
			successors.add((ComplexType)(fieldType.getPureVarType()));

		
		return successors;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Field){
			Field referField = (Field)element;
			
			try{
				return referField.getFieldName().equals(this.getFieldName())
						&& referField.getOwnerType().getId().equals(this.getOwnerType().getId());
			}
			catch(NullPointerException e){
				return false;
			}
		}
		return false;
	}
	
	public int hashCode(){
		try{
			return toString().hashCode();
		}
		catch(NullPointerException e){
			return "nullField".hashCode();
		}
	}

	@Override
	public VarType getVarType() {
		return this.fieldType;
	}

	@Override
	public String getName() {
		return this.fieldName;
	}

	@Override
	public String getSimpleElementName() {
		if(fieldName != null)
			return /*"f:" +*/ getFieldName();
		else return "*";
	}
}
