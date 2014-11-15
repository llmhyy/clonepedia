package ccdemon.model;


import java.util.ArrayList;
import java.util.HashMap;

import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

public class ConfigurationPoint {
	/**
	 * The token sequence that need to be changed.
	 */
	private TokenSeq modifiedTokenSeq;
	/**
	 * The copied token sequence, which is different from (@code modifiedTokenSeq) in that
	 * their position and AST nodes are different.
	 */
	private TokenSeq copiedTokenSeq;
	/**
	 * candidate token sequences to replace {@code tokenSeq} field
	 */
	private SeqMultiset seqMultiset;
	
	private ArrayList<Candidate> candidates = new ArrayList<>();
	/**
	 * @param tokenSeq
	 * @param seqMultiset
	 */
	public ConfigurationPoint(TokenSeq tokenSeq, SeqMultiset seqMultiset) {
		super();
		this.copiedTokenSeq = tokenSeq;
		this.seqMultiset = seqMultiset;
		
		organizeCandidate(getCopiedTokenSeq(), getSeqMultiset());
	}

	private void organizeCandidate(TokenSeq copiedTokenSeq,
			SeqMultiset seqMultiset) {
		// TODO may need to collect more information to adjust the score of candidate to indidate candidate ranking.
		HashMap<TokenSeq, Integer> map = new HashMap<>();
		for(TokenSeq seq: seqMultiset.getSequences()){
			Integer count = map.get(seq);
			if(count == null){
				count = new Integer(1);
			}
			else{
				count++;
			}
			
			map.put(seq, count);
		}
		
		for(TokenSeq seq: map.keySet()){
			Candidate candidate = new Candidate(seq.getText(), map.get(seq));
			candidates.add(candidate);
		}
	}

	/**
	 * @return the tokenSeq
	 */
	public String toString(){
		return copiedTokenSeq.toString();
	}
	
	public TokenSeq getCopiedTokenSeq() {
		return copiedTokenSeq;
	}
	/**
	 * @return the candidates
	 */
	public ArrayList<Candidate> getCandidates() {
		return candidates;
	}

	/**
	 * @param candidates the candidates to set
	 */
	public void setCandidates(ArrayList<Candidate> candidates) {
		this.candidates = candidates;
	}

	/**
	 * @param tokenSeq the tokenSeq to set
	 */
	public void setCopiedTokenSeq(TokenSeq tokenSeq) {
		this.copiedTokenSeq = tokenSeq;
	}
	/**
	 * @return the seqMultiset
	 */
	public SeqMultiset getSeqMultiset() {
		return seqMultiset;
	}
	/**
	 * @param seqMultiset the seqMultiset to set
	 */
	public void setSeqMultiset(SeqMultiset seqMultiset) {
		this.seqMultiset = seqMultiset;
	}
	/**
	 * @return the modifiedTokenSeq
	 */
	public TokenSeq getModifiedTokenSeq() {
		return modifiedTokenSeq;
	}
	/**
	 * @param modifiedTokenSeq the modifiedTokenSeq to set
	 */
	public void setModifiedTokenSeq(TokenSeq modifiedTokenSeq) {
		this.modifiedTokenSeq = modifiedTokenSeq;
	}
	
	
}
