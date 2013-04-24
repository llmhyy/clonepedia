package clonepedia.views.multiorient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import clonepedia.Activator;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.viewer.ClonePatternGroupCategoryList;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.PatternGroupCategoryList;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.model.viewer.comparator.AlphabetAscComparator;
import clonepedia.model.viewer.comparator.AlphabetDescComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberDescComparator;
import clonepedia.model.viewer.comparator.DefaultValueAscComparator;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;
import clonepedia.model.viewer.comparator.DiversityAscComparator;
import clonepedia.model.viewer.comparator.DiversityDescComparator;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.summary.SummaryUtil;
import clonepedia.util.ImageUI;
import clonepedia.views.SummaryView;
import clonepedia.views.util.ViewUIUtil;

public class TopicOrientedView extends MultiOrientedView {
	
	private TopicWrapperList topics;
	
	private HyperlinkAdapter adapter = new HyperlinkAdapter(){
		public void linkActivated(HyperlinkEvent e) {
			String hyperlinkType = (String)e.getHref();
			String content = e.getLabel();
			if(hyperlinkType.equals("CloneSet")){
				PlainCloneSetView viewPart = 
						(PlainCloneSetView) getSite().getWorkbenchWindow().
						getActivePage().findView(CloneSummaryPerspective.PLAIN_CLONESET_VIEW);
				CloneSetWrapper set = ((CloneSetWrapperList)viewPart.getCloneSets()).searchSpecificCloneSetWrapper(content);
				viewPart.openNewTab(set);
				viewPart.getViewer().setSelection(new StructuredSelection(set), true);
				//viewPart.getViewer().setChecked(set, true);
				//viewPart.getViewer().reveal(set);
			}
			else if(hyperlinkType.equals("Topic")){
				TopicWrapper topic = topics.searchSpecificTopic(content);
				openNewTab(topic);
			}
			else if(hyperlinkType.equals("ClonePattern")){
				PatternOrientedView viewPart =
						(PatternOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
				ClonePatternGroupCategoryList cateList = (ClonePatternGroupCategoryList)viewPart.getCategories();
				String uniqueId = content.substring(content.indexOf("#")+1, content.lastIndexOf("]"));
				ClonePatternGroupWrapper clonePattern = cateList.searchSpecificClonePattern(uniqueId);
				viewPart.openNewTab(clonePattern);
				viewPart.getViewer().setSelection(new StructuredSelection(clonePattern), true);
				//viewPart.getViewer().reveal(clonePattern);
			}
		}
	};
	
	protected ISelectionListener externalEventAwareListener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection && part != TopicOrientedView.this) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				Object selectionObject = ss.getFirstElement();
				if(selectionObject instanceof TopicWrapper){
					TopicWrapper tw = (TopicWrapper)selectionObject;
					if(viewer.getInput() != null){
						openNewTab(topics.searchSpecificTopic(tw.getTopic().getTopicString()));
					}
				}
			}
			System.out.print("");
		}
	};
	
	
	public TopicOrientedView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		topics = SummaryUtil.generateTopicOrientedSimpleTree(Activator.getCloneSets().getCloneList());
		Collections.sort(topics, new DefaultValueAscComparator());
		
		viewer.setContentProvider(new MultiOrientedContentProvider());
		viewer.setLabelProvider(new MultiOrientedLabelProvider());
		
		viewer.setInput(topics);
		viewer.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				Object element = selection.getFirstElement();
				if(element instanceof TopicWrapper){
					openNewTab(element);
				}
			}
		});
		
		
		getSite().setSelectionProvider(getViewer());
		getSite().getPage().addSelectionListener(externalEventAwareListener);
		hookActionsOnToolBar();
		/*for(TreeItem item: viewer.getTree().getItems())
			disableSubItemsForTreeItem(item);*/
	}
	
	
	
	private void hookActionsOnToolBar(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		Action switchViewAction = new Action("Show Pattern", IAction.AS_CHECK_BOX){
			public void run(){
				TopicWrapperList list = new TopicWrapperList();
				if(this.isChecked())
					list = SummaryUtil.transferTopicFromSimpleToComplex((TopicWrapperList)viewer.getInput());
				else
					list = SummaryUtil.transferTopicFromComplexToSimple((TopicWrapperList)viewer.getInput());
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		switchViewAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SHOW_DETAILS));
		
		Action sortBySizeAction = new Action("Sort By #CloneSet", IAction.AS_CHECK_BOX){
			public void run(){
				TopicWrapperList list = (TopicWrapperList)viewer.getInput();
				if(this.isChecked()){
					comparator = new ContainedCloneSetNumberDescComparator();
					Collections.sort(list, comparator);
				}
				else{
					comparator = new ContainedCloneSetNumberAscComparator();
					Collections.sort(list, comparator);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortBySizeAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_SIZE));
		
		
		Action sortByDiversityAction = new Action("Sort By #Pattern", IAction.AS_CHECK_BOX){
			public void run(){
				TopicWrapperList list = (TopicWrapperList)viewer.getInput();
				if(this.isChecked()){
					comparator = new DiversityDescComparator();
					Collections.sort(list, comparator);
				}
				else{
					comparator = new DiversityAscComparator();
					Collections.sort(list, comparator);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortByDiversityAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_DIVERSITY));
		
		
		Action sortByDefaultAction = new Action("Sort By Default", IAction.AS_CHECK_BOX){
			public void run(){
				TopicWrapperList list = (TopicWrapperList)viewer.getInput();
				if(this.isChecked()){
					comparator = new DefaultValueDescComparator();
					Collections.sort(list, comparator);
				}
				else{
					comparator = new DefaultValueAscComparator();
					Collections.sort(list, comparator);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortByDefaultAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_DEFAULT));
		
		
		Action sortByAlphebetAction = new Action("Sort By Alphebet", IAction.AS_CHECK_BOX){
			public void run(){
				TopicWrapperList list = (TopicWrapperList)viewer.getInput();
				if(this.isChecked()){
					comparator = new AlphabetAscComparator();
					Collections.sort(list, comparator);
				}
				else{
					comparator = new AlphabetDescComparator();
					Collections.sort(list, comparator);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortByAlphebetAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_ALPHABETIC));
		
		
		/*Action switchSimpleViewAction = new Action(){
			public void run(){
				ArrayList<TopicWrapper> topics = SummaryUtil.generateTopicOrientedSimpleTree(Activator.sets.getCloneList());
				viewer.setInput(topics);
				viewer.refresh();
			}
		};
		switchSimpleViewAction.setText("Remove Details");
		switchSimpleViewAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED_DISABLED))
				);*/
		
		
		toolBar.add(switchViewAction);
		toolBar.add(new Separator());
		toolBar.add(sortByDefaultAction);
		toolBar.add(sortByAlphebetAction);
		toolBar.add(sortBySizeAction);
		toolBar.add(sortByDiversityAction);
		
		//toolBar.add(switchComplexViewAction);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	protected void createSummaryDescription(FormText text, Object targetObject) {
		
		String summary = null;
		
		if(targetObject instanceof TopicWrapper)
			summary = NaturalLanguateTemplate.generateSummary(targetObject);
		
		if(null != summary){
			text.setText(summary, true, false);
			text.addHyperlinkListener(adapter);
		}
	}*/

	@Override
	protected void createTreeDetails(Tree tree, Object targetObject) {
		if(targetObject instanceof TopicWrapper){
			ViewUIUtil.createTreeItemsForSemanticCluster(tree, targetObject);
		}
	}
	
	@Override
	protected void createTextDescription(FormText text, Object targetObject) {
		text.setText(NaturalLanguateTemplate.generateTextDescriptionSummary(targetObject), true, false);
		text.addHyperlinkListener(adapter);
	}

	@Override
	protected void createTopicDescription(FormText text, Object targetObject) {
		text.setText(NaturalLanguateTemplate.generateRelatedTopicSummary(targetObject), true, false);
		text.addHyperlinkListener(adapter);
	}

	@Override
	protected void createClonePatternDescription(FormText text,
			Object targetObject) {
		text.setText(NaturalLanguateTemplate.generateRelatedClonePatternSummary(targetObject), true, false);
		text.addHyperlinkListener(adapter);
	}

	@Override
	protected void createInvolvedCloneSetsDescription(FormText text,
			Object targetObject) {
		text.setText(NaturalLanguateTemplate.generateInvolvedCloneSetsSummary(targetObject), true, false);
		text.addHyperlinkListener(adapter);
	}
	

	@Override
	protected void addSearchTextListener(final Text searchText) {
		searchText.addKeyListener(new KeyAdapter(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 27 || e.character == SWT.CR){
					String searchContent = searchText.getText();
					viewer.setInput(topics.searchContent(searchContent));
					viewer.refresh();
		        }
			}
			
		});
		
	}

	@Override
	protected void addSearchButtonListener(Button searchButton) {
		searchButton.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDown(MouseEvent e) {
				String searchContent = searchText.getText();
				viewer.setInput(topics.searchContent(searchContent));
				viewer.refresh();
			}
			
		});
	}

	public TopicWrapperList getTopics() {
		return topics;
	}

	public void setTopics(TopicWrapperList topics) {
		this.topics = topics;
	}
	
	@Override
	protected void createSections(ScrolledForm form, Object targetObject) {
		//createTextDescriptionSection(form.getBody(), targetObject);
		createInvolvedCloneSetsDescriptionSection(form.getBody(), targetObject);
		//createTopicDescriptionSection(form.getBody(), targetObject);
		createClonePatternDescriptionSection(form.getBody(), targetObject);
	}
}
