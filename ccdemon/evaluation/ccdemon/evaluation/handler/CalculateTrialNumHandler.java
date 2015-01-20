package ccdemon.evaluation.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import clonepedia.model.ontology.CloneSets;

public class CalculateTrialNumHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		System.out.println("Clone Set number: " + sets.getCloneList().size());
		int trialNum = 0;
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			trialNum += clonepediaSet.size();
		}
		
		System.out.println("Trial number: " + trialNum);
		
		return null;
	}

}
