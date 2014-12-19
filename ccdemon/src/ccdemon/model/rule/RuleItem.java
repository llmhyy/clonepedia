package ccdemon.model.rule;

import java.util.ArrayList;

import ccdemon.model.ConfigurationPoint;

public class RuleItem {
	/**
	 * the length of {@code instanceList} should be same as the candidate number of
	 * {@code configurationPoint}
	 */
	private ConfigurationPoint configurationPoint;
	
	private ArrayList<Component> componentList;
	private ArrayList<NameInstance> nameInstanceList = new ArrayList<>();
	
	public RuleItem(ConfigurationPoint configurationPoint){
		this.configurationPoint = configurationPoint;
	}
	
	public String toString(){
		return this.componentList.toString();
	}
	
	/**
	 * @return the nameInstanceList
	 */
	public ArrayList<NameInstance> getNameInstanceList() {
		return nameInstanceList;
	}



	public void addNameInstance(NameInstance name){
		this.nameInstanceList.add(name);
	}

	public boolean isChangeable(){
		return this.configurationPoint != null;
	}
	
	/**
	 * @return the configurationPoint
	 */
	public ConfigurationPoint getConfigurationPoint() {
		return configurationPoint;
	}

	/**
	 * @param configurationPoint the configurationPoint to set
	 */
	public void setConfigurationPoint(ConfigurationPoint configurationPoint) {
		this.configurationPoint = configurationPoint;
	}

	/**
	 * @return the componentList
	 */
	public ArrayList<Component> getComponentList() {
		return componentList;
	}

	/**
	 * @param componentList the componentList to set
	 */
	public void setComponentList(ArrayList<Component> componentList) {
		this.componentList = componentList;
	}
	
	
}
