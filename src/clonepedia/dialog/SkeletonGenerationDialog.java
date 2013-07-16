package clonepedia.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import clonepedia.model.ontology.Class;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.wizard.SkeletonGenerationWizard;

public class SkeletonGenerationDialog extends TitleAreaDialog {
	
	private ClonePatternGroupWrapper cpgWrapper;
	
	private Table candiateIntraPatternList;
	private Table selectedIntraPatternList;
	
	private Button addPatternButton;
	private Button removePatternButton;

	public SkeletonGenerationDialog(Shell parentShell, ClonePatternGroupWrapper cpgWrapper) {
		super(parentShell);
		this.cpgWrapper = cpgWrapper;
	}
	
	public void create() {
		setHelpAvailable(false);
		super.create();
		
		try {
			// Set the title
			setTitle("Code Skeleton Generation:" + this.cpgWrapper.getEpitomise3());
			// Set the message
			setMessage("Please customize your skeleton generating process", 
					IMessageProvider.INFORMATION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = new Composite(parent, SWT.BORDER);
		area.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout areaLayout = new GridLayout();
		areaLayout.numColumns = /*1*/3;
		areaLayout.horizontalSpacing = 10;
		areaLayout.marginHeight = 10;
		areaLayout.marginBottom = 10;
		areaLayout.marginLeft = 10;
		areaLayout.marginRight = 10;
		areaLayout.verticalSpacing = 10;
		area.setLayout(areaLayout);
		
		GridData candidateGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		candidateGridData.widthHint = 150;
		candiateIntraPatternList = new Table(area, SWT.BORDER | SWT.FULL_SELECTION);
		candiateIntraPatternList.setLayoutData(candidateGridData);
		candiateIntraPatternList.setHeaderVisible(true);
		candiateIntraPatternList.setLinesVisible(true);
		TableColumn candidateColumn = new TableColumn(candiateIntraPatternList, SWT.NONE);
		candidateColumn.setText("Candidate Intra Pattern");
		for(PathPatternGroup ppg: this.cpgWrapper.getClonePattern()){
			PathPatternGroupWrapper ppgWrapper = new PathPatternGroupWrapper(ppg);
			
			TableItem item = new TableItem(candiateIntraPatternList, SWT.NONE);
			item.setText(ppgWrapper.getEpitomise3());
			item.setData(ppgWrapper);
		}
		candidateColumn.pack();
		
		Composite buttonComposite = new Composite(area, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));
		
		addPatternButton = new Button(buttonComposite, SWT.NONE);
		addPatternButton.setText(">>");
		addPatternButton.addMouseListener(new AddPatternListener());
		removePatternButton = new Button(buttonComposite, SWT.NONE);
		removePatternButton.setText("<<");
		removePatternButton.addMouseListener(new RemovePatternListener());
		
		GridData selectedGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		selectedGridData.widthHint = 150;
		selectedIntraPatternList = new Table(area, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		selectedIntraPatternList.setLayoutData(selectedGridData);
		selectedIntraPatternList.setHeaderVisible(true);
		selectedIntraPatternList.setLinesVisible(true);
		TableColumn selectedColumn = new TableColumn(selectedIntraPatternList, SWT.NONE);
		selectedColumn.setText("Selected Intra Pattern                                   ");
		selectedColumn.pack();
		
		return area;
	}
	
	class AddPatternListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			int index = candiateIntraPatternList.getSelectionIndex();
			if(index != -1){
				TableItem tobeRemovedItem = candiateIntraPatternList.getItem(index);
				
				String name = tobeRemovedItem.getText();
				PathPatternGroupWrapper wrapper = (PathPatternGroupWrapper)tobeRemovedItem.getData();
				
				SkeletonGenerationWizard wizard = new SkeletonGenerationWizard(wrapper);
				
				WizardDialog skeletonDialog = new WizardDialog(PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getShell(), wizard);
				
				
				if(skeletonDialog.open() == Window.OK){
					PathPatternGroupWrapper.PotentialLocation location = wrapper.new
							PotentialLocation(wizard.getResidingClass(), wizard.getSuperClass(), wizard.getInterfaces(), 
									wizard.getMethodName(), wizard.getMethodReturnType(), wizard.getInterfaces());
					
					wrapper.setLocation(location);
					
					candiateIntraPatternList.remove(index);
					
					wrapper.setWizard(wizard);
					
					TableItem addedItem = new TableItem(selectedIntraPatternList, SWT.NONE);
					addedItem.setText(name);					
					addedItem.setData(wrapper);
					selectedIntraPatternList.getColumn(0).pack();
				}
				
				
			}
			
		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RemovePatternListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			int index = selectedIntraPatternList.getSelectionIndex();
			
			if(index != -1){
				TableItem tobeRemovedItem = selectedIntraPatternList.getItem(index);
				
				String name = tobeRemovedItem.getText();
				Object obj = tobeRemovedItem.getData();
				
				selectedIntraPatternList.remove(index);
				
				TableItem addedItem = new TableItem(candiateIntraPatternList, SWT.NONE);
				addedItem.setText(name);
				addedItem.setData(obj);
				candiateIntraPatternList.getColumn(0).pack();
			}
			
			
		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	protected void createButtonsForButtonBar(Composite parent) {
		GridData parentGridData = new GridData();
		parentGridData.grabExcessHorizontalSpace = true;
		parentGridData.grabExcessVerticalSpace = true;
		parentGridData.horizontalAlignment = GridData.FILL;
		parentGridData.verticalAlignment = GridData.FILL;
		parent.setData(parentGridData);

		parent.setLayoutData(parentGridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Cofirm", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = 
				createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	
	protected Button createOkButton(Composite parent, int id, String label, boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	protected Point getInitialSize() {
		return new Point(800, 575);
	}
	
	private boolean isValidInput() {
		boolean valid = true;
		
		return valid;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		generateCodeSkeleton();
	}

	private void generateCodeSkeleton() {
		
		HashMap<String, clonepedia.model.ontology.Class> targetClassList = 
				new HashMap<String, clonepedia.model.ontology.Class>();
		
		for(TableItem item: selectedIntraPatternList.getItems()){
			PathPatternGroupWrapper wrapper = (PathPatternGroupWrapper)item.getData();
			PathPatternGroupWrapper.PotentialLocation location = wrapper.getLocation();
			
			String className = location.className;
			Class clazz = targetClassList.get(className);
		}
		
	}
}
