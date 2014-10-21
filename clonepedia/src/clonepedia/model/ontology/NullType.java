package clonepedia.model.ontology;

public class NullType implements PureVarType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6285753262201679260L;

	@Override
	public int getConcreteType() {
		// TODO Auto-generated method stub
		return PureVarType.NullType;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return "";
	}

	public String toString(){
		return "";
	}
}
