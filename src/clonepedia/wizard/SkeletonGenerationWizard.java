package clonepedia.wizard;

import org.eclipse.jface.wizard.Wizard;

public class SkeletonGenerationWizard extends Wizard {

	private DetermineClassWizardPage determineClassPage;
	private DetermineMethodWizardPage determineMethodPage;
	
	public SkeletonGenerationWizard(){
		super();
	    setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages(){
		determineClassPage = new DetermineClassWizardPage("Determine the class for clone");
		determineMethodPage = new DetermineMethodWizardPage("Determine the method for clone");
		
		addPage(determineClassPage);
		addPage(determineMethodPage);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

}
