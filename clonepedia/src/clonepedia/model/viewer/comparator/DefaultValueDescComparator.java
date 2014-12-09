package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.IContainer;

public class DefaultValueDescComparator implements
		Comparator<IContainer> {

	@Override
	public int compare(IContainer o1, IContainer o2) {
		return (int) ((o2.value() - o1.value())*100);
	}

}
