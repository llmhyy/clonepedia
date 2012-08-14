package clonepedia.syntactic.util.comparator;

import java.io.Serializable;

import clonepedia.model.syntactic.PathPatternGroup;

public abstract class PatternComparator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7825036567660228132L;

	public abstract double computePatternDistance(PathPatternGroup pattern1, PathPatternGroup pattern2, double averageLength) throws Exception;
	//public abstract double computePatternDistance(PathPatternGroup pattern1, PathPatternGroup pattern2, double averageLength) throws Exception;
}
