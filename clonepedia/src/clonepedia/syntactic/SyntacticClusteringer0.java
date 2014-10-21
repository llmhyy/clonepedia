package clonepedia.syntactic;

import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.summary.PatternClusteringer;
import clonepedia.syntactic.util.comparator.PatternComparator;

public class SyntacticClusteringer0  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4477379819481782870L;
	
	private ArrayList<CloneSet> concernedSets;
	private int clusterIndex = 1;
	
	public static final String allPatternClustering = "allPatternClustering";
	public static final String usagePatternClustering = "usagePatternClustering";
	public static final String locationPatternClustering = "locationPatternClustering";
	public static final String methodFieldVariableCommonPatternClustering = "methodFieldVariablePatternCommonClustering";
	public static final String complexTypeCommonPatternClustering = "complexTypeCommonPatternClustering";
	public static final String methodFieldVariableDiffPatternClustering = "methodFieldVariablePatternDiffClustering";
	public static final String complexTypeDiffPatternClustering = "complexTypeDiffPatternClustering";
	
	public SyntacticClusteringer0(ArrayList<CloneSet> setList) throws Exception{
		
		concernedSets = new ArrayList<CloneSet>();
		for(CloneSet set: setList){
			if(set.getAllClusterablePatterns().size() > 0)
				concernedSets.add(set);
		}
		
	}
	
	public ArrayList<ClonePatternGroup> doClustering() throws Exception{
		
		ArrayList<ClonePatternGroup> list = new ArrayList<ClonePatternGroup>();
		
		ArrayList<PathPatternGroup> locationPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer0.locationPatternClustering);
		ArrayList<ClonePatternGroup> locationP = clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION, null);
		
		ArrayList<PathPatternGroup> mfvDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer0.methodFieldVariableDiffPatternClustering);
		ArrayList<ClonePatternGroup> mfvDiffP = clusterSpecificStyleOfClonePattern(mfvDiffPatterns, PathSequence.DIFF_USAGE, null);
		
		ArrayList<PathPatternGroup> ciDiffPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer0.complexTypeDiffPatternClustering);
		ArrayList<ClonePatternGroup> ciDiffP = clusterSpecificStyleOfClonePattern(ciDiffPatterns, PathSequence.DIFF_USAGE, null);
		
		ArrayList<PathPatternGroup> mfvCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer0.methodFieldVariableCommonPatternClustering);
		ArrayList<ClonePatternGroup> mfvCommonP = clusterSpecificStyleOfClonePattern(mfvCommonPatterns, PathSequence.COMMON_USAGE, null);
		
		ArrayList<PathPatternGroup> ciCommonPatterns = getPatternsFromCloneSets(concernedSets, SyntacticClusteringer0.complexTypeCommonPatternClustering);
		ArrayList<ClonePatternGroup> ciCommonP = clusterSpecificStyleOfClonePattern(ciCommonPatterns, PathSequence.COMMON_USAGE, null);
		
		
		list.addAll(locationP);
		list.addAll(mfvDiffP);
		list.addAll(ciDiffP);
		list.addAll(mfvCommonP);
		list.addAll(ciCommonP);
		
		return list;
	}
	
	public ArrayList<ClonePatternGroup> clusterSpecificStyleOfClonePattern(ArrayList<PathPatternGroup> ppgs, 
			String clonePatternStyle, IProgressMonitor monitor)throws Exception{
		
		long start = System.currentTimeMillis();
		
		PatternClusteringer pcger = new PatternClusteringer();
		ArrayList<ClonePatternGroup> clonePatterns = pcger.doClustering(ppgs, clonePatternStyle, monitor);
		
		for(ClonePatternGroup clonePattern: clonePatterns){
			for(PathPatternGroup ppg: clonePattern){
				CloneSet set = ppg.getCloneSet();
				set.addPatternLabel(clonePattern);
			}
			clonePattern.updateAbstractPathSequence();
			clonePattern.setUniqueId(String.valueOf(clusterIndex++));
		}
		long end = System.currentTimeMillis();
		System.out.println("The time for " +  clonePatternStyle + " in doClustering is: " + (end-start));
		return clonePatterns;
	}
	
	public ArrayList<PathPatternGroup> getPatternsFromCloneSets(ArrayList<CloneSet> sets, String clusteringStyle) throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(CloneSet set: sets)
			if(clusteringStyle.equals(SyntacticClusteringer0.allPatternClustering))
				ppgs.addAll(set.getAllClusterablePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.locationPatternClustering))
				ppgs.addAll(set.getClusterableLocationPatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.usagePatternClustering))
				ppgs.addAll(set.getClusterableUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.methodFieldVariableCommonPatternClustering))
				ppgs.addAll(set.getClusterableMethodFieldVariableCommonUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.complexTypeCommonPatternClustering))
				ppgs.addAll(set.getClusterableComplexTypeCommonUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.methodFieldVariableDiffPatternClustering))
				ppgs.addAll(set.getClusterableMethodFieldVariableDiffUsagePatterns());
			else if(clusteringStyle.equals(SyntacticClusteringer0.complexTypeDiffPatternClustering))
				ppgs.addAll(set.getClusterableComplexTypeDiffUsagePatterns());
		return ppgs;
	}
}
