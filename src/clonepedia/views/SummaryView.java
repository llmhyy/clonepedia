package clonepedia.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.perspective.CloneDiffPerspective;
import clonepedia.views.codesnippet.CloneDiffView;
import clonepedia.views.util.ViewUtil;

public abstract class SummaryView extends ViewPart {

	protected CheckboxTreeViewer viewer;
	protected CTabFolder folder;
	//protected ScrolledForm form;
	
	//protected Tree propertyTree;
	//protected FormText summaryText;
	
	protected FormToolkit toolkit;
	
	protected Text searchText;
	protected Button searchButton;
	
	protected void createSearchBox(Composite parent){
		Composite searchBox = new Composite(parent, SWT.NONE);
		searchBox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		GridData searchBoxData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		searchBoxData.heightHint = 30;
		searchBox.setLayoutData(searchBoxData);
		
		GridLayout searchBoxLayout = new GridLayout();
		searchBoxLayout.numColumns = 2;
		searchBox.setLayout(searchBoxLayout);
		
		searchText = new Text(searchBox, SWT.BORDER);
		FontData searchTextFont = searchText.getFont().getFontData()[0];
		searchTextFont.setHeight(10);
		searchText.setFont(new Font(Display.getCurrent(), searchTextFont));
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addSearchTextListener(searchText);
		
		searchButton = new Button(searchBox, SWT.PUSH);
		GridData buttonData = new GridData(SWT.FILL, SWT.FILL, false, false);
		buttonData.widthHint = 50;
		searchButton.setLayoutData(buttonData);
		searchButton.setText("Go");
		addSearchButtonListener(searchButton);
	}
	
	protected IDoubleClickListener cloneInstanceDoubleClickListener = new IDoubleClickListener() {
	
		@Override
		public void doubleClick(DoubleClickEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			Object element = selection.getFirstElement();
			if (element instanceof CloneInstance) {
				CloneInstance instance = (CloneInstance) element;
				ViewUtil.openJavaEditorForCloneInstace(instance);
			}
			else if(element instanceof CloneSet){
				/*for(CloneInstance instance: (CloneSet)element)
					ViewUtil.openJavaEditorForCloneInstace(instance);*/
			}
			else if(element instanceof CloneSetWrapper){
				/*for(CloneInstance instance: ((CloneSetWrapper)element).getCloneSet())
					ViewUtil.openJavaEditorForCloneInstace(instance);*/
			}
		}
	};

	public void openNewTab(Object selectionObject) {
		
		CTabItem item = getOpenedTabItem(folder, selectionObject);
		if(null != item)
			folder.setSelection(item);
		else{
			Control content = createPropertyPart(folder, selectionObject);
			item = new CTabItem(folder, SWT.CLOSE);
			item.setControl(content);
			item.setText(selectionObject.toString());
			item.setData(selectionObject);
			folder.setSelection(item);
		}
	}
	
	private CTabItem getOpenedTabItem(CTabFolder folder, Object targetObject){
		for(CTabItem item: folder.getItems())
			if(item.getData() == targetObject)
				return item;
		return null;
	}

	protected Control createPropertyPart(Composite parent, Object targetObject){
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		createDescriptionPart(sashForm, targetObject);
		createDetailPart(sashForm, targetObject);
		
		sashForm.setWeights(new int[]{5,5});
		
		return sashForm;
	}

	private void createDescriptionPart(SashForm parent, Object targetObject){
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText(targetObject.toString());
		TableWrapLayout tableLayout = new TableWrapLayout();
		tableLayout.numColumns = 1;
		tableLayout.verticalSpacing = 10;
		form.getBody().setLayout(tableLayout);
		
		createSections(form, targetObject);
		/*createTextDescriptionSection(form.getBody(), targetObject);
		createInvolvedCloneSetsDescriptionSection(form.getBody(), targetObject);
		createTopicDescriptionSection(form.getBody(), targetObject);
		createClonePatternDescriptionSection(form.getBody(), targetObject);*/
	}
	
	abstract protected void createSections(ScrolledForm form, Object targetObject);

	protected void createTextDescriptionSection(Composite parent, Object targetObject){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Summary");
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createTextDescription(text, targetObject);
		section.setClient(text);
	}
	
	protected void createTopicDescriptionSection(Composite parent, Object targetObject){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Related Topics");
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createTopicDescription(text, targetObject);
		section.setClient(text);
	}
	
	protected void createClonePatternDescriptionSection(Composite parent, Object targetObject){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Related Clone Patterns");
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createClonePatternDescription(text, targetObject);
		section.setClient(text);
	}
	
	protected void createInvolvedCloneSetsDescriptionSection(Composite parent, Object targetObject){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText("Summary of Involoved Clone Sets");
		
		FormText text = toolkit.createFormText(section, true);
		setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createInvolvedCloneSetsDescription(text, targetObject);
		section.setClient(text);
	}
	
	protected void setFormTextColorAndFont(FormText text){
		FormColors colors = toolkit.getColors();
		colors.createColor("OntologicalType", colors.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		colors.createColor("OntologicalRelation", colors.getSystemColor(SWT.COLOR_DARK_GREEN));
		colors.createColor("Name", colors.getSystemColor(SWT.COLOR_RED));
		
		colors.createColor("Class", colors.getSystemColor(SWT.COLOR_BLUE));
		colors.createColor("Interface", colors.getSystemColor(SWT.COLOR_DARK_GREEN));
		colors.createColor("Method", colors.getSystemColor(SWT.COLOR_RED));
		colors.createColor("Field", colors.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		colors.createColor("Variable", colors.getSystemColor(SWT.COLOR_DARK_CYAN));
		colors.createColor("MergeableElement", colors.getSystemColor(SWT.COLOR_DARK_RED));
		//colors.createColor("Name", colors.getSystemColor(SWT.COLOR_RED));
		
		
		text.setColor("OntologicalType", colors.getColor("OntologicalType"));
		text.setColor("OntologicalRelation", colors.getColor("OntologicalRelation"));
		text.setColor("Name", colors.getColor("Name"));
		text.setColor("Class", colors.getColor("Class"));
		text.setColor("Interface", colors.getColor("Interface"));
		text.setColor("Method", colors.getColor("Method"));
		text.setColor("Field", colors.getColor("Field"));
		text.setColor("Variable", colors.getColor("Variable"));
		text.setColor("MergeableElement", colors.getColor("MergeableElement"));
		
		text.setFont("bold", JFaceResources.getHeaderFont());
	}
	
	protected void createDetailPart(SashForm parent, Object targetObject){
		Group detailGroup = new Group(parent, SWT.NONE| SWT.H_SCROLL | SWT.V_SCROLL);
		detailGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		detailGroup.setText("Details");
		detailGroup.setLayout(new FillLayout());
		
		final Tree propertyTree = new Tree(detailGroup, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		propertyTree.setHeaderVisible(true);
		propertyTree.setLinesVisible(true);
		
		TreeColumn titleColumn = new TreeColumn(propertyTree, SWT.NONE);
		titleColumn.setText("Title");
		TreeColumn contentColumn = new TreeColumn(propertyTree, SWT.NONE);
		contentColumn.setText("Content");
		titleColumn.setWidth(60);
		contentColumn.setWidth(600);
		
		titleColumn.setResizable(true);
		contentColumn.setResizable(true);
		
		createTreeDetails(propertyTree, targetObject);
		
		//parent.setWeights(new int[]{3,1});
		
		propertyTree.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem item = propertyTree.getItem(new Point(e.x, e.y));
				if(item != null){					
					Object domainObj = item.getData();
					if(domainObj instanceof Path){
						Path path = (Path)domainObj;
						CloneInstance instance = (CloneInstance)path.get(0);
						ViewUtil.openJavaEditorForCloneInstace(instance);
					}
				}
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				TreeItem item = propertyTree.getItem(new Point(e.x, e.y));
				if(item != null){					
					Object domainObj = item.getData();
					if(domainObj instanceof DiffCounterRelationGroupEmulator){
						DiffCounterRelationGroupEmulator group = (DiffCounterRelationGroupEmulator)domainObj;
						CloneDiffView diffViewPart = (CloneDiffView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.CLONE_DIFF_VIEW);
						CloneSet set = group.getRelations().get(0).getInstanceWrapper().getCloneInstance().getCloneSet();
						diffViewPart.showCodeSnippet(set, group);
						
						
						DiffPropertyView propertyViewPart = (DiffPropertyView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.DIFF_PROPERTY_VIEW);
						propertyViewPart.showDiffInformation(group);
					}
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {}
			
		});
	}
	
	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		Action refresh = new Action() {
			public void run() {
				
			}
		};
		refresh.setText("Clone All");
		menuMgr.add(refresh);
	}
	//abstract protected void createSummaryDescription(FormText text, Object targetObject);
	
	abstract protected void createTextDescription(FormText text, Object targetObject);

	abstract protected void createTopicDescription(FormText text, Object targetObject);

	abstract protected void createClonePatternDescription(FormText text,
			Object targetObject);

	abstract protected void createInvolvedCloneSetsDescription(FormText text,
			Object targetObject);

	abstract protected void createTreeDetails(Tree tree, Object targetObject);
	
	abstract protected void addSearchButtonListener(final Button searchButton);
	
	abstract protected void addSearchTextListener(final Text searchText);
}
