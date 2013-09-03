package clonepedia.wizard;


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.wizard.Wizard;

import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.wizard.DetermineElementWizardPage.MethodParameterWrapper;


@SuppressWarnings("restriction")
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
		
		determineClassPage.setNotifyPage(determineMethodPage);
		
		addPage(determineClassPage);
		addPage(determineMethodPage);
	}
	
	
	@Override
	public boolean performFinish() {
		
		//warnAboutTypeCommentDeprecation();
		//boolean res= super.performFinish();
		/*if (res) {
			IResource resource= fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}
		}*/
		return (getContainer().getCurrentPage() == determineMethodPage);
	}
	
	public String getPackageName(){
		return this.determineClassPage.getPackageText();
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
	
	public List<MethodParameterWrapper> getMethodParamters(){
		return this.determineMethodPage.getMethodParameters();
	}

	
}
