package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;

public class ContainedClonePatternInProgrammingElementAscComparator implements
		Comparator<ProgrammingElementWrapper> {

	@Override
	public int compare(ProgrammingElementWrapper arg0,
			ProgrammingElementWrapper arg1) {
		return arg0.getContainedClonePatternNumber() - arg1.getContainedClonePatternNumber();
	}

}
