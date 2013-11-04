package clonepedia.model.semantic;

import java.util.ArrayList;
import java.util.HashSet;

public class WordSet extends HashSet<Word> {
	
	private static final long serialVersionUID = -7118388157631039399L;
	private int threshold = 0;
	
	public ArrayList<Word> getHighFrequencyWords(){
		ArrayList<Word> wordList = new ArrayList<Word>();
		for(Word word: this){
			if(word.count >= threshold){
				wordList.add(word);
			}
		}
		return wordList;
	} 
	
	public void addWord(Word word){
		if(!this.contains(word)){
			word.count = 1;
			this.add(word);
		}
		else{
			for(Word w: this){
				if(w.equals(word))
					w.count++;
			}
		}
			
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	
}
