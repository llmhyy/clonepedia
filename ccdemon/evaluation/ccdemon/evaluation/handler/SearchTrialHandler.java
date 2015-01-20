package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.model.CloneSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ccdemon.evaluation.main.CloneRecoverer;
import ccdemon.evaluation.main.CloneRecoverer.CollectedData;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class SearchTrialHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		ArrayList<String> cloneSetId = new ArrayList<String>();
		CollectedData printData = null;
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			if(!clonepediaSet.getId().equals("69565")){
				continue;
			}
			
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.trial(set);
			
			for(CollectedData data : datas){
				//collect trials when:
				if(data.getSavedEditingEffort() == 0.0 && data.getCorrectness() == 1.0 
						&& data.getConfigurationEffort() < 0.5
//						&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
						&& data.getConfigurationPointNum() >= 5
						&& data.getCloneInstance().getLength() >= 8){
					if(!cloneSetId.contains(clonepediaSet.getId())){
						cloneSetId.add(clonepediaSet.getId());
					}
					printData = data;
				}
				
			}
		}

		System.out.println("-------------------------------------- Result -----------------------------------");
		System.out.println("ID: " + cloneSetId.toString());
		System.out.println(printData.toString());
		
		return null;
	}
}
