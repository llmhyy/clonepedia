package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class AverageCodeFragmentLengthDescComparator implements Comparator<CloneSetWrapper> {
	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return arg1.computeAverageCodeFragmentLength() - arg0.computeAverageCodeFragmentLength();
	}
}
