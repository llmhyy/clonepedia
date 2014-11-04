package mcidiff.main;

import java.util.ArrayList;
import java.util.Collections;

import mcidiff.action.Tokenizer;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.Multiset;
import mcidiff.model.Token;
import mcidiff.util.DiffUtil;
import mcidiff.util.GlobalSettings;

public class MCIDiff {
	
	/**
	 * Given a clone set, this method return a list of multiset representing the differences across
	 * the clone instances of this clone set.
	 * @param set
	 * @return
	 */
	public ArrayList<Multiset> diff(CloneSet set){
		
		new Tokenizer().tokenize(set);
		
		ArrayList<Token>[] lists = set.getTokenLists();
		Object[] commonList = DiffUtil.generateCommonNodeListFromMultiSequence(lists);
		
		TokenSequence[] sequences = transferToModel(set);
		ArrayList<Multiset> results = computeDiff(commonList, sequences);
		
		Collections.sort(results, Multiset.MultisetPositionComparator);
		
		
		return results;
	}
	
	private TokenSequence[] transferToModel(CloneSet set){
		TokenSequence[] sequence = new TokenSequence[set.getInstances().size()];
		for(int i=0; i<sequence.length; i++){
			CloneInstance instance = set.getInstances().get(i);
			sequence[i] = new TokenSequence(instance);
		}
		return sequence;
	}

	private ArrayList<Multiset> computeDiff(Object[] commonList, TokenSequence[] sequences) {
		ArrayList<Multiset> multisetList = new ArrayList<>();
		
		for(int i=1; i<commonList.length; i++){
			Token commonToken = (Token) commonList[i];
			
			/*if(i == 1086){
				System.currentTimeMillis();
			}*/
			
			for(int j=0; j<sequences.length; j++){
				sequences[j].moveCursorTo(commonToken);				
			}
			
			ArrayList<Multiset> partialSet = computeInDiffRange(sequences);
			if(partialSet.size() != 0){
				//set the position of episolon tokens in partialSet;
				//buildSequentialRelationForEpisolonToken(partialSet, sequences);
				multisetList.addAll(partialSet);
			}
			
			for(int j=0; j<sequences.length; j++){
				sequences[j].moveStartToCursor();
			}
		}
		
		
		return multisetList;
	}
	
	
	
	private void buildSequentialRelationForEpisolonToken(ArrayList<Multiset> partialSet, 
			TokenSequence[] sequences){
		for(Multiset set: partialSet){
			for(Token token: set.getTokens()){
				if(token.isEpisolon()){
					ArrayList<Token> correspondingTokens = set.findOtherTokens(token);
					for(Token correspondingToken: correspondingTokens){
						Token corresPrevious = correspondingToken.getPreviousToken();
						TokenSequence seq = findOwnerSequence(token, sequences);

						if(corresPrevious != null){
							TokenSequence corresSeq = findOwnerSequence(corresPrevious, sequences);
							if(corresSeq.get(corresSeq.getStartIndex()) == corresPrevious){
								Token previousToken = seq.getTokenList().get(seq.getStartIndex());
								setPreviousToken(token, previousToken);
							}
							else{
								Token ancestorToken = findCorrespondingToken(seq, corresPrevious, set);
								if(ancestorToken.getPreviousToken() == null){
									Token previousToken = seq.getTokenList().get(seq.getStartIndex());
									setPreviousToken(token, previousToken);
								}
								else{
									setPreviousToken(token, ancestorToken);
								}
							}
						}
						else{
							Token previousToken = seq.getTokenList().get(seq.getStartIndex());
							setPreviousToken(token, previousToken);
						}
					}
				}
			}
		}
	}

	private Token findCorrespondingToken(TokenSequence seq, Token token, Multiset set) {
		ArrayList<Token> otherTokens = set.findOtherTokens(token);
		for(Token t: otherTokens){
			if(t.getCloneInstance().equals(seq.getCloneInstance())){
				return t;
			}
		}
		
		return null;
	}

	/**
	 * @param token
	 * @param previousToken
	 */
	private void setPreviousToken(Token token, Token previousToken) {
		if(token.getPreviousToken() != null){
			if(previousToken.getStartPosition() > token.getPreviousToken().getStartPosition()){
				token.setPreviousToken(previousToken);									
			}									
		}
		else{
			token.setPreviousToken(previousToken);
		}
	}
	
	private Multiset findOwnerMultiset(Token token, ArrayList<Multiset> setList){
		for(Multiset set: setList){
			for(Token t: set.getTokens()){
				if(t == token){
					return set;
				}
			}
		}
		return null;
	}
	
	private TokenSequence findOwnerSequence(Token token, TokenSequence[] sequences){
		for(int i=0; i<sequences.length; i++){
			if(sequences[i].getCloneInstance() == token.getCloneInstance()){
				return sequences[i];
			}
		}
		return null;
	}

	/**
	 * compute the differences in differential ranges. A *range* in a token sequence is between
	 * start index and cursor index - 1.
	 * 
	 * @param sequences
	 * @return
	 */
	private ArrayList<Multiset> computeInDiffRange(TokenSequence[] sequences) {
		ArrayList<Multiset> multisetList = new ArrayList<>();
		for(int i=0; i<sequences.length; i++){
			TokenSequence seedSequence = sequences[i];
			TokenSequence[] otherSeqs = findOtherSequences(sequences, sequences[i]);
			
			for(int j=seedSequence.getStartIndex()+1; j<=seedSequence.getCursorIndex()-1; j++){
				Token seedToken = seedSequence.getTokenList().get(j);
				
				if(!seedToken.isMarked()){
					Multiset multiset = new Multiset();
					multiset.add(seedToken);
					for(int k=0; k<otherSeqs.length; k++){
						TokenSequence otherSeq = otherSeqs[k];
						Token correspondingToken = findBestMatchToken(seedToken, otherSeq);
						correspondingToken.setMarked(true);
						multiset.add(correspondingToken);
					}
					multisetList.add(multiset);
				}
			}
		}
		
		return multisetList;
	}
	
	/**
	 * Given a seed token, return the best matcher in otherSeq, if there is no such matcher, it will return
	 * a episolon token. In addition, the episolon token's clone instance should also be specified.
	 * @param seedToken
	 * @param otherSeq
	 * @return
	 */
	private Token findBestMatchToken(Token seedToken, TokenSequence otherSeq) {
		double similarity = -1;
		Token bestMatcher = null;
		
		for(int i=otherSeq.getStartIndex()+1; i<=otherSeq.getCursorIndex()-1; i++){
			Token otherToken = otherSeq.getTokenList().get(i);
			if(!otherToken.isMarked()){
				double sim = otherToken.compareWith(seedToken);
				if(sim > GlobalSettings.tokenSimilarityThreshold){
					if(similarity == -1){
						similarity = sim;
						bestMatcher = otherToken;
					}
					else{
						if(similarity < sim){
							similarity = sim;
							bestMatcher = otherToken;
						}
					}
				}
			}
		}
		
		if(bestMatcher == null){
			bestMatcher = new Token("e*", null, otherSeq.getCloneInstance(), -1, -1);
		}
		
		return bestMatcher;
	}

	private TokenSequence[] findOtherSequences(TokenSequence[] sequences, TokenSequence sequence){
		ArrayList<TokenSequence> otherSeqList = new ArrayList<>();
		for(int i=0; i<sequences.length; i++){
			if(sequences[i] != sequence){
				otherSeqList.add(sequences[i]);
			}
		}
		
		return otherSeqList.toArray(new TokenSequence[0]);
	}
}
