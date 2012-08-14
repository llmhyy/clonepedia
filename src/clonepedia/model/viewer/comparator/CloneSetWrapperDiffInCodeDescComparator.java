package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class CloneSetWrapperDiffInCodeDescComparator implements
		Comparator<CloneSetWrapper> {
	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return (int)((arg1.differenceInCodeFragments() - arg0.differenceInCodeFragments())*100);
	}
}
