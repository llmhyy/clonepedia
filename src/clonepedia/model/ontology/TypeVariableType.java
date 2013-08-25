package clonepedia.model.ontology;

public class TypeVariableType implements VarType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1328432530989835941L;

	protected String fullName;
	
	public String toString(){
		return fullName;
	}
	
	public String getFullName(){
		return this.fullName;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof VarType){
			VarType vt = (VarType)obj;
			return vt.getFullName().equals(this.fullName);
		}
		return false;
	}
	
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
