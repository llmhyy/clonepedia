package ccdemon.model.rule;

import java.util.ArrayList;
import java.util.Iterator;

import mcidiff.util.DiffUtil;
import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;

public class NamingRule {
	
	private ArrayList<RuleItem> itemList = new ArrayList<>();
	private ArrayList<EquivalentComponentGroup> equivalentComponentGroupList = new ArrayList<>();
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
			
			ArrayList<Component> components = parseComponents(names, item);
			item.setComponentList(components);
		}
		
		generateEquivantGroups(this.itemList);
		
		System.currentTimeMillis();
	}
	
	private void generateEquivantGroups(ArrayList<RuleItem> itemList) {
		ArrayList<Component> compList = new ArrayList<>();
		for(RuleItem item: itemList){
			compList.addAll(item.getComponentList());
		}
		
		while(compList.size() != 0){
			EquivalentComponentGroup group = new EquivalentComponentGroup();
			Component component = compList.get(0);
			group.addComponent(component);
			compList.remove(0);
			
			Iterator<Component> iter = compList.iterator();
			while(iter.hasNext()){
				Component comp = iter.next();
				if(comp.equals(component)){
					group.addComponent(comp);
					iter.remove();
				}
			}
			this.equivalentComponentGroupList.add(group);
		}
		
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Component> parseComponents(String[] names, RuleItem item) {
		ArrayList<Sequence> sequenceList = new ArrayList<>();
		ArrayList<ArrayList<String>> stringLists = new ArrayList<>();
		for(int i=0; i<names.length; i++){
			String name = names[i];
			if(name != null && name.length() > 0){
				String[] comps = DiffUtil.splitCamelString(name);
				ArrayList<String> list = new ArrayList<>();
				list.add("$");
				for(String comp: comps){
					list.add(comp);
				}
				list.add("$");
				stringLists.add(list);
				sequenceList.add(new Sequence(list));
			}
			else{
				sequenceList.add(null);
			}
		}
		
		ArrayList<String>[] stringArray = stringLists.toArray(new ArrayList[0]);
		Object[] commonList = DiffUtil.generateCommonNodeListFromMultiSequence(stringArray);
		
		ArrayList<Component> componentList = constructComponentListInDiffRange(item, names, sequenceList, commonList);
		
		return componentList;
	}

	/**
	 * @param item
	 * @param sequenceList
	 * @param commonList
	 * @return
	 */
	private ArrayList<Component> constructComponentListInDiffRange(RuleItem item, String[] names,
			ArrayList<Sequence> sequenceList, Object[] commonList) {
		ArrayList<Component> componentList = new ArrayList<>();
		for(int i=0; i<commonList.length-1; i++){
			int startIndex = i;
			int endIndex = i+1;
			
			String startString = (String) commonList[startIndex];
			String endString = (String) commonList[endIndex];
			
			if(i != 0 && i != commonList.length-1){
				String[] supportingNames = new String[sequenceList.size()];
				for(int j=0; j<supportingNames.length; j++){
					if(names[j] != null){
						supportingNames[j] = (String) commonList[i];						
					}
				}
				Component component = new Component(item, supportingNames, false);
				componentList.add(component);
			}
			
			String[] supportingNames = new String[sequenceList.size()];
			
			for(int j=0; j<sequenceList.size(); j++){
				Sequence seq = sequenceList.get(j);
				if(seq != null){
					seq.moveStartIndex(startString);
					seq.moveEndIndex(endString);	
					String compString = seq.retrieveComponent();
					supportingNames[j] = compString;
				}
				else{
					supportingNames[j] = null;
				}				
			}
			if(isNotAllNull(supportingNames)){
				Component component = new Component(item, supportingNames, true);
				componentList.add(component);
			}
		}
		return componentList;
	}

	private boolean isNotAllNull(String[] supportingNames) {
		for(String supportingName: supportingNames){
			if(supportingName != null){
				return true;
			}
		}
		return false;
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
