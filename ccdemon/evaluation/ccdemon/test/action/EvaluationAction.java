package ccdemon.test.action;

import java.util.ArrayList;

import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ccdemon.test.main.CloneRecoverer;
import ccdemon.test.main.CloneRecoverer.CollectedData;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class EvaluationAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		int count_for_t1 = 0;
		int count_for_t2 = 0;
		int count_for_t3 = 0;
		int count_for_t4 = 0;
		int count_for_t5 = 0;
		ArrayList<Double> config_effort_t1 = new ArrayList<Double>();
		ArrayList<Double> config_effort_t2 = new ArrayList<Double>();
		ArrayList<Double> config_effort_t3 = new ArrayList<Double>();
		ArrayList<Double> saveedit_effort_t1 = new ArrayList<Double>();
		ArrayList<Double> saveedit_effort_t2 = new ArrayList<Double>();
		ArrayList<Double> saveedit_effort_t3 = new ArrayList<Double>();
		ArrayList<CloneInstance> record_t4 = new ArrayList<CloneInstance>();
		ArrayList<CloneInstance> record_t5 = new ArrayList<CloneInstance>();
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			if(!clonepediaSet.getId().equals("2458")){
				continue;
			}
			
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.trial(set);
			for(CollectedData data : datas){
				if(data.getCorrectness() == 1.0){
					if(data.getSavedEditingEffort() == 1.0){
						count_for_t1++;
						config_effort_t1.add(data.getConfigurationEffort());
						saveedit_effort_t1.add(data.getSavedEditingEffort());
					}else if(data.getSavedEditingEffort() > 0){
						count_for_t2++;
						config_effort_t2.add(data.getConfigurationEffort());
						saveedit_effort_t2.add(data.getSavedEditingEffort());
					}else{
						count_for_t5++;
						record_t5.add(data.getCloneInstance());
					}
				}else if(data.getCorrectness() > 0){
					if(data.getSavedEditingEffort() > 0){
						count_for_t3++;
						config_effort_t3.add(data.getConfigurationEffort());
						saveedit_effort_t3.add(data.getSavedEditingEffort());
					}else{
						count_for_t5++;
						record_t5.add(data.getCloneInstance());
					}
				}else{
					count_for_t4++;
					record_t4.add(data.getCloneInstance());
				}
			}
		}
		
		int count_for_alltypes = count_for_t1 + count_for_t2 + count_for_t3 + count_for_t4 + count_for_t5;
		double t1_percentage = 1.0d * count_for_t1 / count_for_alltypes;
		double t2_percentage = 1.0d * count_for_t2 / count_for_alltypes;
		double t3_percentage = 1.0d * count_for_t3 / count_for_alltypes;
		double t4_percentage = 1.0d * count_for_t4 / count_for_alltypes;
		double t5_percentage = 1.0d * count_for_t5 / count_for_alltypes;
		
		double t1_avg_config = calculateScoreMean(config_effort_t1);
		double t2_avg_config = calculateScoreMean(config_effort_t2);
		double t3_avg_config = calculateScoreMean(config_effort_t3);
		double t1_std_config = calculateSTD(config_effort_t1, t1_avg_config);
		double t2_std_config = calculateSTD(config_effort_t2, t2_avg_config);
		double t3_std_config = calculateSTD(config_effort_t3, t3_avg_config);
		
		double t1_avg_saveedit = calculateScoreMean(saveedit_effort_t1);
		double t2_avg_saveedit = calculateScoreMean(saveedit_effort_t2);
		double t3_avg_saveedit = calculateScoreMean(saveedit_effort_t3);
		double t1_std_saveedit = calculateSTD(saveedit_effort_t1, t1_avg_saveedit);
		double t2_std_saveedit = calculateSTD(saveedit_effort_t2, t2_avg_saveedit);
		double t3_std_saveedit = calculateSTD(saveedit_effort_t3, t3_avg_saveedit);
		
		System.out.println("-------------------------------------- Result -----------------------------------");
		System.out.println("t1_percentage: " + t1_percentage);
		System.out.println("t2_percentage: " + t2_percentage);
		System.out.println("t3_percentage: " + t3_percentage);
		System.out.println("t4_percentage: " + t4_percentage);
		System.out.println("t5_percentage: " + t5_percentage);
		System.out.println("=============================================");
		System.out.println("t1_avg_config: " + t1_avg_config);
		System.out.println("t2_avg_config: " + t2_avg_config);
		System.out.println("t3_avg_config: " + t3_avg_config);
		System.out.println("t1_std_config: " + t1_std_config);
		System.out.println("t2_std_config: " + t2_std_config);
		System.out.println("t3_std_config: " + t3_std_config);
		System.out.println("=============================================");
		System.out.println("t1_avg_saveedit: " + t1_avg_saveedit);
		System.out.println("t2_avg_saveedit: " + t2_avg_saveedit);
		System.out.println("t3_avg_saveedit: " + t3_avg_saveedit);
		System.out.println("t1_std_saveedit: " + t1_std_saveedit);
		System.out.println("t2_std_saveedit: " + t2_std_saveedit);
		System.out.println("t3_std_saveedit: " + t3_std_saveedit);
		System.out.println("=============================================");
		System.out.println("Trial for type4: " + record_t4.toString());
		System.out.println("Trial for type5: " + record_t5.toString());
		
		
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

	private static double calculateScoreMean(ArrayList<Double> rawScores) {
		double scoreAll = 0.0;
		for (Double score : rawScores) {
			scoreAll += score;
		}
		return scoreAll/rawScores.size();
	}
	
	private static double calculateSTD(ArrayList<Double> rawScores, double scoreMean) {
		double allSquare = 0.0;
		for (Double rawScore : rawScores) {
			allSquare += (rawScore - scoreMean)*(rawScore - scoreMean);
		}
		double denominator = rawScores.size() * (rawScores.size() - 1);
		return Math.sqrt(allSquare/denominator);
	}
}
