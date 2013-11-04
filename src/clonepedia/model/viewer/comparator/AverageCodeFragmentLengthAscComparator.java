package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class AverageCodeFragmentLengthAscComparator implements
		Comparator<CloneSetWrapper> {

	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return arg0.computeAverageCodeFragmentLength() - arg1.computeAverageCodeFragmentLength();
	}

}
