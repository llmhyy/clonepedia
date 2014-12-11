package ccdemon.model.rule;

import java.util.ArrayList;
import java.util.Iterator;

import mcidiff.util.DiffUtil;
import ccdemon.model.Candidate;
import ccdemon.model.ConfigurationPoint;

public class NamingRule {
	
	private ArrayList<RuleItem> itemList = new ArrayList<>();
	private EquivalentComponentGroupList equivalentComponentGroupList = new EquivalentComponentGroupList();
	/**
	 * if a (new) candidate is determined in a configuration point, it will has an impact
	 * on the number of candidates in other configuration points. It may generate or remove 
	 * a new candidate on other configuration points.
	 * 
	 * @param candidateString
	 * @param configurationPoint
	 */
	public void applyRule(String candidateString, ConfigurationPoint currentPoint){
		refreshNamingModel(candidateString, currentPoint);
		
		for(RuleItem item: itemList){
			if(item.isChangeable()){
				boolean isValidForAdding = true;
				StringBuffer buffer = new StringBuffer();
				for(Component comp: item.getComponentList()){
					if(comp.isAbstract()){
						String currentValue = comp.getGroup().getCurrentValue();
						if(currentValue != null){
							buffer.append(currentValue);
						}
						else{
							isValidForAdding = false;
							break;
						}
					}
					else{
						buffer.append(comp.getAbstractName());
					}
				}
				
				if(isValidForAdding){
					String newValue = buffer.toString();
					ConfigurationPoint point = item.getConfigurationPoint();
					point.clearRuleGeneratedCandidates();
					if(!point.contains(newValue)){
						point.getCandidates().add(new Candidate(newValue, 0, Candidate.RULE, point));							
					}
				}
				
			}
		}
		
	}
	
	private void refreshNamingModel(String candidateString, ConfigurationPoint currentPoint){
		EquivalentComponentGroup group = this.equivalentComponentGroupList.findEquivalentGroup(currentPoint);
		RuleItem ruleItem = this.equivalentComponentGroupList.findMatchingRuleItem(currentPoint);
		
		if(group != null && ruleItem != null){
			ArrayList<Component> components = ruleItem.getComponentList();
			String[] instanceArray = DiffUtil.splitCamelString(candidateString);
			
			TemplateMatch templateMatch = new TemplateMatch(components.size());
			
			/**
			 * find which components in the new candidate string are corresponded to the "concrete" component
			 */
			int instanceStart = 0;
			for(int i=0; i<components.size(); i++){
				Component comp = components.get(i);
				if(!comp.isAbstract()){
					int index = findMatchingIndex(instanceArray, instanceStart, comp.getAbstractName());
					if(index == -1){
						return;
					}
					else{
						templateMatch.setValue(i, index);
						instanceStart = index + 1;
					}
				}
			}
			
			/**
			 * find which component in the new candidate string are corresponded to the abstract component
			 */
			for(int i=0; i<components.size(); i++){
				Component comp = components.get(i);
				if(comp.isAbstract()){
					int startTemplateCursor = i-1;
					int endTempalteCursor = i+1;
					
					int startInstanceCursor = 0;
					int endInstanceCursor = 0;
					
					if(startTemplateCursor < 0){
						startInstanceCursor = -1;
					}
					else{
						startInstanceCursor = templateMatch.getValue(startTemplateCursor);
					}
					
					if(endTempalteCursor > components.size()-1){
						endInstanceCursor = instanceArray.length;
					}
					else{
						endInstanceCursor = templateMatch.getValue(endTempalteCursor);
					}
					
					StringBuffer buffer = new StringBuffer();
					for(int j=startInstanceCursor+1; j<=endInstanceCursor-1; j++){
						if(j != -1){
							buffer.append(instanceArray[j]);							
						}
					}
					String newComponentName = buffer.toString();
					comp.getGroup().setCurrentValue(newComponentName);
				}
			}
		}
	}
	
	private int findMatchingIndex(String[] instanceArray, int instanceStart, String compName) {
		for(int i=instanceStart; i<instanceArray.length; i++){
			if(instanceArray[i].equals(compName)){
				return i;
			}
		}
		return -1;
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
			component.setGroup(group);
			compList.remove(0);
			
			Iterator<Component> iter = compList.iterator();
			while(iter.hasNext()){
				Component comp = iter.next();
				if(comp.equals(component)){
					group.addComponent(comp);
					comp.setGroup(group);
					iter.remove();
				}
			}
			this.equivalentComponentGroupList.addGroup(group);
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
	public EquivalentComponentGroupList getEquivalentComponentGroupList() {
		return equivalentComponentGroupList;
	}

	/**
	 * @param equivalentComponentGroupList the equivalentComponentGroupList to set
	 */
	public void setEquivalentComponentGroupList(
			EquivalentComponentGroupList equivalentComponentGroupList) {
		this.equivalentComponentGroupList = equivalentComponentGroupList;
	}
}
