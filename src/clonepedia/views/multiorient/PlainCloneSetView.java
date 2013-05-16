package clonepedia.views.multiorient;

import java.util.ArrayList;
import java.util.Collections;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import clonepedia.Activator;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.CompilationUnitPool;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.ClonePatternGroupCategoryList;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.PatternGroupCategoryList;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.ClonePatternGroupWrapperList;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.model.viewer.comparator.AverageCodeFragmentLengthAscComparator;
import clonepedia.model.viewer.comparator.AverageCodeFragmentLengthDescComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberAscComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberDescComparator;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;
import clonepedia.perspective.CloneDiffPerspective;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.summary.SummaryUtil;
import clonepedia.util.ImageUI;
import clonepedia.util.Settings;
import clonepedia.views.DiffPropertyView;
import clonepedia.views.SummaryView;
import clonepedia.views.codesnippet.CloneCodeSnippetView;
import clonepedia.views.codesnippet.CloneDiffView;
import clonepedia.views.plain.PlainContentProvider;
import clonepedia.views.plain.PlainLabelProvider;
import clonepedia.views.util.ViewUIUtil;
import clonepedia.views.util.ViewUtil;

public class PlainCloneSetView extends SummaryView {
	
	private CloneSetWrapperList cloneSets;
	private PlainCloneSetViewComarator comparator;
	
	
	private HyperlinkAdapter adapter = new HyperlinkAdapter(){
		
		public void linkActivated(HyperlinkEvent e) {
			String hyperlinkType = (String)e.getHref();
			String content = e.getLabel();
			if(hyperlinkType.equals("CloneSet")){
				CloneSetWrapper set = null;
				if(viewer.getInput() instanceof CloneSetWrapperList)
					set = ((CloneSetWrapperList)viewer.getInput()).searchSpecificCloneSetWrapper(content);
				
				if(null != set){
					openNewTab(set);
					viewer.setChecked(set, true);
					viewer.reveal(set);
				}
			}
			else if(hyperlinkType.equals("CloneInstance")){
				Object obj = folder.getSelection().getData();
				if(obj instanceof CloneSetWrapper){
					CloneSetWrapper setWrapper = (CloneSetWrapper)obj;
					for(CloneInstance instance: setWrapper.getCloneSet()){
						String instanceString = instance.toString();
						instanceString = instanceString.substring(instanceString.indexOf("(")+1, instanceString.lastIndexOf(")"));
						if(instanceString.equals(content)){
							//openNewTab(instance);
							ViewUtil.openJavaEditorForCloneInstace(instance);
							CloneCodeSnippetView viewpart = (CloneCodeSnippetView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.CODE_SNIPPET_VIEW);
							if(viewpart != null){
								viewpart.createCodeSections((CloneInstance)instance);								
							}
						}
					}
				}
			}
			else if(hyperlinkType.equals("Topic")){
				TopicOrientedView viewPart = 
						(TopicOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.TOPIC_ORIENTED_VIEW);
				if(viewPart != null){
					TopicWrapperList topicList = viewPart.getTopics();
					TopicWrapper topic = topicList.searchSpecificTopic(content);
					viewPart.openNewTab(topic);
					viewPart.getViewer().setSelection(new StructuredSelection(topic), true);					
				}
				//viewPart.getViewer().setChecked(topic, true);
				//viewPart.getViewer().reveal(topic);
			}
			else if(hyperlinkType.equals("ClonePattern")){
				PatternOrientedView viewPart =
						(PatternOrientedView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
				ClonePatternGroupCategoryList cateList = (ClonePatternGroupCategoryList)viewPart.getCategories();
				String uniqueId = content.substring(content.indexOf("#")+1, content.lastIndexOf("]"));
				ClonePatternGroupWrapper clonePattern = cateList.searchSpecificClonePattern(uniqueId);
				viewPart.openNewTab(clonePattern);
				viewPart.getViewer().setSelection(new StructuredSelection(clonePattern), true);
				//viewPart.getViewer().setChecked(clonePattern, true);
				//viewPart.getViewer().reveal(clonePattern);
			}
		}
		
	};
	
	public PlainCloneSetView() {
		comparator = new PlainCloneSetViewComarator();
	}

	@Override
	public void createPartControl(Composite parent) {
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		createSearchBox(parent);
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer = new CheckboxTreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setLabelProvider(new MultiOrientedLabelProvider());
		viewer.setContentProvider(new MultiOrientedContentProvider());
		
		viewer.setComparator(comparator);
		
		createColumns(viewer);
		
		cloneSets = SummaryUtil.wrapCloneSets(Activator.getCloneSets().getCloneList());
		viewer.setInput(cloneSets);
		viewer.addDoubleClickListener(cloneInstanceDoubleClickListener);
		viewer.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				Object element = selection.getFirstElement();
				if((element instanceof CloneSetWrapper)){
					CloneSetWrapper cloneSetWrapper = (CloneSetWrapper)element;
					clonepedia.java.model.CloneSetWrapper syntacticSetWrapper = 
							new clonepedia.java.model.CloneSetWrapper(cloneSetWrapper.getCloneSet(), new CompilationUnitPool());
					
					if(Settings.diffComparisonMode.equals("ASTNode_Based")){
						syntacticSetWrapper = new CloneInformationExtractor().extractCounterRelationalDifferencesOfCloneSet(syntacticSetWrapper);					
					}
					else if(Settings.diffComparisonMode.equals("Statement_Based")){
						syntacticSetWrapper = new CloneInformationExtractor().extractCounterRelationalDifferencesWithinSyntacticBoundary(syntacticSetWrapper);
					}
					
					cloneSetWrapper.setSyntacticSetWrapper(syntacticSetWrapper);
					
					openNewTab(element);
					CloneDiffView viewpart = (CloneDiffView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.CLONE_DIFF_VIEW);
					if(viewpart != null){	
						viewpart.setDiffIndex(-1);
						viewpart.showCodeSnippet(cloneSetWrapper.getSyntacticSetWrapper(), null);
					}
					
					DiffPropertyView propertyViewPart = (DiffPropertyView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.DIFF_PROPERTY_VIEW);
					if(propertyViewPart != null){
						propertyViewPart.showDiffInformation(null, cloneSetWrapper.getSyntacticSetWrapper());
					}
				}
				else if(element instanceof CloneInstance){
					openNewTab(element);
					CloneCodeSnippetView viewpart = (CloneCodeSnippetView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.CODE_SNIPPET_VIEW);
					viewpart.createCodeSections((CloneInstance)element);
				}
			}
		});
		
		Composite propertyPart = new Composite(sashForm, SWT.NONE);
		propertyPart.setLayout(new FillLayout());
		folder = new CTabFolder(propertyPart, SWT.BORDER);
		folder.setSimple(false);
		
		Menu menu = new Menu(folder);
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText("Clone All");
		item.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(CTabItem item: folder.getItems()){
					item.dispose();
				}
			}
			
		});
		folder.setMenu(menu);
		
		hookActionsOnBars();
		
		sashForm.setWeights(new int[]{35, 65});
		//getSite().getPage().addSelectionListener(externalEventAwareListener);
	}
	
	private void createColumns(CheckboxTreeViewer viewer){
		String[] titles = {"Name", "#Ins", "Avg LOC", "Diff/LOC", "#Pattern", "#Topic"};
		int[] bounds = {110, 50, 60, 70, 50, 50};
		
		/*TreeViewerColumn placeHolderCol = createTreeColumn(viewer, titles[0], bounds[0], 0);
		placeHolderCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element){
				if(element instanceof CloneInstance){
					return element.toString();
				}
				return null;
			}
		});*/
		
		TreeViewerColumn idCol = createTreeColumn(viewer, titles[0], bounds[0], 0);
		idCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return ((CloneSetWrapper)element).getCloneSet().getId();
				else if(element instanceof CloneInstance)
					return element.toString();
				else if(element instanceof TopicWrapper)
					return ((TopicWrapper)element).getTopic().getTopicString();
				else if(element instanceof ClonePatternGroupWrapper){
					try {
						return ((ClonePatternGroupWrapper)element).getReverseEpitomise();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public Image getImage(Object element) {
				if(element instanceof TopicWrapper)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
				else if(element instanceof ClonePatternGroupWrapper)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
				else if(element instanceof CloneSetWrapper)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
				else if(element instanceof CloneInstance)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
				else if(element instanceof PatternGroupCategory)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
				
				return null;
			}
		});
		
		TreeViewerColumn insNumCol = createTreeColumn(viewer, titles[1], bounds[1], 1);
		insNumCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return String.valueOf(((CloneSetWrapper)element).getCloneSet().size());
				return null;
			}
		});
		
		TreeViewerColumn codeLengthCol = createTreeColumn(viewer, titles[2], bounds[2], 2);
		codeLengthCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return String.valueOf(((CloneSetWrapper)element).computeAverageCodeFragmentLength());
				return null;
			}
		});
		
		TreeViewerColumn diffInLocNumCol = createTreeColumn(viewer, titles[3], bounds[3], 3);
		diffInLocNumCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return String.valueOf(((CloneSetWrapper)element).differenceInCodeFragments());
				return null;
			}
		});
		
		TreeViewerColumn patternNumCol = createTreeColumn(viewer, titles[4], bounds[4], 4);
		patternNumCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return String.valueOf(((CloneSetWrapper)element).getCloneSet().getPatternLabels().size());
				return null;
			}
		});
		
		TreeViewerColumn topicNumCol = createTreeColumn(viewer, titles[5], bounds[5], 5);
		topicNumCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof CloneSetWrapper)
					return String.valueOf(((CloneSetWrapper)element).getCloneSet().getTopicLabels().size());
				return null;
			}
		});
		
		
	}
	
	private TreeViewerColumn createTreeColumn(CheckboxTreeViewer viewer, String title, int bound, final int columnNumber){
		TreeViewerColumn viewerCol = new TreeViewerColumn(viewer, SWT.NONE);
		TreeColumn col = viewerCol.getColumn();
		col.setText(title);
		col.setWidth(bound);
		col.setResizable(true);
		col.setMoveable(true);
		col.addSelectionListener(getSelectoinAdapter(col, columnNumber));
		return viewerCol;
	}
	
	private SelectionAdapter getSelectoinAdapter(final TreeColumn col, final int index){
		SelectionAdapter adapter = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTree().setSortDirection(dir);
				viewer.getTree().setSortColumn(col);
				viewer.refresh();
			}
		};
		
		return adapter;
	}
	
	public CloneSetWrapperList getCloneSets() {
		return cloneSets;
	}

	public void setCloneSets(CloneSetWrapperList cloneSets) {
		this.cloneSets = cloneSets;
	}
	
	private void hookActionsOnBars(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		
		Action sortCloneSetsByInstanceNumberAction = new Action("Sort By Instance Number", IAction.AS_CHECK_BOX){
			public void run(){
				CloneSetWrapperList list = (CloneSetWrapperList)viewer.getInput();
				if(this.isChecked())
					Collections.sort(list, new CloneSetWrapperInstanceNumberAscComparator());
				else
					Collections.sort(list, new CloneSetWrapperInstanceNumberDescComparator());
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortCloneSetsByInstanceNumberAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_TOOL_UP_HOVER))
				);
		
		Action sortCloneSetsByAverageCodeLengthAction = new Action("Sort By Average Code Fragment", IAction.AS_CHECK_BOX){
			public void run(){
				CloneSetWrapperList list = (CloneSetWrapperList)viewer.getInput();
				if(this.isChecked())
					Collections.sort(list, new AverageCodeFragmentLengthAscComparator());
				else
					Collections.sort(list, new AverageCodeFragmentLengthDescComparator());
				viewer.setInput(list);
				viewer.refresh();
			}
		};
		sortCloneSetsByAverageCodeLengthAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_TOOL_UNDO_HOVER))
				);
		
		Action removeSelectedCloneSetAction = new Action(){
			@SuppressWarnings("rawtypes")
			public void run(){
				
				Object input = viewer.getInput();
				if(input instanceof ArrayList){
					ArrayList list = (ArrayList)input;
					if(list.size() > 0 && (list.get(0) instanceof CloneSetWrapper)){
						Iterator iterator = list.iterator();
						while(iterator.hasNext()){
							CloneSetWrapper s = (CloneSetWrapper)iterator.next();
							for(Object obj: viewer.getCheckedElements()){
								if(obj instanceof CloneSetWrapper){
									CloneSetWrapper set = (CloneSetWrapper)obj;
									if(set.equals(s)){
										iterator.remove();
										break;
									}
								}
							}
						}
						viewer.setInput(list);
					}
					
				}
				viewer.refresh();
			}
		};
		removeSelectedCloneSetAction.setText("Hide Selected Clone Sets");
		removeSelectedCloneSetAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED_DISABLED))
				);
		
		final Action reorgnizeByClonePattern = new Action("Group By Clone Pattern", IAction.AS_CHECK_BOX){
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run(){
				if(this.isChecked()){
					ArrayList list = (ArrayList)viewer.getInput();
					if(list.size() > 0){
						Object obj = list.get(0);
						if(obj instanceof CloneSetWrapper){
							ArrayList<CloneSetWrapper> setList = (ArrayList<CloneSetWrapper>)viewer.getInput();
							viewer.setInput(SummaryUtil.generateClonePatternOrientedSimpleList(setList));
						}
						else if(obj instanceof TopicWrapper){
							TopicWrapperList topicList = (TopicWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.transferTopicFromSimpleToComplex(topicList));
						}
					}
				}
				else{
					ArrayList list = (ArrayList)viewer.getInput();
					if(list.size() > 0){
						Object obj = list.get(0);
						if(obj instanceof ClonePatternGroupWrapper){
							ClonePatternGroupWrapperList setList = (ClonePatternGroupWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.reduceTopClonePatternFromLayer(setList));
						}
						else if(obj instanceof TopicWrapper){
							TopicWrapperList topicList = (TopicWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.transferTopicFromComplexToSimple(topicList));
						}
					}
				}
			}
		};
		reorgnizeByClonePattern.setImageDescriptor(Activator.getDefault().
				getImageRegistry().getDescriptor(ImageUI.CLONE_PATTERN));
		
		final Action reorgnizeByTopic = new Action("Group By Topic", IAction.AS_CHECK_BOX){
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run(){
				if(this.isChecked()){
					ArrayList list = (ArrayList)viewer.getInput();
					if(list.size() > 0){
						Object obj = list.get(0);
						if(obj instanceof CloneSetWrapper){
							ArrayList<CloneSetWrapper> setList = (ArrayList<CloneSetWrapper>)viewer.getInput();
							viewer.setInput(SummaryUtil.generateTopicOrientedSimpleList(setList));
						}
						else if(obj instanceof ClonePatternGroupWrapper){
							ClonePatternGroupWrapperList patternList = (ClonePatternGroupWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.transferPatternFromSimpleToComplex(patternList));
						}
					}
				}
				else{
					ArrayList list = (ArrayList)viewer.getInput();
					if(list.size() > 0){
						Object obj = list.get(0);
						if(obj instanceof TopicWrapper){
							TopicWrapperList setList = (TopicWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.reduceTopTopicFromLayer(setList));
						}
						else if(obj instanceof ClonePatternGroupWrapper){
							ClonePatternGroupWrapperList patternList = (ClonePatternGroupWrapperList)viewer.getInput();
							viewer.setInput(SummaryUtil.transferPatternFromComplexToSimple(patternList));
						}
					}
				}
				viewer.refresh();
			}
		};
		reorgnizeByTopic.setImageDescriptor(Activator.getDefault().
				getImageRegistry().getDescriptor(ImageUI.TOPIC));
		
		Action loadAllCloneSetAction = new Action(){
			public void run(){
				reorgnizeByClonePattern.setChecked(false);
				reorgnizeByTopic.setChecked(false);
				
				viewer.setInput(SummaryUtil.wrapCloneSets(Activator.getCloneSets().getCloneList()));
				viewer.refresh();
			}
		};
		loadAllCloneSetAction.setText("Show All");
		loadAllCloneSetAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED))
				);
		
		
		toolBar.add(reorgnizeByClonePattern);
		toolBar.add(reorgnizeByTopic);
		toolBar.add(new Separator());
		//toolBar.add(sortCloneSetsByInstanceNumberAction);
		//toolBar.add(sortCloneSetsByAverageCodeLengthAction);
		toolBar.add(loadAllCloneSetAction);
		toolBar.add(removeSelectedCloneSetAction);
	}

	@Override
	public void setFocus() {
		

	}
	
	public CheckboxTreeViewer getViewer(){
		return this.viewer;
	}

	/*@Override
	protected void createSummaryDescription(FormText text,
			Object targetObject) {
		if((targetObject instanceof CloneInstance) || (targetObject instanceof CloneSet)){
			String summary = NaturalLanguateTemplate.generateSummary(targetObject);
			
			text.setText(summary, true, false);
			FormColors colors = toolkit.getColors();
			colors.createColor("red", colors.getSystemColor(SWT.COLOR_DARK_MAGENTA));
			colors.createColor("green", colors.getSystemColor(SWT.COLOR_BLUE));
			text.setColor("red", colors.getColor("red"));
			text.setColor("green", colors.getColor("green"));
			
			text.addHyperlinkListener(adapter);
		}
	}*/

	@Override
	protected void createTreeDetails(Tree tree, Object targetObject) {
		if(targetObject instanceof CloneInstance){
			ViewUIUtil.createTreeItemsForCloneInstance(tree, targetObject);
		}
		else if(targetObject instanceof CloneSetWrapper){
			try {
				ViewUIUtil.createTreeItemsForCloneSet(tree, targetObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				/*CloneSetWrapper set = cloneSets.searchSpecificCloneSetWrapper(searchText.getText());
				viewer.reveal(set);
				viewer.setChecked(set, true);*/
				
				//CloneSetWrapperList list = cloneSets.searchRelatedCloneSetWrapperList(searchText.getText());
				CloneSetWrapperList list = cloneSets.searchCloneSetWrapperListById(searchText.getText());
				viewer.setInput(list);
				viewer.refresh();
			}
		});
		
	}

	@Override
	protected void addSearchTextListener(final Text searchText) {
		searchText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 27 || e.character == SWT.CR){
					/*CloneSetWrapper set = cloneSets.searchSpecificCloneSetWrapper(searchText.getText());
					viewer.reveal(set);
					viewer.setChecked(set, true);*/
					CloneSetWrapperList list = cloneSets.searchCloneSetWrapperListById(searchText.getText());
					viewer.setInput(list);
					viewer.refresh();
		        }
			}
		});
		
	}

	@Override
	protected void createSections(ScrolledForm form, Object targetObject) {
		//createTextDescriptionSection(form.getBody(), targetObject);
		createCloneInstanceInformationSections(form.getBody(), targetObject);
		//createCounterRelationalDifferenceSection(form.getBody(), targetObject);
		createPathPatternStructuralInfoSection(form.getBody(), targetObject);
		createClonePatternStructuralInfoSection(form.getBody(), targetObject);
		createTopicDescriptionSection(form.getBody(), targetObject);
		//createClonePatternDescriptionSection(form.getBody(), targetObject);
	}

	private void createCounterRelationalDifferenceSection(Composite parent, Object targetObject) {
		Section section = toolkit.createSection(parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
		CloneSetWrapper setWrapper = (CloneSetWrapper)targetObject;
		
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Path Pattern");
		
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new TableWrapLayout());
		
		Button prevButton = toolkit.createButton(composite, "previous", SWT.PUSH);
		Button nextButton = toolkit.createButton(composite, "next", SWT.PUSH);
		
		prevButton.setLayoutData(new TableWrapData(TableWrapData.LEFT));
		nextButton.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		
		section.setClient(composite);
	}

	protected void createClonePatternStructuralInfoSection(Composite parent, Object targetObject){
		
		parent.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		Section section = toolkit.createSection(parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
		TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		wrapData.grabVertical = true;
		//wrapData.rowspan = 20;
		section.setLayoutData(wrapData);
		section.setExpanded(true);
		section.setText("Related Inter Clone Set Patterns");
		
		CloneSetWrapper setWrapper = (CloneSetWrapper)targetObject;
		
		Composite composite = createClonePatternStructuralInfoContent(section, setWrapper);
		section.setClient(composite);
	}
	
	protected void createPathPatternStructuralInfoSection(Composite parent, Object targetObject){
		parent.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		Section section = toolkit.createSection(parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
		TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		wrapData.grabVertical = true;
		//wrapData.rowspan = 20;
		section.setLayoutData(wrapData);
		section.setExpanded(true);
		section.setText("Related Intra Clone Set Patterns");
		//section.setLayout(new TableWrapLayout());
		
		CloneSetWrapper setWrapper = (CloneSetWrapper)targetObject;
		
		Composite composite = createPathPatternStructuralInfoContent(section, setWrapper);
		section.setClient(composite);
	}
	
	private Composite createPathPatternStructuralInfoContent(Section section, CloneSetWrapper setWrapper){
		Composite composite = toolkit.createComposite(section, SWT.WRAP);
		//composite.setLayoutData(new GridData(GridData.FILL_BOTH,GridData.FILL_BOTH, true, true));
		
		TableWrapLayout compositeLayout = new TableWrapLayout();
		composite.setLayout(compositeLayout);
		
		final TreeViewer relatedClonePatternViewer = new TreeViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		twData.heightHint = 150;
		//GridData treeLayoutData = new GridData(GridData.BEGINNING, GridData.FILL_BOTH, true, true);
		//treeLayoutData.widthHint = 250;
		//treeLayoutData.heightHint = 200;
		//treeLayoutData.grabExcessHorizontalSpace = true;
		//composite.setLayoutData(treeLayoutData);
		relatedClonePatternViewer.getTree().setLayoutData(twData);
		
		//ArrayList<ProgrammingElementWrapper> proList = getProgrammingElementList(setWrapper);
		PatternGroupCategoryList categoryList = setWrapper.getPathPatternCategoryList();
		
		relatedClonePatternViewer.setContentProvider(new MultiOrientedContentProvider());
		relatedClonePatternViewer.setLabelProvider(new MultiOrientedLabelProvider());
		relatedClonePatternViewer.setInput(categoryList);
		
		relatedClonePatternViewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) relatedClonePatternViewer.getSelection();
				Object element = selection.getFirstElement();
				if(element instanceof ClonePatternGroupWrapper){
					//ready for future enhancement
				}
			}
			
		});
		
		return composite;
	}
	
	private Composite createClonePatternStructuralInfoContent(Section section, CloneSetWrapper setWrapper){
		Composite composite = toolkit.createComposite(section, SWT.WRAP);
		
		TableWrapLayout compositeLayout = new TableWrapLayout();
		composite.setLayout(compositeLayout);
		
		/*Label locationLabel = new Label(composite, SWT.NONE);
		locationLabel.setText("Clone Pattern:");
		locationLabel.setFont(new Font(Display.getCurrent(),
				new FontData(locationLabel.getFont().getFontData()[0].getName(), 
						locationLabel.getFont().getFontData()[0].getHeight(), 
						SWT.BOLD)));*/
		
		final TreeViewer relatedClonePatternViewer = new TreeViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		twData.heightHint = 150;
		
		/*GridData treeLayoutData = new GridData(GridData.BEGINNING, GridData.FILL, false, true);
		treeLayoutData.widthHint = 250;
		treeLayoutData.heightHint = 200;
		composite.setLayoutData(treeLayoutData);*/
		relatedClonePatternViewer.getTree().setLayoutData(twData);
		
		//ArrayList<ProgrammingElementWrapper> proList = getProgrammingElementList(setWrapper);
		PatternGroupCategoryList categoryList = setWrapper.getClonePatternCategoryList();
		
		relatedClonePatternViewer.setContentProvider(new MultiOrientedContentProvider());
		relatedClonePatternViewer.setLabelProvider(new MultiOrientedLabelProvider());
		relatedClonePatternViewer.setInput(categoryList);
		
		relatedClonePatternViewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) relatedClonePatternViewer.getSelection();
				Object element = selection.getFirstElement();
				if(element instanceof ClonePatternGroupWrapper){
					ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)element;
					PatternOrientedView viewPart = (PatternOrientedView)PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
					if(viewPart != null){
						clonePattern = ((ClonePatternGroupCategoryList)viewPart.getCategories()).searchSpecificClonePattern(clonePattern.getClonePattern().getUniqueId());
						viewPart.openNewTab(clonePattern);
						viewPart.getViewer().setSelection(new StructuredSelection(clonePattern), true);						
					}
				}
			}
			
		});
		
		return composite;
	}
	
	
	
	protected void createCloneInstanceInformationSections(Composite parent, Object targetObject){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Clone Instances");
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createCloneInstanceDescription(text, targetObject);
		section.setClient(text);
	}

	private void createCloneInstanceDescription(FormText text,
			Object targetObject) {
		text.setText(NaturalLanguateTemplate.generateCloneInstancesDescription(targetObject), true, false);
		text.addHyperlinkListener(adapter);
	}

}
