package clonepedia.syntactic.util.comparator;

import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.Variable;
import clonepedia.syntactic.util.SimilarPairDistanceMatrixComputer;

public class MethodComparator extends OntologicalElementComparator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1623588498834287477L;
	private final double returnTypeProportion = 0.4d;
	private final double parameterTypeProportion = 0.4d;
	private final double methodNameProportion = 0.2d;
	
	@Override
	protected double computeDistanceOfConcreteType(OntologicalElement e1,
			OntologicalElement e2) throws Exception {
		if(e1.getOntologicalType() != OntologicalElementType.Method)
			throw new Exception("Illegal ontological type " + e1.getOntologicalType() + "in MethodComparator");
		Method m1 = (Method)e1;
		Method m2 = (Method)e2;
		
		double distance = 0.0d;
		
		PureVarType type1 = m1.getReturnType();
		PureVarType type2 = m2.getReturnType();
		if(type1==null)
			type1 = (PureVarType) m1.getOwner();
		if(type2==null)
			type2 = (PureVarType) m2.getOwner();
		distance = returnTypeProportion * computeVarTypeDistance(type1, type2)
				+ parameterTypeProportion * computeParameterTypesDistance(m1, m2)
				+ methodNameProportion * computeStringDistance(m1.getMethodName(), m2.getMethodName());
		
		
		
		/*long start = System.currentTimeMillis();
		computeVarTypeDistance(type1, type2);
		long end = System.currentTimeMillis();
		System.out.println("Time used in computeVarTypeDistance:" + (end-start));*/
		
		/*long start = System.currentTimeMillis();
		computeParameterTypesDistance(m1, m2);
		long end = System.currentTimeMillis();
		System.out.println("Time used in computeParameterTypesDistance:" + (end-start));*/
		
		/*start = System.currentTimeMillis();
		computeStringDistance(m1.getMethodName(), m2.getMethodName());
		end = System.currentTimeMillis();
		System.out.println("Time used in computeStringDistance:" + (end-start));*/
		
		return distance;
	}
	
	protected double computeParameterTypesDistance(Method m1, Method m2) throws Exception{
		
		Variable[] params1 = m1.getParameters().toArray(new Variable[0]);
		Variable[] params2 = m2.getParameters().toArray(new Variable[0]);
		
		//long start = System.currentTimeMillis();
		double paramDis = SimilarPairDistanceMatrixComputer.computePairSumRateInDistanceMatrix(params1, params2, new ParameterComparator());
		//long end = System.currentTimeMillis();
		/*System.out.println("Time used in parameter computePairSumRateInDistanceMatrix:" 
				+ (end-start) + "; size:" + params1.length + "*" + params2.length);*/
		return paramDis;
	}

}
