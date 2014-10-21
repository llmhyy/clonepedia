package clonepedia.syntactic.util.comparator;

import java.util.ArrayList;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Interface;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.comparator.InterfaceCompatrator;
import clonepedia.util.Settings;

public class LevenshteinPatternComparator extends PatternComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3117536884875489674L;

	@Override
	public double computePatternDistance(PathPatternGroup pattern1,
			PathPatternGroup pattern2) throws Exception {
		
		/*
		PathAbstractor abstractor = new PathAbstractor();
		PathSequence sequencePath1  = abstractor.extractAbstractPathFromPatternPathGroup(pattern1);
		PathSequencesequencePath2  = abstractor.extractAbstractPathFromPatternPathGroup(pattern2);*/
		
		PathSequence sequencePath1  = pattern1.getAbstractPathSequence();
		PathSequence sequencePath2  = pattern2.getAbstractPathSequence();
		
		int max = pattern1.size();
		int min = pattern2.size();
		
		if(max < min){
			int temp = max;
			max = min;
			min = temp;
		}
		
		if((double)max/min > Settings.thresholdForSkippingPatternComparisonByLength)
			return Settings.thresholdDistanceForSyntacticClustering+1;
		
		double coefficientFactor = (double)max/(double)min;
		
		LevenshteinPathComparator pathComparator = new LevenshteinPathComparator();
		//new ClassComparator().compute((Class)sequencePath1.get(8), (Class)sequencePath2.get(8));
		double distance = pathComparator.computePathDistance(sequencePath1, sequencePath2);
		
		distance *= Math.pow(coefficientFactor, Settings.alphaForInstanceNumberPattern);
		
		return distance;
	}

	/*public double computePatternDistance(PathSequence sequencePath1,
			PathSequence sequencePath2, double averageLength) throws Exception{
		
	}*/
}
