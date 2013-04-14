package clonepedia.util;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

import clonepedia.preference.ClonepediaPreferencePage;

public class Settings {
	
	static{
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		projectName = preferences.get(ClonepediaPreferencePage.TARGET_PORJECT, "");
		inputCloneFile = preferences.get(ClonepediaPreferencePage.CLONE_PATH, "");
	}
	
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
}
