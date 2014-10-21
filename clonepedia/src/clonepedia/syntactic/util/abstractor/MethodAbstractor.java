package clonepedia.syntactic.util.abstractor;

import java.util.ArrayList;

import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;
import clonepedia.syntactic.util.comparator.VariableComparator;

public class MethodAbstractor extends OntologicalElementAbstractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6354564631629543307L;

	@Override
	protected OntologicalElement abstractOntologicalElement(
			OntologicalElement e1, OntologicalElement e2) throws Exception {
		Method method1 = (Method)e1;
		Method method2 = (Method)e2;
		
		VarType type1 = method1.getReturnType();
		VarType type2 = method2.getReturnType();
		/*if(type1==null)
			type1 = (PureVarType) method1.getOwner();
		if(type2==null)
			type2 = (PureVarType) method2.getOwner();*/
		
		VarType abReturnType = abstractVarType(type1, type2);
		ArrayList<Variable> parameters = abstractParameters(method1.getParameters(), method2.getParameters());
		String abName = abstractName(method1.getMethodName(), method2.getMethodName());
		
		Method abstractMethod = new Method(abName, abReturnType, parameters, true);
		attachSupportingElements(abstractMethod, method1, method2);
		
		return abstractMethod;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Variable> abstractParameters(
			ArrayList<Variable> parameters1, ArrayList<Variable> parameters2) throws Exception {
		if(parameters1 == null || parameters2 == null)
			return null;
		if(parameters1.size() == 0 || parameters2.size() ==0)
			return new ArrayList<Variable>();
		
		/*ArrayList<Variable> commonVariables = new ArrayList<Variable>();
		Iterator<Variable> iter1 = parameters1.iterator();
		while(iter1.hasNext()){
			Variable v1 = (Variable)iter1.next();
			Iterator<Variable> iter2 = parameters2.iterator();
			while(iter2.hasNext()){
				Variable v2 = (Variable)iter2.next();
				if(v1.getVariableType().equals(v2.getVariableType())){
					commonVariables.add(v1);
					iter1.remove();
					iter2.remove();
				}
			}
		}*/
		
		OntologicalElement[] es1 = parameters1.toArray(new Variable[0]);
		OntologicalElement[] es2 = parameters2.toArray(new Variable[0]);
		
		return (ArrayList<Variable>) getCommonAbstractedElements(es1, es2, new VariableAbstractor(), new VariableComparator());
	}

}
