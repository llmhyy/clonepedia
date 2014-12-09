package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.Variable;

public class VariableComparator extends OntologicalElementComparator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3173648815455964456L;
	private final double typeProportion = 0.5d;
	private final double nameProportion = 0.5d;
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Variable)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in VariableComparator");
		
		Variable v1 = (Variable)e1;
		Variable v2 = (Variable)e2;
		
		double distance = 0.0d;

		distance = typeProportion * computeVarTypeDistance(v1.getVariableType(), v2.getVariableType())
			+ nameProportion * computeStringDistance(v1.getVariableName(), v2.getVariableName());
		return distance;
	}
}
