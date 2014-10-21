package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.VarType;

public class ConstantComparator extends OntologicalElementComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4388055792000904788L;
	private final double typeProportion = 0.5d;
	private final double nameProportion = 0.5d;
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Constant)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in ConstantComparator");
		
		Constant c1 = (Constant)e1;
		Constant c2 = (Constant)e2;
		
		double distance = 0.0d;

		distance = typeProportion * computeVarTypeDistance(new VarType(c1.getConstantType(), 0), new VarType(c2.getConstantType(), 0))
			+ nameProportion * computeStringDistance(c1.getConstantName(), c2.getConstantName());
		return distance;
	}

}
