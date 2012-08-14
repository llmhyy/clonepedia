package clonepedia.test.patternextraction;

import java.util.ArrayList;
import java.util.Iterator;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Project;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.syntactic.util.comparator.LevenshteinPatternComparator;
import clonepedia.syntactic.util.comparator.PathComparator;
import clonepedia.syntactic.util.comparator.PatternComparator;
import clonepedia.util.MinerUtil;
import junit.framework.TestCase;

public class TestPatternDistance extends TestCase {
	public void testPatternDistance(){
		Project project = new Project("JHotDraw7.0.6", "java", "");
		OntologicalModelGenerator generator = new OntologicalModelGenerator(project);
		
		try{
			
			PathComparator pathComparator = new LevenshteinPathComparator();
			PatternComparator patternComparator = new LevenshteinPatternComparator();
			
			/*CloneSets sets = (CloneSets)MinerUtil.deserialize("ontological_model");
			//PathComparator pathComparator = new VectorPathComparator();
			
			
			//String[] idList = {"473017", "479138", "491370", "479926", "480012", "142742", "473243", "472843", "478591", "473101", "483621", "481653", "472616"};
			//String[] idList = {"473017", "479138", "491370", "142742", "473243", "472843", "483621", "481653"};
			//String[] idList = {"361318", "577880"};
			//filterSetsById(idList, sets);
			filterSetsByInstanceNumber(4, sets);
			sets.setPathComparator(pathComparator);
			sets.computeMedianPathSequenceLength();
			//sets.computeAveragePathSequenceLength();
			
			generator.setSets(sets);
			generator.buildPatternforCloneSets();
			
			MinerUtil.serialize(generator.getSets(), "test_sets4");*/
			
			CloneSets sets = (CloneSets)MinerUtil.deserialize("test_sets4");
			generator.setSets(sets);
			
			int count = 0;
			for(CloneSet set: sets.getCloneList()){
				count += set.getAllClusterablePatterns().size();
			}
			System.out.println("The total number of concerned patterns is: " + count);
			SyntacticClusteringer clusterer = new SyntacticClusteringer(patternComparator);
			CloneSet set1 = sets.getCloneSet("564078");
			CloneSet set2 = sets.getCloneSet("564501");
			//double dis = clusterer.calculateCloneSetDistance(set1, set2);
			
			//System.out.print(dis);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void filterSetsById(String[] idList, CloneSets sets){
		Iterator<CloneSet> iterator = sets.getCloneList().iterator();
		while(iterator.hasNext()){
			CloneSet set = (CloneSet)iterator.next();
			boolean flag = false;
			for(int i=0; i<idList.length; i++){
				if(idList[i].equals(set.getId()))
					flag = true;
			}
			if(!flag)
				iterator.remove();
		}
	}
	
	private void filterSetsByInstanceNumber(int instanceNumber, CloneSets sets){
		Iterator<CloneSet> iterator = sets.getCloneList().iterator();
		while(iterator.hasNext()){
			CloneSet set = (CloneSet)iterator.next();
			if(set.size() != instanceNumber)
				iterator.remove();
		}
	}
}
