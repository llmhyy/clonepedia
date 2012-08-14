package clonepedia.syntactic.util;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalElementType;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.comparator.ClassComparator;
import clonepedia.syntactic.util.comparator.CloneInstanceComparator;
import clonepedia.syntactic.util.comparator.ConstantComparator;
import clonepedia.syntactic.util.comparator.FieldComparator;
import clonepedia.syntactic.util.comparator.InterfaceCompatrator;
import clonepedia.syntactic.util.comparator.MergeableOntologicalElementComparator;
import clonepedia.syntactic.util.comparator.MethodComparator;
import clonepedia.syntactic.util.comparator.VariableComparator;

public class LevenshteinMatrixComputer implements Serializable{
	
	private final static double diffEdgeCost = 1.0d;
	//private final static double extremDiffEdgeCost = 2.3d;
	private final static double mergeableEdgeCost = 0.5d;
	private final static double sameEdgeCost = 0d;
	private final static double nodeDiffCost = 1d;
	private final static double nodeSameCost = 0d;
	private final static double edgenodeDiffCost = 2.5d;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6071506914017895146L;

	private double computeOntologicalElementOrEdgeDistance(Object object1,
			Object object2) throws Exception {
		
		if(object1 instanceof OntologicalRelationType
				&& object2 instanceof OntologicalRelationType){
			OntologicalRelationType rt1 = (OntologicalRelationType)object1;
			OntologicalRelationType rt2 = (OntologicalRelationType)object2;
			if(rt1 == rt2)return sameEdgeCost;
			else if(MergeUtil.isMergeable(rt1, rt2))return mergeableEdgeCost;
			else{
				return diffEdgeCost;
					
			} 
		}
		else if(object1 instanceof OntologicalElement
				&& object2 instanceof OntologicalElement){
			OntologicalElement element1 = (OntologicalElement)object1;
			OntologicalElement element2 = (OntologicalElement)object2;
			if(element1.getOntologicalType() != element2.getOntologicalType()){
				if(MergeUtil.isMergeable(element1, element2))
					return new MergeableOntologicalElementComparator().compute(element1, element2);
				else
					return nodeDiffCost;
			}
			else{
				if(element1.getOntologicalType() == OntologicalElementType.CloneInstance)
					return nodeSameCost;
				if(element1.getOntologicalType() == OntologicalElementType.Class)
					return new ClassComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Interface)
					return new InterfaceCompatrator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.CloneInstance)
					return new CloneInstanceComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Method)
					return new MethodComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Field)
					return new FieldComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Variable)
					return new VariableComparator().compute(element1, element2);
				else if(element1.getOntologicalType() == OntologicalElementType.Constant)
					return new ConstantComparator().compute(element1, element2);
				else
					throw new Exception("The ontological type " + element1.getOntologicalType() 
							+ " is not valid in computeOntologicalElementAndEdgeDistance method");
			}
		}
		else
			return edgenodeDiffCost;
			
	}
	
	public void intializeLevenshteinMatrix(double[][] matrix, PathSequence sequence1, PathSequence sequence2) throws Exception {
		for(int i=0; i<matrix.length; i++)
			matrix[i][0] = i;
		for(int j=0; j<matrix[0].length; j++)
			matrix[0][j] = j;
		
		for(int i=1; i<matrix.length; i++)
			for(int j=1; j<matrix[i].length; j++){
				double entry1 = matrix[i-1][j] + 1;
				double entry2 = matrix[i][j-1] + 1;
				Object obj1 = sequence1.get(i-1);
				Object obj2 = sequence2.get(j-1);
				
				/*if(obj1.toString().equals("java.lang.Object") && obj2.toString().equals("javax.swing.JComponent"))
					System.out.print("");*/
				//long start = System.currentTimeMillis();
				double cost = computeOntologicalElementOrEdgeDistance(obj1, obj2);
				//long end = System.currentTimeMillis();
				//long time = end - start;
				//System.out.println("Time used in computeOntologicalElementOrEdgeDistance:" + time);
				//double cost = computeOntologicalElementOrEdgeDistance(obj1, obj2);
				double entry3 = matrix[i-1][j-1] + cost;
				matrix[i][j] = getSmallestValue(entry1, entry2, entry3);
			}
	}

	private double getSmallestValue(double entry1, double entry2, double entry3){
		double value = (entry1 < entry2)? entry1 : entry2;
		return (value < entry3)? value : entry3;
	}
}
