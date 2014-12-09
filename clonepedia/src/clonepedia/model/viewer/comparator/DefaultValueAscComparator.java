package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.IContainer;

public class DefaultValueAscComparator implements
		Comparator<IContainer> {

	@Override
	public int compare(IContainer arg0,
			IContainer arg1) {
		return (int) ((arg0.value() - arg1.value())*100);
	}

}
