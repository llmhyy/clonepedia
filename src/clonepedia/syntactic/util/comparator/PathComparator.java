package clonepedia.syntactic.util.comparator;

import java.io.Serializable;

import clonepedia.model.syntactic.Path;

public abstract class PathComparator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8347291064683813077L;

	public abstract double computePathDistance(Path p1, Path p2, String cloneSetId, double averageLength) throws Exception;
}
