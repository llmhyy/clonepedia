package mcidiff.util;

import java.util.ArrayList;

import mcidiff.model.CorrespondentListAndSet;
import mcidiff.model.TokenMultiset;
import mcidiff.model.Token;

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
	

	public static CorrespondentListAndSet generateMatchedTokenListFromMultiSequence(ArrayList<Token>[] lists){
		Token[] commonTokenList = lists[0].toArray(new Token[0]);
		TokenMultiset[] setList = new TokenMultiset[commonTokenList.length];
		for(int i=0; i<commonTokenList.length; i++){
			TokenMultiset set = new TokenMultiset();
			set.add(commonTokenList[i]);
			setList[i] = set;
		}
		
		CorrespondentListAndSet cls = new CorrespondentListAndSet(setList, commonTokenList);
		
		if(lists.length == 1){
			return cls;
		}
		else if(lists.length == 2){
			return generateMatchedTokenList(cls, lists[1].toArray(new Token[0]));
		}
		else{
			if (lists.length > 2) {
				for (int k = 1; k < lists.length; k++) {
					cls = generateMatchedTokenList(cls, lists[k].toArray(new Token[0]));
				}
			}
			return cls;
		}
	}
	
	/**
	 * {@code multisetList} represents the multiset list corresponds to {@code commonTokenList}. For example, the common
	 * token list for two strings [a b c a d] and [a c a d] is [a c a d], its corresponding multiset list is {a, a}, {c, c}, 
	 * {a, a} and {d, d}.<p>
	 * 
	 * This method is for recursively invoked. Therefore, I can achieve the common token list and its corresponding multiset
	 * in the process of computing longest common subsequence. <p>
	 * 
	 * Initially, the {@code multisetList} and {@code commonTokenList} have the same content, e.g., [a b c a d] and {a}, {b},
	 * {c}, {a}, {d}.
	 * 
	 * @param multisetList
	 * @param commonTokenList
	 * @param tokenList2
	 */
	public static CorrespondentListAndSet generateMatchedTokenList(CorrespondentListAndSet cls, Token[] tokenList2) {
		TokenMultiset[] multisetList = cls.getMultisetList();
		Token[] commonTokenList = cls.getCommonTokenList();
		
		TokenSimilarityComparator sc = new TokenSimilarityComparator(); 
		
		int[][] commonLengthTable = buildLeveshteinTable(commonTokenList, tokenList2, new DefaultComparator());
		double[][] scoreTable = buildScoreTable(commonTokenList, tokenList2, sc);

		int commonLength = commonLengthTable[commonTokenList.length][tokenList2.length];
		Token[] commonList = new Token[commonLength];
		TokenMultiset[] setList = new TokenMultiset[commonLength];

		for (int k = commonLength - 1, i = commonTokenList.length, j = tokenList2.length; (i > 0 && j > 0);) {
			if (commonTokenList[i - 1].equals(tokenList2[j - 1])) {
				double sim = sc.compute(commonTokenList[i - 1], tokenList2[j - 1]);
				double increase = scoreTable[i][j]-scoreTable[i-1][j-1];
				
				if(Math.abs(sim - increase) < 0.01){
					commonList[k] = commonTokenList[i - 1];
					
					TokenMultiset set = new TokenMultiset();
					set.addAll(multisetList[i - 1]);
					set.add(tokenList2[j - 1]);
					setList[k] = set;
					
					k--;
					i--;
					j--;
				}
				else{
					if (scoreTable[i - 1][j] >= scoreTable[i][j - 1]){
						i--;					
					}
					else{
						j--;					
					}
				}
				
			} else {
				if (scoreTable[i - 1][j] >= scoreTable[i][j - 1]){
					i--;					
				}
				else{
					j--;					
				}
			}
		}

		System.currentTimeMillis();
		return new CorrespondentListAndSet(setList, commonList);
	}
	
	private static double[][] buildScoreTable(Token[] tokenList1, Token[] tokenList2, TokenSimilarityComparator comparator){
		double[][] similarityTable = new double[tokenList1.length + 1][tokenList2.length + 1];
		for (int i = 0; i < tokenList1.length + 1; i++)
			similarityTable[i][0] = 0;
		for (int j = 0; j < tokenList2.length + 1; j++)
			similarityTable[0][j] = 0;

		for (int i = 1; i < tokenList1.length + 1; i++){
			for (int j = 1; j < tokenList2.length + 1; j++) {
				if (tokenList1[i - 1].equals(tokenList2[j - 1])){
					double value = similarityTable[i - 1][j - 1] + comparator.compute(tokenList1[i - 1], tokenList2[j - 1]);
					similarityTable[i][j] = getLargestValue(value, similarityTable[i-1][j], similarityTable[i][j-1]);
				}
				else {
					similarityTable[i][j] = (similarityTable[i - 1][j] >= similarityTable[i][j - 1]) ? 
							similarityTable[i - 1][j] : similarityTable[i][j - 1];
				}
			}
		}
		
		return similarityTable;
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
	public static Object[] generateCommonNodeList(Object[] nodeList1, Object[] nodeList2, IObjComparator comparator) {
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2, comparator);

		int commonLength = commonLengthTable[nodeList1.length][nodeList2.length];
		Object[] commonList = new Object[commonLength];

		for (int k = commonLength - 1, i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (comparator.isEquals(nodeList1[i - 1], nodeList2[j - 1])) {
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
	
	public static Object[] generateCommonNodeListFromMultiSequence(ArrayList<? extends Object>[] lists, IObjComparator comparator){
		if(lists.length < 1){
			return new Object[0];
		}
		if(lists.length == 1){
			return lists[0].toArray(new Object[0]);
		}
		else if(lists.length == 2){
			return generateCommonNodeList(lists[0].toArray(new Object[0]), lists[1].toArray(new Object[0]), comparator);
		}
		else{
			Object[] commonList = generateCommonNodeList(lists[0].toArray(new Object[0]),
					lists[1].toArray(new Object[0]), comparator);
			if (lists.length > 2) {
				for (int k = 2; k < lists.length; k++) {
					commonList = generateCommonNodeList(commonList, lists[k].toArray(new Object[0]), comparator);
				}
			}
			return commonList;
		}
	}
	
	public static int[][] buildLeveshteinTable(Object[] nodeList1, Object[] nodeList2, IObjComparator comparator){
		int[][] commonLengthTable = new int[nodeList1.length + 1][nodeList2.length + 1];
		for (int i = 0; i < nodeList1.length + 1; i++)
			commonLengthTable[i][0] = 0;
		for (int j = 0; j < nodeList2.length + 1; j++)
			commonLengthTable[0][j] = 0;

		for (int i = 1; i < nodeList1.length + 1; i++){
			for (int j = 1; j < nodeList2.length + 1; j++) {
				if (comparator.isEquals(nodeList1[i - 1], nodeList2[j - 1])){
					commonLengthTable[i][j] = commonLengthTable[i - 1][j - 1] + 1;					
				}
				else {
					commonLengthTable[i][j] = (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]) ? 
							commonLengthTable[i - 1][j] : commonLengthTable[i][j - 1];
				}
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
		
		Object[] commonWords = generateCommonNodeList(words1, words2, new StringComparator(false));
		double sim = 2d*commonWords.length/(words1.length+words2.length);
		
		return sim;
	}
	
	public static double getLargestValue(double entry1, double entry2, double entry3){
		double value = (entry1 > entry2)? entry1 : entry2;
		return (value > entry3)? value : entry3;
	}
}
