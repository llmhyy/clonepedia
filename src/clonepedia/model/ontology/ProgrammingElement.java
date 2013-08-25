package clonepedia.model.ontology;

import java.util.ArrayList;

public interface ProgrammingElement extends OntologicalElement{
	/**
	 * Each type of programming element is supposed to be merged with other
	 * ones, therefore, a list of its supporting element shall be recorded
	 * to investigate the origin of an abstracted class.
	 * @return
	 */
	public ArrayList<ProgrammingElement> getSupportingElements();
	
	public boolean isMerged();
	
	public void setMerge(boolean isMerged);

	public String getFullName();
}
