package ccdemon.model.rule;

import java.util.ArrayList;

import mcidiff.util.ASTUtil;
import mcidiff.util.DiffUtil;
import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;

public class NamingRule {
	
	private ArrayList<RuleItem> itemList;
	private ArrayList<EquivalentComponentGroup> equivalentComponentGroupList;
	/**
	 * if a (new) candidate is determined in a configuration point, it will has an impact
	 * on the number of candidates in other configuration points. It may generate or remove 
	 * a new candidate on other configuration points.
	 * 
	 * @param value
	 * @param configurationPoint
	 */
	public void applyRule(String candidate, ConfigurationPoint currentPoint, ConfigurationPointSet pointSet){
		
		//ArrayList<RuleItem> influencedItemList = detectInfluencedItems(candidate, currentPoint, itemList);
		
		for(ConfigurationPoint point: pointSet.getConfigurationPoints()){
			if(point != currentPoint){
				
			}
		}
	}
	
	/**
	 * retrieve naming pattern from {@code itemList}
	 */
	public void parseNamingPattern(){
		for(RuleItem item: this.itemList){
			ArrayList<NameInstance> instanceList = item.getNameInstanceList();
			String[] names = new String[instanceList.size()];
			for(int i=0; i<instanceList.size(); i++){
				NameInstance nameInstance = instanceList.get(i);
				if(nameInstance.isSingleToken()){
					names[i] = nameInstance.getName();
				}
				else{
					names[i] = null;
				}
			}
			
			ArrayList<Component> components = parseComponents(names);
			item.setComponentList(components);
		}
		
		generateEquivantGroups(this.itemList);
	}
	
	private void generateEquivantGroups(ArrayList<RuleItem> itemList2) {
		// TODO Auto-generated method stub
		
	}

	private ArrayList<Component> parseComponents(String[] names) {
		for(int i=0; i<names.length; i++){
			String name = names[i];
			if(name != null){
				String[] comps = DiffUtil.splitCamelString(name);
				
			}
		}
		return null;
	}

	public void addItem(RuleItem item){
		this.itemList.add(item);
	}
	
	/**
	 * @return the itemList
	 */
	public ArrayList<RuleItem> getItemList() {
		return itemList;
	}
	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(ArrayList<RuleItem> itemList) {
		this.itemList = itemList;
	}
	
	/**
	 * @return the equivalentComponentGroupList
	 */
	public ArrayList<EquivalentComponentGroup> getEquivalentComponentGroupList() {
		return equivalentComponentGroupList;
	}

	/**
	 * @param equivalentComponentGroupList the equivalentComponentGroupList to set
	 */
	public void setEquivalentComponentGroupList(
			ArrayList<EquivalentComponentGroup> equivalentComponentGroupList) {
		this.equivalentComponentGroupList = equivalentComponentGroupList;
	}
}
