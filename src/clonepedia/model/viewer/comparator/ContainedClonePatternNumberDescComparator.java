package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class ContainedClonePatternNumberDescComparator implements
		Comparator<CloneSetWrapper> {
	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return arg1.getCloneSet().getPatternLabels().size() - arg0.getCloneSet().getPatternLabels().size();
	}
}
