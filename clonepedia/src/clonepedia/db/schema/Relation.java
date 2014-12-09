package clonepedia.db.schema;

public class Relation extends DBTable{
	public static final String ImplementRelation = "ImplementRelation";
	public static final String InterfaceExtendRelation = "InterfaceExtendRelation";
	public static final String CommonPartAccess = "CommonPartAccess";
	public static final String CommonPartCall = "CommonPartCall";
	public static final String CommonPartUseType = "CommonPartUseType";
	public static final String DiffPartAccess = "DiffPartAccess";
	public static final String DiffPartCall = "DiffPartCall";
	public static final String DiffPartUseType = "DiffPartUseType";
	public static final String DiffPartKeyword = "DiffPartKeyword";
	
	public Relation(String value){
		assert(isInputValid(value, Relation.class)): "The table " + value + " has not been defined yet.";
		this.value = value;
	}
}
