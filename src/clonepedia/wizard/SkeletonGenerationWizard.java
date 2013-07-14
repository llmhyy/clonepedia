package clonepedia.wizard;


import org.eclipse.jface.wizard.Wizard;

import clonepedia.model.viewer.PathPatternGroupWrapper;


public class SkeletonGenerationWizard extends Wizard {

	private PathPatternGroupWrapper wrapper;
	
	private DetermineClassWizardPage determineClassPage;
	private DetermineMethodWizardPage determineMethodPage;
	
	public SkeletonGenerationWizard(PathPatternGroupWrapper wrapper){
		super();
		
		this.wrapper = wrapper;
		
	    setNeedsProgressMonitor(true);
		setWindowTitle("Generate Code Skeleton Based on Code Clones");
	}
	
	@Override
	public void addPages(){
		determineClassPage = new DetermineClassWizardPage(wrapper, "Determine the class for clone");
		determineMethodPage = new DetermineMethodWizardPage(wrapper, "Determine the method for clone");
		
		addPage(determineClassPage);
		addPage(determineMethodPage);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

}
