package clonepedia.actions;

import java.io.File;

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

import clonepedia.actions.steps.GenerateCloneTopicStep;
import clonepedia.actions.steps.GenerateInterSetPatternStep;
import clonepedia.actions.steps.GenerateIntraSetPatternStep;
import clonepedia.actions.steps.GenerateOntologyStep;
import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.StructureExtractor;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Project;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class StartFromCloneFileAction implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Job job = new Job("Pattern Generating"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Project project = new Project(Settings.projectName, "java", "");
				StructureExtractor extractor = new StructureExtractor(project, false);
				
				GenerateOntologyStep step1 = new GenerateOntologyStep(project, extractor);
				monitor.beginTask("Generating Ontology Model", step1.getTotolEffort());
				step1.run(monitor);
				
				CloneSets sets = step1.getCloneSets();
				sets.setPathComparator(new LevenshteinPathComparator());
				
				GenerateIntraSetPatternStep step2 = new GenerateIntraSetPatternStep(sets);
				monitor.beginTask("Generating Intra Clone Set Pattern", step2.getTotolEffort());
				step2.run(monitor);
				
				if(monitor.isCanceled()){
					return Status.CANCEL_STATUS;
				}
				
				GenerateInterSetPatternStep step3 = new GenerateInterSetPatternStep(sets);
				monitor.beginTask("Generating Inter Clone Set Pattern", step3.getTotolEffort());
				step3.run(monitor);
				
				
				if(monitor.isCanceled()){
					return Status.CANCEL_STATUS;
				}
				
				GenerateCloneTopicStep step4 = new GenerateCloneTopicStep(sets);
				monitor.beginTask("Generating Clone Topics", step4.getTotolEffort());
				step4.run(monitor);
				
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
