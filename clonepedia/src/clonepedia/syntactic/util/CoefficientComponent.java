package clonepedia.syntactic.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.syntactic.Path;
import clonepedia.util.Settings;

public class CoefficientComponent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4869468608172087389L;
	public static int averagePathSequenceLength;
	
	public static int computeAveragePathSequenceLength(CloneSets sets){
		int totalNodeNumber = 0;;
		int totalPathNumber = 0;
		int sampleNumber = (int)(Settings.selectedSampleRateToComputeAveragePathSequenceLength * sets.getCloneList().size());
		
		for(int i=0; i<sampleNumber; i++){
			int index = new Random().nextInt() % sets.getCloneList().size();
			CloneSet set = sets.getCloneList().get(index);
			ArrayList<Path> pathList = set.getAllContainedPaths();
			for(Path path: pathList){
				totalNodeNumber += path.size();
				totalPathNumber ++;
			}
		}
		
		/**
		 * because I have to compute the Levenshtein distance, in which a path sequence instead of path
		 * is considered. The difference between path and path sequence is that path just contained all
		 * the ontological nodes while path sequence contains both ontological nodes and ontolgical edges.
		 */
		int totalLength = 2*totalNodeNumber - 1;
		averagePathSequenceLength = totalLength / totalPathNumber;
		return averagePathSequenceLength;
	}
}
