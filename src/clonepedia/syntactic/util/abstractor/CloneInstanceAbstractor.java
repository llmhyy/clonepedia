package clonepedia.syntactic.util.abstractor;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.OntologicalElement;

public class CloneInstanceAbstractor extends OntologicalElementAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6949710613166322360L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		CloneInstance instance1 = (CloneInstance) e1;
		CloneInstance instance2 = (CloneInstance) e2;

		CloneInstance instance = new CloneInstance();
		instance.setStartLine(0);
		instance.setEndLine(0);
		
		try {
			if (instance1.getFileLocation().equals(instance2.getFileLocation()))
				instance.setFileLocation(instance1.getFileLocation());
		} catch (NullPointerException e) {
			instance.setFileLocation("");
		}

		try {
			if (instance1.getResidingMethod().equals(instance2.getResidingMethod()))
				instance.setResidingMethod(instance1.getResidingMethod());
		} catch (NullPointerException e) {
			instance.setResidingMethod(null);
		}
		

		return instance;
	}

}
