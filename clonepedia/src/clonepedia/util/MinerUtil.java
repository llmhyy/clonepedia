package clonepedia.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;

import clonepedia.model.Placeholder;

public class MinerUtil {

	public final static int CamelSplitting = 0;
	public final static int DotSplitting = 1;
	
	public static boolean checkNumber(String string){
		try{
			Double.parseDouble(string);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static String getFileConent(File file){
		try {
			BufferedReader reader = new BufferedReader( new FileReader (file));
			String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");

		    while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		        stringBuilder.append( ls );
		    }

		    return stringBuilder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		return null;
	}
	
	public static void logMessage(String log, String fileName) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(log);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
	}

	public static void serialize(Object obj, String fileName, boolean isAbsolutePath) throws Exception {
		
		FileOutputStream fos;
		if(isAbsolutePath){
			fos = new FileOutputStream(fileName);
		}
		else{
			File configurationDir = new File("configurations");
			if(!configurationDir.exists()){
				configurationDir.mkdir();
			}
			
			String targetDir = "configurations" + File.separator + Settings.projectName;
			File dir = new File(targetDir);
			if(!dir.exists()){
				dir.mkdir();
			}
			
			fos = new FileOutputStream(targetDir + File.separator + fileName);
			
		}
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public static Object deserialize(String fileName, boolean isAbsolutePath) {
		
		FileInputStream fis;
		try {
			if(isAbsolutePath){
				fis = new FileInputStream(fileName);
			}
			else{
				fis = new FileInputStream("configurations" + File.separator
						+ Settings.projectName + File.separator + fileName);				
			}
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}
	
	public static int[][] buildLeveshteinTable(Object[] nodeList1,
			Object[] nodeList2, BoolComparator comparator){
		int[][] commonLengthTable = new int[nodeList1.length + 1][nodeList2.length + 1];
		for (int i = 0; i < nodeList1.length + 1; i++)
			commonLengthTable[i][0] = 0;
		for (int j = 0; j < nodeList2.length + 1; j++)
			commonLengthTable[0][j] = 0;

		for (int i = 1; i < nodeList1.length + 1; i++)
			for (int j = 1; j < nodeList2.length + 1; j++) {
				if (comparator.isMatch(nodeList1[i - 1], nodeList2[j - 1]))
					commonLengthTable[i][j] = commonLengthTable[i - 1][j - 1] + 1;
				else {
					commonLengthTable[i][j] = (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]) ? commonLengthTable[i - 1][j]
							: commonLengthTable[i][j - 1];
				}

			}
		
		return commonLengthTable;
	}
	
	/**
	 * For string1: a b c d e
	 *     string2: a f c d
	 * The result is a * c d *
	 * @param nodeList1
	 * @param nodeList2
	 * @param comparator
	 * @return
	 */
	public static Object[] generateAbstractCommonNodeList(Object[] nodeList1,
			Object[] nodeList2, BoolComparator comparator){
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2, comparator);

		//int commonLength = (nodeList1.length > nodeList2.length)? nodeList1.length : nodeList2.length;
		ArrayList<Object> commonList = new ArrayList<Object>();

		int i, j = 0;
		for (i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (comparator.isMatch(nodeList1[i - 1], nodeList2[j - 1])) {
				//commonList[k] = nodeList1[i - 1];
				//k--;
				commonList.add(nodeList1[i-1]);
				i--;
				j--;
			} else {
				if (commonLengthTable[i - 1][j] == commonLengthTable[i][j - 1]){
					//commonList[k--] = new Placeholder();
					commonList.add(new Placeholder());
					i--;
					j--;
				}
				else if (commonLengthTable[i - 1][j] > commonLengthTable[i][j - 1]){
					i--;
					//commonList[k--] = new Placeholder();
					commonList.add(new Placeholder());
				}
				else{
					j--;
					//commonList[k--] = new Placeholder();
					commonList.add(new Placeholder());
				}
			}
		}
		
		while(i > 0 || j > 0){
			commonList.add(new Placeholder());
			i--;
			j--;
		}
		
		Object[] commonArray = new Object[commonList.size()];
		for(int m=0; m<commonArray.length; m++){
			commonArray[m] = commonList.get(commonList.size()-1-m);
		}

		return commonArray;
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
	public static Object[] generateCommonNodeList(Object[] nodeList1,
			Object[] nodeList2, BoolComparator comparator) {
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2, comparator);

		int commonLength = commonLengthTable[nodeList1.length][nodeList2.length];
		Object[] commonList = new Object[commonLength];

		for (int k = commonLength - 1, i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (comparator.isMatch(nodeList1[i - 1], nodeList2[j - 1])) {
				commonList[k] = nodeList1[i - 1];
				k--;
				i--;
				j--;
			} else {
				if (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1])
					i--;
				else
					j--;
			}
		}

		return commonList;
	}
	
	public static Object[] generateCommonNodeListFromMultiSequence(ArrayList<? extends Object>[] lists, BoolComparator comparator){
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
			Object[] commonList = MinerUtil.generateCommonNodeList(lists[0].toArray(new Object[0]),
					lists[1].toArray(new Object[0]), comparator);
			if (lists.length > 2) {
				for (int k = 2; k < lists.length; k++) {
					commonList = MinerUtil.generateCommonNodeList(commonList, lists[k].toArray(new ASTNode[0]),
							comparator);
				}
			}
			return commonList;
		}
	}
	
	public static double computeLevenshteinDistance(ArrayList<? extends Object> seq1, ArrayList<? extends Object> seq2, SimilarityComparator comparator) throws Exception{
		
		if(comparator == null)
			comparator = new DefaultComparator(); 
		
		double matrix[][] = new double[seq1.size()+1][seq2.size()+1];
		
		for(int i=0; i<matrix.length; i++)
			matrix[i][0] = i;
		for(int j=0; j<matrix[0].length; j++)
			matrix[0][j] = j;
		
		for(int i=1; i<matrix.length; i++){
			for(int j=1; j<matrix[i].length; j++){
				double entry1 = matrix[i-1][j] + 1;
				double entry2 = matrix[i][j-1] + 1;
				
				Object obj1 = seq1.get(i-1);
				Object obj2 = seq2.get(j-1);
				
				double cost = comparator.computeCost(obj1, obj2);
				double entry3 = matrix[i-1][j-1] + cost;
				
				matrix[i][j] = getSmallestValue(entry1, entry2, entry3);
			}	
		}
		
		return matrix[seq1.size()][seq2.size()];
	}
	
	public static double computeAdjustedLevenshteinDistance(ArrayList<? extends Object> seq1, ArrayList<? extends Object> seq2, SimilarityComparator comparator) throws Exception{
		
		if(comparator == null)
			comparator = new DefaultComparator(); 
		
		double value = computeLevenshteinDistance(seq1, seq2, comparator);
		double maxLen = (seq1.size() > seq2.size())? seq1.size() : seq2.size();
		//double minLen = (seq1.size() < seq2.size())? seq1.size() : seq2.size();
		
		//return (maxLen/minLen)*(2*value)/(maxLen + minLen);
		//return (2*value)/(seq1.size() + seq2.size());
		return value/maxLen;
	}
	
	public static double getSmallestValue(double entry1, double entry2, double entry3){
		double value = (entry1 < entry2)? entry1 : entry2;
		return (value < entry3)? value : entry3;
	}

	public static Character[] convertChar(char[] charArray) {
		Character[] string = new Character[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			string[i] = Character.valueOf(charArray[i]);

		return string;
	}

	public static String switchSpecialCharacterForSQL(String s) {
		char[] chars = s.toCharArray();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			
			
			if (chars[i] == '\'' || chars[i] == '"' || chars[i]=='\\') {
				buffer.append('\\');
			}
			
			buffer.append(chars[i]);
		}
		//System.out.print("");
		//return String.valueOf(chars);
		return buffer.toString();
	}

	public static Character[] convertCharacter(Object[] objectList) {
		Character[] nodeList = new Character[objectList.length];
		for (int i = 0; i < objectList.length; i++)
			nodeList[i] = (Character) objectList[i];

		return nodeList;
	}

	public static String[] splitCamelString(String s) {
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(?<!^)(?=(\\*)+)");
	}
	
	public static String convertCharacterArrayToString(Character[] array){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<array.length; i++){
			sb.append(array[i]);
		}
		return sb.toString();
	}

	public static String xmlStringProcess(String str){
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace("\"", "&quot;");
		str = str.replace("'", "&apos;");
		return str;
	}
	
	public static void changeEachHeadOfStringIntoUpperCase(String[] list){
		for(int i=0; i<list.length; i++){
			if(list[i].length() > 0){
				char[] ch = list[i].toCharArray();
				char head = Character.toUpperCase(list[i].charAt(0));
				ch[0] = head;
				list[i] = String.valueOf(ch);
			}
			
		}
	}
	
	public static String changeWordBeforeRegularExpression(String word){
		word = word.replace("\\", "\\\\");
		word = word.replace("{", "\\{");
		word = word.replace("}", "\\}");
		word = word.replace("(", "\\(");
		word = word.replace(")", "\\)");
		word = word.replace("+", "\\+");
		word = word.replace("*", "\\*");
		word = word.replace("?", "\\?");
		word = word.replace("|", "\\|");
		word = word.replace(".", "\\.");
		word = word.replace("$", "\\$");
		word = word.replace("[", "\\[");
		word = word.replace("]", "\\]");
		word = word.replace("^", "\\^");
		
		return word;
	}
	
	public static String generateAbstractString(ArrayList<String> stringList, int splittingStyle){
		String abstractName = stringList.get(0);
		
		for(int i=1; i<stringList.size(); i++){
			
			String[] abstractNameWords = splitString(splittingStyle, abstractName);
			String[] specialNameWords = splitString(splittingStyle, stringList.get(i));
			
			Object[] commonWords = MinerUtil.generateAbstractCommonNodeList(specialNameWords, 
					abstractNameWords, new DefaultComparator());
			
			String separator = "";
			if(splittingStyle == DotSplitting){
				separator = ".";
			}
			
			
			String abString = "";
			for(Object word: commonWords){
				abString += word.toString() + separator;
			}
			
			if(splittingStyle != CamelSplitting){
				abString = abString.substring(0, abString.length()-1);
			}
			
			abstractName = abString;
		}
		
		abstractName = removeDuplicatedAsteroid(abstractName);
		
		return abstractName;
	}
	
	public static String removeDuplicatedAsteroid(String name){
		if(name.contains("***")){
			System.currentTimeMillis();
		}
		
		Pattern p = Pattern.compile("\\*{2,}");
		Matcher matcher = p.matcher(name);
		String newName = matcher.replaceAll("*");
		return newName;
	}
	
	public static boolean isTwoStringSetEqual(List<String> stringList1, List<String> stringList2){
		if(stringList1.size() == stringList2.size()){
			String[] array1 = stringList1.toArray(new String[0]);
			String[] array2 = stringList2.toArray(new String[0]);
			
			Arrays.sort(array1);
			Arrays.sort(array2);
			
			for(int i=0; i<array1.length; i++){
				if(!array1[i].equals(array2[i])){
					return false;
				}
			}
			return true;
		}
		
		return false;
	}
	
	public static String[] splitString(int splittingStype, String string){
		if(splittingStype == CamelSplitting){
			return splitCamelString(string);
		}
		else if(splittingStype == DotSplitting){
			return string.split("\\.");
		}
		return null;
	}

	public static boolean isTwoSetSame(HashSet set1, HashSet set2) {
		if(set1.size() == set2.size()){
			HashSet clone = new HashSet(set2); 
		    Iterator it = set1.iterator();
		    while (it.hasNext() ){
		        Object obj = it.next();
		        if (clone.contains(obj)){ 
		            clone.remove(obj);
		        } else {
		            return false;
		        }
		    }
		    
		    return true;
		}
		
		return false;
	}
}
