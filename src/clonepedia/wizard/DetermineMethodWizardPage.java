package clonepedia.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DetermineMethodWizardPage extends WizardPage {

	public DetermineMethodWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	public DetermineMethodWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
	}

}
