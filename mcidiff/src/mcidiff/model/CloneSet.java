package mcidiff.model;

import java.util.ArrayList;

public class CloneSet {
	private String id;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	private ArrayList<CloneInstance> instances = new ArrayList<>();

	public void addInstance(CloneInstance instance){
		this.instances.add(instance);
	}
	
	/**
	 * @return the instances
	 */
	public ArrayList<CloneInstance> getInstances() {
		return instances;
	}

	/**
	 * @param instances the instances to set
	 */
	public void setInstances(ArrayList<CloneInstance> instances) {
		this.instances = instances;
	}
	
	public CloneInstance findCloneInstance(String fileName, int startLine, int endLine){
		for(CloneInstance instance: getInstances()){
			if(instance.getFileName().equals(fileName) && instance.getStartLine() == startLine
					&& instance.getEndLine() == endLine){
				return instance;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Token>[] getTokenLists(){
		ArrayList[] lists = new ArrayList[this.instances.size()];
		for(int i=0; i<this.instances.size(); i++){
			lists[i] = this.instances.get(i).getTokenList();
		}
		return lists;
	}
}
