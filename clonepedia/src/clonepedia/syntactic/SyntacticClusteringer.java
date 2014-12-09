package clonepedia.syntactic;

import java.io.Serializable;
import java.util.ArrayList;


import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.summary.PatternClusteringer;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.util.Settings;

@Deprecated
public class SyntacticClusteringer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3669223348356522539L;
	//private CloneSets cloneSets;
	
	
	private PatternComparator patternComparator;
	//private String clusteringStyle;
	private ArrayList<CloneSet> concernedSets;
	private int clusterIndex = 1;
	

	public static final String allPatternClustering = "allPatternClustering";
	public static final String usagePatternClustering = "usagePatternClustering";
	public static final String locationPatternClustering = "locationPatternClustering";
	public static final String methodFieldVariableCommonPatternClustering = "methodFieldVariablePatternCommonClustering";
	public static final String complexTypeCommonPatternClustering = "complexTypeCommonPatternClustering";
	public static final String methodFieldVariableDiffPatternClustering = "methodFieldVariablePatternDiffClustering";
	public static final String complexTypeDiffPatternClustering = "complexTypeDiffPatternClustering";
	
	public SyntacticClusteringer(ArrayList<CloneSet> setList, PatternComparator patternComparator/*, String clusteringStyle*/) throws Exception{
		//this.cloneSets = cloneSets;
		this.patternComparator = patternComparator;
		//this.clusteringStyle = clusteringStyle;
		
		concernedSets = new ArrayList<CloneSet>();
		for(CloneSet set: setList){
			if(set.getAllClusterablePatterns().size() > 0)
				concernedSets.add(set);
		}
		
	}

	//private double thresholdDistance = 1.7d;
	//private double distanceDeductRate = 0.9d;
	//private double weight = 1/2d;
	
	public PatternComparator getPatternComparator() {
		return patternComparator;
	}

	public void setPatternComparator(PatternComparator patternComparator) {
		this.patternComparator = patternComparator;
	}
	
	public SyntacticClusteringer(PatternComparator patternComparator){
		this.patternComparator = patternComparator;
	}

	
	
	public ArrayList<ClonePatternGroup> doClustering0() throws Exception{
		
		ArrayList<ClonePatternGroup> list = new ArrayList<ClonePatternGroup>();
		
		ArrayList<PathPatternGroup> locationPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.locationPatternClustering);
		ArrayList<ClonePatternGroup> locationP = clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION);
		
		ArrayList<PathPatternGroup> mfvDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.methodFieldVariableDiffPatternClustering);
		ArrayList<ClonePatternGroup> mfvDiffP = clusterSpecificStyleOfClonePattern(mfvDiffPatterns, PathSequence.DIFF_USAGE);
		
		ArrayList<PathPatternGroup> ciDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.complexTypeDiffPatternClustering);
		ArrayList<ClonePatternGroup> ciDiffP = clusterSpecificStyleOfClonePattern(ciDiffPatterns, PathSequence.DIFF_USAGE);
		
		ArrayList<PathPatternGroup> mfvCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.methodFieldVariableCommonPatternClustering);
		ArrayList<ClonePatternGroup> mfvCommonP = clusterSpecificStyleOfClonePattern(mfvCommonPatterns, PathSequence.COMMON_USAGE);
		
		ArrayList<PathPatternGroup> ciCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.complexTypeCommonPatternClustering);
		ArrayList<ClonePatternGroup> ciCommonP = clusterSpecificStyleOfClonePattern(ciCommonPatterns, PathSequence.COMMON_USAGE);
		
		list.addAll(locationP);
		list.addAll(mfvDiffP);
		list.addAll(ciDiffP);
		list.addAll(mfvCommonP);
		list.addAll(ciCommonP);
		
		return list;
	}
	
	private ArrayList<ClonePatternGroup> clusterSpecificStyleOfClonePattern(ArrayList<PathPatternGroup> ppgs, 
			String clonePatternStyle)throws Exception{
		
		long start = System.currentTimeMillis();
		
		PatternClusteringer pcger = new PatternClusteringer();
		ArrayList<ClonePatternGroup> clonePatterns = pcger.doClustering(ppgs, clonePatternStyle, null);
		
		for(ClonePatternGroup clonePattern: clonePatterns){
			for(PathPatternGroup ppg: clonePattern){
				CloneSet set = ppg.getCloneSet();
				set.addPatternLabel(clonePattern);
			}
			clonePattern.updateAbstractPathSequence();
			clonePattern.setUniqueId(String.valueOf(clusterIndex++));
		}
		long end = System.currentTimeMillis();
		System.out.println("The time for " +  clonePatternStyle + " in doClustering0 is: " + (end-start));
		return clonePatterns;
	}
	
	private ArrayList<PathPatternGroup> getPatternsFromCloneSets(ArrayList<CloneSet> sets, String clusteringStyle) throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(CloneSet set: sets)
			if(clusteringStyle.equals(SyntacticClusteringer.allPatternClustering))
				ppgs.addAll(set.getAllClusterablePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.locationPatternClustering))
				ppgs.addAll(set.getClusterableLocationPatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.usagePatternClustering))
				ppgs.addAll(set.getClusterableUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.methodFieldVariableCommonPatternClustering))
				ppgs.addAll(set.getClusterableMethodFieldVariableCommonUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.complexTypeCommonPatternClustering))
				ppgs.addAll(set.getClusterableComplexTypeCommonUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.methodFieldVariableDiffPatternClustering))
				ppgs.addAll(set.getClusterableMethodFieldVariableDiffUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer.complexTypeDiffPatternClustering))
				ppgs.addAll(set.getClusterableComplexTypeDiffUsagePatterns());
		return ppgs;
	}
	
	
	
	

	/*private void nullifyColumnInMatrix(double[][] matrix, int c) {
		for(int i=0; i<matrix.length; i++)
			matrix[i][c] = -1;
	}
	
	private void recomputeMatrixInCertainRow(double[][] matrix, int r, CloneSetCluster[] clusters) throws Exception{
		for(int j=r+1; j<matrix.length; j++){
			matrix[r][j] = calculateClusterDistance(clusters[r], clusters[j]);
		}
	}
	
	private void recomputeMatrixInCertainColumn(double[][] matrix, int c, CloneSetCluster[] clusters) throws Exception{
		for(int i=0; i<c; i++){
			matrix[i][c] = calculateClusterDistance(clusters[i], clusters[c]);
		}
	}*/

	
	
	private class PatternDistance{
		public PathPatternGroup ppg1;
		public PathPatternGroup ppg2;
		double distance;
		public PatternDistance(PathPatternGroup ppg1, PathPatternGroup ppg2,
				double distance) {
			super();
			this.ppg1 = ppg1;
			this.ppg2 = ppg2;
			this.distance = distance;
		}
	}
	
	public double calculateCloneSetDistance(CloneSet set1, CloneSet set2/*, String clusteringStyle*/) throws Exception{
		
		
		double sum = 0;
		/*ArrayList<PatternPathGroup> ppgs1 = set1.getConcernedPatternsForClustering();
		ArrayList<PatternPathGroup> ppgs2 = set2.getConcernedPatternsForClustering();*/
		
		ArrayList<PathPatternGroup> ppgs1;
		ArrayList<PathPatternGroup> ppgs2;
		
		ppgs1 = set1.getPatterns();
		ppgs2 = set2.getPatterns();
		
		/*if(clusteringStyle.equals(SyntacticClusteringer.usagePatternClustering)){
			ppgs1 = set1.getClusterableUsagePatterns();
			ppgs2 = set2.getClusterableUsagePatterns();
		}
		else if(clusteringStyle.equals(SyntacticClusteringer.locationPatternClustering)){
			ppgs1 = set1.getClusterableLocationPatterns();
			ppgs2 = set2.getClusterableLocationPatterns();
		}
		else if(clusteringStyle.equals(SyntacticClusteringer.allPatternClustering)){
			ppgs1 = set1.getAllClusterableConcernedPatterns();
			ppgs2 = set2.getAllClusterableConcernedPatterns();
		}
		else throw new UnknowClusteringException("unknow clustering style: " + clusteringStyle);*/
		
		/*ArrayList<PatternPathGroup> ppgs1 = set1.getUsagePatterns();
		ArrayList<PatternPathGroup> ppgs2 = set2.getUsagePatterns();*/
		
		if(ppgs1.size() == 0 || ppgs2.size() == 0)
			return Settings.thresholdDistanceForSyntacticClustering + 1;
		
		ArrayList<PathPatternGroup> temp;
		if(ppgs1.size() < ppgs2.size()){
			temp = ppgs1;
			ppgs1 = ppgs2;
			ppgs2 = temp;
		}
		
		/**
		 * A trick is used here. The pattern is divided into two categories: Location Pattern and Usage
		 * Pattern. Location pattern means the pattern represent commonaility among the paths indicating 
		 * location information, such as the clone instance resides in which method of which class, while
		 * usage pattern represent commonality among the paths indicating usage information, such as the
		 * clone instance access which field of which type. When comparing two clone sets, a location pattern
		 * usually has a larger distance with a usage distance, however, such a distance is not necessary
		 * to be taken into account when computing average distances of two groups of patterns because such
		 * kind of difference will be the noise to cluster clone set with similar locations but with different
		 * usage. In other words, the clustering gives a higher priority to location pattern.
		 */
		int nullifiedDistancePairNumber = 0;
		
		for(PathPatternGroup ppg1: ppgs1){
			//int averageLength = ppg1.getCloneSet().getCloneSets().getAveragePathSequenceLength();
			double smallestDistance = patternComparator.computePatternDistance(ppg1, ppgs2.get(0));
			PatternDistance pd = new PatternDistance(ppg1, ppgs2.get(0), smallestDistance);
			for(PathPatternGroup ppg2: ppgs2){
				//long start = System.currentTimeMillis();
				double distance = patternComparator.computePatternDistance(ppg1, ppg2);
				//long end = System.currentTimeMillis();
				//long time = end - start;
				//System.out.println("Time used in computePatternDistance:" + time);
				/*if(distance < 2.5){
					System.out.print("");
					patternComparator.computePatternDistance(ppg1, ppg2, averageLength);
					//patternComparator.computePatternDistance(ppg1, ppg2, averageLength);
				}*/
					
				if(distance < smallestDistance){
					smallestDistance = distance;
					pd.ppg1 = ppg1;
					pd.ppg2 = ppg2;
					pd.distance = distance;
				}
					
			}
			
			if(pd.distance > Settings.thresholdDistanceForSyntacticClustering 
					&& (pd.ppg1.isLocationPattern()^pd.ppg2.isLocationPattern()))
				nullifiedDistancePairNumber++;
			else
				sum += smallestDistance;
		}
		
		//new MinerUtil().logMessage(log);
		//return sum;
		return sum/(ppgs1.size()-nullifiedDistancePairNumber);
		//return SyntacticUtil.calculateAverageDistanceOfTwoGroups(ppgs1.size(), ppgs2.size(), sum);
	}

	/*public String getClusteringStyle() {
		return clusteringStyle;
	}

	public void setClusteringStyle(String clusteringStyle) {
		this.clusteringStyle = clusteringStyle;
	}*/
	
	
	
	/*private double calculateCloneSetDistance(CloneSet set1, CloneSet set2) throws Exception{
		double sum = 0;
		ArrayList<PatternPathGroup> ppgs1 = set1.getConcernedPatternsForClustering();
		ArrayList<PatternPathGroup> ppgs2 = set2.getConcernedPatternsForClustering();
		
		for(PatternPathGroup ppg1: ppgs1)
			for(PatternPathGroup ppg2: ppgs2){
				if(ppg1.isConcernedPatternForClustering() 
						&& ppg2.isConcernedPatternForClustering())
					sum += calcuatePatternDistance(ppg1, ppg2);
			}
		
		return sum/(ppgs1.size()*ppgs2.size());
	}*/
	
	

	/*public CloneSets getCloneSets() {
		return cloneSets;
	}

	public void setCloneSets(CloneSets cloneSets) {
		this.cloneSets = cloneSets;
	}*/
	
	//private String log = "";
}
