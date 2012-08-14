package clonepedia.model.ontology;

import java.io.Serializable;

public abstract class VarType implements Serializable{
	
	public static int ClassType = 0;
	public static int EnumType = 1;
	public static int InterfaceType	= 2;
	public static int PrimiType = 3;
	public static int TypeVariableType = 4;
	public static int UnknownType = 5;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7322941254706084499L;
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
	
	public abstract int getConcreteType();
}
