package clonepedia.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import clonepedia.actions.steps.GenerateCloneTopicStep;
import clonepedia.actions.steps.GenerateInterSetPatternStep;
import clonepedia.actions.steps.GenerateIntraSetPatternStep;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.syntactic.SyntacticClusteringer0;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.syntactic.util.comparator.PathComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class StartFromOntologyAction implements
		IWorkbenchWindowActionDelegate {

	//private double intraCloneSetPatternProgress = 0.5;
	//private double interCloneSetPatternProgress = 0.4;
	//private double topicProgress = 0.1;
	
	//private int totalUnit = 1000;
	
	@Override
	public void run(IAction action) {
		
		Job job = new Job("Pattern Generating"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CloneSets sets = (CloneSets) MinerUtil.deserialize(Settings.ontologyFile, true);
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
				
				GenerateCloneTopicStep step4 = new GenerateCloneTopicStep(sets);
				monitor.beginTask("Generating Clone Topics", step4.getTotolEffort());
				step4.run(monitor);
				
				if(monitor.isCanceled()){
					return Status.CANCEL_STATUS;
				}
				
				/*try{
					
					PathComparator pathComparator = new LevenshteinPathComparator();
					CloneSets sets = (CloneSets)MinerUtil.deserialize("ontological_model");
					
					System.out.println("Ontological model extracted.");
					sets.setPathComparator(pathComparator);
					
					if(Settings.skipPattern.equals("No")){
						
						intraCloneSetPatternGeneration(monitor, sets);
						
						printPatternNumber(sets);
						
						interCloneSetPatternGeneration(monitor, sets);
						
					}
					topicGeneration(monitor, sets);
					
					if(monitor.isCanceled()){
						return Status.CANCEL_STATUS;
					}
				}
				catch(InterruptedException e){
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				catch(Exception e){
					e.printStackTrace();
				}*/
				
				return Status.OK_STATUS;
			}
			
		};
		
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		job.addJobChangeListener(new JobChangeAdapter(){
			public void done(IJobChangeEvent e){
				if(e.getResult().isOK()){
					MessageDialog.openConfirm(shell, "progress", "pattern generation sucess");
				}
				else{
					
				}
			}
		});
		
		job.schedule();
		

	}
	
	/*private void intraCloneSetPatternGeneration(IProgressMonitor monitor, CloneSets sets) throws Exception{
		
		monitor.subTask("generating intra clone set pattern");
		
		long startTime = 0;
		long endTime = 0;
		
		startTime = System.currentTimeMillis();
		
		int setsNum = sets.getCloneList().size();
		int i = 0;
		for(CloneSet set: sets.getCloneList()){
			
			i++;
			if(set == null)
				continue;
			
			if(monitor.isCanceled()){
				return;
			}
			
			set.buildPatterns();
			
			if(i%10 == 0){
				monitor.worked((int)(10*totalUnit/setsNum*this.intraCloneSetPatternProgress));
				
				double percentage = ((double)i/setsNum)*100;
				String percentageString = String.valueOf(percentage).substring(0, 5);
				System.out.println(percentageString + "% clone sets have been computed");
			}
		}
		
		
		endTime = System.currentTimeMillis();
		System.out.println("The time spended on buildPatternForCloneSets is: " + (endTime-startTime));
		
		MinerUtil.serialize(sets, "intra_pattern_sets");
	}
	
	private void interCloneSetPatternGeneration(IProgressMonitor monitor, CloneSets sets) throws Exception{
		
		if(monitor.isCanceled()){
			return;
		}
		
		monitor.subTask("generating inter clone set pattern");
		
		SyntacticClusteringer0 clusteringer  = new SyntacticClusteringer0(sets.getCloneList());

		
		ArrayList<PathPatternGroup> locationPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.locationPatternClustering);
		ArrayList<PathPatternGroup> mfvDiffPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.methodFieldVariableDiffPatternClustering);
		ArrayList<PathPatternGroup> ciDiffPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.complexTypeDiffPatternClustering);

		int locationNum = locationPatterns.size();
		int mfvNum = locationPatterns.size();
		int ciNum = ciDiffPatterns.size();
		double totalNum = locationNum + mfvNum + ciNum;
		
		clusteringer.clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION);
		monitor.worked((int)(locationNum/totalNum*totalUnit*this.interCloneSetPatternProgress));
		
		if(monitor.isCanceled()){
			return;
		}
		
		clusteringer.clusterSpecificStyleOfClonePattern(mfvDiffPatterns, PathSequence.DIFF_USAGE);
		monitor.worked((int)(mfvNum/totalNum*totalUnit*this.interCloneSetPatternProgress));
		
		if(monitor.isCanceled()){
			return;
		}
		
		clusteringer.clusterSpecificStyleOfClonePattern(ciDiffPatterns, PathSequence.DIFF_USAGE);
		monitor.worked((int)(ciNum/totalNum*totalUnit*this.interCloneSetPatternProgress));
		
		MinerUtil.serialize(sets, "inter_sets");
	}*/
	
	/*private void topicGeneration(IProgressMonitor monitor, CloneSets sets) throws Exception{
		
		if(monitor.isCanceled()){
			return;
		}
		
		monitor.subTask("generating topic of clone sets");
		
		SemanticClusteringer clusteringer = new SemanticClusteringer();
		
		//CloneSets sets = (CloneSets)MinerUtil.deserialize("syntactic_sets");
		clusteringer.doClustering(sets.getCloneList());
		monitor.worked((int)(totalUnit*this.topicProgress));
		
		MinerUtil.serialize(sets, "sets");
	}*/

	/*private void printPatternNumber(CloneSets sets) throws Exception{
		int count = 0;
		for(CloneSet set: sets.getCloneList()){
			count += set.getClusterableLocationPatterns().size();
		}
		System.out.println("The total number of location patterns is: " + count);
		
		count = 0;
		for(CloneSet set: sets.getCloneList()){
			count += set.getClusterableDiffUsagePatterns().size();
		}
		System.out.println("The total number of diff usage patterns is: " + count);
		
		count = 0;
		for(CloneSet set: sets.getCloneList()){
			count += set.getClusterableMethodFieldVariableDiffUsagePatterns().size();
		}
		System.out.println("The total number of diff method/field/variable usage patterns is: " + count);
		
		count = 0;
		for(CloneSet set: sets.getCloneList()){
			count += set.getClusterableComplexTypeDiffUsagePatterns().size();
		}
		System.out.println("The total number of diff class/interface usage patterns is: " + count);
		
		
	}*/
	
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
