package clonepedia.model.ontology;

public class EnumType implements PureVarType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7893536159932421005L;
	/**
	 * 
	 */
	protected String fullName;
	
	public String toString(){
		return fullName;
	}
	
	public String getFullName(){
		return this.fullName;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof PureVarType){
			PureVarType vt = (PureVarType)obj;
			return vt.getFullName().equals(this.fullName);
		}
		return false;
	}
	public EnumType(String typeName){
		this.fullName = typeName;
	}
	
	public String getTypeName(){
		return this.fullName;
	}

	@Override
	public int getConcreteType() {
		return PureVarType.EnumType;
	}
}
