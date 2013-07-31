package clonepedia.test.patternextraction;

import java.util.ArrayList;

import clonepedia.debug.Logger;
import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.util.comparator.LevenshteinPatternComparator;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.util.MinerUtil;

public class TestSemSynClustering {
	
	public static void main(String[] args) throws Exception{
		OntologicalModelGenerator generator = (OntologicalModelGenerator)MinerUtil.deserialize("generator", false);
		ArrayList<CloneSet> setList = generator.getSets().getCloneList();
		SemanticClusteringer clusterer = new SemanticClusteringer();
		ArrayList<SemanticCluster> semCluList = clusterer.doClustering(setList);
		
		/*for(SemanticCluster semCluster: semCluList){
			semCluster.doInnerClustering();
		}*/
		
		//MinerUtil.serialize(semCluList, "semantic_syntactic_clustering");
		
		Logger.log1(semCluList, "semantic_clustering_for_clone_set");
		
		
		
		
	}
}
