package clonepedia.wizard;


import java.util.List;

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
		return (getContainer().getCurrentPage() == determineMethodPage);
	}

	public String getResidingClass(){
		return this.determineClassPage.getTypeName();
	}
	
	public String getSuperClass(){
		return this.determineClassPage.getSuperClass();
	}
	
	public List<String> getInterfaces(){
		return this.determineClassPage.getSuperInterfaces();
	}
	
	public String getMethodReturnType(){
		return this.determineMethodPage.getMethodReturnType();
	}
	
	public String getMethodName(){
		return this.determineMethodPage.getMethodName();
	}
	
	public List<String> getMethodParamters(){
		return this.determineMethodPage.getMethodParameters();
	}
}
