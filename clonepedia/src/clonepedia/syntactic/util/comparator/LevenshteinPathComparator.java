package clonepedia.syntactic.util.comparator;

import java.util.ArrayList;

import clonepedia.debug.Debugger;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.DistanceComputationPool;
import clonepedia.syntactic.util.LevenshteinMatrixComputer;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class LevenshteinPathComparator extends PathComparator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2513889993761524615L;
	private static int times = 0;
	
	//LevenshteinMatrixComputer lmc = new LevenshteinMatrixComputer();
	@Override
	public double computePathDistance(Path p1, Path p2, String cloneSetId)
			throws Exception {
		PathSequence sequence1 = p1.transferToSequence();
		PathSequence sequence2 = p2.transferToSequence();
		
		return computePathDistance(sequence1, sequence2);
	}
	
	/**
	 * In order to compute the distance between two paths, except for the inherent Levenshtein
	 * distance, one factor is also very important: the length of two paths. It is not difficult
	 * to imagine that the differences of two pair of paths with the same levenshtein distance 
	 * are not the same. For example, two pair of paths P<p1, p2> and P<p3, p4> could have the 
	 * same levenshtein distance, say 2.0. However both the length of p1 and p2 is 9 while that
	 * of p3 and p4 is 5. Therefore, a penalty/bonus is imposed on the distance based on the path
	 * length. 
	 * 
	 * The formula is: d = lev(p1, p2)*[(standardLen/avg(p1, p2))^alpha]
	 * standardLen and alpha is configured in {@link Settings}}
	 */
	public double computePathDistance(PathSequence sequence1, PathSequence sequence2)
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
			//double matrix[][] = new double[sequence1.size()+1][sequence2.size()+1];
			//lmc.intializeLevenshteinMatrix(matrix, sequence1, sequence2);
			
			//Debugger.printLevenshteinMatrix(matrix, sequence1, sequence2);
			//distance = matrix[sequence1.size()][sequence2.size()];
			distance = MinerUtil.computeLevenshteinDistance(sequence1, sequence2, new OntologicalNodeAndEdgeLevenshteinComparator());
			int length = (sequence1.size() + sequence2.size())/2;
			double lengthCoefficientFactor = (Settings.standardLength-2)/(length-2);
			
			if(lengthCoefficientFactor >= 1){
				distance *= Math.pow(lengthCoefficientFactor, Settings.upperAlphaForLengthOfPath);				
			}
			else{
				distance *= Math.pow(lengthCoefficientFactor, Settings.lowerAlphaForLengthOfPath);				
			}
			
			DistanceComputationPool.pathDisMap.put(pair, distance);
		}
		else
			times++;
		
		return distance;
	}
	
	
}
