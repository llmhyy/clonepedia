package clonepedia.model.ontology;

import java.util.ArrayList;

/**
 * Field, Variable or Method
 * @author linyun
 *
 */
public class MergeableSimpleConcreteElement extends MergeableSimpleOntologicalElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2086258912930192160L;
	private VarType varType;
	private String name;
	
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
	public String getFullName() {
		return this.name;
	}
}
