package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.IContainer;

public class DiversityAscComparator implements
		Comparator<IContainer> {

	@Override
	public int compare(IContainer o1, IContainer o2) {
		return o1.diversity() - o2.diversity();
	}

}
