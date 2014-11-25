package mcidiff.model;

import mcidiff.util.DiffUtil;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;


public class Token {
	
	public static String episolonSymbol = "e*";
	
	private String tokenName;
	/**
	 * the minimum AST node which encloses the token.
	 */
	private ASTNode node;
	private CloneInstance cloneInstance;
	
	private int startPosition;
	private int endPosition;
	
	private Token previousToken;
	private Token postToken;
	
	private boolean isMarked;
	
	/**
	 * @param tokenName
	 * @param node
	 */
	public Token(String tokenName, ASTNode node, CloneInstance cloneInstance, 
			int startPosition, int endPosition) {
		super();
		this.tokenName = tokenName;
		this.node = node;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.setCloneInstance(cloneInstance);
	}
	
	@Override
	public String toString(){
		return this.tokenName;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Token){
			Token thatToken = (Token)obj;

			if(thatToken.getNode() == null && getNode() == null){
				return getTokenName().equals(thatToken.getTokenName());
			}
			else if(thatToken.getNode() != null && getNode() != null){
				return getTokenName().equals(thatToken.getTokenName()) &&
						astMatch(thatToken.getNode(), getNode());
			}
		}
		
		return false;
	}
	
	private boolean astMatch(ASTNode node1, ASTNode node2){
		if(node1 instanceof MethodInvocation && node2 instanceof SuperMethodInvocation){
			return true;
		}
		else if(node1 instanceof SuperMethodInvocation && node2 instanceof MethodInvocation){
			return true;
		}
		else{
			return node1.getNodeType() == node2.getNodeType();
		}
	}
	
	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}
	/**
	 * @param tokenName the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	/**
	 * @return the node
	 */
	public ASTNode getNode() {
		return node;
	}
	/**
	 * @param node the node to set
	 */
	public void setNode(ASTNode node) {
		this.node = node;
	}

	/**
	 * @return the cloneInstance
	 */
	public CloneInstance getCloneInstance() {
		return cloneInstance;
	}

	/**
	 * @param cloneInstance the cloneInstance to set
	 */
	public void setCloneInstance(CloneInstance cloneInstance) {
		this.cloneInstance = cloneInstance;
	}

	/**
	 * @return the isMarked
	 */
	public boolean isMarked() {
		return isMarked;
	}

	/**
	 * @param isMarked the isMarked to set
	 */
	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}
	
	/**
	 * @return the startPosition
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPosition the startPosition to set
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * @param endPosition the endPosition to set
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	/**
	 * @return the previousToken
	 */
	public Token getPreviousToken() {
		return previousToken;
	}

	/**
	 * @param previousToken the previousToken to set
	 */
	public void setPreviousToken(Token previousToken) {
		this.previousToken = previousToken;
	}

	/**
	 * @return the postToken
	 */
	public Token getPostToken() {
		return postToken;
	}

	/**
	 * @param postToken the postToken to set
	 */
	public void setPostToken(Token postToken) {
		this.postToken = postToken;
	}

	public double compareWith(Token seedToken) {
		ASTNode seedNode = seedToken.getNode();
		ASTNode thisNode = getNode();
		if(seedNode.getNodeType() != thisNode.getNodeType()){
			return 0;
		}
		else{
			if(seedNode.getNodeType() == ASTNode.SIMPLE_NAME 
					&& thisNode.getNodeType() == ASTNode.SIMPLE_NAME){
				SimpleName seedName = (SimpleName)seedNode;
				SimpleName thisName = (SimpleName)thisNode;
				
				ASTNode seedParent = seedName.getParent();
				ASTNode thisParent = thisName.getParent();
				/**
				 * type should not be matched to method/field/variable
				 */
				if(seedParent instanceof Type || thisParent instanceof Type){
					if(seedParent.getNodeType() != thisParent.getNodeType()){
						return 0;
					}
				}
				
			}
			
			return 0.1 + DiffUtil.compareStringSimilarity(seedToken.getTokenName(), getTokenName());
		}
		
	}
	
	public boolean isEpisolon(){
		return getTokenName().equals(Token.episolonSymbol);
	}
}
