package clonepedia.model.ontology;

import java.io.Serializable;
import java.util.ArrayList;

public class CounterRelationGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1069122289568844697L;
	private String counterRelationsId;
	private ArrayList<InstanceElementRelation> relationList = new ArrayList<InstanceElementRelation>();
	public CounterRelationGroup(String counterRelationsId) {
		super();
		this.counterRelationsId = counterRelationsId;
	}
	
	public void add(InstanceElementRelation relation){
		relationList.add(relation);
	}
	
	public String getCounterRelationsId() {
		return counterRelationsId;
	}
	public void setCounterRelationId(String counterRelationsId) {
		this.counterRelationsId = counterRelationsId;
	}
	public ArrayList<InstanceElementRelation> getRelationList() {
		return relationList;
	}
	public void setRelationList(ArrayList<InstanceElementRelation> relationList) {
		this.relationList = relationList;
	}
	
	
}
