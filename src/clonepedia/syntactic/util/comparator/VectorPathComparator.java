package clonepedia.syntactic.util.comparator;

import java.util.ArrayList;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.syntactic.Path;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.Settings;

public class VectorPathComparator extends PathComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1532082521662919597L;

	public double computePathDistance(Path p1, Path p2, String cloneSetId, double averageLength) throws Exception{
		ArrayList<OntologicalElement> nodes = new ArrayList<OntologicalElement>();
		ArrayList<OntologicalRelationType> edges = new ArrayList<OntologicalRelationType>();
		
		SyntacticUtil.collectNodesAndEdges(p1, nodes, edges);
		SyntacticUtil.collectNodesAndEdges(p2, nodes, edges);
		
		/**
		 * the node types, edge types compose all the dimension of the vector.
		 */
		int vectorSize = nodes.size() + edges.size();
		
		/**
		 * The order is as the following: node types in path1 and path2, 
		 * edge types in path1 and path2.
		 */
		double vector1[] = new double[vectorSize];
		double vector2[] = new double[vectorSize];
		
		initializeVector(vector1, p1, nodes, edges, cloneSetId);
		initializeVector(vector2, p2, nodes, edges, cloneSetId);
		
		double distance = SyntacticUtil.computeEuclideanDistance(vector1, vector2);
		/**
		 * compute how many ontological nodes do vectors exactly share.
		 */
		int numberOfSharedNodes = SyntacticUtil.getCommonNodeNumber(nodes.size(), vector1, vector2);
		
		distance *= Math.pow(Settings.distanceDeductRateForPattern, numberOfSharedNodes);
		
		return distance;
		//return new MinerUtil().computeCosineValue(vector1, vector2);
	}
	
	private void initializeVector(double[] vector, Path path, 
			ArrayList<OntologicalElement> nodes, ArrayList<OntologicalRelationType> edges, String cloneSetId){
		int offset = nodes.size();
		for(int i=0; i<offset; i++){
			OntologicalElement node = nodes.get(i);
			vector[i] = path.getContaningOntologicalElementNumber(node);
			/*if(path.contains(node))
				vector[i] = 1;
			else
				vector[i] = 0;*/
		}
		
		for(int i=offset; i<vector.length; i++){
			OntologicalRelationType edgeType = edges.get(i-offset);
			try {
				vector[i] = path.getContainingOntologicalRelationNumber(edgeType);
			} catch (Exception e) {
				System.out.println(cloneSetId);
				e.printStackTrace();
			}
		}
		
	}

}
