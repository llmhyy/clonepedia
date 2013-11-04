package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class CloneSetWrapperInstanceNumberDescComparator implements
		Comparator<CloneSetWrapper> {
	@Override
	public int compare(CloneSetWrapper arg0, CloneSetWrapper arg1) {
		return ((CloneSetWrapper)arg1).getCloneSet().size() - ((CloneSetWrapper)arg0).getCloneSet().size();
	}
}
