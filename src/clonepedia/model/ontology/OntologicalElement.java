package clonepedia.model.ontology;

import java.io.Serializable;
import java.util.ArrayList;

public interface OntologicalElement extends Serializable{
	public OntologicalElementType getOntologicalType();
	/**
	 * if an ontological element do not have any successor, it should return a list with
	 * zero size instead of null value.
	 * @return
	 */
	public ArrayList<OntologicalElement> getSuccessors();
	public boolean equals(OntologicalElement element);
	public int hashCode();
	public String getSimpleElementName();
	public String toString();
}
