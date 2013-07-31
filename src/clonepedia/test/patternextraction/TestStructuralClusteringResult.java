package clonepedia.test.patternextraction;

import java.util.ArrayList;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.util.MinerUtil;
import junit.framework.TestCase;

public class TestStructuralClusteringResult extends TestCase {
	public void testClusteringResult() throws Exception{
		
		ArrayList<SyntacticCluster> list = ((SyntacticClusteringer)MinerUtil.deserialize("all_clusterer", false)).doClustering();
		
		System.out.print("");
		
		OntologicalModelGenerator generator = (OntologicalModelGenerator)MinerUtil.deserialize("generator", false);
		SyntacticClusteringer allCluster = (SyntacticClusteringer)MinerUtil.deserialize("all_clusterer_new", false);
		ArrayList<SyntacticCluster> allList = allCluster.doClustering();
		/*StructuralClusterer locationCluster = (StructuralClusterer)MinerUtil.deserialize("location_clusterer_new");
		ArrayList<CloneSetCluster> locationList = locationCluster.doClustering();
		StructuralClusterer usageCluster = (StructuralClusterer)MinerUtil.deserialize("usage_clusterer_new");
		ArrayList<CloneSetCluster> usageList = usageCluster.doClustering();*/
		System.out.print("");
	}
}
