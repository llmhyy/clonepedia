package clonepedia.model.viewer;

import java.util.ArrayList;

public class PatternGroupCategoryList extends ArrayList<PatternGroupCategory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8957050834571320329L;
	
	
	public void reconstructProgrammingHierarchy(){
		for(PatternGroupCategory category: this){
			category.getPatterns().constructProgrammingElementHierarchy();
		}
	}
}
