package clonepedia.syntactic.util;

import java.io.Serializable;

import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.comparator.OntologicalNodeAndEdgeLevenshteinComparator;
import clonepedia.util.MinerUtil;


public class LevenshteinMatrixComputer implements Serializable{
	
	private static final long serialVersionUID = -6071506914017895146L;
	
	/**
	 * This part of code is a clone of the code in 
	 * {@link clonepeida.util.MinerUtil#computeLevenshteinDistance(ArrayList<? extends Object>, ArrayList<? extends Object>, SimilarityComparator) MinerUtil.compcomputeLevenshteinDistance}.
	 * <p>
	 * 
	 * The difference between them lies in that some code may need to use the computed Levenshtein matrix. Therefore, only a value
	 * of cost is not enough. However, for other cases where only the value of cost is needed. The method 
	 * {@link clonepeida.util.MinerUtil#computeLevenshteinDistance(ArrayList<? extends Object>, ArrayList<? extends Object>, SimilarityComparator) MinerUtil.compcomputeLevenshteinDistance}
	 * is recommended.
	 * 
	 * @param matrix
	 * @param sequence1
	 * @param sequence2
	 * @throws Exception
	 */
	public double[][] intializeLevenshteinMatrix(PathSequence sequence1, PathSequence sequence2) throws Exception {
		
		double matrix[][] = new double[sequence1.size()+1][sequence2.size()+1];
		
		for(int i=0; i<matrix.length; i++)
			matrix[i][0] = i;
		for(int j=0; j<matrix[0].length; j++)
			matrix[0][j] = j;
		
		for(int i=1; i<matrix.length; i++){
			for(int j=1; j<matrix[i].length; j++){
				double entry1 = matrix[i-1][j] + 1;
				double entry2 = matrix[i][j-1] + 1;
				Object obj1 = sequence1.get(i-1);
				Object obj2 = sequence2.get(j-1);
				
				/*if(obj1.toString().equals("java.lang.Object") && obj2.toString().equals("javax.swing.JComponent"))
					System.out.print("");*/
				//long start = System.currentTimeMillis();
				double cost = new OntologicalNodeAndEdgeLevenshteinComparator().computeCost(obj1, obj2);
				//long end = System.currentTimeMillis();
				//long time = end - start;
				//System.out.println("Time used in computeOntologicalElementOrEdgeDistance:" + time);
				//double cost = computeOntologicalElementOrEdgeDistance(obj1, obj2);
				double entry3 = matrix[i-1][j-1] + cost;
				matrix[i][j] = MinerUtil.getSmallestValue(entry1, entry2, entry3);
			}
		}
		return matrix;
	}
}
