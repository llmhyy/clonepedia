package clonepedia.test.model;

import clonepedia.model.semantic.Word;
import clonepedia.model.semantic.WordSet;
import junit.framework.TestCase;

public class TestWord extends TestCase{
	
	public void testWordStructure(){
		Word w1 = new Word("test");
		Word w2 = new Word("test");
		
		WordSet set = new WordSet();
		set.addWord(w1);
		set.addWord(w2);
		
		assertTrue(set.size() == 1);
		
		Word w = set.toArray(new Word[0])[0];
		
		assertTrue(w.count == 2);
		
		String s = "havePPTPrepared";
		String l[] = s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
		System.out.println(l);
	}
}
