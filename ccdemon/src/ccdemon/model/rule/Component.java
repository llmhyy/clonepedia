package ccdemon.model.rule;

public class Component {
	private RuleItem ruleItem;
	
	private String abstractName;
	private String[] supportingNames;
	
	private boolean isAbstract;
	
	
	public Component(RuleItem item, String[] supportingNames, boolean isAbstract) {
		this.ruleItem = item;
		this.supportingNames = supportingNames;
		this.isAbstract = isAbstract;
		
		if(isAbstract){
			this.abstractName = "*";
		}
		else{
			for(int i=0; i<supportingNames.length; i++){
				if(supportingNames[i] != null){
					this.abstractName = supportingNames[i];
					break;
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Component){
			Component thatComp = (Component)obj;
			if(thatComp.getSupportingNames().length != this.getSupportingNames().length){
				return false;
			}
			else{
				for(int i=0; i<this.getSupportingNames().length; i++){
					String thatString = thatComp.getSupportingNames()[i];
					String thisString = this.getSupportingNames()[i];
					if(thatString != null && thisString != null){
						if(!thatString.equals(thisString)){
							return false;
						}
					}
					else if((thatString != null && thisString == null) || 
							(thatString == null && thisString != null)){
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the ruleItem
	 */
	public RuleItem getRuleItem() {
		return ruleItem;
	}
	/**
	 * @param ruleItem the ruleItem to set
	 */
	public void setRuleItem(RuleItem ruleItem) {
		this.ruleItem = ruleItem;
	}
	/**
	 * @return the abstractName
	 */
	public String getAbstractName() {
		return abstractName;
	}
	/**
	 * @param abstractName the abstractName to set
	 */
	public void setAbstractName(String abstractName) {
		this.abstractName = abstractName;
	}
	/**
	 * @return the supportingNames
	 */
	public String[] getSupportingNames() {
		return supportingNames;
	}
	/**
	 * @param supportingNames the supportingNames to set
	 */
	public void setSupportingNames(String[] supportingNames) {
		this.supportingNames = supportingNames;
	}
	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	
}
