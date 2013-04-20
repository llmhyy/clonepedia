package clonepedia.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.java.StructureExtractor;
import clonepedia.model.ontology.Project;
import clonepedia.util.Settings;

public class DirectOntologicalModelGeneration implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Job job = new Job("Program Structure Extracting"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Project project = new Project(Settings.projectName, "java", "");
				StructureExtractor extractor = new StructureExtractor(project, false);
				//extractor.extractProjectOutline();
				try {
					OntologicalModelDataFetcher modelFetcher = 
							(OntologicalModelDataFetcher)extractor.extractProjectContent();
					
					System.out.println();
					
				} catch (JavaModelException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
