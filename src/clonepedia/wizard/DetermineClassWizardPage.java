package clonepedia.wizard;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class DetermineClassWizardPage extends CustomizedTypeWizardPage {
	
	//private StringButtonDialogField fTypeNameDialogField;

	public DetermineClassWizardPage(String pageName) {
		super(true, pageName);
		
		//fTypeNameDialogField= new StringButtonDialogField(adapter);
	}


	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		//createMethodStubSelectionControls(composite, nColumns);

		//createCommentControls(composite, nColumns);
		//enableCommentControl(true);

		setControl(composite);

		//Dialog.applyDialogFont(composite);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

}
