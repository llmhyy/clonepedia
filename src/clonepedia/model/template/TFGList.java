package clonepedia.model.template;

import java.util.ArrayList;


public class TFGList extends ArrayList<TemplateFeatureGroup>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 806907071451200161L;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
