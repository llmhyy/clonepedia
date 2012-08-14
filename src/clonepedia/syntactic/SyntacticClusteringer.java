package clonepedia.syntactic;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.debug.Debugger;
import clonepedia.exception.UnknowClusteringException;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.summary.PatternClusteringer;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

@Deprecated
public class SyntacticClusteringer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3669223348356522539L;
	//private CloneSets cloneSets;
	private SyntacticCluster[] clusters;
	private double matrix[][];
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
		
		int size = concernedSets.size();
		
		clusters = new SyntacticCluster[size];
		for(int i=0; i<clusters.length; i++){
			clusters[i] = new SyntacticCluster();
			clusters[i].add(concernedSets.get(i));
		}
		
		matrix = new double[size][size];
		for(int i=0; i<size; i++)
			for(int j=0; j<size; j++)
				matrix[i][j] = -1;
		
		
		//Debugger.printClusteringMatrix(matrix, clusters);
		//System.out.print("");
	}

	//private double thresholdDistance = 1.7d;
	//private double distanceDeductRate = 0.9d;
	//private double weight = 1/2d;
	
	private class Distance{
		public int i;
		public int j;
		public double distance;
		public Distance(int i, int j, double distance) {
			super();
			this.i = i;
			this.j = j;
			this.distance = distance;
		}
	}
	
	public PatternComparator getPatternComparator() {
		return patternComparator;
	}

	public void setPatternComparator(PatternComparator patternComparator) {
		this.patternComparator = patternComparator;
	}
	
	public SyntacticClusteringer(PatternComparator patternComparator){
		this.patternComparator = patternComparator;
	}

	public ArrayList<SyntacticCluster> doClustering() throws Exception{
		
		initializeMatrix(matrix, clusters);
		
		Distance sd = findShortestDistanceInMatrix(matrix);
		
		int count = 0;
		while(sd.distance < Settings.thresholdDistanceForSyntacticClustering){
			try{
				clusters[sd.i].merge(clusters[sd.j]);
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
			
			
			clusters[sd.j] = null;
			recomputeMatrixWithSimpilfiedAverageLinkage(matrix, sd.i, sd.j);
			nullifyOneClusterInMatrix(matrix, sd.j);
			
			sd = findShortestDistanceInMatrix(matrix);
			count++;
			if(null == sd)
				break;
		}
		
		System.out.println("The iteration time for clustering is:" + count);
		
		ArrayList<SyntacticCluster> list = new ArrayList<SyntacticCluster>();
		
		for(int i=0; i<clusters.length; i++)
			if(clusters[i] != null){
				for(CloneSet set: clusters[i]){
					set.setResidingCluster(clusters[i]);
				}
				list.add(clusters[i]);
			}
		
		long start = System.currentTimeMillis();
		attachPatternLabels(list);
		long end = System.currentTimeMillis();
		System.out.println("The time for attachPatternLabels is: " + (end-start));
		
		return list;
	}
	
	public ArrayList<ClonePatternGroup> doClustering0() throws Exception{
		
		ArrayList<ClonePatternGroup> list = new ArrayList<ClonePatternGroup>();
		
		ArrayList<PathPatternGroup> locationPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.locationPatternClustering);
		ArrayList<ClonePatternGroup> locationP = clusterSpecificStyleOfClonePattern(locationPatterns, ClonePatternGroup.LOCATION);
		
		ArrayList<PathPatternGroup> mfvDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.methodFieldVariableDiffPatternClustering);
		ArrayList<ClonePatternGroup> mfvDiffP = clusterSpecificStyleOfClonePattern(mfvDiffPatterns, ClonePatternGroup.DIFF_USAGE);
		
		ArrayList<PathPatternGroup> ciDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.complexTypeDiffPatternClustering);
		ArrayList<ClonePatternGroup> ciDiffP = clusterSpecificStyleOfClonePattern(ciDiffPatterns, ClonePatternGroup.DIFF_USAGE);
		
		ArrayList<PathPatternGroup> mfvCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.methodFieldVariableCommonPatternClustering);
		ArrayList<ClonePatternGroup> mfvCommonP = clusterSpecificStyleOfClonePattern(mfvCommonPatterns, ClonePatternGroup.COMMON_USAGE);
		
		ArrayList<PathPatternGroup> ciCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer.complexTypeCommonPatternClustering);
		ArrayList<ClonePatternGroup> ciCommonP = clusterSpecificStyleOfClonePattern(ciCommonPatterns, ClonePatternGroup.COMMON_USAGE);
		
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
		ArrayList<ClonePatternGroup> clonePatterns = pcger.doClustering(ppgs, clonePatternStyle);
		
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
	
	private void attachPatternLabels(ArrayList<SyntacticCluster> list) throws Exception{
		PatternClusteringer pcger = new PatternClusteringer();
		for(SyntacticCluster cluster: list){
			ArrayList<ClonePatternGroup> clonePatterns = pcger.doClustering(cluster.getConcernedContainedPatternPathGroups(), ClonePatternGroup.LOCATION);
			
			for(ClonePatternGroup clonePattern: clonePatterns){
				for(PathPatternGroup ppg: clonePattern){
					CloneSet set = ppg.getCloneSet();
					set.addPatternLabel(clonePattern);
				}
				clonePattern.updateAbstractPathSequence();
			}
			
			cluster.setClonePatterns(clonePatterns);
		}
	}
	
	/**
	 * index1 must be smaller than index2
	 * @param matrix
	 * @param index1
	 * @param index2
	 * @throws Exception
	 */
	private void recomputeMatrixWithCompleteLinkage(double[][] matrix, int index1, int index2) throws Exception{
		if(index1 >= index2)throw new Exception("index1 cannot be larger than index2");
		for(int i=0; i<matrix.length; i++){
			if(i<index1){
				matrix[i][index1] = (matrix[i][index1] > matrix[i][index2])? matrix[i][index1] : matrix[i][index2];
			}								
			else if (i>index1 && i<index2){
				matrix[index1][i] = (matrix[index1][i] > matrix[i][index2])? matrix[index1][i] : matrix[i][index2];
			}				
			else if(i>index2){
				matrix[index1][i] = (matrix[index1][i] > matrix[index2][i])? matrix[index1][i] : matrix[index2][i];
			}	
		}
	}
	
	/**
	 * index1 must be smaller than index2
	 * @param matrix
	 * @param index1
	 * @param index2
	 * @throws Exception
	 */
	private void recomputeMatrixWithSimpilfiedAverageLinkage(double[][] matrix, int index1, int index2) throws Exception{
		if(index1 >= index2)throw new Exception("index1 cannot be larger than index2");
		for(int i=0; i<matrix.length; i++){
			if(i<index1){
				matrix[i][index1] = (matrix[i][index1] + matrix[i][index2])/2;
			}								
			else if (i>index1 && i<index2){
				matrix[index1][i] = (matrix[index1][i] + matrix[i][index2])/2;
			}				
			else if(i>index2){
				matrix[index1][i] = (matrix[index1][i] + matrix[index2][i])/2;
			}	
		}
	}
	
	private void nullifyOneClusterInMatrix(double[][] matrix, int index) {
		for(int i=0; i<index; i++)
			matrix[i][index] = -1;
		for(int j=index+1; j<matrix.length; j++)
			matrix[index][j] = -1;
		
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

	private Distance findShortestDistanceInMatrix(double[][] matrix){
		Distance sd = findFirstValidDistanceInMatrix(matrix);
		if(null == sd)
			return null;
		int m = sd.i;
		//int n = sd.j;
		
		for(int i=m; i<matrix.length; i++)
			for(int j=i+1; j<matrix[i].length; j++){
				/*if(i==5 && j==6)
					System.out.print("");*/
				
				if(matrix[i][j] != -1 && matrix[i][j] < sd.distance){
					sd.i = i;
					sd.j = j;
					sd.distance = matrix[i][j];
				}
			}
		
		return sd;
	}
	
	private Distance findFirstValidDistanceInMatrix(double[][] matrix){
		for(int i=0; i<matrix.length; i++)
			for(int j=i+1; j<matrix[i].length; j++){
				if(matrix[i][j] != -1){
					return new Distance(i, j, matrix[i][j]);
				}
			}
		
		return null;
	}
	
	private void initializeMatrix(double matrix[][], SyntacticCluster[] clusters) throws Exception{
		System.out.println("Matrix initiliazation start");
		int length = clusters.length;
		for(int i=0; i<length; i++){
			for(int j=i+1; j<length; j++){
				SyntacticCluster cluster1 = clusters[i];
				SyntacticCluster cluster2 = clusters[j];
				
				/*if(cluster1.isContainsCloneSet("472571") && cluster2.isContainsCloneSet("473017"))
					System.out.print("");
				*/
				/*if(cluster1.isContainsCloneSet("479138") && cluster2.isContainsCloneSet("491370"))
					System.out.print("");*/
				
				if(null != cluster1 && null != cluster2){
					//matrix[i][j] = calculateClusterDistance(cluster1, cluster2);
					//long start = System.currentTimeMillis();
					matrix[i][j] = calculateSingleElementClusterDistance(cluster1, cluster2);
					//long end = System.currentTimeMillis();
					//long time = end-start;
					//System.out.println("Time used in calculateSingleElementClusterDistance:" + time);
					/*if(time > 800){
						System.out.println(cluster1.get(0).getPatterns().size() + "*" + cluster1.get(0).getPatterns().size() + ":" + time);
					}*/
					//System.out.print("");
				}
				else
					matrix[i][j] = -1;
				
				double progress = ((double)((2*length-i+1)*i)+2*j)/(length*(length-1));
				//Debugger.printProgress(progress);
			}
		}
	}

	private double calculateClusterDistance(SyntacticCluster cluster1, SyntacticCluster cluster2) throws Exception {
		return getTheLongestDistanceBetweenTwoClusters(cluster1, cluster2);
	}
	
	private double getTheLongestDistanceBetweenTwoClusters(SyntacticCluster cluster1, 
			SyntacticCluster cluster2) throws Exception{
		
		if(cluster1 == null || cluster2 == null) return -1;
		
		double longestDistance = 0;
		for(CloneSet set1: cluster1)
			for(CloneSet set2: cluster2){
				double distance = calculateCloneSetDistance(set1, set2/*, clusteringStyle*/);
				if(distance > longestDistance)
					longestDistance = distance;
			}
		return longestDistance;
	}
	
	private double calculateSingleElementClusterDistance(SyntacticCluster cluster1, SyntacticCluster cluster2) throws Exception{
		CloneSet set1 = cluster1.get(0);
		CloneSet set2 = cluster2.get(0);
		
		/*if(set1.getId().equals("104501") && set2.getId().equals("218346"))
			System.out.print("");*/
		double result = 0;
		try{
			//long start = System.currentTimeMillis();
			result = calculateCloneSetDistance(set1, set2/*, clusteringStyle*/);
			//long end = System.currentTimeMillis();
			//long time = end - start;
			//System.out.println("Time used in calculateCloneSetDistance:" + time);
			
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		
		
		return result;
	}
	
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
			int averageLength = ppg1.getCloneSet().getCloneSets().getAveragePathSequenceLength();
			double smallestDistance = patternComparator.computePatternDistance(ppg1, ppgs2.get(0), averageLength);
			PatternDistance pd = new PatternDistance(ppg1, ppgs2.get(0), smallestDistance);
			for(PathPatternGroup ppg2: ppgs2){
				//long start = System.currentTimeMillis();
				double distance = patternComparator.computePatternDistance(ppg1, ppg2, averageLength);
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
