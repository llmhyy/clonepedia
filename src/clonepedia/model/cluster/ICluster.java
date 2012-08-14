package clonepedia.model.cluster;

import java.util.ArrayList;

import clonepedia.model.ontology.CloneSet;

public interface ICluster {
	public ArrayList<ICluster> getUnits();
	public void setUnits(ArrayList<ICluster> clusterList);
	public void doInnerClustering() throws Exception;
	public ICluster getParentCluster();
	public void setParentCluster(ICluster cluster);
	public ArrayList<CloneSet> getCloneSets();
}
