package clonepedia.views.plain;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import clonepedia.model.cluster.ICluster;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
@Deprecated
public class PlainContentProvider implements ITreeContentProvider{

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof ArrayList){
			@SuppressWarnings("unchecked")
			ArrayList<CloneSet> sets = (ArrayList<CloneSet>)inputElement;
			return sets.toArray(new CloneSet[0]);
		}
		
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof CloneSet) {
			CloneSet set = (CloneSet) parentElement;
			return set.toArray(new CloneInstance[0]);
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof CloneInstance)
			return ((CloneInstance) element).getCloneSet();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof CloneSet) {
			return true;
		}
		return false;
	}
	
}
