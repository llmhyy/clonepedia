package clonepedia.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
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

import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.syntactic.util.SyntacticUtil;
import clonepedia.util.Settings;

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
		
		section.setText("Distribution");
		
		FormText text = this.toolkit.createFormText(section, true);
		text.setText(generateDescription(), true, false);
		section.setClient(text);
	}
	
	private String generateDescription(){
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
		
		String type = getDistributionType(list);
		StringBuffer content = new StringBuffer();
		content.append("<form><p>");
		content.append("It is a ");
		content.append(type);
		content.append(" diff.");
		content.append("</p>");
		
		String totalNumString = String.valueOf(diff.getRelations().size());
		if(type.equals("even")){	
			int average = diff.getRelations().size()/bucketSet.size();
			content.append("Each ");
			content.append(String.valueOf(average));
			content.append(" of ");
			content.append(totalNumString);
			content.append(" clone instances use different element");
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
			return "even";
		}
		else if(standardDeviation > Settings.thresholdForSlightPartial){
			return "partial";
		}
		else{
			return "slightly partial";
		}
		
	}

	private HashMap<String, ArrayList<CloneInstance>> reorgnizeCRD(){
		HashMap<String, ArrayList<CloneInstance>> bucketSet = new HashMap<String, ArrayList<CloneInstance>>();
		if(this.diff != null){
			for(DiffInstanceElementRelationEmulator tuple: diff.getRelations()){
				try {
					CloneInstanceWrapper instanceWrapper = tuple.getInstanceWrapper();
					CloneInstance instance = instanceWrapper.getCloneInstance();
					
					ASTNode node = tuple.getNode();
					ProgrammingElement element = MinerUtilforJava.transferASTNodesToProgrammingElementType(node);
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
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return bucketSet;
	}
	
	@Override
	public void setFocus() {
		
		

	}

}
