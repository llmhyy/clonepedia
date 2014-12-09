package clonepedia.syntactic.util.abstractor;

import clonepedia.model.ontology.MergeableSimpleConcreteElement;
import clonepedia.model.ontology.MergeableSimpleOntologicalElement;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;

public class MergeableSimpleTypeAbstractor extends OntologicalElementAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7477407654190332489L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		MergeableSimpleOntologicalElement st1 = (MergeableSimpleOntologicalElement)e1;
		MergeableSimpleOntologicalElement st2 = (MergeableSimpleOntologicalElement)e2;
		
		VarType abType = abstractVarType(st1.getVarType(), st2.getVarType());
		String abName = abstractName(st1.getName(), st2.getName());
		
		MergeableSimpleConcreteElement mergConElement = new MergeableSimpleConcreteElement(abName, abType, true);
		attachSupportingElements(mergConElement, st1, st2);
		
		return mergConElement;
	}

}
