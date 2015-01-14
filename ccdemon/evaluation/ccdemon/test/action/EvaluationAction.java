package ccdemon.test.action;

import mcidiff.model.CloneSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ccdemon.test.main.CloneRecoverer;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class EvaluationAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;;
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			if(!clonepediaSet.getId().equals("3210")){
				continue;
			}
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			recoverer.trial(set);
			
		}
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
