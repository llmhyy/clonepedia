package clonepedia.model.cluster;

import java.util.ArrayList;


import clonepedia.model.ontology.CloneSet;
import clonepedia.model.semantic.WordBag;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.semantic.SemanticClusteringer;

public class SyntacticCluster extends ArrayList<CloneSet> implements ICluster, ISemanticClusterable{
	private ICluster parentCluster;
	private ArrayList<ICluster> units = new ArrayList<ICluster>();
	private ArrayList<ClonePatternGroup> clonePatterns = new ArrayList<ClonePatternGroup>();
	/**
	 * 
	 */
	private static final long serialVersionUID = -3962685128345816429L;
	
	public void merge(SyntacticCluster cluster){
		this.addAll(cluster);
	}
	
	public ArrayList<PathPatternGroup> getConcernedContainedPatternPathGroups() throws Exception{
		ArrayList<PathPatternGroup> ppgList = new ArrayList<PathPatternGroup>();
		for(CloneSet set: this)
			for(PathPatternGroup ppg: set.getAllClusterablePatterns())
				ppgList.add(ppg);
		return ppgList;
	}
	
	/**
	 * The method is just for debugging
	 * @param cloneSetId
	 * @return
	 */
	public boolean isContainsCloneSet(String cloneSetId){
		for(CloneSet set: this){
			if(set.getId().equals(cloneSetId))
				return true;
		}
		return false;
	}
	
	public WordBag getRelatedWords(){
		WordBag wordBag = new WordBag();
		
		for(CloneSet set: this)
			wordBag.addAll(set.getRelatedWords());
		
		return wordBag;
	}

	@Override
	public ArrayList<ICluster> getUnits() {
		return units;
	}

	public void setUnits(ArrayList<ICluster> units) {
		this.units = units;
	}

	@Override
	public void doInnerClustering() throws Exception {
		if(units.size() == 0){
			SemanticClusteringer scer = new SemanticClusteringer();
			ArrayList<SemanticCluster> scList = scer.doClustering(this);
			for(SemanticCluster cluster: scList){
				cluster.setParentCluster(this);
				units.add(cluster);
			}
		}
		
	}
	
	public ICluster getParentCluster() {
		return parentCluster;
	}

	public void setParentCluster(ICluster parentCluster) {
		this.parentCluster = parentCluster;
	}

	@Override
	public ArrayList<CloneSet> getCloneSets() {
		return this;
	}

	public ArrayList<ClonePatternGroup> getClonePatterns() {
		return clonePatterns;
	}

	public void setClonePatterns(ArrayList<ClonePatternGroup> clonePatterns) {
		this.clonePatterns = clonePatterns;
	}
}
