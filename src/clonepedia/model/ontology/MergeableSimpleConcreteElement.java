package clonepedia.model.ontology;

import java.util.ArrayList;

public class MergeableSimpleConcreteElement implements MergeableSimpleOntologicalElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2086258912930192160L;
	private VarType varType;
	private String name;
	
	private boolean isMerged = false;
	private ArrayList<ProgrammingElement> supportingElements = 
			new ArrayList<ProgrammingElement>();
	
	public MergeableSimpleConcreteElement(String name, VarType varType, boolean isMerged){
		this.name = name;
		this.varType = varType;
	}
	
	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.MergeableSimpleElement;
	}

	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		return this == element;
	}

	@Override
	public String getSimpleElementName() {
		return name;
	}

	@Override
	public VarType getVarType() {
		return this.varType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isMerged(){
		return this.isMerged;
	}
	
	@Override
	public ArrayList<ProgrammingElement> getSupportingElements(){
		return this.supportingElements;
	}
	
	@Override
	public void setMerge(boolean isMerged) {
		this.isMerged = isMerged;
	}
}
