package clonepedia.model.ontology;

import java.io.Serializable;

public class VarType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3123599444700457238L;

	private PureVarType pureVarType;
	private int arrayLevel = 0;

	public VarType(PureVarType pureVarType, int arrayLevel) {
		super();
		this.pureVarType = pureVarType;
		this.arrayLevel = arrayLevel;
	}

	public PureVarType getPureVarType() {
		return pureVarType;
	}

	public void setPureVarType(PureVarType pureVarType) {
		this.pureVarType = pureVarType;
	}

	public int getArrayLevel() {
		return arrayLevel;
	}

	public void setArrayLevel(int arrayLevel) {
		this.arrayLevel = arrayLevel;
	}

	public String getFullName(){
		String arrayString = "";
		for(int i=0; i<this.arrayLevel; i++){
			arrayString += "[]";
		}
		return pureVarType.getFullName() + arrayString;
	}
	
	@Override
	public String toString(){
		return getFullName();
	}
	
	@Override
	public int hashCode(){
		return getFullName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof VarType){
			VarType referVarType = (VarType)obj;
			return referVarType.getPureVarType().equals(this.getPureVarType()) && referVarType.getArrayLevel() == this.getArrayLevel();
		}
		
		return false;
	}
}
