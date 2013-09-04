package clonepedia.model.ontology;

/**
 * the class is an abstract ontological class, which used for abstracted path.
 * It is used to abstract field/variable/method
 */
public abstract class MergeableSimpleOntologicalElement extends ProgrammingElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8768948477169008720L;
	public abstract PureVarType getVarType();
	public abstract String getName();
}
