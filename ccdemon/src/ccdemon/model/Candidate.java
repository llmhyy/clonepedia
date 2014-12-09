package ccdemon.model;


public class Candidate {
	
	public static final String HISTORY = "history";
	public static final String ENVIRONMENT = "environment";
	public static final String RULE = "rule";
	
	private String text;
	private double score;
	
	private String origin;
	
	/**
	 * @param text
	 * @param score
	 */
	public Candidate(String text, double score, String origin) {
		super();
		this.text = text;
		this.score = score;
		this.origin = origin;
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
	
	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public String toString(){
		return text;
	}
}
