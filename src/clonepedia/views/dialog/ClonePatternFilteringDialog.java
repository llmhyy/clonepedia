package clonepedia.views.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ClonePatternFilteringDialog extends TitleAreaDialog {

	private Text minimumText;
	private Text maximumText;
	private String minimum;
	private String maximum;
	
	public ClonePatternFilteringDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public void create() {
		setHelpAvailable(false);
		super.create();
		// Set the title
		setTitle("Clone Pattern Filtering");
		// Set the message
		setMessage("Please input your search conditions.", 
				IMessageProvider.INFORMATION);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		//parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		Composite workArea = new Composite(parent, SWT.NONE);
		workArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout workAreaLayout = new GridLayout();
		workAreaLayout.numColumns = /*1*/1;
		workAreaLayout.horizontalSpacing = 10;
		workAreaLayout.marginHeight = 10;
		workAreaLayout.marginBottom = 10;
		workAreaLayout.marginLeft = 10;
		workAreaLayout.marginRight = 10;
		workAreaLayout.verticalSpacing = 10;
		workArea.setLayout(workAreaLayout);
		
		Group cloneSetNumberGroup = new Group(workArea, SWT.NONE);
		cloneSetNumberGroup.setText("Control Corresponding Clone Set Number");
		cloneSetNumberGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		groupLayout.verticalSpacing = 10;
		cloneSetNumberGroup.setLayout(groupLayout);

		// The text fields will grow with the size of the dialog

		Label minimunLabel = new Label(cloneSetNumberGroup, SWT.NONE);
		minimunLabel.setText("minimum");
		minimunLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		minimumText = new Text(cloneSetNumberGroup, SWT.BORDER);
		minimumText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label maximumLabel = new Label(cloneSetNumberGroup, SWT.NONE);
		maximumLabel.setText("maximum");
		maximumLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		// You should not re-use GridData
		maximumText = new Text(cloneSetNumberGroup, SWT.BORDER);
		maximumText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		return workArea;
	}

	@Override
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
		createOkButton(parent, OK, "Search", true);
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

	private boolean isValidInput() {
		boolean valid = true;
		
		
		if (!isInt(minimumText.getText())) {
			setErrorMessage("Please recheck the format of minimum");
			valid = false;
		}
		if (!isInt(maximumText.getText())) {
			setErrorMessage("Please recheck the format of maximum");
			valid = false;
		}
		return valid;
	}
	
	private boolean isInt(String str){
		try {
			Integer.parseInt(str);

		} catch (NumberFormatException ex) {
			return false;
		}

		return true;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

	// Coyy textFields because the UI gets disposed
	// and the Text Fields are not accessible any more.
	private void saveInput() {
		minimum = minimumText.getText();
		maximum = maximumText.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getMinimum() {
		return minimum;
	}

	public String getMaximum() {
		return maximum;
	}
}
