package clonepedia.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.syntactic.SyntacticClusteringer0;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.syntactic.util.comparator.PathComparator;
import clonepedia.util.MinerUtil;

public class PatternAndTopicGeneration implements
		IWorkbenchWindowActionDelegate {

	private double intraCloneSetPatternProgress = 0.5;
	private double interCloneSetPatternProgress = 0.4;
	private double topicProgress = 0.1;
	
	
	@Override
	public void run(IAction action) {
		
		Job job = new Job("Pattern and Topic Extracting"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Pattern Generation", 100);
				
				try{
					
					PathComparator pathComparator = new LevenshteinPathComparator();
					CloneSets sets = (CloneSets)MinerUtil.deserialize("ontological_model");
					
					System.out.println("Ontological model extracted.");
					sets.setPathComparator(pathComparator);
					
					intraCloneSetPatternGeneration(monitor, sets);
					
					printPatternNumber(sets);
					
					interCloneSetPatternGeneration(monitor, sets);
					
					topicGeneration(monitor, sets);
					
				}
				catch(InterruptedException e){
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		

	}
	
	private void intraCloneSetPatternGeneration(IProgressMonitor monitor, CloneSets sets) throws Exception{
		
		long startTime = 0;
		long endTime = 0;
		
		startTime = System.currentTimeMillis();
		
		int setsNum = sets.getCloneList().size();
		int i = 0;
		for(CloneSet set: sets.getCloneList()){
			
			i++;
			if(set == null)
				continue;
			
			set.buildPatterns();
			
			if(i%10 == 0){
				monitor.worked((int)(10*100/setsNum*this.intraCloneSetPatternProgress));
				
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
		
		
		SyntacticClusteringer0 clusteringer  = new SyntacticClusteringer0(sets.getCloneList());

		
		ArrayList<PathPatternGroup> locationPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.locationPatternClustering);
		ArrayList<PathPatternGroup> mfvDiffPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.methodFieldVariableDiffPatternClustering);
		ArrayList<PathPatternGroup> ciDiffPatterns = clusteringer.getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.complexTypeDiffPatternClustering);

		int locationNum = locationPatterns.size();
		int mfvNum = locationPatterns.size();
		int ciNum = ciDiffPatterns.size();
		double totalNum = locationNum + mfvNum + ciNum;
		
		clusteringer.clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION);
		monitor.worked((int)(locationNum/totalNum*100*this.interCloneSetPatternProgress));
		
		clusteringer.clusterSpecificStyleOfClonePattern(mfvDiffPatterns, PathSequence.DIFF_USAGE);
		monitor.worked((int)(mfvNum/totalNum*100*this.interCloneSetPatternProgress));
		
		clusteringer.clusterSpecificStyleOfClonePattern(ciDiffPatterns, PathSequence.DIFF_USAGE);
		monitor.worked((int)(ciNum/totalNum*100*this.interCloneSetPatternProgress));
		
		MinerUtil.serialize(sets, "inter_sets");
	}
	
	private void topicGeneration(IProgressMonitor monitor, CloneSets sets) throws Exception{
		SemanticClusteringer clusteringer = new SemanticClusteringer();
		
		//CloneSets sets = (CloneSets)MinerUtil.deserialize("syntactic_sets");
		clusteringer.doClustering(sets.getCloneList());
		monitor.worked((int)(100*this.topicProgress));
		
		MinerUtil.serialize(sets, "sets");
	}

	private void printPatternNumber(CloneSets sets) throws Exception{
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
