package clonepedia.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.syntactic.util.abstractor.PathAbstractor;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.syntactic.util.comparator.LevenshteinPatternComparator;
import clonepedia.util.Settings;

public class PatternClusteringer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5756069611600048769L;

	private class Distance{
		public double distance;
		public ClonePatternGroup patternCluster;
		public Distance(double distance, ClonePatternGroup patternCluster) {
			super();
			this.distance = distance;
			this.patternCluster = patternCluster;
		}
	}
	
	/**
	 * Given a pattern path group set, return a pattern cluster set where each cluster should be correspond to
	 * ?at least two clone sets?. ?Intrinsically, a clone pattern should be formed by at least two clone sets?. 
	 * @param ppgList
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ClonePatternGroup> doClustering(ArrayList<PathPatternGroup> ppgList, String clonePatternStyle) throws Exception{
		ArrayList<ClonePatternGroup> clusterList = new ArrayList<ClonePatternGroup>();
		for(PathPatternGroup ppg: ppgList){
			if(clusterList.size() == 0)
				clusterList.add(new ClonePatternGroup(ppg, clonePatternStyle));
			else{
				Distance dis = findClusterWithShortestDistance(clusterList, ppg, clonePatternStyle);
				if(dis.distance < Settings.thresholdDistanceForPatternClustering){
					dis.patternCluster.add(ppg);
					dis.patternCluster.updateAbstractPathSequenceByJustOneComparison();
					/*if(dis.patternCluster.isOverGeneral(clonePatternStyle)){
						dis.patternCluster.remove(ppg);
						dis.patternCluster.updateAbstractPathSequence();
						
						ClonePatternGroup cpg = new ClonePatternGroup(ppg, clonePatternStyle);
						cpg.updateAbstractPathSequence();
						clusterList.add(cpg);
					}*/
				}
				else{
					ClonePatternGroup cpg = new ClonePatternGroup(ppg, clonePatternStyle);
					cpg.updateAbstractPathSequence();
					clusterList.add(cpg);
				}
					
			}
		}
		
		//if(isForLocationClustering)
		filterThePatternClusterJustCorrespondingToOneCloneSet(clusterList);
		
		return clusterList;
	}
	
	private void filterThePatternClusterJustCorrespondingToOneCloneSet(ArrayList<ClonePatternGroup> clusterList){
		Iterator<ClonePatternGroup> iter = clusterList.iterator();
		while(iter.hasNext()){
			ClonePatternGroup pc = iter.next();
			HashSet<CloneSet> sets = new HashSet<CloneSet>();
			for(PathPatternGroup ppg: pc)
				sets.add(ppg.getCloneSet());
			
			if(sets.size() == 1)
				iter.remove();
		}
	}

	private Distance findClusterWithShortestDistance(
			ArrayList<ClonePatternGroup> clusterList, PathPatternGroup ppg, String clonePatternStyle) throws Exception {
		Distance distance = new Distance(Settings.thresholdDistanceForPatternClustering + 1, null);
		for(ClonePatternGroup pc: clusterList){
			double disBetweenPP = getDistanceBetweenPatternAndCluster(pc, ppg);
			if(disBetweenPP < distance.distance){
				
				//pc.add(ppg);
				//pc.updateAbstractPathSequenceByJustOneComparison();
				/**
				 * Make sure the path patterns in clone pattern are compatible and will not be over general.
				 */
				if(SyntacticUtil.isConsistentInUsage(pc.getAbstractPathSequence(), ppg.getAbstractPathSequence())){
					PathSequence sequence = new PathAbstractor().extractAbstractPath(pc.getAbstractPathSequence(), ppg.getAbstractPathSequence());
					if(null != sequence && 
							!SyntacticUtil.isOverGeneral(clonePatternStyle, sequence))
						distance = new Distance(disBetweenPP, pc);
				}
				//pc.remove(ppg);
				//pc.updateAbstractPathSequenceByJustOneComparison();
				
			}
		}
		
		
		return distance;
	}

	/**
	 * Given a pattern cluster and pattern p, their distance is the distance between pattern p
	 * and the pattern which has the largest distance with p in the cluster.
	 * @param pc
	 * @param ppg
	 * @return
	 * @throws Exception
	 */
	private double getDistanceBetweenPatternAndCluster(ClonePatternGroup pc,
			PathPatternGroup ppg) throws Exception {
		LevenshteinPatternComparator comparator = new LevenshteinPatternComparator();
		int averageLength = ppg.getCloneSet().getCloneSets().getAveragePathSequenceLength();
		return new LevenshteinPathComparator().computePathDistance(ppg.getAbstractPathSequence(), pc.getAbstractPathSequence(), averageLength);
		/*double distance = comparator.computePatternDistance(pc.get(0), ppg, averageLength);
		for(PathPatternGroup group: pc){
			double comparingDis = comparator.computePatternDistance(ppg, group, averageLength);
			if(comparingDis > distance){
				distance = comparingDis;
			}
		}
		
		return distance;*/
	}
}
