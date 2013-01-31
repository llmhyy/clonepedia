package clonepedia.model.viewer;

public class ClonePatternGroupCategoryList extends PatternGroupCategoryList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4210448337188483634L;

	public ClonePatternGroupWrapper searchSpecificClonePattern(String uniqueId){
		for(PatternGroupCategory category: this){
			ClonePatternGroupWrapperList list = (ClonePatternGroupWrapperList) category.getPatterns();
			ClonePatternGroupWrapper clonePatternWrapper = list.searchSpecificClonePattern(uniqueId);
			if(null != clonePatternWrapper)
				return clonePatternWrapper;
		}
		return null;
	}
	
	public PatternGroupCategoryList searchContent(String content){
		
		PatternGroupCategoryList list = new PatternGroupCategoryList();
		
		content = content.toLowerCase();
		for(PatternGroupCategory category: this){
			PatternGroupCategory cate = new PatternGroupCategory(category.getName(), new ClonePatternGroupWrapperList());
			ClonePatternGroupWrapperList cpgwlist = (ClonePatternGroupWrapperList) category.getPatterns();
			cate.setPatterns(cpgwlist.searchContent(content));
			list.add(cate);
		}
		
		return list;
	}
	
	public PatternGroupCategory searchSpecificCategory(String categoryName){
		for(PatternGroupCategory category: this)
			if(category.getName().equals(categoryName))
				return category;
		
		return null;
	}
}
