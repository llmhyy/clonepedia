package clonepedia.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import clonepedia.model.viewer.PathPatternGroupWrapper;

public class DetermineMethodWizardPage extends WizardPage {
	
	private PathPatternGroupWrapper wrapper;

	public DetermineMethodWizardPage(PathPatternGroupWrapper wrapper, String pageName) {
		super(pageName);
		
		this.wrapper = wrapper;
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
