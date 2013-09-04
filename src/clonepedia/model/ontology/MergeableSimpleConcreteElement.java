package clonepedia.model.ontology;

import java.util.ArrayList;

public class MergeableSimpleConcreteElement extends MergeableSimpleOntologicalElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2086258912930192160L;
	private PureVarType varType;
	private String name;
	
	public MergeableSimpleConcreteElement(String name, PureVarType varType, boolean isMerged){
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
	public PureVarType getVarType() {
		return this.varType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getFullName() {
		return this.name;
	}
}
