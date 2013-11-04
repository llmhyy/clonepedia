package clonepedia.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.actions.steps.GenerateCloneTopicStep;
import clonepedia.actions.steps.GenerateInterSetPatternStep;
import clonepedia.actions.steps.GenerateIntraSetPatternStep;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class StartFromIntraSetPatternAction implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		Job job = new Job("Pattern Generating"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CloneSets sets = (CloneSets) MinerUtil.deserialize(Settings.intraSetFile, true);
				
				GenerateInterSetPatternStep step3 = new GenerateInterSetPatternStep(sets);
				monitor.beginTask("Generating Inter Clone Set Pattern", step3.getTotolEffort());
				step3.run(monitor);
				
				GenerateCloneTopicStep step4 = new GenerateCloneTopicStep(sets);
				monitor.beginTask("Generating Clone Topics", step4.getTotolEffort());
				step4.run(monitor);
				
				if(monitor.isCanceled()){
					return Status.CANCEL_STATUS;
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
