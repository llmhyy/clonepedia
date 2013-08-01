package clonepedia.util;

import clonepedia.Activator;
import clonepedia.preference.ClonepediaPreferencePage;

public class Settings {
	
	static{
		//Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		if(Activator.getDefault() != null){
			projectName = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.TARGET_PORJECT);
			inputCloneFile = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.CLONE_PATH);
			ontologyFile = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.ONTOLOGY_PATH);
			intraSetFile = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.INTRA_SET_PATH);
			interSetFile = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.INTER_SET_PATH);
			diffComparisonMode = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.DIFF_LEVEL);
			skipPattern = Activator.getDefault().getPreferenceStore().getString(ClonepediaPreferencePage.SKIP_PATTERN);
		}
	}
	
	/**
	 * Currently, we support two mode for clone instance diff comparison.
	 * 
	 * ASTNode_Basd
	 * Statement_Based
	 */
	public static String diffComparisonMode;
	
	/**
	 * Currently, support two options: "Yes" or "No"
	 */
	public static String skipPattern;
	
	public static double diffComparisonThreshold = 0.7d;
	
	public static double thresholdDistanceForSyntacticClustering = 2.55d;
	public static double thresholdDistanceToFormPattern = 1.5d;//3.5d
	
	public static double thresholdDistanceForPatternClustering = 1.5d;
	
	//======================================================================
	//public static double thresholdSimilartiyToFormPattern = 0.4d;
	
	public static double distanceDeductRateForPattern = 0.9d;
	public static double distanceDeductRateForClustering = 0.98d;
	
	//======================================================================
	//public static double thresholdToDistinguishModifyOperation = 0.5d;
	
	//======================================================================
	public static double selectedSampleRateToComputeAveragePathSequenceLength = 0.2d;
	/**
	 * In order to compute the distance between two paths, except for the inherent Levenshtein
	 * distance, one factor is also very important: the length of two paths. It is not difficult
	 * to imagine that the differences of two pair of paths with the same levenshtein distance 
	 * are not the same. For example, two pair of paths P<p1, p2> and P<p3, p4> could have the 
	 * same levenshtein distance, say 2.0. However both the length of p1 and p2 is 9 while that
	 * of p3 and p4 is 5. Therefore, a penalty/bonus is imposed on the distance based on the path
	 * length. 
	 */
	public static double upperAlphaForLengthOfPath = 0.8d;
	public static double lowerAlphaForLengthOfPath = 0.5d;
	public static double standardLength = 7d;
	
	public static double alphaForInstanceNumberPattern = 1d;
	
	public static double thresholdForSkippingPatternComparisonByLength = 1.8d;
	//======================================================================
	
	/**
	 * Decide how different two statements are.
	 */
	public static double thresholdForStatementDifference = 0.5d;
	
	//======================================================================
	/**
	 * Decide the threshold for even, partial or slightly partial counter realtional difference.
	 */
	public static double thresholdForEvenDistribution = 0.0d;
	public static double thresholdForSlightPartial = 0.5d;
	
	//======================================================================
	public static String projectName;
	public static String inputCloneFile;
	public static String ontologyFile;
	public static String intraSetFile;
	public static String interSetFile;
	
	//======================================================================
	public static double thresholdDistanceForHierarchyClustering = 0.4d;
}
