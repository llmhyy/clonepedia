package ccdemon.model.rule;

public class Component {
	private RuleItem ruleItem;
	
	private String abstractName;
	private String[] supportingNames;
	
	private boolean isAbstract;
	
	
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
