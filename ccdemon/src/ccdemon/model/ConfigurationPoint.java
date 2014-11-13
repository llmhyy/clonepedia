package ccdemon.model;


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
	 * @param tokenSeq
	 * @param seqMultiset
	 */
	public ConfigurationPoint(TokenSeq tokenSeq, SeqMultiset seqMultiset) {
		super();
		this.copiedTokenSeq = tokenSeq;
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
