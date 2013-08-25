package clonepedia.model.ontology;

import java.io.Serializable;

public interface VarType extends Serializable{
	
	public static int ClassType = 0;
	public static int EnumType = 1;
	public static int InterfaceType	= 2;
	public static int PrimiType = 3;
	public static int TypeVariableType = 4;
	public static int UnknownType = 5;
	
	
	public abstract int getConcreteType();

	public abstract String getFullName();
}
