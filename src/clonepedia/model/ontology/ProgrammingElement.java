package clonepedia.model.ontology;

import java.util.ArrayList;

public abstract class ProgrammingElement implements OntologicalElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2827085823859863179L;

	protected boolean isMerged = false;
	protected String fullName;
	
	protected ArrayList<ProgrammingElement> supportingElements = new ArrayList<ProgrammingElement>();
	
	/**
	 * Each type of programming element is supposed to be merged with other
	 * ones, therefore, a list of its supporting element shall be recorded
	 * to investigate the origin of an abstracted class.
	 * @return
	 */
	public ArrayList<ProgrammingElement> getSupportingElements(){
		return this.supportingElements;
	}
	
	public boolean isMerged(){
		return this.isMerged;
	};
	
	public void setMerge(boolean isMerged){
		this.isMerged = isMerged;
	};

	public String getFullName(){
		return this.fullName;
	}
	
	public void setFullName(String fullName){
		this.fullName = fullName;
	}
}
