package clonepedia.model.template;

import java.util.ArrayList;


public class TFGList extends ArrayList<TemplateFeatureGroup>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 806907071451200161L;
	
	private Template template;

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
}
