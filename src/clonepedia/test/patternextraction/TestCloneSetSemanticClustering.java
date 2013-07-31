package clonepedia.test.patternextraction;

import clonepedia.model.ontology.CloneSets;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.util.MinerUtil;

public class TestCloneSetSemanticClustering {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SemanticClusteringer clusteringer = new SemanticClusteringer();
		/*OntologicalModelGenerator generator = (OntologicalModelGenerator)MinerUtil.deserialize("syntactic_generator");
		clusteringer.doClustering(generator.getSets().getCloneList());
		MinerUtil.serialize(generator.getSets(), "sets");*/
		
		CloneSets sets = (CloneSets)MinerUtil.deserialize("inter_pattern_sets", false);
		clusteringer.doClustering(sets.getCloneList());
		MinerUtil.serialize(sets, "sets");
	}

}
