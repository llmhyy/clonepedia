package mcidiff.model;

import java.util.ArrayList;

public class Multiset {
	private ArrayList<Token> tokens = new ArrayList<>();

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
	
	public void add(Token token){
		this.tokens.add(token);
	}
	
	public String toString(){
		return tokens.toString();
	}
	
	public ArrayList<Token> findOtherTokens(Token token){
		ArrayList<Token> otherList = new ArrayList<>();
		for(Token t: tokens){
			if(t != token){
				otherList.add(t);
			}
		}
		return otherList;
	}
}
