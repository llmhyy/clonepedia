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
			
			
			double textualSim = computeNodeSim(token1.getNode(), token2.getNode());
			double contextualSim = computeNodeSim(node1.getParent(), node2.getParent());
			double positionSim = 1-Math.abs(token1.getRelativePositionRatio() - token2.getRelativePositionRatio());
			
			//double avgWeight = (1.0)/3;
			return 0.8*contextualSim + 0.1*textualSim + 0.1*positionSim;
		}
		
		return 0;
	};	
	
	private double computeNodeSim(ASTNode node1, ASTNode node2){
		if(node1 == null || node2 == null){
			return 0;
		}
		
		Character[] str1 = transferChar(node1.toString());
		Character[] str2 = transferChar(node2.toString());
		
		double length = DiffUtil.buildLeveshteinTable(str1, str2, new DefaultComparator())[str1.length][str2.length];
		
		return 2*length/(str1.length+str2.length);
	}
	
	private Character[] transferChar(String string){
		char[] chars = string.toCharArray();
		Character[] charactors = new Character[chars.length];
		for(int i=0; i<chars.length; i++){
			charactors[i] = new Character(chars[i]);
		}
		
		return charactors;
	}
}
