package clonepedia.util;

public interface SimilarityComparator {
	public abstract double computeCost(Object obj1, Object obj2) throws Exception;
}
