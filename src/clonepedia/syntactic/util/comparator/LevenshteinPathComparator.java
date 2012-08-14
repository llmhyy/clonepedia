package clonepedia.syntactic.util.comparator;

import java.util.ArrayList;

import clonepedia.debug.Debugger;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.DistanceComputationPool;
import clonepedia.syntactic.util.LevenshteinMatrixComputer;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.Settings;

public class LevenshteinPathComparator extends PathComparator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2513889993761524615L;
	private static int times = 0;
	
	LevenshteinMatrixComputer lmc = new LevenshteinMatrixComputer();
	@Override
	public double computePathDistance(Path p1, Path p2, String cloneSetId, double averageLength)
			throws Exception {
		PathSequence sequence1 = p1.transferToSequence();
		PathSequence sequence2 = p2.transferToSequence();
		
		return computePathDistance(sequence1, sequence2, averageLength);
	}
	
	public double computePathDistance(PathSequence sequence1, PathSequence sequence2, double averageLength)
			throws Exception {
		/*long start = 0;
		long end = 0;
		long time = 0;*/
		PathSequenceComparingPair pair = new PathSequenceComparingPair(sequence1, sequence2);
		//start = System.currentTimeMillis();
		Double distance = DistanceComputationPool.pathDisMap.get(pair);
		//end = System.currentTimeMillis();
		//time = end - start;
		//System.out.println("Time used in path hash get:" + time);
		
		
		if(null == distance){
			double matrix[][] = new double[sequence1.size()+1][sequence2.size()+1];
			//start = System.currentTimeMillis();
			lmc.intializeLevenshteinMatrix(matrix, sequence1, sequence2);
			//end = System.currentTimeMillis();
			//time = end - start;
			//System.out.println("Time used in intializeLevenshteinMatrix:" + time);
			//lmc.intializeLevenshteinMatrix(matrix, sequence1, sequence2);
			//Debugger.printLevenshteinMatrix(matrix, sequence1, sequence2);
			distance = matrix[sequence1.size()][sequence2.size()];
			
			int length = (sequence1.size() + sequence2.size())/2;
			double lengthCoefficientFactor = (averageLength-2)/(length-2);
			
			if(lengthCoefficientFactor >= 1)
				distance *= Math.pow(lengthCoefficientFactor, Settings.upperAlphaForLengthOfPath);
			else
				distance *= Math.pow(lengthCoefficientFactor, Settings.lowerAlphaForLengthOfPath);
			
			DistanceComputationPool.pathDisMap.put(pair, distance);
		}
		else
			times++;
		
		return distance;
	}
	
	
}
