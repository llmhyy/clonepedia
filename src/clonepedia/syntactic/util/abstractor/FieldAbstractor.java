package clonepedia.syntactic.util.abstractor;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.VarType;

public class FieldAbstractor extends OntologicalElementAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2356984950871037347L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		Field field1 = (Field)e1;
		Field field2 = (Field)e2;
		
		VarType abType = abstractVarType(field1.getFieldType(), field2.getFieldType());
		String abName = abstractName(field1.getFieldName(), field2.getFieldName());
		
		Field abstractField = new Field(abName, abType, true);
		attachSupportingElements(abstractField, field1, field2);
		
		return abstractField;
	}

}
