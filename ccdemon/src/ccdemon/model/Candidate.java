package ccdemon.model;

import mcidiff.model.TokenSeq;

public class Candidate {
	private TokenSeq tokenSeq;
	private double score;
	
	/**
	 * @param tokenSeq
	 * @param score
	 */
	public Candidate(TokenSeq tokenSeq, double score) {
		super();
		this.tokenSeq = tokenSeq;
		this.score = score;
	}
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
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public String toString(){
		return getTokenSeq().toString();
	}
}
