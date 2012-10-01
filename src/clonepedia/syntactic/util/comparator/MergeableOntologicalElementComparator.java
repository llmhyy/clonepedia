package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.MergeableSimpleOntologicalElement;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;

public class MergeableOntologicalElementComparator extends OntologicalElementComparator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9064344261225834818L;
	private final double typeProportion = 0.5d;
	private final double nameProportion = 0.5d;
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.MergeableSimpleElement)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in MergeableOntologicalElementComparator");
		
		MergeableSimpleOntologicalElement me1 = (MergeableSimpleOntologicalElement)e1;
		MergeableSimpleOntologicalElement me2 = (MergeableSimpleOntologicalElement)e2;
		
		double distance = 0.0d;
		try{
			distance = typeProportion * computeVarTypeDistance(me1.getVarType(), me2.getVarType()) 
					+ nameProportion * computeStringDistance(me1.getName(), me2.getName());
		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
		return distance;
	}

}
