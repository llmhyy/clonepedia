package clonepedia.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.model.ontology.Project;
import clonepedia.util.Settings;

public class CloneInformationExtractionAction implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Job job = new Job("Clone Information Extracting"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Project project = new Project(Settings.projectName, "java", "");
				CloneInformationExtractor extractor = 
						new CloneInformationExtractor(new CloneDetectionFileParser(), project);
				extractor.extract();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		//System.out.println("success");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		

	}

	@Override
	public void dispose() {
		

	}

	@Override
	public void init(IWorkbenchWindow window) {
		

	}

}
