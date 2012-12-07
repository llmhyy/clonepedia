package clonepedia.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.model.ontology.Project;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.syntactic.util.comparator.VectorPatternComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class OntologicalModelBuildingAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Job job = new Job("Ontological Model Generating"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Project project = new Project(Settings.projectName, "java", "");
				OntologicalModelGenerator generator = new OntologicalModelGenerator(project);
				try {
					generator.reconstructOntologicalModel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		
		/*StructuralClusterer clusterer;
		try {
			PatternComparator patternComparator = new VectorPatternComparator();
			//clusterer = new StructuralClusterer(generator.getSets(), patternComparator);
			//MinerUtil.serialize(clusterer, "initialized_clusterer");
			//clusterer.doClustering();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("");*/
		
		/*CloneSets sets = generator.getSets();
		for(CloneSet set: sets.getCloneList()){
			
		}*/
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
