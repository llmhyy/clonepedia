package clonepedia.model.ontology;

public class TypeVariableType extends VarType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1328432530989835941L;

	public TypeVariableType(String typeName){
		this.fullName = typeName;
	}
	
	public String getTypeName(){
		return this.fullName;
	}

	@Override
	public int getConcreteType() {
		return VarType.TypeVariableType;
	}
}
