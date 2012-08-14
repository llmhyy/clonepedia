package clonepedia.syntactic.util.abstractor;

import java.util.ArrayList;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.syntactic.util.comparator.FieldComparator;
import clonepedia.syntactic.util.comparator.MethodComparator;

public abstract class ComplexTypeAbstractor extends OntologicalElementAbstractor {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -95590111786364362L;

	protected abstract OntologicalElement abstractOntologicalElement(OntologicalElement e1, OntologicalElement e2) throws Exception;
	@SuppressWarnings("unchecked")
	protected ArrayList<Field> abstractFields(
			ArrayList<Field> fields1, ArrayList<Field> fields2) throws Exception {
		if(fields1 == null || fields2 == null)
			return null;
		if(fields1.size() == 0 || fields2.size() ==0)
			return new ArrayList<Field>();
		
		OntologicalElement[] es1 = fields1.toArray(new Field[0]);
		OntologicalElement[] es2 = fields2.toArray(new Field[0]);
		
		return (ArrayList<Field>) getCommonAbstractedElements(es1, es2, new FieldAbstractor(), new FieldComparator());
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<Method> abstractMethods(ArrayList<Method> methods1, ArrayList<Method> methods2) throws Exception{
		if(methods1 == null || methods2 == null)
			return null;
		if(methods1.size() == 0 || methods2.size() == 0)
			return new ArrayList<Method>();
		
		OntologicalElement[] es1 = methods1.toArray(new Method[0]);
		OntologicalElement[] es2 = methods2.toArray(new Method[0]);
		
		return (ArrayList<Method>) getCommonAbstractedElements(es1, es2, new MethodAbstractor(), new MethodComparator());
	}
	
	
}
