package clonepedia.model.cluster.hierarchy;

import java.util.ArrayList;

import clonepedia.model.cluster.Distance;
import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.summary.PatternClusteringer;
import clonepedia.util.Settings;

public class HierarchyClusterAlgorithm {

	public final static int SingleLinkage = 0;
	public final static int CompleteLinkage = 1;
	public final static int AverageLinkage = 2;
	
	private double matrix[][];
	private NormalCluster[] clusters;
	private double threshold;
	private int clusteringType = SingleLinkage;

	public HierarchyClusterAlgorithm(IClusterable[] elements, double threshold, int clusteringType) {
		
		this.setThreshold(threshold);
		this.setClusteringType(clusteringType);
		
		int size = elements.length;

		clusters = new NormalCluster[size];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new NormalCluster();
			clusters[i].add(elements[i]);
		}

		matrix = new double[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				matrix[i][j] = -1;
		
		//Debugger.printClusteringMatrix(matrix, clusters);
		//System.out.print("");
	}

	public ArrayList<NormalCluster> doClustering() throws Exception {

		if(clusters.length < 2){
			ArrayList<NormalCluster> list = new ArrayList<NormalCluster>();
			list.add(clusters[0]);
			return list;
		}
		
		initializeMatrix(matrix, clusters);

		Distance sd = findShortestDistanceInMatrix(matrix);

		//int count = 0;
		while (sd.distance < this.threshold) {
			try {
				clusters[sd.i].merge(clusters[sd.j]);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			clusters[sd.j] = null;
			//recomputeMatrixWithSimpilfiedAverageLinkage(matrix, sd.i, sd.j);
			switch(this.clusteringType){
			case SingleLinkage:
				recomputeMatrixWithSingleLinkage(matrix, sd.i, sd.j);
			case CompleteLinkage:
				recomputeMatrixWithCompleteLinkage(matrix, sd.i, sd.j);
			case AverageLinkage:
				recomputeMatrixWithSimpilfiedAverageLinkage(matrix, sd.i, sd.j);	
			}
			
			nullifyOneClusterInMatrix(matrix, sd.j);

			sd = findShortestDistanceInMatrix(matrix);
			//count++;
			if (null == sd)
				break;
		}

		//System.out.println("The iteration time for clustering is:" + count);

		ArrayList<NormalCluster> list = new ArrayList<NormalCluster>();

		for (int i = 0; i < clusters.length; i++)
			if (clusters[i] != null) {
				list.add(clusters[i]);
			}

		return list;
	}

	private void initializeMatrix(double matrix[][], NormalCluster[] clusters)
			throws Exception {
		//System.out.println("Matrix initiliazation start");
		int length = clusters.length;
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				NormalCluster cluster1 = clusters[i];
				NormalCluster cluster2 = clusters[j];

				/*
				 * if(cluster1.isContainsCloneSet("472571") &&
				 * cluster2.isContainsCloneSet("473017")) System.out.print("");
				 */
				/*
				 * if(cluster1.isContainsCloneSet("479138") &&
				 * cluster2.isContainsCloneSet("491370")) System.out.print("");
				 */

				if (null != cluster1 && null != cluster2) {
					// matrix[i][j] = calculateClusterDistance(cluster1,
					// cluster2);
					// long start = System.currentTimeMillis();
					matrix[i][j] = calculateSingleElementClusterDistance(
							cluster1, cluster2);
					System.out.print("");
					// long end = System.currentTimeMillis();
					// long time = end-start;
					// System.out.println("Time used in calculateSingleElementClusterDistance:"
					// + time);
					/*
					 * if(time > 800){
					 * System.out.println(cluster1.get(0).getPatterns().size() +
					 * "*" + cluster1.get(0).getPatterns().size() + ":" + time);
					 * }
					 */
				} else
					matrix[i][j] = -1;

				//double progress = ((double) ((2 * length - i + 1) * i) + 2 * j) / (length * (length - 1));
				// Debugger.printProgress(progress);
			}
		}
	}

	private double calculateClusterDistance(NormalCluster cluster1, NormalCluster cluster2) throws Exception {
		return getTheLongestDistanceBetweenTwoClusters(cluster1, cluster2);
	}

	private Distance findShortestDistanceInMatrix(double[][] matrix) {
		Distance sd = findFirstValidDistanceInMatrix(matrix);
		if (null == sd)
			return null;
		int m = sd.i;
		// int n = sd.j;

		for (int i = m; i < matrix.length; i++)
			for (int j = i + 1; j < matrix[i].length; j++) {
				/*
				 * if(i==5 && j==6) System.out.print("");
				 */

				if (matrix[i][j] != -1 && matrix[i][j] < sd.distance) {
					sd.i = i;
					sd.j = j;
					sd.distance = matrix[i][j];
				}
			}

		return sd;
	}

	private Distance findFirstValidDistanceInMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = i + 1; j < matrix[i].length; j++) {
				if (matrix[i][j] != -1) {
					return new Distance(i, j, matrix[i][j]);
				}
			}

		return null;
	}

	private double getTheLongestDistanceBetweenTwoClusters(
			NormalCluster cluster1, NormalCluster cluster2)
			throws Exception {

		if (cluster1 == null || cluster2 == null)
			return -1;

		double longestDistance = 0;
		for (IClusterable element1 : cluster1)
			for (IClusterable element2 : cluster2) {
				double distance = element1.computeDistanceWith(element2);
				if (distance > longestDistance)
					longestDistance = distance;
			}
		return longestDistance;
	}

	private double calculateSingleElementClusterDistance(
			NormalCluster cluster1, NormalCluster cluster2)
			throws Exception {
		IClusterable element1 = cluster1.get(0);
		IClusterable element2 = cluster2.get(0);

		/*
		 * if(set1.getId().equals("104501") && set2.getId().equals("218346"))
		 * System.out.print("");
		 */
		double result = 0;
		try {
			// long start = System.currentTimeMillis();
			result = element1.computeDistanceWith(element2);
			// long end = System.currentTimeMillis();
			// long time = end - start;
			// System.out.println("Time used in calculateCloneSetDistance:" +
			// time);

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * index1 must be smaller than index2
	 * 
	 * @param matrix
	 * @param index1
	 * @param index2
	 * @throws Exception
	 */
	private void recomputeMatrixWithCompleteLinkage(double[][] matrix,
			int index1, int index2) throws Exception {
		if (index1 >= index2)
			throw new Exception("index1 cannot be larger than index2");
		for (int i = 0; i < matrix.length; i++) {
			if (i < index1) {
				matrix[i][index1] = (matrix[i][index1] > matrix[i][index2]) ? matrix[i][index1]
						: matrix[i][index2];
			} else if (i > index1 && i < index2) {
				matrix[index1][i] = (matrix[index1][i] > matrix[i][index2]) ? matrix[index1][i]
						: matrix[i][index2];
			} else if (i > index2) {
				matrix[index1][i] = (matrix[index1][i] > matrix[index2][i]) ? matrix[index1][i]
						: matrix[index2][i];
			}
		}
	}
	
	/**
	 * index1 must be smaller than index2
	 * 
	 * @param matrix
	 * @param index1
	 * @param index2
	 * @throws Exception
	 */
	private void recomputeMatrixWithSingleLinkage(double[][] matrix,
			int index1, int index2) throws Exception {
		if (index1 >= index2)
			throw new Exception("index1 cannot be larger than index2");
		for (int i = 0; i < matrix.length; i++) {
			if (i < index1) {
				matrix[i][index1] = (matrix[i][index1] < matrix[i][index2]) ? matrix[i][index1]
						: matrix[i][index2];
			} else if (i > index1 && i < index2) {
				matrix[index1][i] = (matrix[index1][i] < matrix[i][index2]) ? matrix[index1][i]
						: matrix[i][index2];
			} else if (i > index2) {
				matrix[index1][i] = (matrix[index1][i] < matrix[index2][i]) ? matrix[index1][i]
						: matrix[index2][i];
			}
		}
	}

	/**
	 * index1 must be smaller than index2
	 * 
	 * @param matrix
	 * @param index1
	 * @param index2
	 * @throws Exception
	 */
	private void recomputeMatrixWithSimpilfiedAverageLinkage(double[][] matrix,
			int index1, int index2) throws Exception {
		if (index1 >= index2)
			throw new Exception("index1 cannot be larger than index2");
		for (int i = 0; i < matrix.length; i++) {
			if (i < index1) {
				matrix[i][index1] = (matrix[i][index1] + matrix[i][index2]) / 2;
			} else if (i > index1 && i < index2) {
				matrix[index1][i] = (matrix[index1][i] + matrix[i][index2]) / 2;
			} else if (i > index2) {
				matrix[index1][i] = (matrix[index1][i] + matrix[index2][i]) / 2;
			}
		}
	}

	private void nullifyOneClusterInMatrix(double[][] matrix, int index) {
		for (int i = 0; i < index; i++)
			matrix[i][index] = -1;
		for (int j = index + 1; j < matrix.length; j++)
			matrix[index][j] = -1;

	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getClusteringType() {
		return clusteringType;
	}

	public void setClusteringType(int clusteringType) {
		this.clusteringType = clusteringType;
	}

	/*private void attachPatternLabels(ArrayList<NormalCluster> list)
			throws Exception {
		PatternClusteringer pcger = new PatternClusteringer();
		for (NormalCluster cluster : list) {
			ArrayList<ClonePatternGroup> clonePatterns = pcger.doClustering(
					cluster.getConcernedContainedPatternPathGroups(),
					PathSequence.LOCATION, null);

			for (ClonePatternGroup clonePattern : clonePatterns) {
				for (PathPatternGroup ppg : clonePattern) {
					CloneSet set = ppg.getCloneSet();
					set.addPatternLabel(clonePattern);
				}
				clonePattern.updateAbstractPathSequence();
			}

			cluster.setClonePatterns(clonePatterns);
		}
	}*/
}
