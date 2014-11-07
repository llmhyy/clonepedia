package clonepedia.model.ontology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.model.syntactic.Path;
import clonepedia.syntactic.util.comparator.PathComparator;
import clonepedia.util.Settings;

public class CloneSets implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8625442283785857386L;
	private ArrayList<CloneSet> cloneList = new ArrayList<CloneSet>();
	
	private PathComparator pathComparator;
	private int averagePathSequenceLength;
	
	private OntologicalDataFetcher dataFetcher;
	
	//private ArrayList<TemplateMethodGroup> templateMethodGroupList = new ArrayList<TemplateMethodGroup>();
	//private ArrayList<ArrayList<TemplateFeatureGroup>> featureGroupList = new ArrayList<ArrayList<TemplateFeatureGroup>>();
	
	/**
	 * Whenever a clone instance overlapping with the given code fragment, its clone set
	 * will be returned.
	 * @param fileName
	 * @param startLine
	 * @param endLine
	 * @return
	 */
	public ArrayList<CloneSet> findCloneSets(String fileName, int startLine, int endLine){
		ArrayList<CloneSet> setsInNeed = new ArrayList<>();
		for(CloneSet set: this.cloneList){
			for(CloneInstance instance: set){
				
				//if(instance.)
				
				if(instance.getFileLocation().equals(fileName)){
					if(instance.getStartLine()<=endLine && instance.getEndLine()>=startLine){
						setsInNeed.add(set);
						continue;
					}
				}
			}
		}
		
		return setsInNeed;
	}
	
	public void buildPatternforCloneSets() throws Exception{
		int setsNum = this.getCloneList().size();
		int i = 0;
		for(CloneSet set: this.getCloneList()){
			//long start = System.currentTimeMillis();
			//if(set.getId().equals("321280"))
			if(set == null)
				continue;
			
			set.buildPatterns();
			//processed++;
			/*long end = System.currentTimeMillis();
			long time = end - start;
			setTimeMap.put(set.getId(), time);*/
			//System.out.println("Time used in buildPatterns:" + time);
			i++;
			if(i%10 == 0){
				double percentage = ((double)i/setsNum)*100;
				String percentageString = String.valueOf(percentage).substring(0, 5);
				System.out.println(percentageString + "% clone sets have been computed");
			}
		}
		//System.out.print("");
	}
	
	public int computeMedianPathSequenceLength(){
		int sampleNumber = (int)(Settings.selectedSampleRateToComputeAveragePathSequenceLength * cloneList.size());
		ArrayList<Integer> lengthList = new ArrayList<Integer>();
		
		for(int i=0; i<sampleNumber; i++){
			try{
				int index = new Random().nextInt(cloneList.size());
				CloneSet set = cloneList.get(index);
				ArrayList<Path> pathList = set.getAllContainedPaths();
				for(Path path: pathList){
					lengthList.add(path.size());
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Integer[] array = lengthList.toArray(new Integer[0]);
		Arrays.sort(array);
		averagePathSequenceLength = 2*array[array.length/2]-1;
		return averagePathSequenceLength;
	}
	
	public int computeAveragePathSequenceLength(){
		int totalNodeNumber = 0;;
		int totalPathNumber = 0;
		int sampleNumber = (int)(Settings.selectedSampleRateToComputeAveragePathSequenceLength * cloneList.size());
		
		for(int i=0; i<sampleNumber; i++){
			try{
				int index = new Random().nextInt(cloneList.size());
				CloneSet set = cloneList.get(index);
				ArrayList<Path> pathList = set.getAllContainedPaths();
				for(Path path: pathList){
					totalNodeNumber += path.size();
					totalPathNumber ++;
				}
			}
			catch(Exception e){
				e.printStackTrace();
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
	
	public void add(CloneSet set){
		this.cloneList.add(set);
	}
	
	public void remove(CloneSet set){
		this.cloneList.remove(set);
	}
	
	public ArrayList<CloneSet> getCloneList(){
		return this.cloneList;
	}
	
	public CloneSet getCloneSet(String id){
		for(CloneSet set: this.cloneList){
			if(set.getId().equals(id)){
				return set;
			}
		}
		return null;
	}

	public PathComparator getPathComparator() {
		return pathComparator;
	}

	public void setPathComparator(PathComparator pathComparator) {
		this.pathComparator = pathComparator;
	}

	public int getAveragePathSequenceLength() {
		return averagePathSequenceLength;
	}

	/*public ArrayList<TemplateMethodGroup> getTemplateMethodGroup() {
		return templateMethodGroupList;
	}

	public void setTemplateMethodGroup(ArrayList<TemplateMethodGroup> templateMethodGroupList) {
		this.templateMethodGroupList = templateMethodGroupList;
	}*/

	public OntologicalDataFetcher getDataFetcher() {
		return dataFetcher;
	}

	public void setDataFetcher(OntologicalDataFetcher dataFetcher) {
		this.dataFetcher = dataFetcher;
	}

	/*public ArrayList<ArrayList<TemplateFeatureGroup>> getFeatureGroupList() {
		return featureGroupList;
	}

	public void setFeatureGroupList(ArrayList<ArrayList<TemplateFeatureGroup>> featureGroupList) {
		this.featureGroupList = featureGroupList;
	}*/
	
	
}
