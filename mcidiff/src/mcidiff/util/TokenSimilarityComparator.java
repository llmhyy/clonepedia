package mcidiff.util;

import mcidiff.model.Token;

import org.eclipse.jdt.core.dom.ASTNode;

public class TokenSimilarityComparator{
	public double compute(Token token1, Token token2){
		
		if(token1.isEpisolon() && token2.isEpisolon()){
			return 1;
		}
		
		ASTNode node1 = token1.getNode();
		ASTNode node2 = token2.getNode();
		
		if(node1 != null && node2 != null){
			Character[] str1 = transferChar(node1.toString());
			Character[] str2 = transferChar(node2.toString());
			
			double length = DiffUtil.buildLeveshteinTable(str1, str2)[str1.length][str2.length];
			
			return 2*length/(str1.length+str2.length);
		}
		
		return 0;
	};
	
	private Character[] transferChar(String string){
		char[] chars = string.toCharArray();
		Character[] charactors = new Character[chars.length];
		for(int i=0; i<chars.length; i++){
			charactors[i] = new Character(chars[i]);
		}
		
		return charactors;
	}
}
