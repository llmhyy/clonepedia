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
	
}
