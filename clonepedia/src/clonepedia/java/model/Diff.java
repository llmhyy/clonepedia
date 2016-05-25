package clonepedia.java.model;

import java.util.ArrayList;

/**
 * a counter diff in a clone set
 * @author linyun
 *
 */

public class Diff {
	
	private String id;
	ArrayList<DiffElement> elements = new ArrayList<DiffElement>();
	
	
	public Diff() {
		super();
	}

	public Diff(String id) {
		super();
		this.id = id;
	}
	
	public void addElement(DiffElement relation){
		elements.add(relation);
	}
	
	
	public ArrayList<DiffElement> getElements() {
		return elements;
	}

	public void setElements(
			ArrayList<DiffElement> relations) {
		this.elements = relations;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
