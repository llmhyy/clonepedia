package clonepedia.actions.steps;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import clonepedia.model.ontology.CloneSets;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.SyntacticClusteringer0;
import clonepedia.util.MinerUtil;

public class GenerateInterSetPatternStep implements Step {

	private CloneSets sets;
	
	public GenerateInterSetPatternStep(CloneSets sets){
		this.sets = sets;
	}
	
	@Override
	public void run(IProgressMonitor monitor) {
		if(monitor.isCanceled()){
			return;
		}
		
		monitor.subTask("generating inter clone set pattern");
		
		try{
			SyntacticClusteringer0 clusteringer  = new SyntacticClusteringer0(sets.getCloneList());
			
			
			ArrayList<PathPatternGroup> locationPatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.locationPatternClustering);
			ArrayList<PathPatternGroup> diffTypePatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.complexTypeDiffPatternClustering);
			ArrayList<PathPatternGroup> diffMemberPatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.methodFieldVariableDiffPatternClustering);
			
			
			clusteringer.clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION, monitor);
			clusteringer.clusterSpecificStyleOfClonePattern(diffTypePatterns, PathSequence.DIFF_USAGE, monitor);
			clusteringer.clusterSpecificStyleOfClonePattern(diffMemberPatterns, PathSequence.DIFF_USAGE, monitor);
			
			
			if(monitor.isCanceled()){
				return;
			}
			
			MinerUtil.serialize(sets, "inter_pattern_sets", false);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public int getTotolEffort() {
		
		try {
			SyntacticClusteringer0 clusteringer = new SyntacticClusteringer0(sets.getCloneList());
			ArrayList<PathPatternGroup> locationPatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.locationPatternClustering);
			ArrayList<PathPatternGroup> diffTypePatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.complexTypeDiffPatternClustering);
			ArrayList<PathPatternGroup> diffMemberPatterns = clusteringer.
					getPatternsFromCloneSets(sets.getCloneList(), SyntacticClusteringer0.methodFieldVariableDiffPatternClustering);
			
			return locationPatterns.size() + diffTypePatterns.size() + diffMemberPatterns.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

}
