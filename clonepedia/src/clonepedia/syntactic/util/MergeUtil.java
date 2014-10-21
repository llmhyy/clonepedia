package clonepedia.syntactic.util;

import clonepedia.model.ontology.MergeableSimpleOntologicalElement;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;

public class MergeUtil {
	public static boolean isMergeable(OntologicalRelationType edgeType1, OntologicalRelationType edgeType2){
		if(((edgeType1 == OntologicalRelationType.access || 
				edgeType1 == OntologicalRelationType.refer) ||
				(edgeType1 == OntologicalRelationType.call) ||
				(edgeType1 == OntologicalRelationType.use))
				&& 
				((edgeType2 == OntologicalRelationType.access || 
				edgeType2 == OntologicalRelationType.refer) ||
				(edgeType2 == OntologicalRelationType.call) ||
				(edgeType2 == OntologicalRelationType.use))
				)
			return true;
		else return false;
	}
	
	public static boolean isMergeable(OntologicalElement node1, OntologicalElement node2){
		if((node1 instanceof MergeableSimpleOntologicalElement) && (node2 instanceof MergeableSimpleOntologicalElement))
			return true;
		else return false;
	}
}
