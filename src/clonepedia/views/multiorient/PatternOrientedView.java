package clonepedia.views.multiorient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import clonepedia.Activator;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.ClonePatternGroupCategoryList;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.PatternGroupCategoryList;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.model.viewer.comparator.AlphabetAscComparator;
import clonepedia.model.viewer.comparator.AlphabetDescComparator;
import clonepedia.model.viewer.comparator.ContainedClonePatternInProgrammingElementAscComparator;
import clonepedia.model.viewer.comparator.ContainedClonePatternInProgrammingElementDescComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberDescComparator;
import clonepedia.model.viewer.comparator.DefaultValueAscComparator;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;
import clonepedia.model.viewer.comparator.DiversityAscComparator;
import clonepedia.model.viewer.comparator.DiversityDescComparator;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.summary.SummaryUtil;
import clonepedia.util.ImageUI;
import clonepedia.views.dialog.ClonePatternFilteringDialog;
import clonepedia.views.util.ViewUIUtil;


public class PatternOrientedView extends MultiOrientedView {
	
	 

	protected ISelectionListener externalEventAwareListener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection && (part != PatternOrientedView.this)) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				Object selectionObject = ss.getFirstElement();
				if(selectionObject instanceof ClonePatternGroupWrapper){
					ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)selectionObject;
					if(viewer.getInput() != null){
						ClonePatternGroupWrapper clonePatternWrapper = clonePatternCategories.searchSpecificClonePattern(clonePattern.getClonePattern().getUniqueId());
						openNewTab(clonePatternWrapper);
						//viewer.expandToLevel(2);
						viewer.setSelection(new StructuredSelection(clonePatternWrapper), true);
						viewer.refresh();
					}
				}
					
			}
		}
	};
	
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
				TopicOrientedView viewPart = 
						(TopicOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.TOPIC_ORIENTED_VIEW);
				TopicWrapperList topicList = (TopicWrapperList)viewPart.getTopics();
				TopicWrapper topic = topicList.searchSpecificTopic(content);
				viewPart.openNewTab(topic);
				viewPart.getViewer().setSelection(new StructuredSelection(topic), true);
				//viewPart.getViewer().setChecked(topic, true);
				//viewPart.getViewer().reveal(topic);
			}
			else if(hyperlinkType.equals("ClonePattern")){
				String uniqueId = content.substring(content.indexOf("#"), content.indexOf("]"));
				ClonePatternGroupWrapper clonePattern = clonePatternCategories.searchSpecificClonePattern(uniqueId);
				openNewTab(clonePattern);
			}
		}
	};
	
	private ClonePatternGroupCategoryList clonePatternCategories;
	
	public ClonePatternGroupCategoryList getClonePatternCategories() {
		return clonePatternCategories;
	}


	public void setClonePatternCategories(
			ClonePatternGroupCategoryList clonePatternCategories) {
		this.clonePatternCategories = clonePatternCategories;
	}


	public PatternOrientedView() {
	}

	
	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
		//clonePatterns = SummaryUtil.generateClonePatternOrientedSimpleTree(Activator.sets.getCloneList());
		try {
			clonePatternCategories = (ClonePatternGroupCategoryList)SummaryUtil.generateClonePatternSimplifiedCategories(Activator.getCloneSets().getCloneList());
			for(PatternGroupCategory category: clonePatternCategories){
				Collections.sort(category.getPatternList(), new DefaultValueDescComparator());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewer.setContentProvider(new MultiOrientedContentProvider());
		viewer.setLabelProvider(new MultiOrientedLabelProvider());
		viewer.setInput(clonePatternCategories);
		viewer.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				Object element = selection.getFirstElement();
				if(element instanceof ClonePatternGroupWrapper){
					openNewTab(element);
				}
				else if(element instanceof ProgrammingElementWrapper){
					ProgrammingElementWrapper proElement = (ProgrammingElementWrapper)element;
					if(proElement.getPatterns().size() > 0){
						openNewTab(proElement.getPatterns().get(0));
					}
				}
			}
		});
		getSite().setSelectionProvider(getViewer());
		getSite().getPage().addSelectionListener(externalEventAwareListener);
		//viewer.setInput(clonePatterns);
		
		hookActionsOnToolBar();
		
		hookMenu(viewer);
	}
	
	private void hookMenu(CheckboxTreeViewer viewer){
		 MenuManager menuManager = new MenuManager();
		 Menu menu = menuManager.createContextMenu(viewer.getTree());
		 
		 viewer.getTree().setMenu(menu);
		 
		 getSite().registerContextMenu(menuManager, viewer);
		 getSite().setSelectionProvider(viewer);
	}

	private void hookActionsOnToolBar(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		Action switchViewAction = new Action("Show Topic", IAction.AS_CHECK_BOX){
			public void run(){
				PatternGroupCategoryList list = new PatternGroupCategoryList();
				if(this.isChecked())
					list = SummaryUtil.transferPatternFromSimpleToComplexInCatetory((PatternGroupCategoryList)viewer.getInput());
				else
					list = SummaryUtil.transferPatternFromComplexToSimpleInCatetory((PatternGroupCategoryList)viewer.getInput());
				
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		switchViewAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SHOW_DETAILS));
		
		final Action sortBySizeAction = new Action("Sort By #CloneSet", IAction.AS_CHECK_BOX){
			public void run(){
				if(this.isChecked()){
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						comparator = new ContainedCloneSetNumberDescComparator();
						Collections.sort(category.getPatternList(), comparator);
					}
				}	
				else{
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						comparator = new ContainedCloneSetNumberAscComparator();
						Collections.sort(category.getPatternList(), comparator);
					}
				}
				viewer.setInput(clonePatternCategories);
				viewer.refresh();
			}
		};
		sortBySizeAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_SIZE));
		
		final Action sortByDiversityAction = new Action("Sort By #Topic", IAction.AS_CHECK_BOX){
			public void run(){
				if(this.isChecked()){
					comparator = new DiversityDescComparator();
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						Collections.sort(category.getPatternList(), comparator);
					}
				}	
				else{
					comparator = new DiversityAscComparator();
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						Collections.sort(category.getPatternList(), comparator);
					}
				}
				viewer.setInput(clonePatternCategories);
				viewer.refresh();
			}
		};
		sortByDiversityAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_DIVERSITY));
		
		final Action sortByDefaultAction = new Action("Sort By Default", IAction.AS_CHECK_BOX){
			public void run(){
				if(this.isChecked()){
					comparator = new DefaultValueDescComparator();
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						Collections.sort(category.getPatternList(), comparator);
					}
				}	
				else{
					comparator = new DefaultValueAscComparator();
					for(PatternGroupCategory category: (PatternGroupCategoryList)viewer.getInput()){
						Collections.sort(category.getPatternList(), comparator);
					}
				}
				viewer.setInput(clonePatternCategories);
				viewer.refresh();
			}
		};
		sortByDefaultAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_DEFAULT));
		
		/*Action switchToSimpleViewAction = new Action(){
			public void run(){
				categories = SummaryUtil.transferPatternFromComplexToSimpleInCatetory(categories);
				viewer.refresh();
			}
		};
		switchToSimpleViewAction.setText("Remove Details");
		switchToSimpleViewAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED_DISABLED))
				);*/
		
		final Action sortByAlphebetAction = new Action("Sort By Alphebet", IAction.AS_CHECK_BOX){
			public void run(){
				PatternGroupCategoryList list = (PatternGroupCategoryList)viewer.getInput();
				if(this.isChecked()){
					comparator = new AlphabetAscComparator();
					for(PatternGroupCategory category: list)
						Collections.sort(category.getPatternList(), comparator);
				}
				else{
					comparator = new AlphabetDescComparator();
					for(PatternGroupCategory category: list)
						Collections.sort(category.getPatternList(), comparator);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortByAlphebetAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_ALPHABETIC));
		
		final Action sortProgrammingElementsByContainedPatternAction = new Action("Sort By #Pattern", IAction.AS_CHECK_BOX){
			public void run(){
				PatternGroupCategoryList list = (PatternGroupCategoryList)viewer.getInput();
				if(this.isChecked()){
					if(clonePatternCategories.get(0).isProgrammingHierachicalModel()){
						MultiOrientedContentProvider contentProvider = (MultiOrientedContentProvider) viewer.getContentProvider();
						contentProvider.setProgrammingElementComparator(new ContainedClonePatternInProgrammingElementDescComparator() );
					}
				}
				else{
					if(clonePatternCategories.get(0).isProgrammingHierachicalModel()){
						MultiOrientedContentProvider contentProvider = (MultiOrientedContentProvider) viewer.getContentProvider();
						contentProvider.setProgrammingElementComparator(new ContainedClonePatternInProgrammingElementAscComparator() );
					}
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortProgrammingElementsByContainedPatternAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.SORT_BY_CONTAINED_PATTERN_NUMBER));
		
		Action showHierarchicalModelAction = new Action("Type Hierarchy", IAction.AS_CHECK_BOX){
			public void run(){
				PatternGroupCategoryList list = (PatternGroupCategoryList)viewer.getInput();
				if(this.isChecked())
					for(PatternGroupCategory category: list){
						category.constructProgrammingElementHierarchy();
						category.setProgrammingHierachicalModel(true);
						
						sortByAlphebetAction.setEnabled(false);
						sortByDefaultAction.setEnabled(false);
						sortByDiversityAction.setEnabled(false);
						sortBySizeAction.setEnabled(false);
						sortProgrammingElementsByContainedPatternAction.setEnabled(true);
					}
				else{
					for(PatternGroupCategory category: list){
						category.setProgrammingHierachicalModel(false);
					}
					
					sortByAlphebetAction.setEnabled(true);
					sortByDefaultAction.setEnabled(true);
					sortByDiversityAction.setEnabled(true);
					sortBySizeAction.setEnabled(true);
					sortProgrammingElementsByContainedPatternAction.setEnabled(false);
				}
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		showHierarchicalModelAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.PATTERN_HIERARCHY));
		showHierarchicalModelAction.setChecked(false);
		showHierarchicalModelAction.run();		
		
		Action filterClonePatternAction = new Action(){
			public void run(){
				ClonePatternFilteringDialog filterDialog = 
						new ClonePatternFilteringDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				filterDialog.create();
				if (filterDialog.open() == Window.OK) {
					/*clonePatterns = SummaryUtil.filterClonePatternByNumber(clonePatterns, 
							Integer.valueOf(filterDialog.getMinimum()), Integer.valueOf(filterDialog.getMaximum()));
					viewer.setInput(clonePatterns);*/
					clonePatternCategories = (ClonePatternGroupCategoryList)SummaryUtil.filterClonePatternByNumberInCategory(clonePatternCategories, 
							Integer.valueOf(filterDialog.getMinimum()), Integer.valueOf(filterDialog.getMaximum()));
					viewer.setInput(clonePatternCategories);
					viewer.refresh();
				} 
			}
		};
		filterClonePatternAction.setText("Do Filtering");
		filterClonePatternAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.FILTER));
		
		Action restoreClonePatternAction = new Action(){
			public void run(){
				/*clonePatterns = SummaryUtil.generateClonePatternOrientedSimpleTree(Activator.sets.getCloneList());
				viewer.setInput(clonePatterns);*/
				try {
					clonePatternCategories = (ClonePatternGroupCategoryList)SummaryUtil.generateClonePatternSimplifiedCategories(Activator.getCloneSets().getCloneList());
					viewer.setInput(clonePatternCategories);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				viewer.refresh();
			}
		};
		restoreClonePatternAction.setText("Restore Patterns");
		restoreClonePatternAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD_DISABLED))
				);
		
		//toolBar.add(switchToSimpleViewAction);
		toolBar.add(showHierarchicalModelAction);
		toolBar.add(switchViewAction);
		toolBar.add(new Separator());
		toolBar.add(sortByDefaultAction);
		toolBar.add(sortByAlphebetAction);
		toolBar.add(sortBySizeAction);
		toolBar.add(sortByDiversityAction);
		toolBar.add(sortProgrammingElementsByContainedPatternAction);
		//toolBar.add(new Separator());
		//toolBar.add(filterClonePatternAction);
		//toolBar.add(restoreClonePatternAction);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	protected void createSummaryDescription(FormText text, Object targetObject) {
		
		String summary = null;
		
		if(targetObject instanceof ClonePatternGroupWrapper)
			summary = NaturalLanguateTemplate.generateSummary(targetObject);
		
		if(null != summary){
			text.setText(summary, true, false);
			text.addHyperlinkListener(adapter);
		}
	}*/

	@Override
	protected void createTreeDetails(Tree tree, Object targetObject) {
		if(targetObject instanceof ClonePatternGroupWrapper){
			ViewUIUtil.createTreeItemsForClonePattern(tree, targetObject);
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
	protected void addSearchButtonListener(final Button serachButton) {
		searchButton.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDown(MouseEvent e) {
				String searchContent = searchText.getText();
				viewer.setInput(clonePatternCategories.searchContent(searchContent));
				viewer.refresh();
			}
		});
		
	}

	public PatternGroupCategoryList getCategories() {
		return clonePatternCategories;
	}

	@Override
	protected void addSearchTextListener(final Text searchText) {
		searchText.addKeyListener(new KeyAdapter(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 27 || e.character == SWT.CR){
					String searchContent = searchText.getText();
					viewer.setInput(clonePatternCategories.searchContent(searchContent));
					viewer.refresh();
				}
				
			}
		});
		
	}

	@Override
	protected void createSections(ScrolledForm form, Object targetObject) {
		createClonePatternDetailSection(form.getBody(), targetObject);
		//createTextDescriptionSection(form.getBody(), targetObject);
		createInvolvedCloneSetsDescriptionSection(form.getBody(), targetObject);
		createTopicDescriptionSection(form.getBody(), targetObject);
	}

	private void createClonePatternDetailSection(Composite parent, Object targetObject) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		
		ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)targetObject;
		
		String sectionTitle = "";
		String style = clonePattern.getClonePattern().getStyle(); 
		
		if(style.equals(PathSequence.LOCATION)){
			sectionTitle = "Where are Clones?";			
		}
		else if(style.equals(PathSequence.DIFF_USAGE) || style.equals(PathSequence.COMMON_USAGE)){
			sectionTitle = "What do Clones Use?";			
		}
		section.setText(sectionTitle);
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		text.setText(NaturalLanguateTemplate.
				generateDetailsFromClonePattern0((ClonePatternGroupWrapper)targetObject), true, false);
		section.setClient(text);
		
	}

	public void setCategories(ClonePatternGroupCategoryList categories) {
		this.clonePatternCategories = categories;
	}
}
