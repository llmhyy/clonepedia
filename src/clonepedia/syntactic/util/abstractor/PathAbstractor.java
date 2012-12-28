package clonepedia.syntactic.util.abstractor;

import java.io.Serializable;
import java.util.ArrayList;


import clonepedia.exception.OntologicalElementMergeException;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.LevenshteinMatrixComputer;
import clonepedia.syntactic.util.MergeUtil;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;

public class PathAbstractor implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1298524987434296847L;
	LevenshteinMatrixComputer lmc = new LevenshteinMatrixComputer();
	PathPatternGroup ppg;
	
	public PathSequence extractAbstractPathFromPathPatternGroup(PathPatternGroup ppg) throws Exception{
		this.ppg = ppg;
		//compute how many paths have been abstracted into abstract path.
		int count = 0;
		
		Path[] paths = ppg.toArray(new Path[0]);
		PathSequence abstractPathSeq = paths[0].transferToSequence();
		for(int i=1; i<paths.length; i++){
			PathSequence newPathSeq  = paths[i].transferToSequence();
			PathSequence mergedPath = extractAbstractPath(abstractPathSeq, newPathSeq); 
			if(null != mergedPath){
				abstractPathSeq = mergedPath;
				count++;
			}
			else{
				if(count < 2){
					int index = findAnIndexOtherThanMergedPathIndexes(paths.length, i, i-1);
					if(-1 != index){
						LevenshteinPathComparator comparator = new LevenshteinPathComparator();
						PathSequence surveyorSeq = paths[index].transferToSequence();
						
						int averageLength = ppg.getCloneSet().getCloneSets().getAveragePathSequenceLength();
						
						double distance1 = comparator.computePathDistance(abstractPathSeq, surveyorSeq, averageLength);
						double distance2 = comparator.computePathDistance(newPathSeq, surveyorSeq, averageLength);
						
						if(distance1 > distance2)
							abstractPathSeq = newPathSeq;
					}
				}
			}
		}
		return abstractPathSeq;
	}
	
	public PathSequence extractAbstractPathFromPathSequences(PathSequence[] sequences, int averageLength) throws Exception{
		// compute how many paths have been abstracted into abstract path.
		int count = 0;

		PathSequence abstractPathSeq = sequences[0];
		for (int i = 1; i < sequences.length; i++) {
			PathSequence newPathSeq = sequences[i];
			PathSequence mergedPath = extractAbstractPath(abstractPathSeq, newPathSeq);
			if (null != mergedPath) {
				abstractPathSeq = mergedPath;
				count++;
			} else {
				if (count < 2) {
					int index = findAnIndexOtherThanMergedPathIndexes(sequences.length, i, i - 1);
					if (-1 != index) {
						LevenshteinPathComparator comparator = new LevenshteinPathComparator();
						PathSequence surveyorSeq = sequences[index];

						double distance1 = comparator.computePathDistance(abstractPathSeq, surveyorSeq, averageLength);
						double distance2 = comparator.computePathDistance(newPathSeq, surveyorSeq, averageLength);

						if (distance1 > distance2)
							abstractPathSeq = newPathSeq;
					}
				}
			}
		}
		return abstractPathSeq;
	}
	
	public PathSequence extractAbstractPathFromClonePatternGroup(ClonePatternGroup cpg) throws Exception{
		ArrayList<PathSequence> sequenceList = new ArrayList<PathSequence>();
		for(PathPatternGroup ppg: cpg){
			sequenceList.add(ppg.getAbstractPathSequence());
		}
		
		PathSequence[] sequences = sequenceList.toArray(new PathSequence[0]);
		int averageLength = cpg.get(0).getCloneSet().getCloneSets().getAveragePathSequenceLength();
		
		return extractAbstractPathFromPathSequences(sequences, averageLength);
	}
	
	private int findAnIndexOtherThanMergedPathIndexes(int length, int i, int j) {
		for(int k=0; k<length; k++){
			if(k != i && k != j)
				return k;
		}
		return -1;
	}

	/**
	 * Get an abstracted path from given two paths. If these two paths are unmeargable, a null
	 * value will be returned.
	 * @param pathSeq1
	 * @param pathSeq2
	 * @return
	 * @throws Exception
	 */
	public PathSequence extractAbstractPath(PathSequence pathSeq1, PathSequence pathSeq2) throws Exception{
		
		//ArrayList<Object> sequence1 = su.transferPathToSequence(p1);
		//ArrayList<Object> sequence2 = su.transferPathToSequence(p2);
		
		double[][] matrix = lmc.intializeLevenshteinMatrix(pathSeq1, pathSeq2);;
		//long start = System.currentTimeMillis();
		
		//long end = System.currentTimeMillis();
		//long time = end - start;
		//System.out.println("Time used in intializeLevenshteinMatrix:" + time);
		
		return getAbstractPath(matrix, pathSeq1, pathSeq2);
	} 
	
	private PathSequence getAbstractPath(double[][] matrix, PathSequence sequence1, PathSequence sequence2) throws Exception{
		PathSequence revAbstractSequence = new PathSequence();
		for(int i=matrix.length-1, j=matrix[0].length-1; i>0 && j>0;){
			
			Distance dis1 = new Distance(i-1, j, matrix[i-1][j]);
			Distance dis2 = new Distance(i, j-1, matrix[i][j-1]);
			Distance dis3 = new Distance(i-1, j-1, matrix[i-1][j-1]);
			
			Distance dis = getLeastCostDistance(dis1, dis2, dis3);
			
			
			try{
				if(dis.i == i-1 && dis.j == j-1){
					Object obj1 = sequence1.get(i-1);
					Object obj2 = sequence2.get(j-1);
					
					//System.out.println();
					
					Object obj = merge(obj1, obj2);
					if(null != obj)
						revAbstractSequence.add(obj);						
				}
			}
			catch(OntologicalElementMergeException e){
				/*int averageLength = ppg.getCloneSet().getCloneSets().getAveragePathSequenceLength();
				double d = new LevenshteinPathComparator().computePathDistance(sequence1, sequence2, averageLength);
				System.out.println("cloneSet: " + ppg.getCloneSet() + "; distance: " + d + " and " + e);*/
				return null;
			}
			finally{
				i = dis.i;
				j = dis.j;
			}	
		}
		
		Object[] abArray = revAbstractSequence.toArray(new Object[0]);
		PathSequence abstractSequence = new PathSequence();
		for(int i=abArray.length-1; i>=0; i--){
			abstractSequence.add(abArray[i]);
		}
		
		return abstractSequence;
	}

	private Object merge(Object obj1, Object obj2) throws Exception {
		if((obj1 instanceof OntologicalRelationType) && (obj2 instanceof OntologicalRelationType)){
			OntologicalRelationType edgeType1 = (OntologicalRelationType)obj1;
			OntologicalRelationType edgeType2 = (OntologicalRelationType)obj2;
			if(edgeType1 == edgeType2)
				return edgeType1;
			else if(MergeUtil.isMergeable(edgeType1, edgeType2))
				return OntologicalRelationType.use;
			else
				throw new OntologicalElementMergeException("cannot merge different edge type of obj " + obj1 + " and " + obj2 );
		}
		else if((obj1 instanceof OntologicalElement) && (obj2 instanceof OntologicalElement)){
			OntologicalElement element1 = (OntologicalElement)obj1;
			OntologicalElement element2 = (OntologicalElement)obj2;
			if(element1.getOntologicalType() == element2.getOntologicalType()){
				return mergedOntologialElementOfSameType(element1, element2);
			}
			else if(MergeUtil.isMergeable(element1, element2))
				return new MergeableSimpleTypeAbstractor().abstractElement(element1, element2);
			else
				throw new OntologicalElementMergeException("cannot merge different node type of obj " + obj1 + " and " + obj2 );
		}
		else
			throw new OntologicalElementMergeException("cannot merge different type of obj " + obj1 + " and " + obj2 );
	}

	private Object mergedOntologialElementOfSameType(OntologicalElement element1,
			OntologicalElement element2) throws Exception {
		if(element1.getOntologicalType() != element2.getOntologicalType())
			throw new OntologicalElementMergeException("cannot merge different type of obj " + element1 + " and " + element2 );
		
		if(element1 instanceof CloneInstance)
			return new CloneInstanceAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Class)
			return new ClassAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Interface)
			return new InterfaceAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Method)
			return new MethodAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Field)
			return new FieldAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Variable)
			return new VariableAbstractor().abstractElement(element1, element2);
		else if(element1 instanceof Constant)
			return new ConstantAbstractor().abstractElement(element1, element2);
		else
			throw new Exception("unrecognized type " + element1.getOntologicalType());
	}

	private Distance getLeastCostDistance(Distance dis1, Distance dis2, Distance dis3) {
		Distance dis = (dis1.distance < dis2.distance)? dis1 : dis2;
		return (dis.distance < dis3.distance)? dis : dis3;
	}

	private class Distance{
		public int i;
		public int j;
		public double distance;
		public Distance(int i, int j, double distance) {
			super();
			this.i = i;
			this.j = j;
			this.distance = distance;
		}
		
	}
}
