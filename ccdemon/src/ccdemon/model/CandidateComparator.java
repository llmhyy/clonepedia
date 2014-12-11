package ccdemon.model;

import java.util.Comparator;

public class CandidateComparator implements Comparator<Candidate>{

	private ConfigurationPointSet points;
	private OccurrenceTable table;
	
	/**
	 * @param points
	 * @param table
	 */
	public CandidateComparator(ConfigurationPointSet points,
			OccurrenceTable table) {
		super();
		this.points = points;
		this.table = table;
	}

	@Override
	public int compare(Candidate candidate1, Candidate candidate2) {
		double ruleWeight = 0.6;
		double environmentWeight = 0;
		double occuranceWeight = 0;
		
		ConfigurationPoint point = candidate1.getConfigurationPoint();
		
		if(point.toString().contains("double")){
			System.currentTimeMillis();
		}
		
		//the maxium entropy is log(m)
		double threshold = 0.5 * Math.log(point.getSeqMultiset().getSize());
		
		if(point.getHistoryEntropy() >= threshold){
			environmentWeight = 0.3;
			occuranceWeight = 0.1;
		}
		else{
			occuranceWeight = 0.3;
			environmentWeight = 0.1;
		}
		
		double score1 = candidate1.computeScore(ruleWeight, environmentWeight, occuranceWeight);
		double score2 = candidate2.computeScore(ruleWeight, environmentWeight, occuranceWeight);

		int flag = (score1 - score2 > 0) ? -1 : 1; 
		
		return flag;
	}

}
