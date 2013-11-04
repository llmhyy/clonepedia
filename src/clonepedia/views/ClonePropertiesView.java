package clonepedia.views;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.summary.NaturalLanguateTemplate;
import clonepedia.summary.PatternClusteringer;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.views.util.ViewUtil;
@Deprecated
public class ClonePropertiesView extends ViewPart {

	private Object selectionObject = new Object();
	//private HashMap<TreeItem, Object> domainMap = new HashMap<TreeItem, Object>();
	
	//private TableViewer tableViewer;
	private Tree tree;
	private Text summaryText;
	ISelectionListener listener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				selectionObject = ss.getFirstElement();
				//tree.clearAll();
				tree.removeAll();
				summaryText.setText("");
				//domainMap.clear();
				//tree.remove(0, tree.getItemCount()-1);
				try {
					updateView();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*updateTable(tree.getParent());
				tree.getParent().update();
				tree.update();*/
			}
			System.out.print("");
		}

	};
	
	public ClonePropertiesView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//getSite().getPage().addSelectionListener("CloneMiner.SummaryView",listener);
		parent.setLayout(new FillLayout());
		Composite panel = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		//gridLayout.fill = true;
		//gridLayout.type = SWT.VERTICAL;
		/*gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginBottom = 5;*/
		
		panel.setLayout(gridLayout);
		
		GridData summaryGridData = new GridData(); 
		summaryGridData.horizontalAlignment = GridData.FILL;
		summaryGridData.grabExcessHorizontalSpace = true;
		summaryGridData.heightHint = 80;
		//summaryGridData.grabExcessVerticalSpace = true;
		
		Group summaryGroup = new Group(panel, SWT.NONE| SWT.H_SCROLL | SWT.V_SCROLL);
		summaryGroup.setText("Summary");
		summaryGroup.setLayoutData(summaryGridData);
		summaryGroup.setLayout(new FillLayout());
		
		
		summaryText = new Text(summaryGroup, SWT.MULTI| SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		summaryText.setEditable(false);
		//summary.setText("test");
		
		
		GridData detailGridData = new GridData(); 
		detailGridData.horizontalAlignment = GridData.FILL;
		detailGridData.grabExcessHorizontalSpace = true;
		detailGridData.heightHint = 250;
		
		Group detailGroup = new Group(panel, SWT.NONE| SWT.H_SCROLL | SWT.V_SCROLL);
		
		detailGroup.setText("Details");
		detailGroup.setLayoutData(detailGridData);
		detailGroup.setLayout(new FillLayout());
		fillTree(detailGroup);
		
		getSite().getPage().addSelectionListener(listener);
		tree.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem item = tree.getItem(new Point(e.x, e.y));
				//Object domainObj = domainMap.get(item);
				Object domainObj = item.getData();
				if(domainObj instanceof Path){
					Path path = (Path)domainObj;
					CloneInstance instance = (CloneInstance)path.get(0);
					ViewUtil.openJavaEditorForCloneInstance(instance);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private void updateTree() throws Exception{
		if((selectionObject instanceof SemanticCluster) ||
				(selectionObject instanceof TopicWrapper))
			createPropertiesForSemanticCluster(tree, selectionObject);
		else if(selectionObject instanceof SyntacticCluster)
			createTreeItemsForSyntacticCluster(tree, selectionObject);
		else if((selectionObject instanceof CloneSet) ||
				(selectionObject instanceof CloneSetWrapper))
			createTreeItemsForCloneSet(tree, selectionObject);
		else if(selectionObject instanceof CloneInstance)
			createPropertiesForCloneInstance(tree, selectionObject);
		else if(selectionObject instanceof ClonePatternGroupWrapper)
			createPropertiesForClonePattern(tree, selectionObject);
	}
	
	private void updateView() throws Exception{
		updateSummaryText();
		updateTree();
	}
	
	private void updateSummaryText() {
		// TODO Auto-generated method stub
		
	}

	private void fillTree(Composite parent){
		/*tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree tree = tableViewer.getTable();*/
		tree = new Tree(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeColumn titleColumn = new TreeColumn(tree, SWT.NONE);
		titleColumn.setText("Title");
		TreeColumn contentColumn = new TreeColumn(tree, SWT.NONE);
		contentColumn.setText("Content");
		titleColumn.setWidth(180);
		contentColumn.setWidth(600);
		
		titleColumn.setResizable(true);
		contentColumn.setResizable(true);
		/*try {
			updateTree();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	private void createPropertiesForSemanticCluster(Tree tree, Object obj){
		String label;
		if(obj instanceof SemanticCluster)
			label = ((SemanticCluster)obj).getLabel();
		else if(obj instanceof TopicWrapper)
			label = ((TopicWrapper)obj).getTopic().getTopicString();
		else 
			return;
		
		createSimpleTreeItem(tree, "Labels:", label, 0, obj);
		createSemanticSummary(summaryText, obj);
	}
	
	private void createPropertiesForCloneInstance(Tree tree, Object obj){
		
		CloneInstance instance = (CloneInstance)obj;
		
		createSimpleTreeItem(tree, "Instance Id:", instance.getId(), 0);
		createSimpleTreeItem(tree, "Residing Method:", instance.getResidingMethod().toString(), 1);
		createSimpleTreeItem(tree, "File Location:", instance.getFileLocation(), 2);
		createSimpleTreeItem(tree, "Start Line:", String.valueOf(instance.getStartLine()), 3);
		createSimpleTreeItem(tree, "End Line:", String.valueOf(instance.getEndLine()), 4);
		
		createCloneInstanceSummary(summaryText, obj);
	}

	private void createPropertiesForClonePattern(Tree tree, Object obj){
		TreeItem item = createSimpleTreeItem(tree, "Patterns", "Corresponding Clone Sets", new Object());
		
		ClonePatternGroup cp;
		if(obj instanceof ClonePatternGroup)
			cp = (ClonePatternGroup)obj;
		else if(obj instanceof ClonePatternGroupWrapper)
			cp = ((ClonePatternGroupWrapper)obj).getClonePattern();
		else
			return;
		HashMap<String, HashSet<CloneSet>> map = generatePathPatternCloneSetsMap(cp);
		for(String keyPath: map.keySet()){
			
			HashSet<CloneSet> sets = map.get(keyPath);
			String content = "";
			for(CloneSet set: sets)
				content += set.getId() + ", ";
			content = content.substring(0, content.length()-2);
			
			createSubTreeItem(item, keyPath, content, sets);
		}
		
		item.setExpanded(true);
		createClonePatternSummary(summaryText, obj);
	}

	private void createClonePatternSummary(Text summaryText2, Object obj) {
		//summaryText.setText(NaturalLanguateTemplate.generateSummary(obj));
	}

	private void createCloneInstanceSummary(Text summaryText2, Object obj) {
		//summaryText.setText(NaturalLanguateTemplate.generateSummary(obj));
	}

	private void createSemanticSummary(Text summaryText, Object obj) {
		//summaryText.setText(NaturalLanguateTemplate.generateSummary(obj));
	}

	private void createTreeItemsForCloneSet(Tree tree, Object obj) throws Exception{
		
		CloneSet set;
		if(obj instanceof CloneSet)
			set = (CloneSet)obj;
		else if(obj instanceof CloneSetWrapper)
			set = ((CloneSetWrapper)obj).getCloneSet();
		else
			return;
		
		createSimpleTreeItem(tree, "Instance Number:", String.valueOf(set.size()), new Object());
		TreeItem patternsItem = createSimpleTreeItem(tree, "Patterns:", "", new Object());
		
		//int ppIndex = 0;
		for(PathPatternGroup ppg: set.getLocationPatterns()){
			/*TreeItem abSeqItem = new TreeItem(patternItem, SWT.NONE);
			abSeqItem.setText(new String[]{"Pattern: ", ppg.getAbstractPathSequence().toString()});*/
			
			TreeItem abSeqItem = createSubTreeItem(patternsItem, "Pattern:", ppg.getAbstractPathSequence().toString(), ppg);
			abSeqItem.setFont(1, new Font(Display.getCurrent(), "Arial",9, SWT.BOLD));
			//int pIndex = 0;
			for(Path path: ppg){
				createSubTreeItem(abSeqItem, "Path:", path.toString(), path);
				//System.out.println(i);
				
			}
			
		}
		
		patternsItem.setExpanded(true);
		
		TreeItem countersItem = createSimpleTreeItem(tree, "Counter Relations:", "", new Object());
		for(CounterRelationGroup group: set.getCounterRelationGroups()){
			TreeItem counterItem = createSubTreeItem(countersItem, "Counter Relation:", "", group);
			int index = 0;
			for(InstanceElementRelation relation: group.getRelationList()){
				String content = relation.getCloneInstance().getId() + " " + 
						SyntacticUtil.identifyOntologicalRelation(relation.getCloneInstance(), relation.getElement()).toString() + " " +
						relation.getElement().toString();
				
				createSubTreeItem(counterItem, String.valueOf(++index), content, relation);
			}
		}
		
		//countersItem.setExpanded(true);
	}
	
	private void createTreeItemsForSyntacticCluster(Tree tree, Object obj){
		SyntacticCluster syntacticCluster = (SyntacticCluster)obj;
		try {
			ArrayList<ClonePatternGroup> clusters = syntacticCluster.getClonePatterns();
			
			for(ClonePatternGroup pc: clusters){
				TreeItem titleItem = createSimpleTreeItem(tree, "Patterns", "Corresponding Clone Sets", pc);
				
				HashMap<String, HashSet<CloneSet>> map = generatePathPatternCloneSetsMap(pc);
				for(String keyPath: map.keySet()){
					
					HashSet<CloneSet> sets = map.get(keyPath);
					String content = "";
					for(CloneSet set: sets)
						content += set.getId() + ", ";
					content = content.substring(0, content.length()-2);
					
					createSubTreeItem(titleItem, keyPath, content, sets);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private TreeItem createSimpleTreeItem(Tree tree, String title, String content, int index, Object domainObj){
		TreeItem item = new TreeItem(tree, SWT.NONE, index);
		item.setText(new String[]{title, content});
		//domainMap.put(item, domainObj);
		item.setData(domainObj);
		return item;
	}
	
	private TreeItem createSimpleTreeItem(Tree tree, String title, String content, Object domainObj){
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText(new String[]{title, content});
		//domainMap.put(item, domainObj);
		item.setData(domainObj);
		return item;
	}
	
	private TreeItem createSubTreeItem(TreeItem item, String title, String content, Object domainObj){
		TreeItem i = new TreeItem(item, SWT.NONE);
		i.setText(new String[]{title, content});
		//domainMap.put(item, domainObj);
		i.setData(domainObj);
		return i;
	}
	
	private TreeItem createSubTreeItem(TreeItem item, String title, String content, int index, Object domainObj){
		TreeItem i = new TreeItem(item, SWT.NONE, index);
		i.setText(new String[]{title, content});
		//domainMap.put(item, domainObj);
		i.setData(domainObj);
		return i;
	}
	
	private HashMap<String, HashSet<CloneSet>> generatePathPatternCloneSetsMap(ClonePatternGroup pc){
		HashMap<String, HashSet<CloneSet>> map = new HashMap<String, HashSet<CloneSet>>();
		for(PathPatternGroup ppg: pc){
			String keyPath = ppg.getAbstractPathSequence().toString();
			HashSet<CloneSet> sets = map.get(keyPath);
			if(sets == null){
				HashSet<CloneSet> setList = new HashSet<CloneSet>();
				setList.add(ppg.getCloneSet());
				map.put(keyPath	, setList);
			}
			else
				sets.add(ppg.getCloneSet());
		}
		return map;
	}

	/*private void addTreeItemListener(TreeItem item){
		item.addListener(SWT.MouseDoubleClick, new Listener(){

			@Override
			public void handleEvent(Event event) {
				Object obj = event.data;
				System.out.println(obj);
			}
			
		});
		
		item.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				Object obj = event.data;
				System.out.println(obj);
			}
			
		});
	}*/
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		ISelectionService s = getSite().getWorkbenchWindow()
				.getSelectionService();
		s.removeSelectionListener(listener);
		super.dispose();
	}

}
