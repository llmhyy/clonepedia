package clonepedia.syntactic.util;

import java.io.Serializable;

import clonepedia.debug.Debugger;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.syntactic.util.comparator.OntologicalElementComparator;

public class SimilarPairDistanceMatrixComputer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8960106880586803837L;

	public static class Distance{
		public int i;
		public int j;
		public double distance;
		
		public Distance(int i, int j, double distance){
			this.i = i;
			this.j = j;
			this.distance = distance;
		}
	}
	
	public static double computePairSumRateInDistanceMatrix(OntologicalElement[] es1, OntologicalElement[] es2, OntologicalElementComparator comparator) throws Exception{
		
		double[][] distanceMatrix = initializeDistanceMatrix(es1, es2, comparator);
		
		double distanceSum = 0.0d;
		//Debugger.printPairDistanceMatrix(distanceMatrix, es1, es2);
		//System.out.print("");
		while(null != findLeastValidEntryInMatrix(distanceMatrix)){
			Distance smallestDistance = findSmallestDistanceInMatrix(distanceMatrix);
			distanceSum += smallestDistance.distance;
			nullifyIrreleventEntriesInMatrix(distanceMatrix, smallestDistance);
		}
		
		int maxiumParameterNumber = (distanceMatrix.length > distanceMatrix[0].length)? distanceMatrix.length: distanceMatrix[0].length;
		int miniumParameterNumber = (distanceMatrix.length < distanceMatrix[0].length)? distanceMatrix.length: distanceMatrix[0].length;
		return SyntacticUtil.calculateAverageDistanceOfTwoGroups(maxiumParameterNumber, miniumParameterNumber, distanceSum);
	}
	
	public static double[][] initializeDistanceMatrix(OntologicalElement[] es1, OntologicalElement[] es2, OntologicalElementComparator comparator) throws Exception{
		
		if(((es1.length + es2.length) != 0) && (es1.length * es2.length == 0))
			return new double[][]{{1}};
		else if((es1.length == 0) && (es2.length == 0))
			return new double[][]{{0}};
		
		double distanceMatrix[][] = new double[es1.length][es2.length];
		for(int i=0; i<es1.length; i++)
			for(int j=0; j<es2.length; j++){
				OntologicalElement e1 = es1[i];
				OntologicalElement e2 = es2[j];
				distanceMatrix[i][j] = comparator.compute(e1, e2);
			}
				
		//System.out.print("");
		return distanceMatrix;
	}
	
	public static void nullifyIrreleventEntriesInMatrix(double[][] matrix, Distance distance){
		for(int i=0; i<matrix.length; i++)
			matrix[i][distance.j] = -1;
		for(int j=0; j<matrix[distance.i].length; j++)
			matrix[distance.i][j] = -1;
	}
	
	public static Distance findSmallestDistanceInMatrix(double[][] matrix){
		Distance distance = findLeastValidEntryInMatrix(matrix);
		for(int i=0; i<matrix.length; i++)
			for(int j=0; j<matrix[i].length; j++){
				if(matrix[i][j] != -1 && matrix[i][j] < distance.distance){
					distance = new Distance(i, j, matrix[i][j]);
				}
			}
		return distance;
	}
	
	public static Distance findLeastValidEntryInMatrix(double[][] matrix){
		for(int i=0; i<matrix.length; i++)
			for(int j=0; j<matrix[i].length; j++)
				if(matrix[i][j] != -1)
					return new Distance(i, j, matrix[i][j]);
		return null;
	}
}
