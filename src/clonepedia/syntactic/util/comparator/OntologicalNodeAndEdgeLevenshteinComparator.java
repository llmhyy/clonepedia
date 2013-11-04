package clonepedia.syntactic.util.comparator;

import java.io.Serializable;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.syntactic.util.MergeUtil;
import clonepedia.util.SimilarityComparator;

public class OntologicalNodeAndEdgeLevenshteinComparator implements
		SimilarityComparator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2320618412441705127L;
	
	
	private final static double diffEdgeCost = 1.0d;
	//private final static double extremDiffEdgeCost = 2.3d;
	private final static double mergeableEdgeCost = 0.5d;
	private final static double sameEdgeCost = 0d;
	private final static double nodeDiffCost = 1d;
	private final static double nodeSameCost = 0d;
	private final static double edgenodeDiffCost = 2.5d;
	
	@Override
	public double computeCost(Object object1, Object object2) throws Exception {
		if(object1 instanceof OntologicalRelationType
				&& object2 instanceof OntologicalRelationType){
			OntologicalRelationType rt1 = (OntologicalRelationType)object1;
			OntologicalRelationType rt2 = (OntologicalRelationType)object2;
			if(rt1 == rt2)return sameEdgeCost;
			else if(MergeUtil.isMergeable(rt1, rt2))return mergeableEdgeCost;
			else{
				return diffEdgeCost;
					
			} 
		}
		else if(object1 instanceof OntologicalElement
				&& object2 instanceof OntologicalElement){
			OntologicalElement element1 = (OntologicalElement)object1;
			OntologicalElement element2 = (OntologicalElement)object2;
			if(element1.getOntologicalType() != element2.getOntologicalType()){
				if(MergeUtil.isMergeable(element1, element2))
					return new MergeableOntologicalElementComparator().compute(element1, element2);
				else
					return nodeDiffCost;
			}
			else{
				if(element1.getOntologicalType() == OntologicalElementType.CloneInstance)
					return nodeSameCost;
				if(element1.getOntologicalType() == OntologicalElementType.Class)
					return new ClassComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Interface)
					return new InterfaceCompatrator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.CloneInstance)
					return new CloneInstanceComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Method)
					return new MethodComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Field)
					return new FieldComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Variable)
					return new VariableComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Constant)
					return new ConstantComparator().compute(element1, element2);
				else
					throw new Exception("The ontological type " + element1.getOntologicalType() 
							+ " is not valid in computeOntologicalElementAndEdgeDistance method");
			}
		}
		else
			return edgenodeDiffCost;
	}

}
