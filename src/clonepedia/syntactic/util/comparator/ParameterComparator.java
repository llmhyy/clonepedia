package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.Variable;

public class ParameterComparator extends OntologicalElementComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4791432053187050789L;

	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Variable)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in ParameterComparator");
		
		Variable v1 = (Variable)e1;
		Variable v2 = (Variable)e2;
		
		double distance = 0.0d;

		distance = computeVarTypeDistance(v1.getVariableType(), v2.getVariableType());
		
		return distance;
	}

}
