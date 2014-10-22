package mcidiff.util;

import java.util.ArrayList;

public class DiffUtil {
//	public static double computeAdjustedLevenshteinDistance(ArrayList<? extends Object> seq1, 
//			ArrayList<? extends Object> seq2, SimilarityComparator comparator) throws Exception{
//		
//		double value = computeLevenshteinDistance(seq1, seq2, comparator);
//		double maxLen = (seq1.size() > seq2.size())? seq1.size() : seq2.size();
//		
//		return value/maxLen;
//	}
//	
//	public static double computeLevenshteinDistance(ArrayList<? extends Object> seq1, 
//			ArrayList<? extends Object> seq2, SimilarityComparator comparator) throws Exception{
//		
//		if(comparator == null){
//			comparator = new DefaultComparator(); 			
//		}
//		
//		double matrix[][] = new double[seq1.size()+1][seq2.size()+1];
//		
//		for(int i=0; i<matrix.length; i++)
//			matrix[i][0] = i;
//		for(int j=0; j<matrix[0].length; j++)
//			matrix[0][j] = j;
//		
//		for(int i=1; i<matrix.length; i++){
//			for(int j=1; j<matrix[i].length; j++){
//				double entry1 = matrix[i-1][j] + 1;
//				double entry2 = matrix[i][j-1] + 1;
//				
//				Object obj1 = seq1.get(i-1);
//				Object obj2 = seq2.get(j-1);
//				
//				double cost = comparator.computeCost(obj1, obj2);
//				double entry3 = matrix[i-1][j-1] + cost;
//				
//				matrix[i][j] = getSmallestValue(entry1, entry2, entry3);
//			}	
//		}
//		
//		return matrix[seq1.size()][seq2.size()];
//	}
	
	public static double getSmallestValue(double entry1, double entry2, double entry3){
		double value = (entry1 < entry2)? entry1 : entry2;
		return (value < entry3)? value : entry3;
	}
	
	/**
	 * For string1: a b c d
	 *     string2: a f c d
	 * The result is a c d
	 * @param nodeList1
	 * @param nodeList2
	 * @param comparator
	 * @return
	 */
	public static Object[] generateCommonNodeList(Object[] nodeList1, Object[] nodeList2) {
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2);

		int commonLength = commonLengthTable[nodeList1.length][nodeList2.length];
		Object[] commonList = new Object[commonLength];

		for (int k = commonLength - 1, i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (nodeList1[i - 1].equals(nodeList2[j - 1])) {
				commonList[k] = nodeList1[i - 1];
				k--;
				i--;
				j--;
			} else {
				if (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]){
					i--;					
				}
				else{
					j--;					
				}
			}
		}

		return commonList;
	}
	
	public static Object[] generateCommonNodeListFromMultiSequence(ArrayList<? extends Object>[] lists){
		if(lists.length < 1){
			return new Object[0];
		}
		if(lists.length == 1){
			return lists[0].toArray(new Object[0]);
		}
		else if(lists.length == 2){
			return generateCommonNodeList(lists[0].toArray(new Object[0]), lists[1].toArray(new Object[0]));
		}
		else{
			Object[] commonList = generateCommonNodeList(lists[0].toArray(new Object[0]),
					lists[1].toArray(new Object[0]));
			if (lists.length > 2) {
				for (int k = 2; k < lists.length; k++) {
					commonList = generateCommonNodeList(commonList, lists[k].toArray(new Object[0]));
				}
			}
			return commonList;
		}
	}
	
	private static int[][] buildLeveshteinTable(Object[] nodeList1, Object[] nodeList2){
		int[][] commonLengthTable = new int[nodeList1.length + 1][nodeList2.length + 1];
		for (int i = 0; i < nodeList1.length + 1; i++)
			commonLengthTable[i][0] = 0;
		for (int j = 0; j < nodeList2.length + 1; j++)
			commonLengthTable[0][j] = 0;

		for (int i = 1; i < nodeList1.length + 1; i++)
			for (int j = 1; j < nodeList2.length + 1; j++) {
				if (nodeList1[i - 1].equals(nodeList2[j - 1]))
					commonLengthTable[i][j] = commonLengthTable[i - 1][j - 1] + 1;
				else {
					commonLengthTable[i][j] = (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]) ? commonLengthTable[i - 1][j]
							: commonLengthTable[i][j - 1];
				}

			}
		
		return commonLengthTable;
	}
	
	public static String[] splitCamelString(String s) {
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(?<!^)(?=(\\*)+)");
	}
	
	public static double compareStringSimilarity(String str1, String str2){
		
		if(str1 == null || str2 == null){
			return 0;
		}
		
		String[] words1 = splitCamelString(str1);
		String[] words2 = splitCamelString(str2);
		
		Object[] commonWords = generateCommonNodeList(words1, words2);
		double sim = 2d*commonWords.length/(words1.length+words2.length);
		
		return sim;
	}
}
