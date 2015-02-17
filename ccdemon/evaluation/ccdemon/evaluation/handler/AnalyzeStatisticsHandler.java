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
import ccdemon.evaluation.util.ExcelExporterWithPOI;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class AnalyzeStatisticsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Job job = new Job("analyzing project statistics"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AnalyzeStatisticsHandler handler = new AnalyzeStatisticsHandler();
				handler.run();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		

		return null;
	}
	
	private void run(){
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;
		int count = 0;
		ExcelExporterWithPOI exporter = new ExcelExporterWithPOI();
		exporter.start();
		
		ArrayList<String> alreadyExportedId = getExportedIds();
		ArrayList<String> nowExportedId = new ArrayList<String>();

//		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
		for(int countTo75 = 0; countTo75 < 75; countTo75++){
			clonepedia.model.ontology.CloneSet clonepediaSet = sets.getCloneList().get(countTo75);
			if(alreadyExportedId.contains(clonepediaSet.getId())){
				continue;
			}else{
				nowExportedId.add(clonepediaSet.getId());
			}
			
			System.out.println("--------------------current: " + sets.getCloneList().indexOf(clonepediaSet) + ", total: " + sets.getCloneList().size() + " -----------------------");
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				ArrayList<String> exportList = new ArrayList<String>();
				//cloneSetID
				exportList.add(clonepediaSet.getId());
				//instanceNum
				exportList.add(set.getInstances().size() + "");
				//avgLineNum
				exportList.add(data.getLineNum() + "");
				//typeIIorIII
				exportList.add(data.getTypeIIorIII());
				//type1to7;
				if(data.getRecall() == 1.0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("1");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("2");
					}else{
						exportList.add("3");
					}
				}else if(data.getRecall() > 0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("4");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("5");
					}else{
						exportList.add("6");
					}
				}else{
					exportList.add("7");
				}
				//recall;
				exportList.add(data.getRecall() + "");
				//precision;
				exportList.add(data.getPrecision() + "");
				//configurationEffort;
				exportList.add(data.getConfigurationEffort() + "");
				//savedEditingEffort;
				exportList.add(data.getSavedEditingEffort() + "");
				//trialTime;
				exportList.add(data.getTrialTime() + "");
				//cloneInstance;;
				exportList.add(data.getCloneInstance().toString());
				
				exporter.export(exportList, count);
				count++;
			}
			
		}
		
		exporter.end("jasperreports-1");
		System.out.println("export ids:");
		for(String id : nowExportedId){
			System.out.print(id + ",");
		}
	}

	private ArrayList<String> getExportedIds(){
		ArrayList<String> exportedList = new ArrayList<String>();
		String alreadyExportedString = "";
		
		String[] exportedArray = alreadyExportedString.split(",");
		for(String s : exportedArray){
			exportedList.add(s);
		}
		
		return exportedList;
	}
}
