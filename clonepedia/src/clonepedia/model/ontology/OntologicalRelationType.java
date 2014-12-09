package clonepedia.model.ontology;

public enum OntologicalRelationType {
	contain,
	call,
	access,
	resideIn,
	extendClass,
	extendInterface,
	implement,
	declaredIn,
	hasParameterType,
	hasType,
	isInnerClassOf,
	useConstant,
	useClass,
	useInterface,
	define,
	refer,
	/**
	 * the use type is an abstract relation type, which used for abstracted path.
	 * It is abstracted for (refer/access/call)
	 */
	use
}
