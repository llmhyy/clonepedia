package clonepedia.model.viewer;

import java.util.ArrayList;

public class ClonePatternGroupCategoryList extends ArrayList<ClonePatternGroupCategory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8957050834571320329L;
	
	public ClonePatternGroupWrapper searchSpecificClonePattern(String uniqueId){
		for(ClonePatternGroupCategory category: this){
			ClonePatternGroupWrapper clonePatternWrapper = category.getPatterns().searchSpecificClonePattern(uniqueId);
			if(null != clonePatternWrapper)
				return clonePatternWrapper;
		}
		return null;
	}
	
	public ClonePatternGroupCategoryList searchContent(String content){
		
		ClonePatternGroupCategoryList list = new ClonePatternGroupCategoryList();
		
		content = content.toLowerCase();
		for(ClonePatternGroupCategory category: this){
			ClonePatternGroupCategory cate = new ClonePatternGroupCategory(category.getName());
			cate.setPatterns(category.getPatterns().searchContent(content));
			list.add(cate);
		}
		
		return list;
	}
	
	public ClonePatternGroupCategory searchSpecificCategory(String categoryName){
		for(ClonePatternGroupCategory category: this)
			if(category.getName().equals(categoryName))
				return category;
		
		return null;
	}
	
	public void reconstructProgrammingHierarchy(){
		for(ClonePatternGroupCategory category: this){
			category.getPatterns().constructProgrammingElementHierarchy();
		}
	}
}
