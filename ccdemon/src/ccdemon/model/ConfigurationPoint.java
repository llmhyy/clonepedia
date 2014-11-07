package ccdemon.model;


import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

public class ConfigurationPoint {
	private TokenSeq tokenSeq;
	private SeqMultiset seqMultiset;
	/**
	 * @return the tokenSeq
	 */
	public TokenSeq getTokenSeq() {
		return tokenSeq;
	}
	/**
	 * @param tokenSeq the tokenSeq to set
	 */
	public void setTokenSeq(TokenSeq tokenSeq) {
		this.tokenSeq = tokenSeq;
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
	 * @param tokenSeq
	 * @param seqMultiset
	 */
	public ConfigurationPoint(TokenSeq tokenSeq, SeqMultiset seqMultiset) {
		super();
		this.tokenSeq = tokenSeq;
		this.seqMultiset = seqMultiset;
	}
	
	
}
