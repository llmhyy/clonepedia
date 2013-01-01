package clonepedia.util;

public class Settings {
	
	/**
	 * Currently, we support two mode for clone instance diff comparison.
	 * 
	 * ASTNode_Basd
	 * Statement_Based
	 */
	public static String diffComparisonMode = "Statement_Based";
	
	public static double diffComparisonThreshold = 0.7d;
	
	public static double thresholdDistanceForSyntacticClustering = 2.55d;
	public static double thresholdDistanceToFormPattern = 1.2d;//3.5d
	
	public static double thresholdDistanceForPatternClustering = 1.5d;
	
	//======================================================================
	//public static double thresholdSimilartiyToFormPattern = 0.4d;
	
	public static double distanceDeductRateForPattern = 0.9d;
	public static double distanceDeductRateForClustering = 0.98d;
	
	//======================================================================
	//public static double thresholdToDistinguishModifyOperation = 0.5d;
	
	//======================================================================
	public static double selectedSampleRateToComputeAveragePathSequenceLength = 0.2d;
	public static double lowerAlphaForLengthOfPath = 0.5d;
	public static double upperAlphaForLengthOfPath = 0.8d;
	public static double alphaForInstanceNumberPattern = 1d;
	
	public static double thresholdForSkippingPatternComparisonByLength = 1.8d;
	//======================================================================
	
	public static double thresholdForStatementDifference = 0.8d;
	
	//======================================================================
	public static String projectName = "JHotDraw7";
	public static String inputCloneFile = "D:\\Test_Project\\JHotDraw7\\out\\clones.xml";
}
