package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.IContainer;

public class ContainedCloneSetNumberDescComparator implements
		Comparator<IContainer> {

	@Override
	public int compare(IContainer o1, IContainer o2) {
		return o2.getAllContainedCloneSet().size() - o1.getAllContainedCloneSet().size();
	}

}
