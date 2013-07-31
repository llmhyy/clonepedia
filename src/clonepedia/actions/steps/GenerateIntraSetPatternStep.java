package clonepedia.actions.steps;

import org.eclipse.core.runtime.IProgressMonitor;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.MinerUtil;

public class GenerateIntraSetPatternStep implements Step {

	private CloneSets sets;
	
	public GenerateIntraSetPatternStep(CloneSets sets){
		this.sets = sets;
	}
	
	@Override
	public void run(IProgressMonitor monitor) {
		monitor.subTask("generating intra clone set pattern");

		try{
			long startTime = 0;
			long endTime = 0;

			startTime = System.currentTimeMillis();

			int setsNum = sets.getCloneList().size();
			int i = 0;
			for (CloneSet set : sets.getCloneList()) {

				i++;
				if (set == null)
					continue;

				if (monitor.isCanceled()) {
					return;
				}

				set.buildPatterns();
				
				monitor.worked(1);

				if (i % 10 == 0) {

					double percentage = ((double) i / setsNum) * 100;
					String percentageString = String.valueOf(percentage).substring(
							0, 5);
					System.out.println(percentageString
							+ "% clone sets have been computed");
				}
			}

			endTime = System.currentTimeMillis();
			System.out.println("The time spended on buildPatternForCloneSets is: "
					+ (endTime - startTime));

			printPatternNumber(sets);
			
			MinerUtil.serialize(sets, "intra_pattern_sets");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public int getTotolEffort() {
		return sets.getCloneList().size();
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
}
