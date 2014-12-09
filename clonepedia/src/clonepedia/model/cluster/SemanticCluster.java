package clonepedia.model.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.semantic.Topic;
import clonepedia.model.semantic.WordBag;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.util.comparator.LevenshteinPatternComparator;
import clonepedia.util.MinerUtil;

public class SemanticCluster extends ArrayList<CloneSet> implements ICluster{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3832525778494178669L;
	//private HashMap<Document, ISemanticClusterable> map = new HashMap<Document, ISemanticClusterable>();
	//private Cluster cluster;
	private ArrayList<ICluster> units = new ArrayList<ICluster>(); 
	private String label;
	private ICluster parentCluster;
	private double score;

	public SemanticCluster(HashMap<Document, ISemanticClusterable> map,
			Cluster cluster) {
		super();
		//this.map = map;
		//this.cluster = cluster;
		//this.parentCluster = parentCluster;
		this.setLabel(cluster.getLabel());
		this.setScore(cluster.getScore());
		
		if(containsCloneSet(map, cluster)){
			List<Document> docList = cluster.getDocuments();
			for (Document doc : docList) {
				ISemanticClusterable unit = map.get(doc);
				CloneSet set = (CloneSet)unit;
				set.setResidingCluster(this);
				this.add(set);
			}
		}
		else{
			List<Document> docList = cluster.getDocuments();
			for (Document doc : docList) {
				ISemanticClusterable unit = map.get(doc);
				SyntacticCluster synCluster = (SyntacticCluster)unit;
				synCluster.setParentCluster(this);
				units.add(synCluster);
			}
		}
		
		for(CloneSet set: this){
			set.addTopicLabel(new Topic(this.getLabel(), this.getScore()));
		}
	}
	
	private boolean containsCloneSet(HashMap<Document, ISemanticClusterable> map, Cluster cluster){
		ISemanticClusterable unit = map.get(cluster.getDocuments().get(0));
		if(unit instanceof CloneSet)
			return true;
		else return false;
	}
	
	@Override
	public void doInnerClustering() throws Exception {
		
		/*SyntacticClusteringer scer = 
				new SyntacticClusteringer(this, new LevenshteinPatternComparator(), SyntacticClusteringer.allPatternClustering);
		ArrayList<SyntacticCluster> scList = scer.doClustering();
		
		for(SyntacticCluster cluster: scList){
			cluster.setParentCluster(this);
			units.add(cluster);
		}*/
	}


	/*public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}*/


	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public ArrayList<ICluster> getUnits() {
		return units;
	}

	public void setUnits(ArrayList<ICluster> units) {
		this.units = units;
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

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
