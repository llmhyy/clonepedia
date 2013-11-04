package clonepedia.model.ontology;

import java.io.Serializable;

public class InstanceElementRelation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2556773300857778016L;
	private CloneInstance cloneInstance;
	private ProgrammingElement element;

	public InstanceElementRelation(CloneInstance cloneInstance,
			ProgrammingElement element) {
		super();
		this.cloneInstance = cloneInstance;
		this.element = element;
	}
	
	public String toString(){
		return cloneInstance.getId() + ":" + element.toString();
	}

	public CloneInstance getCloneInstance() {
		return cloneInstance;
	}

	public void setCloneInstance(CloneInstance cloneInstance) {
		this.cloneInstance = cloneInstance;
	}

	public ProgrammingElement getElement() {
		return element;
	}

	public void setElement(ProgrammingElement element) {
		this.element = element;
	}

}
