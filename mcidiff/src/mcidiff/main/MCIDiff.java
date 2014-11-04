package mcidiff.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
		identifyEpsilonTokenPosition(results);
		
		filterCommonSet(results);
		
		return results;
	}
	
	private void filterCommonSet(ArrayList<Multiset> results){
		Iterator<Multiset> iterator = results.iterator();
		while(iterator.hasNext()){
			Multiset set = iterator.next();
			if(set.isCommon()){
				iterator.remove();
			}
		}
	}
	
	/**
	 * results is a sorted multisets w.r.t position.
	 * @param results
	 */
	private void identifyEpsilonTokenPosition(ArrayList<Multiset> results){
		for(int i=0; i<results.size(); i++){
			Multiset set = results.get(i);
			for(Token token: set.getTokens()){
				if(token.isEpisolon()){
					/**
					 * set episolon's previous token
					 */
					if(i != 0){
						Token prevToken = findPreviousNonEpisolonToken(i, results, token);
						if(prevToken != null){
							token.setPreviousToken(prevToken);
							//token.setStartPosition(prevToken.getEndPosition());
													
						}
					}
					
					/**
					 * set episolon's post token
					 */
					if(i != results.size()-1){
						Token postToken = findPostNonEpisolonToken(i, results, token);
						if(postToken != null){
							token.setPostToken(postToken);		
							//token.setEndPosition(postToken.getStartPosition());	
						}
					}
				}
			}
		}
	}
	
	private Token findPreviousNonEpisolonToken(int index, ArrayList<Multiset> results, Token episolonToken) {
		int cursor = index-1;
		while(cursor >= 0){
			Multiset prevSet = results.get(cursor);
			Token previousToken = prevSet.findToken(episolonToken.getCloneInstance());
			if(!previousToken.isEpisolon()){
				return previousToken;
			}
			cursor--;
		}
		
		return null;
	}
	
	private Token findPostNonEpisolonToken(int index, ArrayList<Multiset> results, Token episolonToken) {
		int cursor = index+1;
		while(cursor < results.size()){
			Multiset postSet = results.get(cursor);
			Token postToken = postSet.findToken(episolonToken.getCloneInstance());
			if(!postToken.isEpisolon()){
				return postToken;
			}
			cursor++;
		}
		
		return null;
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
			
			Multiset commonSet = new Multiset();
			commonSet.setCommon(true);
			for(int j=0; j<sequences.length; j++){
				Token cToken = sequences[j].moveCursorTo(commonToken);
				commonSet.add(cToken);
			}
			multisetList.add(commonSet);
			
			ArrayList<Multiset> partialSet = computeInDiffRange(sequences);
			if(partialSet.size() != 0){
				multisetList.addAll(partialSet);
			}
			
			for(int j=0; j<sequences.length; j++){
				sequences[j].moveStartToCursor();
			}
		}

		return multisetList;
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
