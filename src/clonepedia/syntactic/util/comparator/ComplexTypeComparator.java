package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.syntactic.util.SimilarPairDistanceMatrixComputer;

public abstract class ComplexTypeComparator extends OntologicalElementComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3766033634221858581L;

	//private SimilarPairDistanceMatrixComputer spdmc = new SimilarPairDistanceMatrixComputer();
	protected abstract double computeDistanceOfConcreteType(OntologicalElement e1, OntologicalElement e2) throws Exception;
	
	protected double computeFieldsDistance(ComplexType ct1, ComplexType ct2) throws Exception{
		Field[] fields1 = ct1.getFields().toArray(new Field[0]);
		Field[] fields2 = ct2.getFields().toArray(new Field[0]);
		
		if(fields1.length == 0 && fields2.length == 0)return -1;
		
		return SimilarPairDistanceMatrixComputer.computePairSumRateInDistanceMatrix(fields1, fields2, new FieldComparator());
	}
	
	protected double computeMethodsDistance(ComplexType ct1, ComplexType ct2) throws Exception{
		Method[] methods1 = ct1.getMethods().toArray(new Method[0]);
		Method[] methods2 = ct2.getMethods().toArray(new Method[0]);
		
		if(methods1.length == 0 && methods2.length == 0)return -1;
		
		return SimilarPairDistanceMatrixComputer.computePairSumRateInDistanceMatrix(methods1, methods2, new MethodComparator());
	}

}
