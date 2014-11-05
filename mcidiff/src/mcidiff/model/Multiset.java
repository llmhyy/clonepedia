package mcidiff.model;

import java.util.ArrayList;
import java.util.Comparator;

public class Multiset{
	
	private ArrayList<Token> tokens = new ArrayList<>();
	private boolean isCommon = false;
	
	/**
	 * @return the isCommon
	 */
	public boolean isCommon() {
		return isCommon;
	}

	/**
	 * @param isCommon the isCommon to set
	 */
	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
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
	
	public Token findToken(CloneInstance instance){
		for(Token token: getTokens()){
			if(token.getCloneInstance().equals(instance)){
				return token;
			}
		}
		
		return null;
	}
	
	public boolean isParamertized(){
		for(int i=0; i<getTokens().size(); i++){
			Token token1 = getTokens().get(i);
			if(!token1.isEpisolon()){
				for(int j=i+1; j<getTokens().size(); j++){
					Token token2 = getTokens().get(j);
					if(!token2.isEpisolon()){
						if(!token1.equals(token2)){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isGapped(){
		for(Token token: getTokens()){
			if(token.isEpisolon()){
				return true;
			}
		}
		return false;
	}
	
	public boolean isPartiallySame(){
		for(int i=0; i<getTokens().size(); i++){
			Token token1 = getTokens().get(i);
			if(!token1.isEpisolon()){
				for(int j=i+1; j<getTokens().size(); j++){
					Token token2 = getTokens().get(j);
					if(!token2.isEpisolon()){
						if(token1.equals(token2)){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	public static Comparator<Multiset> MultisetPositionComparator =
			new Comparator<Multiset>() {

			@Override
			public int compare(Multiset prev, Multiset post) {
				if(prev != null && post != null){
					int sum = 0;
					
					for(Token token: prev.getTokens()){
						CloneInstance instance = token.getCloneInstance();
						Token corresToken = post.findToken(instance);
						
						if(!corresToken.isEpisolon() && !token.isEpisolon()){
							sum += token.getStartPosition() - corresToken.getStartPosition();;							
						}
					}
					
					return sum;
				}
				
				return 0;
			}
	};
	
}