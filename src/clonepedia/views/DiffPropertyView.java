package clonepedia.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.syntactic.util.SyntacticUtil;

public class DiffPropertyView extends ViewPart {

	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private DiffCounterRelationGroupEmulator diff;
	
	public DiffPropertyView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		this.toolkit = new FormToolkit(parent.getDisplay());
		this.form = toolkit.createScrolledForm(parent);
		this.form.setText("Diff Property");
		
		TableWrapLayout tableLayout = new TableWrapLayout();
		tableLayout.numColumns = 1;
		tableLayout.verticalSpacing = 10;
		
		this.form.getBody().setLayout(tableLayout);
	}
	
	public void showDiffInformation(DiffCounterRelationGroupEmulator diff){
		this.diff = diff;
		for(Control control: this.form.getBody().getChildren()){
			if(control instanceof Section){
				control.dispose();
			}
		}
		createDiffSections(this.form);
		this.form.getBody().pack();
	}

	private void createDiffSections(Composite parent) {
		createDiffTypeSection(form.getBody());
		createDiffSyntacticSection(form.getBody());
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
		
		TreeColumn instanceColumn = new TreeColumn(tree, SWT.NONE);
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
		elementColumn.setResizable(true);
		
		if(diff != null){
			createTreeItemsForSingleDiff(tree, diff);			
		}
		
		tree.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setClient(tree);
		
	}

	private void createTreeItemsForSingleDiff(Tree tree, DiffCounterRelationGroupEmulator diff){
		for(DiffInstanceElementRelationEmulator tuple: diff.getRelations()){
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

	private void createDiffTypeSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		
		section.setText("Description");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
