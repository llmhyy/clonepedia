package clonepedia.syntactic.util.abstractor;

import java.util.ArrayList;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.ProgrammingElement;



public class ClassAbstractor extends ComplexTypeAbstractor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3479088345852578922L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		
		Class clas1 = (Class)e1;
		Class clas2 = (Class)e2;
		
		ArrayList<Method> commonAbstractMethods = abstractMethods(clas1.getMethods(), clas2.getMethods());
		ArrayList<Field> commonAbstractFields = abstractFields(clas1.getFields(), clas2.getFields());
		
		System.out.print("");
		String abName = abstractName(clas1.getSimpleName(), clas2.getSimpleName());
		
		Class abstractClass = new Class(abName, commonAbstractMethods, commonAbstractFields, true);
		attachSupportingElements(abstractClass, clas1, clas2);
		
		return abstractClass;
	}
	
	
	
}
