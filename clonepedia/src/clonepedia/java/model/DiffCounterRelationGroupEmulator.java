package clonepedia.java.model;

import java.util.ArrayList;

/**
 * a counter diff in a clone set
 * @author linyun
 *
 */

public class DiffCounterRelationGroupEmulator {
	
	private String id;
	ArrayList<DiffInstanceElementRelationEmulator> relations
		= new ArrayList<DiffInstanceElementRelationEmulator>();
	
	
	public DiffCounterRelationGroupEmulator() {
		super();
	}

	public DiffCounterRelationGroupEmulator(String id) {
		super();
		this.id = id;
	}
	
	public void addRelation(DiffInstanceElementRelationEmulator relation){
		relations.add(relation);
	}
	
	
	public ArrayList<DiffInstanceElementRelationEmulator> getElements() {
		return relations;
	}

	public void setRelations(
			ArrayList<DiffInstanceElementRelationEmulator> relations) {
		this.relations = relations;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
