package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;

public class ClassComparator extends ComplexTypeComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3780611308486992398L;
	private double fieldTypeProportion = 0.3d;
	private double methodTypeProportion = 0.5d;
	private double classNameProportion = 0.2d;
	
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Class)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in ClassComparator");
		
		Class cl1 = (Class)e1;
		Class cl2 = (Class)e2;
		
		double fieldsDistance = computeFieldsDistance(cl1, cl2);
		double methodsDistance = computeMethodsDistance(cl1, cl2);
		double nameDistance = computeStringDistance(cl1.getSimpleName(), cl2.getSimpleName());
		
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
