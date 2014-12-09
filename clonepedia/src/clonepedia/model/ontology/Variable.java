package clonepedia.model.ontology;

import java.util.ArrayList;

public class Variable extends MergeableSimpleOntologicalElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2791145150546745177L;
	private String variableName;
	private VarType variableType;

	private VariableUseType useType;
	private RegionalOwner owner;
	
	public Variable(String variableName, VarType variableType, boolean isMerged) {
		super();
		this.variableName = variableName;
		this.variableType = variableType;
		this.isMerged = isMerged;
	}

	public Variable(String variableName, VarType variableType,
			VariableUseType useType) {
		super();
		this.variableName = variableName;
		this.variableType = variableType;
		this.useType = useType;
	}

	

	public Variable(String variableName, VarType variableType,
			VariableUseType useType, RegionalOwner owner) {
		super();
		this.variableName = variableName;
		this.variableType = variableType;
		this.useType = useType;
		this.owner = owner;
	}
	
	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Variable){
			Variable referVariable = (Variable)element;
			try{
				return this.getOwner().getId().equals(referVariable.getOwner().getId())
						&& this.getUseType() == referVariable.getUseType()
						&& this.variableName == referVariable.getVariableName();
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
			return "nullVariable".hashCode();
		}
	}

	public String toString() {
		if(variableType != null)
			return variableType.toString() + " " + variableName;
		else
			return variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public VarType getVariableType() {
		return variableType;
	}

	public void setVariableType(VarType variableType) {
		this.variableType = variableType;
	}

	public VariableUseType getUseType() {
		return useType;
	}

	public void setUseType(VariableUseType useType) {
		this.useType = useType;
	}

	public RegionalOwner getOwner() {
		return owner;
	}

	public void setOwner(RegionalOwner owner) {
		this.owner = owner;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Variable;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		
		if(variableType.getPureVarType() instanceof ComplexType)
			successors.add((ComplexType)(variableType.getPureVarType()));

		return successors;
	}

	@Override
	public VarType getVarType() {
		return this.variableType;
	}

	@Override
	public String getName() {
		return this.variableName;
	}

	@Override
	public String getSimpleElementName() {
		if(variableName != null)
			return /*"v:" +*/ getName();
		else
			return "*";
	}
	
	@Override
	public String getFullName() {
		return this.variableName;
	}
}
