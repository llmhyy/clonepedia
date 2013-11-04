package clonepedia.syntactic.util;

import java.util.HashMap;

import clonepedia.syntactic.util.comparator.OntologicalComparingPair;
import clonepedia.syntactic.util.comparator.PathSequenceComparingPair;

public class DistanceComputationPool {
	public static HashMap<OntologicalComparingPair, Double> ontoDisMap = new HashMap<OntologicalComparingPair, Double>();
	public static HashMap<PathSequenceComparingPair, Double> pathDisMap = new HashMap<PathSequenceComparingPair, Double>();
}
