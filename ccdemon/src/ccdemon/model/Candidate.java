package ccdemon.model;

import mcidiff.model.TokenSeq;


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
			double historyWeight, ConfigurationPointSet points) {
		double ruleValue = isRuleBased() ? 1 : 0;
		double environmentValue = isEnvironmentBased() ? 1 : 0;
		double historyValue = 0; 
		if(isHistoryBased()){
			historyValue = computeHistoryValue(points);
		}
		
		return ruleWeight*ruleValue + environmentWeight*environmentValue + historyWeight*historyValue;
	}
	
	private double computeHistoryValue(ConfigurationPointSet points){
		double occurenceWeight = 0.3;
		double contextWeight = 0.3;
		double dynamicWeight = 0.4;
		
		double occurenceValue = computeOccurrenceValue();
		double contextValue = computeContextValue(points);
		double dynamicValue = computeDynamicValue(points);
		
		return occurenceWeight*occurenceValue + contextWeight*contextValue + dynamicWeight*dynamicValue;
	}

	private double computeOccurrenceValue() {
		double count = 0;
		double totalCount = 0;
		for(TokenSeq tokenSeq: this.configurationPoint.getSeqMultiset().getSequences()){
			if(tokenSeq.getText().equals(this.text)){
				count ++;
			}
			totalCount++;
		}
		return count/totalCount;
	}

	private double computeContextValue(ConfigurationPointSet points) {
		// TODO 
		return 1;
	}
	
	private double computeDynamicValue(ConfigurationPointSet points) {
		OccurrenceTable table = points.getOccurrences();
		int columnIndex = points.getConfigurationPoints().indexOf(configurationPoint);
		
		int totalDeterminedPointSize = 0;
		double totalContribution = 0;
		
		for(ConfigurationPoint cp: points.getConfigurationPoints()){
			if(cp.isConfigured() && !cp.equals(this.configurationPoint)){
				
				double occurrenceNum = cp.findHistoryCandidateOccurenceNumber(cp.getCurrentValue());
				int count = 0;
				int determinedColumnIndex = points.getConfigurationPoints().indexOf(cp);
				 
				String[][] stringTable = table.getTable();
				for(int i=0; i<stringTable.length; i++){
					if(stringTable[i][determinedColumnIndex].equals(cp.getCurrentValue())
							&& stringTable[i][columnIndex].equals(this.text)){
						count++;
					}
				}
				
				totalContribution += count/occurrenceNum;
				totalDeterminedPointSize++;
			}
		}
		
		if(totalDeterminedPointSize == 0){
			return 0;
		}
		else{
			double value = totalContribution/totalDeterminedPointSize;
			return value;
		}
		
	}
}
