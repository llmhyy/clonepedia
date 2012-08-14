package clonepedia.test.patternextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;

import clonepedia.debug.Logger;
import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSets;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.semantic.util.SemanticUtil;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.util.MinerUtil;
import junit.framework.TestCase;

public class TestSemanticClustering extends TestCase {
	
	public static void main(String[] args) throws Exception{
		//OntologicalModelGenerator generator = (OntologicalModelGenerator)MinerUtil.deserialize("generator");
		
		System.out.println("Clustering started");
		@SuppressWarnings("unchecked")
		ArrayList<SyntacticCluster> clusters = (ArrayList<SyntacticCluster>) MinerUtil
				.deserialize("all_cluster");
		//ArrayList<SyntacticCluster> allList = allCluster.doClustering();

		ArrayList<SemanticCluster> semanticClusterList = new SemanticClusteringer().doClustering(clusters);
		for (SemanticCluster semC : semanticClusterList) {
			for (ICluster clus : semC.getUnits()) {
				SyntacticCluster synC = (SyntacticCluster) clus;
				synC.doInnerClustering();
				// System.out.print("");
			}
			// System.out.print("");
		}
		System.out.println("Clustering finished");
		Logger.log2(semanticClusterList, "syntactic_semantic_clustering");
		
		CloneSets sets = semanticClusterList.get(0).getUnits().get(0).getCloneSets().get(0).getCloneSets();
		
		MinerUtil.serialize(semanticClusterList, "semantic_clusters");
		MinerUtil.serialize(sets, "sets");
		//ConsoleFormatter.displayClusters(clustersByTopic);
	}
	
	
}
