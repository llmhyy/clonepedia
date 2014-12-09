package clonepedia.syntactic.util.abstractor;

import java.util.ArrayList;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;

public class InterfaceAbstractor extends ComplexTypeAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1977427743648283369L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		Interface interf1 = (Interface)e1;
		Interface interf2 = (Interface)e2;
		
		ArrayList<Method> commonAbstractMethods = abstractMethods(interf1.getMethods(), interf2.getMethods());
		ArrayList<Field> commonAbstractFields = abstractFields(interf1.getFields(), interf2.getFields());
		
		String abName = abstractName(interf1.getSimpleName(), interf2.getSimpleName());
		
		Interface abstractInterface = new Interface(abName, commonAbstractMethods, commonAbstractFields, true);
		attachSupportingElements(abstractInterface, interf1, interf2);
		
		return abstractInterface;
	}

}
