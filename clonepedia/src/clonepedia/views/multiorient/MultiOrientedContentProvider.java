package clonepedia.views.multiorient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.Path;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;

public class MultiOrientedContentProvider implements ITreeContentProvider {
	
	private Comparator<ProgrammingElementWrapper> programmingElementComparator;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof ArrayList){
			ArrayList list = (ArrayList)inputElement;
			return list.toArray(new Object[0]);
			
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		
		if(parentElement instanceof TopicWrapper)
			return ((TopicWrapper)parentElement).getContent().toArray(new IContent[0]);
		else if(parentElement instanceof ClonePatternGroupWrapper)
			return ((ClonePatternGroupWrapper)parentElement).getContent().toArray(new IContent[0]);
		else if(parentElement instanceof PathPatternGroupWrapper){
			PathPatternGroupWrapper ppgw = (PathPatternGroupWrapper)parentElement;
			int size = ppgw.getPathPattern().size();
			CloneInstance[] instances = new CloneInstance[size];
			int i = 0;
			for(Path path: ppgw.getPathPattern()){
				instances[i++] = (CloneInstance)path.get(0);
			}
			return instances;
		}
		/*else if(parentElement instanceof CloneSetWrapper)
			return ((CloneSetWrapper)parentElement).getCloneSet().toArray(new CloneInstance[0]);*/
		else if(parentElement instanceof PatternGroupCategory){
			PatternGroupCategory category = (PatternGroupCategory)parentElement;
			if(!category.isProgrammingHierachicalModel())
				return category.getPatterns().toArray(new ClonePatternGroupWrapper[0]);
			else{
				if(null == programmingElementComparator){
					ArrayList<ProgrammingElementWrapper> list = new ArrayList<ProgrammingElementWrapper>();
					for(String key: category.getProgrammingHierachicalRoots().keySet())
						list.add(category.getProgrammingHierachicalRoots().get(key));
					return list.toArray(new ProgrammingElementWrapper[0]);
				}
				else{
					return ((PatternGroupCategory)parentElement).
							getPatterns().getSortedRootProgrammingElements(programmingElementComparator).
							toArray(new ProgrammingElementWrapper[0]);
				}
			}
		}
		else if(parentElement instanceof ProgrammingElementWrapper){
			ProgrammingElementWrapper wrapper = (ProgrammingElementWrapper)parentElement;
			@SuppressWarnings("rawtypes")
			ArrayList list = new ArrayList();
			
			if(null == programmingElementComparator){
				for(String key: wrapper.getChildren().keySet())
					list.add(wrapper.getChildren().get(key));
				list.addAll(wrapper.getPatterns());
			}
			else{
				list.addAll(((ProgrammingElementWrapper)parentElement).getChildren(programmingElementComparator));
				list.addAll(((ProgrammingElementWrapper)parentElement).getPatterns());
			}
			
			return list.toArray(new Object[0]);
		}
			
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof TopicWrapper)
			return ((TopicWrapper)element).getContainer();
		else if(element instanceof ClonePatternGroupWrapper)
			return ((ClonePatternGroupWrapper)element).getContainer();
		else if(element instanceof CloneSetWrapper)
			return ((CloneSetWrapper)element).getContainer();

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof CloneSetWrapper)
			return false;
		return true;
	}

	public Comparator<ProgrammingElementWrapper> getProgrammingElementComparator() {
		return programmingElementComparator;
	}

	public void setProgrammingElementComparator(
			Comparator<ProgrammingElementWrapper> programmingElementComparator) {
		this.programmingElementComparator = programmingElementComparator;
	}


}
