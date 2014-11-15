package ccdemon.model;


public class Candidate {
	private String text;
	private double score;
	
	/**
	 * @param text
	 * @param score
	 */
	public Candidate(String text, double score) {
		super();
		this.text = text;
		this.score = score;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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
		return text;
	}
}
