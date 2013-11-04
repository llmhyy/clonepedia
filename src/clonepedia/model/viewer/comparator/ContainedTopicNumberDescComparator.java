package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class ContainedTopicNumberDescComparator implements
		Comparator<CloneSetWrapper> {
	@Override
	public int compare(CloneSetWrapper o1, CloneSetWrapper o2) {
		return o2.getCloneSet().getTopicLabels().size() - o1.getCloneSet().getTopicLabels().size();
	}
}
