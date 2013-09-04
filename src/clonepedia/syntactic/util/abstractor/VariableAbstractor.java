package clonepedia.syntactic.util.abstractor;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.Variable;

public class VariableAbstractor extends OntologicalElementAbstractor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 766317272072484729L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {

		Variable variable1 = (Variable)e1;
		Variable variable2 = (Variable)e2;
		
		PureVarType abType = abstractVarType(variable1.getVariableType(), variable2.getVariableType());
		String abName = abstractName(variable1.getVariableName(), variable2.getVariableName());
		
		Variable abstractVariable = new Variable(abName, abType, true);
		attachSupportingElements(abstractVariable, variable1, variable2);
		
		return abstractVariable;
	}
}
