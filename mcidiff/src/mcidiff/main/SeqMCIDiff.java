package mcidiff.main;

import java.util.ArrayList;

import mcidiff.action.Tokenizer;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.CorrespondentListAndSet;
import mcidiff.model.Multiset;
import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenMultiset;
import mcidiff.model.TokenSeq;
import mcidiff.util.DiffUtil;

import org.eclipse.jdt.core.IJavaProject;

public class SeqMCIDiff extends MCIDiff{
	/**
	 * Given a clone set, this method return a list of multiset representing the differences across
	 * the clone instances of this clone set.
	 * @param set
	 * @return
	 */
	public ArrayList<? extends Multiset> diff(CloneSet set, IJavaProject project){
		
		new Tokenizer().tokenize(set, project);
		
		ArrayList<Token>[] lists = set.getTokenLists();
		CorrespondentListAndSet cls = DiffUtil.generateMatchedTokenListFromMultiSequence(lists);
		
		TokenSequence[] sequences = transferToModel(set);
		ArrayList<? extends Multiset> results = computeDiff(cls, sequences);
		
		identifyEpsilonTokenPosition(results);
		filterCommonSet(results);
		
		System.currentTimeMillis();
		
		return results;
	}
	
	/**
	 * This method returns an ordered list of token-multiset and token-seq-multiset.
	 * For example, it returns [{int, int, int}, {a, a, a}, {=, =, =}, {b1+c1, b2+c2, b3-c3},
	 * {;, ;, ;}].
	 * 
	 * @param cls
	 * @param sequences
	 * @return
	 */
	protected ArrayList<? extends Multiset> computeDiff(CorrespondentListAndSet cls, TokenSequence[] sequences) {
		ArrayList<Multiset> seqMultisetList = new ArrayList<>();
		
		for(int i=1; i<cls.getCommonTokenList().length; i++){
			
			TokenMultiset commonSet = cls.getMultisetList()[i];
			commonSet.setCommon(true);
			for(int j=0; j<sequences.length; j++){
				CloneInstance instance = sequences[j].getCloneInstance();
				Token cToken = commonSet.findToken(instance);
				sequences[j].moveEndCursorTo(cToken);
			}
			
			SeqMultiset seqMultiset = generateSeqMultiset(sequences);
			if(!seqMultiset.isAllEmpty()){
				seqMultisetList.add(seqMultiset);
			}
			
			seqMultisetList.add(commonSet);
			
			for(int j=0; j<sequences.length; j++){
				sequences[j].moveStartCursorToEndCursor();
			}
		}
		
		return seqMultisetList;
	}

	private SeqMultiset generateSeqMultiset(TokenSequence[] sequences) {
		SeqMultiset seqMultiset = new SeqMultiset();
		for(TokenSequence seq: sequences){
			TokenSeq tokenSeq = new TokenSeq();
			for(int i=seq.getStartIndex()+1; i<=seq.getEndIndex()-1; i++){
				Token token = seq.get(i);
				tokenSeq.addToken(token);
			}
			
			if(tokenSeq.size() == 0){
				Token t = new Token(Token.episolonSymbol, null, seq.getCloneInstance(), -1, -1);
				tokenSeq.addToken(t);
			}
			
			tokenSeq.retrieveTextFromDoc();
			seqMultiset.addTokenSeq(tokenSeq);
		}
		
		return seqMultiset;
	}
	
	private void identifyEpsilonTokenPosition(ArrayList<? extends Multiset> results){
		for(int i=0; i<results.size(); i++){
			Object obj = results.get(i);
			if(obj instanceof SeqMultiset){
				SeqMultiset set = (SeqMultiset)obj;
				for(TokenSeq seq: set.getSequences()){
					if(seq.isEpisolonTokenSeq()){
						Token t = seq.getTokens().get(0);
						
						if(i != 0){
							TokenMultiset previousMultiset = (TokenMultiset)(results.get(i-1));
							Token prevToken = previousMultiset.findToken(t.getCloneInstance());
							t.setPreviousToken(prevToken);
						}
						
						if(i != results.size()-1){
							TokenMultiset postMultiset = (TokenMultiset)(results.get(i+1));
							Token postToken = postMultiset.findToken(t.getCloneInstance());
							t.setPostToken(postToken);
							
							t.setStartPosition(postToken.getStartPosition());
							t.setEndPosition(postToken.getStartPosition());	
						}
					}
					
				}
			}
		}
	}
}
