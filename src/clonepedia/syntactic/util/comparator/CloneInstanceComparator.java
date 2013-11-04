package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;

public class CloneInstanceComparator extends OntologicalElementComparator {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1302378736423341804L;

	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.CloneInstance)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in CloneInstanceComparator");
		return 1.0;
	}

}
