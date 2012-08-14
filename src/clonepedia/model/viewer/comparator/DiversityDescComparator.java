package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.IContainer;

public class DiversityDescComparator implements Comparator<IContainer> {
	@Override
	public int compare(IContainer o1, IContainer o2) {
		return o2.diversity() - o1.diversity();
	}
}
