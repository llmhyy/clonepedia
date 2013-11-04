package clonepedia.db.schema;


public class Entity extends DBTable{
	public static final String Class = "Class";
	public static final String CloneInstance = "CloneInstance";
	public static final String CloneSet = "CloneSet";
	public static final String Constant = "Constant";
	public static final String Field = "Field";
	public static final String Interface = "Interface";
	public static final String Method = "Method";
	public static final String MethodParameter = "MethodParameter";
	public static final String Project = "Project";
	public static final String Variable = "Variable";
	
	public Entity(String value){
		assert(isInputValid(value, Entity.class)): "The table " + value + " has not been defined yet.";
		this.value = value;
	}
}
