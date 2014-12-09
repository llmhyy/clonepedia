package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.CloneSetWrapper;

public class ContainedTopicNumberAscComparator implements
		Comparator<CloneSetWrapper> {

	@Override
	public int compare(CloneSetWrapper o1, CloneSetWrapper o2) {
		return o1.getCloneSet().getTopicLabels().size() - o2.getCloneSet().getTopicLabels().size();
	}

}
