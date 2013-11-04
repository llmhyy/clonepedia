package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;

public class InterfaceCompatrator extends ComplexTypeComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6066664299615145757L;
	private double fieldTypeProportion = 0.2d;
	private double methodTypeProportion = 0.6d;
	private double classNameProportion = 0.2d;
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Interface)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in InterfaceComparator");
		
		Interface interf1 = (Interface)e1;
		Interface interf2 = (Interface)e2;
		
		double fieldsDistance = computeFieldsDistance(interf1, interf2);
		double methodsDistance = computeMethodsDistance(interf1, interf2);
		double nameDistance = computeStringDistance(interf1.getSimpleName(), interf2.getSimpleName());
		
		if(fieldsDistance == -1)
			fieldTypeProportion = 0;
		if(methodsDistance == -1)
			methodTypeProportion = 0;
		
		double distance = (fieldTypeProportion*fieldsDistance
				+ methodTypeProportion*methodsDistance
				+ classNameProportion*nameDistance)/(fieldTypeProportion + methodTypeProportion + classNameProportion);
		
		return distance;
	}

}
