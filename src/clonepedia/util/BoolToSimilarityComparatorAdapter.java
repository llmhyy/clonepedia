package clonepedia.util;

public class BoolToSimilarityComparatorAdapter implements SimilarityComparator {

	private BoolComparator boolComparator;
	private double unmatchedCost = 1;
	
	public BoolToSimilarityComparatorAdapter(BoolComparator boolComparator) {
		super();
		this.boolComparator = boolComparator;
	}

	@Override
	public double computeCost(Object obj1, Object obj2) throws Exception {
		return (boolComparator.isMatch(obj1, obj2))? 0 : this.unmatchedCost;
	}

	public BoolComparator getBoolComparator() {
		return boolComparator;
	}

	public void setBoolComparator(BoolComparator boolComparator) {
		this.boolComparator = boolComparator;
	}

	public double getUnmatchedCost() {
		return unmatchedCost;
	}

	public void setUnmatchedCost(double unmatchedCost) {
		this.unmatchedCost = unmatchedCost;
	}

}
