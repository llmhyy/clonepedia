package mcidiff.model;

public class CorrespondentListAndSet{
	private Multiset[] multisetList; 
	private Token[] commonTokenList;
	
	/**
	 * @param multisetList
	 * @param commonTokenList
	 */
	public CorrespondentListAndSet(Multiset[] multisetList,
			Token[] commonTokenList) {
		super();
		this.multisetList = multisetList;
		this.commonTokenList = commonTokenList;
	}
	/**
	 * @return the multisetList
	 */
	public Multiset[] getMultisetList() {
		return multisetList;
	}
	/**
	 * @param multisetList the multisetList to set
	 */
	public void setMultisetList(Multiset[] multisetList) {
		this.multisetList = multisetList;
	}
	/**
	 * @return the commonTokenList
	 */
	public Token[] getCommonTokenList() {
		return commonTokenList;
	}
	/**
	 * @param commonTokenList the commonTokenList to set
	 */
	public void setCommonTokenList(Token[] commonTokenList) {
		this.commonTokenList = commonTokenList;
	}
}