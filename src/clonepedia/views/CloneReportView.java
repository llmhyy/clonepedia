package clonepedia.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import clonepedia.Activator;
import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.CompilationUnitPool;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.perspective.CloneDiffPerspective;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.summary.SummaryUtil;
import clonepedia.util.Settings;
import clonepedia.views.codesnippet.CloneCodeSnippetView;
import clonepedia.views.codesnippet.CloneDiffView;
import clonepedia.views.multiorient.MultiOrientedContentProvider;
import clonepedia.views.multiorient.MultiOrientedLabelProvider;
import clonepedia.views.multiorient.PlainCloneSetViewComarator;
import clonepedia.views.util.ViewUIUtil;
import clonepedia.views.util.ViewUtil;

public class CloneReportView extends SummaryView {
	
	private CloneSetWrapperList cloneSets;
	private PlainCloneSetViewComarator comparator;
	
	
	private HyperlinkAdapter adapter = new HyperlinkAdapter(){
		
		public void linkActivated(HyperlinkEvent e) {
			String hyperlinkType = (String)e.getHref();
			String content = e.getLabel();
			if(hyperlinkType.equals("CloneInstance")){
				Object obj = folder.getSelection().getData();
				if(obj instanceof CloneSetWrapper){
					CloneSetWrapper setWrapper = (CloneSetWrapper)obj;
					for(CloneInstance instance: setWrapper.getCloneSet()){
						String instanceString = instance.toString();
						instanceString = instanceString.substring(instanceString.indexOf("(")+1, instanceString.lastIndexOf(")"));
						if(instanceString.equals(content)){
							ViewUtil.openJavaEditorForCloneInstance(instance);
							CloneCodeSnippetView viewpart = (CloneCodeSnippetView)getSite().getWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.CODE_SNIPPET_VIEW);
							if(viewpart != null){
								viewpart.createCodeSections((CloneInstance)instance);								
							}
						}
					}
				}
			}
		}
		
	};
	
	public CloneReportView() {
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
		CloneSets sets = new CloneDetectionFileParser(false, "").getCloneSets();
		cloneSets = SummaryUtil.wrapCloneSets(sets.getCloneList());
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
						try {
							syntacticSetWrapper = new CloneInformationExtractor().extractCounterRelationalDifferencesWithinSyntacticBoundary(syntacticSetWrapper);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
			}
		});
		
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
		
		hookActionsOnBars();
		
		sashForm.setWeights(new int[]{65, 35});
		//getSite().getPage().addSelectionListener(externalEventAwareListener);
	}
	
	private void createColumns(CheckboxTreeViewer viewer){
		String[] titles = {"Name", "#Ins", "Avg LOC", "Diff/LOC"};
		int[] bounds = {110, 50, 60, 70};
		
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
				
				return null;
			}
			
			@Override
			public Image getImage(Object element) {
				if(element instanceof CloneSetWrapper)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
				
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
	
	private void hookActionsOnBars(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		
		Action loadAllCloneSetAction = new Action(){
			public void run(){
				
				CloneSets sets = new CloneDetectionFileParser(false, "").getCloneSets();
				cloneSets = SummaryUtil.wrapCloneSets(sets.getCloneList());
				viewer.setInput(cloneSets);
				
				
				viewer.refresh();
			}
		};
		loadAllCloneSetAction.setText("Refresh");
		loadAllCloneSetAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED))
				);
		
		toolBar.add(loadAllCloneSetAction);
	}

	@Override
	public void setFocus() {
		

	}
	
	public CheckboxTreeViewer getViewer(){
		return this.viewer;
	}

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
		createCloneInstanceInformationSections(form.getBody(), targetObject);
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

	@Override
	protected void createTopicDescription(FormText text, Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createClonePatternDescription(FormText text,
			Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createInvolvedCloneSetsDescription(FormText text,
			Object targetObject) {
		// TODO Auto-generated method stub
		
	}

}
