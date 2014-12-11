package ccdemon.model;


public class Candidate {
	
	public static final String HISTORY = "history";
	public static final String ENVIRONMENT = "environment";
	public static final String RULE = "rule";

	private String text;
	private double score;
	private ConfigurationPoint configurationPoint;
	
	private String origin;
	
	/**
	 * @param text
	 * @param score
	 */
	public Candidate(String text, double score, String origin, ConfigurationPoint configurationPoint) {
		super();
		this.text = text;
		this.score = score;
		this.origin = origin;
		this.configurationPoint = configurationPoint;
	}
	
	public boolean isHistoryBased(){
		return getOrigin().equals(Candidate.HISTORY);
	}
	
	public boolean isEnvironmentBased(){
		return getOrigin().equals(Candidate.ENVIRONMENT);
	}
	
	public boolean isRuleBased(){
		return getOrigin().equals(Candidate.RULE);
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

	/**
	 * @return the configurationPoint
	 */
	public ConfigurationPoint getConfigurationPoint() {
		return configurationPoint;
	}

	/**
	 * @param configurationPoint the configurationPoint to set
	 */
	public void setConfigurationPoint(ConfigurationPoint configurationPoint) {
		this.configurationPoint = configurationPoint;
	}

	public double computeScore(double ruleWeight, double environmentWeight,
			double occuranceWeight) {
		double ruleValue = isRuleBased() ? 1 : 0;
		double environmentValue = isEnvironmentBased() ? 1 : 0;
		double historyValue = isHistoryBased() ? 1 : 0;
		
		return ruleWeight*ruleValue + environmentWeight*environmentValue + occuranceWeight*historyValue;
	}
	
	
}
