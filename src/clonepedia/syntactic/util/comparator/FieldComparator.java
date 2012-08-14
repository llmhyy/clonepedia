package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;

public class FieldComparator extends OntologicalElementComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3325411550897372222L;
	private final double typeProportion = 0.5d;
	private final double nameProportion = 0.5d;
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Field)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in FieldComparator");
		
		Field f1 = (Field)e1;
		Field f2 = (Field)e2;
		
		double distance = 0.0d;
		try{
			distance = typeProportion * computeVarTypeDistance(f1.getFieldType(), f2.getFieldType()) 
					+ nameProportion * computeStringDistance(f1.getFieldName(), f2.getFieldName());
		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
		return distance;
	}
	
	

}
