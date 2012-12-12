package clonepedia.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import clonepedia.util.IComparator;

public class MinerUtil {

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

	public static void serialize(Object obj, String fileName) throws Exception {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public static Object deserialize(String location) {
		FileInputStream fis;
		try {
			fis = new FileInputStream("configurations" + File.separator
					+ location);
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

	public static Object[] generateCommonNodeList(Object[] nodeList1,
			Object[] nodeList2, IComparator comparator) {
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
			
			
			if (chars[i] == '\'' || chars[i] == '"' || chars[i]=='\'') {
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
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|([\\*])");
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
}
