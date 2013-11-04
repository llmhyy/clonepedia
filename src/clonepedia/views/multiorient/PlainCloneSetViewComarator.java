package clonepedia.views.multiorient;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.IContainer;
import clonepedia.perspective.CloneSummaryPerspective;

public class PlainCloneSetViewComarator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public PlainCloneSetViewComarator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		if(!(e1 instanceof CloneSetWrapper)){
			IContainer container1 = (IContainer)e1;
			IContainer container2 = (IContainer)e2;
			
			if(propertyIndex == 0){
				if(container1 instanceof ClonePatternGroupWrapper){
					PatternOrientedView patternViewPart = (PatternOrientedView) PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().
							findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
					
					return patternViewPart.getComparator().compare(container1, container2);
				}
				else{
					TopicOrientedView topicViewPart = (TopicOrientedView) PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().
							findView(CloneSummaryPerspective.TOPIC_ORIENTED_VIEW);
					return topicViewPart.getComparator().compare(container1, container2);
				}
				
			}
			else 
				return 0;
			
		}
		
		
		CloneSetWrapper setWrapper1 = (CloneSetWrapper)e1;
		CloneSetWrapper setWrapper2 = (CloneSetWrapper)e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = setWrapper1.getCloneSet().getId().compareTo(setWrapper2.getCloneSet().getId());
			break;
		case 1:
			rc = setWrapper1.getCloneSet().size() - setWrapper2.getCloneSet().size();
			break;
		case 2:
			rc = setWrapper1.computeAverageCodeFragmentLength() - setWrapper2.computeAverageCodeFragmentLength();
			break;
		case 3:
			rc = setWrapper1.getCloneSet().getPatternLabels().size() - setWrapper2.getCloneSet().getPatternLabels().size();
			break;
		case 4:
			rc = setWrapper1.getCloneSet().getTopicLabels().size() - setWrapper2.getCloneSet().getTopicLabels().size();
			break;
		case 5:
			rc = (int)((setWrapper1.differenceInCodeFragments() - setWrapper2.differenceInCodeFragments()) * 100);
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
