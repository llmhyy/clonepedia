package clonepedia.model.viewer;

import java.util.ArrayList;

import clonepedia.model.semantic.Word;

public class CloneSetWrapperList extends ArrayList<CloneSetWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7894669234427875358L;
	
	public CloneSetWrapper searchSpecificCloneSetWrapper(String cloneSetId){
		for(CloneSetWrapper set: this)
			if(set.getCloneSet().getId().equals(cloneSetId))
				return set;
		return null;
	}
	
	public int getSpecificIndexOf(CloneSetWrapper set){
		int index = 0;
		for(CloneSetWrapper s: this){
			if(s.getCloneSet().getId().equals(set.getCloneSet().getId()))
				return index;
			index++;
		}
			
		return -1;
	}
	
	public CloneSetWrapperList searchRelatedCloneSetWrapperList(String keyWord){
		
		keyWord = keyWord.toLowerCase();
		
		CloneSetWrapperList list = new CloneSetWrapperList();
		for(CloneSetWrapper set: this)
			for(Word word: set.getCloneSet().getRelatedWords()){
				if(word.getName().toLowerCase().contains(keyWord)){
					list.add(set);
					break;
				}
			}
		
		return list;
	}
	
	public CloneSetWrapperList searchCloneSetWrapperListById(String cloneSetId){
		
		CloneSetWrapperList list = new CloneSetWrapperList();
		for(CloneSetWrapper set: this){			
			if(set.getCloneSet().getId().equals(cloneSetId)){
				list.add(set);
			}
		}
		
		return list;
	}
}
