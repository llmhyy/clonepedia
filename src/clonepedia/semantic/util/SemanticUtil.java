package clonepedia.semantic.util;

import java.util.ArrayList;

import clonepedia.model.semantic.Word;


public class SemanticUtil {
	public static String combineWord(ArrayList<Word> wordList){
		StringBuffer buffer = new StringBuffer();
		for(Word word: wordList){
			buffer.append(word.getName());
			buffer.append(" ");
		}
		return buffer.toString();
	}
}
