package clonepedia.model.ontology;

public class EnumType extends VarType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7893536159932421005L;

	public EnumType(String typeName){
		this.fullName = typeName;
	}
	
	public String getTypeName(){
		return this.fullName;
	}

	@Override
	public int getConcreteType() {
		return VarType.EnumType;
	}
}
