package clonepedia.actions.steps;

import org.eclipse.core.runtime.IProgressMonitor;

import clonepedia.model.ontology.CloneSets;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.util.MinerUtil;

public class GenerateCloneTopicStep implements Step {

	private CloneSets sets;
	
	public GenerateCloneTopicStep(CloneSets sets){
		this.sets = sets;
	}
	
	@Override
	public void run(IProgressMonitor monitor) {
		if(monitor.isCanceled()){
			return;
		}
		
		monitor.subTask("generating topic of clone sets");
		
		SemanticClusteringer clusteringer = new SemanticClusteringer();
		
		//CloneSets sets = (CloneSets)MinerUtil.deserialize("syntactic_sets");
		clusteringer.doClustering(sets.getCloneList());
		monitor.worked(1);
		
		try {
			MinerUtil.serialize(sets, "sets", false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getTotolEffort() {
		// TODO Auto-generated method stub
		return 1;
	}
}
