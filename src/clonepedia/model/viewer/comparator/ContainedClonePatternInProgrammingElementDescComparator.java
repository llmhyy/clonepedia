package clonepedia.model.viewer.comparator;

import java.util.Comparator;

import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;

public class ContainedClonePatternInProgrammingElementDescComparator implements
		Comparator<ProgrammingElementWrapper> {
	@Override
	public int compare(ProgrammingElementWrapper arg0,
			ProgrammingElementWrapper arg1) {
		return arg1.getContainedClonePatternNumber() - arg0.getContainedClonePatternNumber();
	}
}
