package clonepedia.views.multiorient;

import org.eclipse.jface.viewers.ICheckStateProvider;

import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;

public class MultiOrientedCheckStateProvider implements ICheckStateProvider{

	@Override
	public boolean isChecked(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGrayed(Object element) {
		if(element instanceof IContainer){
			IContainer container = (IContainer)element;
			if(container instanceof IContent)
				if(((IContent)container).getContainer() == null)
					return false;
		}
		return true;
	}

}
