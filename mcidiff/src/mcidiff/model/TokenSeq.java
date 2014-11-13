package mcidiff.model;

import java.util.ArrayList;

/**
 * TokenSeq contains consecutive differential tokens.
 * 
 * @author linyun
 *
 */
public class TokenSeq {
	private ArrayList<Token> tokens = new ArrayList<>();

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(Token token: tokens){
			buffer.append(token.getTokenName() + " ");
		}
		
		return buffer.toString();
	}
	
	public boolean isEpisolonTokenSeq(){
		if(getTokens().size() > 0){
			for(Token t: getTokens()){
				if(!t.isEpisolon()){
					return false;
				}
			}
			
			return true;
		}
		
		return true;
	}
	
	/**
	 * @return the tokens
	 */
	public ArrayList<Token> getTokens() {
		return tokens;
	}

	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(Token t){
		if(!t.isEpisolon()){
			this.tokens.add(t);			
		}
		else if(this.tokens.size() == 0){
			this.tokens.add(t);	
		}
	}
	
	public CloneInstance getCloneInstance(){
		if(getTokens().size() != 0){
			return getTokens().get(0).getCloneInstance();
		}
		
		return null;
	}
	
	public int getStartPosition(){
		if(getTokens().size() != 0){
			return getTokens().get(0).getStartPosition();
		}
		
		return -1;
	}
	
	public int getEndPosition(){
		if(getTokens().size() != 0){
			return getTokens().get(getTokens().size()-1).getEndPosition();
		}
		
		return -1;
	}
}
