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
			
			clusteringer.clusterSpecificStyleOfClonePattern(locationPatterns, PathSequence.LOCATION, monitor);
			
			if(monitor.isCanceled()){
				return;
			}
			
			MinerUtil.serialize(sets, "inter_sets");
			
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
			
			return locationPatterns.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

}
