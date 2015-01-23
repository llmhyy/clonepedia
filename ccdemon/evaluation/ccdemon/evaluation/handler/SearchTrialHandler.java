package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.model.CloneSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ccdemon.evaluation.main.CloneRecoverer;
import ccdemon.evaluation.main.CloneRecoverer.CollectedData;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class SearchTrialHandler extends AbstractHandler{
	
	private boolean isTypeI(CollectedData data){
		if(data.getCorrectness() == 1.0 && data.getSavedEditingEffort() == 1.0
				&& data.getConfigurationEffort() < 0.5
				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeII(CollectedData data){
		if(data.getCorrectness() == 1.0 && 0 < data.getSavedEditingEffort() && data.getSavedEditingEffort() < 1
				&& data.getConfigurationEffort() < 0.5
				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeIII(CollectedData data){
		if(0 == data.getSavedEditingEffort() && data.getCorrectness() == 1.0 
				&& data.getConfigurationEffort() < 0.5
//				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeIV(CollectedData data){
		if(0<data.getCorrectness() && data.getCorrectness()<1 && data.getSavedEditingEffort() == 1.0
				&& data.getConfigurationEffort() < 0.5
				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeV(CollectedData data){
		if(0<data.getCorrectness() && data.getCorrectness()<1 && 0 < data.getSavedEditingEffort() && data.getSavedEditingEffort() < 1
				&& data.getConfigurationEffort() < 0.5
				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeVI(CollectedData data){
		if( 0<data.getCorrectness() && data.getCorrectness()<1 && data.getSavedEditingEffort() == 0.0
				&& data.getConfigurationEffort() < 0.5
//				&& data.getHistoryNum() > 0 && data.getRuleNum() > 0 && data.getEnvironmentNum() > 0 
				&& data.getConfigurationPointNum() >= 5
				&& data.getCloneInstance().getLength() >= 8){
			return true;
		}
		
		return false;
	}
	
	private boolean determineTarget(int type, CollectedData data){
		switch(type){
		case 1: 
			return isTypeI(data);
		case 2:
			return isTypeII(data);
		case 3:
			return isTypeIII(data);
		case 4:
			return isTypeVI(data);
		case 5:
			return isTypeV(data);
		case 6:
			return isTypeIV(data);
		default: return true;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Job job = new Job("searching trial"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SearchTrialHandler handler = new SearchTrialHandler();
				handler.run();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		
		return null;
	}
	
	private void run(){
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		ArrayList<CollectedData> dataList = new ArrayList<>();
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			if(!clonepediaSet.getId().equals("17185")){
				continue;
			}
			
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				//collect trials when:
				boolean isNeeded = determineTarget(5, data);
				if(isNeeded){
					dataList.add(data);				
				}
			}
		}

		System.out.println("-------------------------------------- Result -----------------------------------");
		System.out.println(dataList.toString());
	}
}
