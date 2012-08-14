package clonepedia.model.ontology;

public class PrimiType extends VarType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3176680308112333471L;

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
	
}
