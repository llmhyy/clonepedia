package clonepedia.syntactic.util.comparator;

import java.util.ArrayList;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.Settings;

public class VectorPatternComparator extends PatternComparator {

	public double computePatternDistance(PathPatternGroup pattern1, PathPatternGroup pattern2, double averageLength) throws Exception{
		ArrayList<OntologicalElement> nodes = new ArrayList<OntologicalElement>();
		ArrayList<OntologicalRelationType> edges = new ArrayList<OntologicalRelationType>();
		
		for(Path p: pattern1){
			SyntacticUtil.collectNodesAndEdges(p, nodes, edges);
		}
		for(Path p: pattern2){
			SyntacticUtil.collectNodesAndEdges(p, nodes, edges);
		}
		
		System.out.print("");
		
		int vectorSize = nodes.size() + edges.size() + 1;
		double vector1[] = new double[vectorSize];
		double vector2[] = new double[vectorSize];
		
		initializeVector(vector1, nodes, edges, pattern1);
		initializeVector(vector2, nodes, edges, pattern2);
		
		double distance = SyntacticUtil.computeEuclideanDistance(vector1, vector2);	
		int numberOfSharedNodes = SyntacticUtil.getCommonNodeNumber(nodes.size(), vector1, vector2);
		
		distance *= Math.pow(Settings.distanceDeductRateForClustering, numberOfSharedNodes);
		
		/*String str = pattern1.toString() + '\n' + pattern2 + '\n' + distance + '\n';
		log += str;*/
		//double vector1[]
		return distance;
	}
	
	/**
	 * The order is: contained nodes, contained edges and the instance number
	 * @param vector
	 * @param nodes
	 * @param edges
	 * @param ppg
	 */
	private void initializeVector(double[] vector, ArrayList<OntologicalElement> nodes, 
			ArrayList<OntologicalRelationType> edges, PathPatternGroup ppg){
		int offset = nodes.size();
		for(int i=0; i<offset; i++){
			OntologicalElement node = nodes.get(i);
			vector[i] = ppg.getContaningOntologicalElementNumber(node)/ppg.size();
			/*if(path.contains(node))
				vector[i] = 1;
			else
				vector[i] = 0;*/
		}
		
		for(int i=offset; i<vector.length-1; i++){
			OntologicalRelationType edgeType = edges.get(i-offset);
			try {
				vector[i] = ppg.getContainingOntologicalRelationNumber(edgeType)/ppg.size();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		vector[vector.length-1] = ppg.size();
	}

}
