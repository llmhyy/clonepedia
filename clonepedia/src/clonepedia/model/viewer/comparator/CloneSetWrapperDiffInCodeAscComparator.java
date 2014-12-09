package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class CloneSetWrapperDiffInCodeAscComparator implements
		Comparator<CloneSetWrapper> {

	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return (int)((arg0.differenceInCodeFragments() - arg1.differenceInCodeFragments())*100);
	}

}
