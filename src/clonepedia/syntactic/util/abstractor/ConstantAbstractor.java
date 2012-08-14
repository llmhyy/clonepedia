package clonepedia.syntactic.util.abstractor;

import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.VarType;

public class ConstantAbstractor extends OntologicalElementAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4415389897125323744L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2)  throws Exception {
		Constant constant1 = (Constant)e1;
		Constant constant2 = (Constant)e2;
		
		VarType abType = abstractVarType(constant1.getConstantType(), constant2.getConstantType());
		String abName = abstractName(constant1.getConstantName(), constant2.getConstantName());
		
		Constant abstractConstant = new Constant(abName, (PrimiType)abType, true);
		attachSupportingElements(abstractConstant, constant1, constant2);
		
		return abstractConstant;
	}

}
