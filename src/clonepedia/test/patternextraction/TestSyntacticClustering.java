package clonepedia.test.patternextraction;


import java.util.ArrayList;
import java.util.Iterator;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Project;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.SyntacticClusteringer0;
import clonepedia.syntactic.util.CoefficientComponent;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.syntactic.util.comparator.LevenshteinPatternComparator;
import clonepedia.syntactic.util.comparator.PathComparator;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.syntactic.util.comparator.VectorPathComparator;
import clonepedia.syntactic.util.comparator.VectorPatternComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;
import junit.framework.TestCase;

public class TestSyntacticClustering {
	
	
	public static void main(String[] args){
		
		long startTime = 0;
		long endTime = 0;
		
		try{
			
			PathComparator pathComparator = new LevenshteinPathComparator();
			PatternComparator patternComparator = new LevenshteinPatternComparator();
			
			CloneSets sets = (CloneSets)MinerUtil.deserialize("ontological_model");
			
			System.out.println("Ontological model extracted.");
			//PathComparator pathComparator = new VectorPathComparator();
			
			
			//String[] idList = {"473017", "479138", "491370", "479926", "480012", "142742", "473243", "472843", "478591", "473101", "483621", "481653", "472616"};
			//String[] idList = {"473017", "479138", "491370", "142742", "473243", "472843", "483621", "481653"};
			//String[] idList = {"176823", "577880"};
			//filterSetsById(idList, sets);
			//filterSetsByInstanceNumber(4, sets);
			sets.setPathComparator(pathComparator);
			//sets.computeMedianPathSequenceLength();
			//sets.computeAveragePathSequenceLength();
			
			startTime = System.currentTimeMillis();
			sets.buildPatternforCloneSets();
			endTime = System.currentTimeMillis();
			System.out.println("The time spended on buildPatternForCloneSets is: " + (endTime-startTime));
			//MinerUtil.serialize(generator, "generator");
			
			//generator = (OntologicalModelGenerator)MinerUtil.deserialize("generator");
			
			int count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableLocationPatterns().size();
			}
			System.out.println("The total number of location patterns is: " + count);
			
			count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableDiffUsagePatterns().size();
			}
			System.out.println("The total number of diff usage patterns is: " + count);
			
			count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableMethodFieldVariableDiffUsagePatterns().size();
			}
			System.out.println("The total number of diff method/field/variable usage patterns is: " + count);
			
			count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableComplexTypeDiffUsagePatterns().size();
			}
			System.out.println("The total number of diff class/interface usage patterns is: " + count);
			
			
			count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableMethodFieldVariableCommonUsagePatterns().size();
			}
			System.out.println("The total number of common method/field/variable usage patterns is: " + count);
			
			count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getClusterableComplexTypeCommonUsagePatterns().size();
			}
			System.out.println("The total number of common class/interface usage patterns is: " + count);
			SyntacticClusteringer0 clusteringer = null;
			try {
				clusteringer = new SyntacticClusteringer0(sets.getCloneList());
				clusteringer.doClustering();
				/*clusteringer = new SyntacticClusteringer(generator.getSets().getCloneList(), patternComparator, SyntacticClusteringer.locationPatternClustering);
				clusteringer.doClustering0();
				clusteringer.setClusteringStyle(SyntacticClusteringer.methodFieldVariablePatternClustering);
				clusteringer.doClustering0();
				clusteringer.setClusteringStyle(SyntacticClusteringer.complexTypePatternClustering);
				clusteringer.doClustering0();*/
				/*startTime = System.currentTimeMillis();
				
				ArrayList<SyntacticCluster> clusters = clusteringer.doClustering();
				
				endTime = System.currentTimeMillis();
				System.out.println("The time spended on all doClustering is: " + (endTime-startTime));
				MinerUtil.serialize(clusters, "all_cluster");*/
				//MinerUtil.serialize(clonePatterns, "clone_patterns");
				MinerUtil.serialize(sets, "syntactic_sets");
				/*startTime = System.currentTimeMillis();
				clusterer = new StructuralClusterer(generator.getSets(), patternComparator, StructuralClusterer.usagePatternClustering);
				endTime = System.currentTimeMillis();
				System.out.println("The time spended on usage StructuralClusterer is: " + (endTime-startTime));
				MinerUtil.serialize(clusterer, "usage_clusterer");
				
				startTime = System.currentTimeMillis();
				clusterer = new StructuralClusterer(generator.getSets(), patternComparator, StructuralClusterer.locationPatternClustering);
				endTime = System.currentTimeMillis();
				System.out.println("The time spended on location StructuralClusterer is: " + (endTime-startTime));
				MinerUtil.serialize(clusterer, "location_clusterer");*/
				//clusterer.doClustering();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			//StructuralClusterer clusterer = (StructuralClusterer)mu.deserialize("initialized_clusterer");
			//ArrayList<CloneSetCluster> list = clusterer.doClustering();
			
			//mu.serialize(list, "clusters_4");
			
			//System.out.print(list);
			//MinerUtil.serialize(list, "usage_cluster");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void filterSetsById(String[] idList, CloneSets sets){
		Iterator<CloneSet> iterator = sets.getCloneList().iterator();
		while(iterator.hasNext()){
			CloneSet set = (CloneSet)iterator.next();
			boolean flag = false;
			for(int i=0; i<idList.length; i++){
				if(idList[i].equals(set.getId()))
					flag = true;
			}
			if(!flag)
				iterator.remove();
		}
	}
	
	private void filterSetsByInstanceNumber(int instanceNumber, CloneSets sets){
		Iterator<CloneSet> iterator = sets.getCloneList().iterator();
		while(iterator.hasNext()){
			CloneSet set = (CloneSet)iterator.next();
			if(set.size() != instanceNumber)
				iterator.remove();
		}
	}
	
	
}
