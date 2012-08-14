package clonepedia.model.ontology;

/**
 * the class is an abstract ontological class, which used for abstracted path.
 * It is used to abstract field/variable/method
 */
public interface MergeableSimpleOntologicalElement extends ProgrammingElement {
	public VarType getVarType();
	public String getName();
}
