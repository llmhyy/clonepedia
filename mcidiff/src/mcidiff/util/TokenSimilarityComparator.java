package mcidiff.util;

import java.util.HashMap;
import java.util.HashSet;

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
			
			double contextualSim = textualSim;
			
			if(!isGodParent(node1) && !isGodParent(node2)){
				contextualSim = computeNodeSim(node1.getParent(), node2.getParent());
			}
			
			double positionSim = 1-Math.abs(token1.getRelativePositionRatio() - token2.getRelativePositionRatio());
			
			//double avgWeight = (1.0)/3;
			return 0.5*contextualSim + 0.1*textualSim + 0.4*positionSim;
		}
		
		return 0;
	};	
	
	private boolean isGodParent(ASTNode child){
		ASTNode parent = child.getParent();
		if(parent == null){
			return true;
		}
		else{
			int parentLen = parent.getLength();
			int childLen = child.getLength();
		
			return parentLen > GlobalSettings.godParentRatio*childLen;
		}
	}
	
	private double computeNodeSim(ASTNode node1, ASTNode node2){
		if(node1 == null || node2 == null){
			return 0;
		}
		
		Character[] str1 = transferChar(node1.toString());
		Character[] str2 = transferChar(node2.toString());
		
		//double length = DiffUtil.buildLeveshteinTable(str1, str2, new DefaultComparator())[str1.length][str2.length];
		//return 2*length/(str1.length+str2.length);
		
		double sim = getSim(str1, str2);
		return sim;
	}
	
	
	
	private double getSim(Character[] str1, Character[] str2) {
		HashSet<Character> set = new HashSet<>();
		HashMap<Character, Integer> map1 = new HashMap<>(); 
		HashMap<Character, Integer> map2 = new HashMap<>(); 
		
		for(Character character: str1){
			set.add(character);
			Integer count = map1.get(character);
			if(count == null){
				count = 1;
			}
			else{
				count++;
			}
			map1.put(character, count);
		}
		for(Character character: str2){
			set.add(character);
			Integer count = map2.get(character);
			if(count == null){
				count = 1;
			}
			else{
				count++;
			}
			map2.put(character, count);
		}
		
		double numberator = 0;
		double delimeter1 = 0;
		double delimeter2 = 0;
		
		for(Character key: set){
			Integer count1 = map1.get(key);
			count1 = (count1==null)? 0 : count1;
			
			Integer count2 = map2.get(key);
			count2 = (count2==null)? 0 : count2;
			
			//double count = count1 + count2;
			//double ratio1 = count1/count;
			//double ratio2 = count2/count;
			
			numberator += count1*count2;
			delimeter1 += count1*count1;
			delimeter2 += count2*count2;
		}
		
		double consine = numberator / (Math.sqrt(delimeter1)*Math.sqrt(delimeter2));
		
		return consine;
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
