package clonepedia.model.ontology;

import java.util.ArrayList;

/**
 * Currently, the supported concerned type is String, Number, Character and Boolean.
 * @author TSLLINY
 *
 */
public class Constant extends UnstructuralProgrammingElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4797388534663545959L;
	private String constantName;
	private PrimiType constantType;
	private RegionalOwner owner;
	
	public String toString(){
		return constantType + " " + constantName;
	}

	public Constant(String constantName, PrimiType constantType, boolean isMerged) {
		super();
		this.constantName = constantName;
		this.constantType = constantType;
		this.isMerged = false;
	}

	public Constant(String constantName, PrimiType constantType,
			RegionalOwner owner) {
		super();
		this.constantName = constantName;
		this.constantType = constantType;
		this.owner = owner;
	}

	public String getConstantName() {
		return constantName;
	}

	public void setConstantName(String constantName) {
		this.constantName = constantName;
	}

	public PrimiType getConstantType() {
		return constantType;
	}

	public void setConstantType(PrimiType constantType) {
		this.constantType = constantType;
	}

	public RegionalOwner getOwner() {
		return owner;
	}

	public void setOwner(RegionalOwner owner) {
		this.owner = owner;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Constant;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		return new ArrayList<OntologicalElement>();
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Constant){
			Constant referConstant = (Constant)element;
			
			try{
				return referConstant.getConstantName().equals(this.getConstantName())
						&& referConstant.getOwner().getId().equals(this.getOwner().getId());
			}
			catch(NullPointerException e){
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String getFullName(){
		return this.constantName;
	}
	
	public int hashCode(){
		try{
			return toString().hashCode();
		}
		catch(NullPointerException e){
			return "nullConstant".hashCode();
		}
	}

	@Override
	public String getSimpleElementName() {
		if(constantName != null)
			return /*"c:" +*/ getConstantName();
		else
			return "*";
	}
}
