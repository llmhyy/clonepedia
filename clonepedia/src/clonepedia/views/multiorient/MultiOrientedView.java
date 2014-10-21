package clonepedia.views.multiorient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.part.ViewPart;

import clonepedia.Activator;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.views.SummaryView;
import clonepedia.views.util.ViewUIUtil;
import clonepedia.views.util.ViewUtil;

public abstract class MultiOrientedView extends SummaryView {
	
	protected Comparator<IContainer> comparator = new DefaultValueDescComparator();
	
	private ICheckStateListener checkListener = new ICheckStateListener(){

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			if(event.getElement() instanceof IContainer){
				
				PatternOrientedView patternViewPart = (PatternOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
				TopicOrientedView topicViewPart = (TopicOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.TOPIC_ORIENTED_VIEW);
				
				HashSet<CloneSetWrapper> checkedSets = new HashSet<CloneSetWrapper>();
				
				for(Object obj: patternViewPart.getViewer().getCheckedElements())
					if(obj instanceof ClonePatternGroupWrapper)
						checkedSets.addAll(((ClonePatternGroupWrapper)obj).getAllContainedCloneSet());
				
				for(Object obj: topicViewPart.getViewer().getCheckedElements())
					if(obj instanceof TopicWrapper)
						checkedSets.addAll(((TopicWrapper)obj).getAllContainedCloneSet());
				
				CloneSetWrapperList setWrappers = new CloneSetWrapperList();
				for(CloneSetWrapper set: checkedSets)
					setWrappers.add(set);
				
				PlainCloneSetView plainViewPart = (PlainCloneSetView) getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PLAIN_CLONESET_VIEW);
				plainViewPart.getViewer().setInput(setWrappers);
				plainViewPart.getViewer().refresh();
			}
			
		}
		
	};
	
	public void createPartControl(Composite parent) {
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		createSearchBox(parent);
		
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer = new CheckboxTreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.addDoubleClickListener(cloneInstanceDoubleClickListener);
		viewer.setCheckStateProvider(new MultiOrientedCheckStateProvider());
		viewer.addCheckStateListener(checkListener);
		
		
		Composite propertyPart = new Composite(sashForm, SWT.NONE);
		propertyPart.setLayout(new FillLayout());
		folder = new CTabFolder(propertyPart, SWT.BORDER);
		folder.setSimple(false);
		
		Menu menu = new Menu(folder);
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText("Close All");
		item.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(CTabItem item: folder.getItems()){
					item.dispose();
				}
			}
			
		});
		folder.setMenu(menu);
		
		sashForm.setWeights(new int[]{1, 2});
		
		//hookContextMenu();
	}
	
	public CheckboxTreeViewer getViewer(){
		return (CheckboxTreeViewer)viewer;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public Comparator<IContainer> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<IContainer> comparator) {
		this.comparator = comparator;
	}
	
	//protected abstract void createSummaryDescription(FormText text, Object targetObject);

	
	protected abstract void createTreeDetails(Tree tree, Object targetObject);
}
