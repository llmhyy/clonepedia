package clonepedia.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.Diff;
import clonepedia.java.model.DiffElement;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.Settings;

public class DiffPropertyView extends ViewPart {

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Composite parentComposite;
	
	private Diff diff;
	private clonepedia.java.model.CloneSetWrapper syntacticSetWrapper;
	
	private TreeViewer viewer;
	private HashMap<TypeNode, ArrayList<DiffElement>> relationMap;
	
	public DiffPropertyView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		this.toolkit = new FormToolkit(parent.getDisplay());
		this.form = toolkit.createScrolledForm(parent);
		this.form.setText("Diff Property");
		
		this.parentComposite = parent;
		
		TableWrapLayout tableLayout = new TableWrapLayout();
		tableLayout.numColumns = 1;
		tableLayout.verticalSpacing = 10;
		
		//this.form.getBody().setRedraw(true);
		this.form.getBody().setLayout(tableLayout);
		
	}
	
	public void showDiffInformation(Diff diff){
		this.diff = diff;
		for(Control control: this.form.getBody().getChildren()){
			if(control instanceof Section){
				control.dispose();
			}
		}
		
		
		createDiffSections(this.form);			
		
		this.form.getBody().redraw();
		this.form.getBody().update();
	}
	
	public void showDiffInformation(Diff diff, clonepedia.java.model.CloneSetWrapper syntacticSetWrapper){
		this.syntacticSetWrapper = syntacticSetWrapper;
		showDiffInformation(diff);
	}

	private void createDiffSections(Composite parent) {
		createCloneSetDiffSummary(form.getBody());
		if(this.diff != null){
			createDiffTypeSection(form.getBody());
			createDiffSyntacticSection(form.getBody());
		}
		
		createEmptySection(parent);
		
	}
	
	public class StructureLabelProvider extends ColumnLabelProvider{

		public String getText(Object element) {
			if(element instanceof TypeNode){
				TypeNode node = (TypeNode)element;
				if(node.binding.isClass() || node.binding.isInterface() || node.binding.isPrimitive()){
					return node.binding.getName();
				}
			}
			else if(element instanceof DiffElement){
				DiffElement relation = (DiffElement)element;
				return MinerUtilforJava.getConcernedASTNodeName(relation.getNode());
			}
			return null;
		}
		
		@SuppressWarnings("restriction")
		@Override
		public Image getImage(Object element) {
			
			if(element instanceof TypeNode){
				TypeNode node = (TypeNode)element;
				if(node.binding.isClass()){
					return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
				}
				else if(node.binding.isInterface()){
					return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
				}
				else if(node.binding.isPrimitive()){
					return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CUNIT);
				}
				else{
					return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_BREAKPOINT_INSTALLED);
				}
			}
			else if(element instanceof DiffElement){
				ASTNode node = ((DiffElement)element).getNode();
				
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding(); 
					if(binding instanceof ITypeBinding){
						ITypeBinding tBinding = (ITypeBinding)binding;
						if(tBinding.isClass()){
							return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS_DEFAULT);						
						}
						else if(tBinding.isInterface()){
							return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE_DEFAULT);						
						}
						else if(tBinding.isPrimitive()){
							return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CUNIT);						
						}
						else{
							return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_BREAKPOINT_INSTALLED);						
						}
					}
					else if(binding instanceof IMethodBinding){
						return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ENV_VAR);	
					}
					else if(binding instanceof IVariableBinding){
						return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);	
					}
					else{
						return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CAST);	
					}
				}
				else{
					return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CAST);	
				}
			}
			return null;
		}
		
	}

	public class ColumnContentProvider implements ITreeContentProvider{

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
			if(inputElement instanceof HashMap){
				HashMap<ITypeBinding, TypeNode> nodes = (HashMap<ITypeBinding, TypeNode>)inputElement;
				
				ArrayList<TypeNode> nodeList = new ArrayList<DiffPropertyView.TypeNode>();
				for(TypeNode node: nodes.values()){
					if(node.parents.size() == 0){
						nodeList.add(node);
					}
				}
				
				return nodeList.toArray(new Object[0]);
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof TypeNode){
				TypeNode node = (TypeNode)parentElement;
				
				ArrayList<DiffElement> relations = relationMap.get(node); 
				if(node.children.size() > 0 || relations != null){
					
					if(relations != null){
						System.out.println();
					}
					
					int increment = (relations == null) ? 0 : relations.size();
					int size = node.children.size() + increment;
					Object[] list = new Object[size];
					
					int i=0;
					for(TypeNode tn: node.children){
						list[i++] = tn;
					}
					
					if(relations != null){
						for(DiffElement relation: relations){
							list[i++] = relation;
						}
					}
					
					return list;
				}
				
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof DiffElement){
				return false;
			}
			return true;
		}
		
	}
	
	private void createEmptySection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
	}
	
	private void createDiffSyntacticSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		
		section.setText("Syntactic Information");
		
		Tree tree = toolkit.createTree(section, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new ColumnContentProvider());
		this.viewer.setLabelProvider(new StructureLabelProvider());
		this.viewer.setInput(constructHierarchicalTree());
		/*ArrayList<DiffInstanceElementRelationEmulator> relList = new ArrayList<DiffInstanceElementRelationEmulator>();
		for(ArrayList<DiffInstanceElementRelationEmulator> list: this.relationMap.values()){
			relList.addAll(list);
		}
		this.viewer.setExpandedElements(relList.toArray(new Object[0]));*/
		
		/*TreeColumn instanceColumn = new TreeColumn(tree, SWT.NONE);
		instanceColumn.setText("Clone Instance");
		TreeColumn operationColumn = new TreeColumn(tree, SWT.NONE);
		operationColumn.setText("Operation");
		TreeColumn elementColumn = new TreeColumn(tree, SWT.NONE);
		elementColumn.setText("Element");
		
		instanceColumn.setWidth(300);
		operationColumn.setWidth(80);
		elementColumn.setWidth(350);
		
		instanceColumn.setResizable(true);
		operationColumn.setResizable(true);
		elementColumn.setResizable(true);*/
		
		if(diff != null){
			createColumns(viewer);
			//createTreeItemsForSingleDiff(tree, diff);			
		}
		
		this.viewer.expandAll();
		tree.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setClient(tree);
	}
	
	private void createColumns(TreeViewer viewer){
		String[] titles = {"Type", "Instance"};
		int[] bounds = {300, 500};
		
		TreeViewerColumn typeCol = createTreeColumn(viewer, titles[0], bounds[0], 0);
		typeCol.setLabelProvider(new StructureLabelProvider());
		
		/*TreeViewerColumn elementCol = createTreeColumn(viewer, titles[1], bounds[1], 1);
		elementCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof DiffInstanceElementRelationEmulator){
					DiffInstanceElementRelationEmulator relation = (DiffInstanceElementRelationEmulator)element;
					return MinerUtilforJava.getConcernedASTNodeName(relation.getNode());
				}
					
				return null;
			}
		});*/
		
		TreeViewerColumn instanceCol = createTreeColumn(viewer, titles[1], bounds[1], 1);
		instanceCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof DiffElement){
					DiffElement relation = (DiffElement)element;
					return relation.getInstanceWrapper().getCloneInstance().toString();
				}
				return null;
			}
		});
		
	}
	
	private TreeViewerColumn createTreeColumn(TreeViewer viewer, String title, int bound, final int columnNumber){
		TreeViewerColumn viewerCol = new TreeViewerColumn(viewer, SWT.NONE);
		TreeColumn col = viewerCol.getColumn();
		col.setText(title);
		col.setWidth(bound);
		col.setResizable(true);
		col.setMoveable(true);
		return viewerCol;
	}

	private void createTreeItemsForSingleDiff(Tree tree, Diff diff){
		for(DiffElement tuple: diff.getElements()){
			TreeItem item = new TreeItem(tree, SWT.NONE);
			
			CloneInstance instance = tuple.getInstanceWrapper().getCloneInstance();
			String instanceContent =  instance.toString();
			
			try {
				ProgrammingElement element = MinerUtilforJava.transferASTNodesToProgrammingElementType(tuple.getNode());
				String instanceString = instanceContent.substring(instanceContent.indexOf("(")+1, instanceContent.lastIndexOf("]")+1);
				String operationString = SyntacticUtil.identifyOntologicalRelation(instance, element).toString();
				String elementString = element.toString();
				
				item.setText(new String[]{instanceString, operationString, elementString});
				item.setData(diff);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	private void createCloneSetDiffSummary(Composite parent){
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		
		section.setText("Overal Summary for Diffs");
		
		FormText text = this.toolkit.createFormText(section, true);
		text.setText(generateOverallDiffDescription(), true, false);
		section.setClient(text);
	}
	
	private String generateOverallDiffDescription(){
		String word1 = "<form>There are "; 
		String word2 = " differential multisets in this clone set.";
		String word3 = "The differences are ";; 
		String word4 = "generally regular</form>";
		
		return word1 + this.syntacticSetWrapper.getPatternNodeSets().size() + word2 + word3 + word4;
	}

	private void createDiffTypeSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		
		section.setText("Distribution");
		
		FormText text = this.toolkit.createFormText(section, true);
		text.setText(generateSingleDiffDescription(), true, false);
		section.setClient(text);
	}
	
	private String generateSingleDiffDescription(){
		
		HashMap<String, ArrayList<CloneInstance>> bucketSet = reorgnizeCRD();
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		/**
		 * the element list with maximum size
		 */
		ArrayList<String> elementKeyList = new ArrayList<String>();
		
		for(String key: bucketSet.keySet()){
			int size = bucketSet.get(key).size();
			list.add(size);
			
			if(elementKeyList.size() == 0){
				elementKeyList.add(key);
			}
			else{
				String referElement = elementKeyList.get(0);
				int referSize = bucketSet.get(referElement).size();
				if(referSize < size){
					elementKeyList.clear();
					elementKeyList.add(key); 
				}
				else if(size == referSize){
					elementKeyList.add(key);
				}
			}
		}
		
		int totalSize = diff.getElements().get(0).getInstanceWrapper().getCloneInstance().getCloneSet().size();
		int differSize = totalSize - diff.getElements().size(); 
		if(differSize > 0){
			list.add(differSize);
		}
		
		String type = getDistributionType(list);
		StringBuffer content = new StringBuffer();
		content.append("<form><p>");
		content.append("It is a ");
		content.append(type);
		content.append(" diff.");
		content.append("</p>");
		
		String totalNumString = String.valueOf(totalSize);
		if(type.equals("all-parameterized")){	
			int average = diff.getElements().size()/bucketSet.size();
			content.append("Each ");
			content.append(String.valueOf(average));
			content.append(" of ");
			content.append(totalNumString);
			content.append(" clone instances use different element");
			if(differSize > 0){
				content.append(", and " + differSize + " instance(s) missed in such a counter difference");
			}
			content.append("<br/>");
		}
		else{
			
			for(String elementKey: elementKeyList){
				String keyNumString = String.valueOf(bucketSet.get(elementKey).size());
				
				content.append(keyNumString);
				content.append(" of ");
				content.append(totalNumString);
				content.append(" clone instances in diff use ");
				content.append(elementKey);
				
				if(differSize > 0){
					content.append(", and " + differSize + " instance(s) missed in such a counter difference");
				}
				
				content.append("<br/>");
			}
			
		}
		
		content.append("</form>");
		return content.toString();
	}
	
	private double getStandardDeviation(ArrayList<Integer> list){
		double sequareSum = 0;
		double sum = 0;
		double size = list.size();
		
		for(Integer i: list){
			sum += i;
			sequareSum += i*i;
		}
		
		double average = sum/size;
		double standardDeviation = Math.sqrt((sequareSum/size)-average*average);
		
		return standardDeviation;
	}
	
	private String getDistributionType(ArrayList<Integer> list){
		double standardDeviation = getStandardDeviation(list);
		
		if(standardDeviation == Settings.thresholdForEvenDistribution){
			return "all-parameterized";
		}
		else if(standardDeviation > Settings.thresholdForSlightPartial){
			return "partial";
		}
		else{
			return "slightly partial";
		}
		
	}

	/**
	 * return a map like token-instanceList. Noted that several clone instances may share same
	 * tokens in a counter difference.
	 * @return
	 */
	private HashMap<String, ArrayList<CloneInstance>> reorgnizeCRD(){
		HashMap<String, ArrayList<CloneInstance>> bucketSet = new HashMap<String, ArrayList<CloneInstance>>();
		if(this.diff != null){
			for(DiffElement tuple: diff.getElements()){
				
				try {
					CloneInstanceWrapper instanceWrapper = tuple.getInstanceWrapper();
					CloneInstance instance = instanceWrapper.getCloneInstance();
					
					ASTNode node = tuple.getNode();
					if(node != null){
						ProgrammingElement element = MinerUtilforJava.transferASTNodesToProgrammingElementType(node);
						if(element != null){
							String elementString = element.toString();
							
							ArrayList<CloneInstance> instanceList = bucketSet.get(elementString);
							if(instanceList == null){
								instanceList = new ArrayList<CloneInstance>();
								instanceList.add(instance);
								bucketSet.put(elementString, instanceList);
							}
							else{
								instanceList.add(instance);
							}
						}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return bucketSet;
	}
	
	public class TypeNode{
		public ITypeBinding binding;
		public HashSet<TypeNode> children = new HashSet<TypeNode>();
		public HashSet<TypeNode> parents = new HashSet<TypeNode>();
		
		public TypeNode(ITypeBinding binding){
			this.binding = binding;
		}
		
		public int hashCode(){
			return binding.getQualifiedName().hashCode();
		}
		
		public boolean equals(Object obj){
			if(obj instanceof TypeNode){
				TypeNode tn = (TypeNode)obj;
				return this.binding.getQualifiedName().equals(tn.binding.getQualifiedName());
			}
			return false;
		}
		
		public String toString(){
			return this.binding.getQualifiedName();
		}
	}
	
	private HashMap<String, TypeNode> constructHierarchicalTree(){
		
		HashMap<String, TypeNode> nodes = new HashMap<String, TypeNode>();
		
		this.relationMap = new HashMap<TypeNode, ArrayList<DiffElement>>();
		
		HashSet<TypeNode> tmpSet = new HashSet<TypeNode>();
		for(DiffElement relation: diff.getElements()){
			ASTNode node = relation.getNode();
			if(node != null){
				ITypeBinding binding = MinerUtilforJava.getBinding(node);
				
				if(binding != null){
					TypeNode typeNode = new TypeNode(binding);
					
					tmpSet.add(typeNode);
					
					ArrayList<DiffElement> relationList = relationMap.get(typeNode);
					if(relationList == null){
						relationList = new ArrayList<DiffElement>();
						relationMap.put(typeNode, relationList);
					}
					relationList.add(relation);
					
					nodes.put(binding.getQualifiedName(), typeNode);
					
				}
			}
			
			
		}
		
		while(tmpSet.size() != 0){
			tmpSet = buildParentSets(tmpSet, nodes);
			
			/*for(TypeNode tn: tmpSet){
				if(!nodes.containsKey(tn.binding)){
					nodes.put(tn.binding, tn);
				}
			}*/
			//System.out.println();
		}
		
		return nodes;
	}
	
	private HashSet<TypeNode> buildParentSets(HashSet<TypeNode> tmpSet, 
			HashMap<String, TypeNode> nodes){
		
		HashSet<TypeNode> parentSet = new HashSet<TypeNode>();
		for(TypeNode node: tmpSet){
			
			node = nodes.get(node.binding.getQualifiedName());
			ITypeBinding binding = node.binding;
			
			ITypeBinding superClassBinding = binding.getSuperclass();
			ITypeBinding[] interfaceBindingList = binding.getInterfaces();
			
			if(superClassBinding != null){
				TypeNode parentNode = nodes.get(superClassBinding.getQualifiedName());
				if(parentNode == null){
					parentNode = new TypeNode(superClassBinding);
				}
				
				parentNode.children.add(node);
				node.parents.add(parentNode);
				parentSet.add(parentNode);
				nodes.put(parentNode.binding.getQualifiedName(), parentNode);
			}
			
			
			for(ITypeBinding interfaceBinding: interfaceBindingList){
				TypeNode parentNode = nodes.get(interfaceBinding.getQualifiedName());
				if(parentNode == null){
					parentNode = new TypeNode(interfaceBinding);
				}
				
				parentNode.children.add(node);
				node.parents.add(parentNode);
				parentSet.add(parentNode);
				nodes.put(parentNode.binding.getQualifiedName(), parentNode);
			}
		}
		
		return parentSet;
	}
	
	@Override
	public void setFocus() {
		
		

	}

}
