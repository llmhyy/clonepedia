package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

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

		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){

			System.out.println("--------------------current: " + sets.getCloneList().indexOf(clonepediaSet) + ", total: " + sets.getCloneList().size() + " -----------------------");
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			if(set.getInstances().size() < 3){
				continue;
			}
			try {
				if(!isTypeIII(set)){
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		
		exporter.end("jasperreports-type3");
	}

	private boolean isTypeIII(CloneSet set) throws Exception{
		SeqMCIDiff diff = new SeqMCIDiff();
		IJavaProject proj = CCDemonUtil.retrieveWorkingJavaProject();
		ArrayList<SeqMultiset> diffList;
		diffList = diff.diff(set, proj);
		
		ArrayList<SeqMultiset> matchableDiffs = findMatchableDiff(diffList, set.getInstances().get(0));
		
		boolean isTypeIII = false;
		for(int i = 0; i < matchableDiffs.size() && !isTypeIII; i++){
			SeqMultiset seqMulti = matchableDiffs.get(i);
			for(TokenSeq seq : seqMulti.getSequences()){
				if(seq.isEpisolonTokenSeq()){
					isTypeIII = true;
					break;
				}
			}
		}
		
		return isTypeIII;
	}
	

	
	/**
	 * Given a target instance, the diffs can be divided into two categories: 
	 * Type I: The token sequence for target instance is different from the token sequences for other instances, 
	 * in this case, those token sequences in other instances are exactly the same.
	 * Type II (i.e., matchable diff): The token sequences for other instances are different with each other.
	 * 
	 * By differentiating these differences, I am able to identify the correct candidate for each configuration
	 * point.
	 * 
	 * @param diffList
	 * @return
	 */
	private ArrayList<SeqMultiset> findMatchableDiff(ArrayList<SeqMultiset> diffList, CloneInstance targetInstance){
		ArrayList<SeqMultiset> typeTwoDiffList = new ArrayList<>();
		
		for(SeqMultiset diff : diffList){
			//TokenSeq targetSeq = diff.findTokenSeqByCloneInstance(targetInstance);
			int difference = 0;
			for(int i = 0; i < diff.getSequences().size(); i++){
				if(!diff.getSequences().get(i).getCloneInstance().equals(targetInstance)){
					for(int j = i + 1; j < diff.getSequences().size(); j++){
						if(!diff.getSequences().get(j).getCloneInstance().equals(targetInstance)){
							if(!diff.getSequences().get(i).equals(diff.getSequences().get(j))){
								difference++;
							}
						}
					}
				}
			}
			if(difference != 0){
				SeqMultiset newDiff = new SeqMultiset();
				for(TokenSeq seq : diff.getSequences()){
					newDiff.addTokenSeq(seq);
				}
				typeTwoDiffList.add(newDiff);
			}
		}
		
		return typeTwoDiffList;
	}
}
