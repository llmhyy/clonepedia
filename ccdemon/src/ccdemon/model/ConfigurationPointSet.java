package ccdemon.model;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.CompilationUnit;

import mcidiff.model.TokenSeq;
import clonepedia.model.ontology.CloneInstance;

public class ConfigurationPointSet {
	private ArrayList<ConfigurationPoint> configurationPoints = new ArrayList<>();
	private ArrayList<ReferrableCloneSet> referrableCloneSets = new ArrayList<>();
	private CompilationUnit pastedCompilationUnit;
	private int startPositionInPastedFile;

	public ConfigurationPointSet(){}
	
	/**
	 * @param configurationPoints
	 */
	public ConfigurationPointSet(ArrayList<ConfigurationPoint> configurationPoints, 
			ArrayList<ReferrableCloneSet> referrableCloneSets, CompilationUnit pastedCompilationUnit,
			int startPositionInPastedFile) {
		super();
		this.configurationPoints = configurationPoints;
		this.referrableCloneSets = referrableCloneSets;
		this.pastedCompilationUnit = pastedCompilationUnit;
		this.startPositionInPastedFile = startPositionInPastedFile;
	}
	
	public void prepareForInstallation(ArrayList<ReferrableCloneSet> referrableCloneSets){
		
		expandEnvironmentBasedCandidates(this.configurationPoints);
		generateNamingRules(this.configurationPoints, this.referrableCloneSets);
		
		OccurrenceTable occurrences = constructCandidateOccurrences(referrableCloneSets);
		adjustCandidateRanking(this.configurationPoints, occurrences);
	}
	
	private void generateNamingRules(
			ArrayList<ConfigurationPoint> configurationPoints, ArrayList<ReferrableCloneSet> referrableCloneSets) {
		// TODO Auto-generated method stub
		
	}
 
	private void expandEnvironmentBasedCandidates(
			ArrayList<ConfigurationPoint> configurationPoints) {
		for(ConfigurationPoint point: configurationPoints){
			if(point.isType()){
				//TODO find its sibling types
			}
			else if(point.isVariableOrField()){
				//TODO find compatible variable in the context
			}
			else if(point.isMethod()){
				//do nothing for now
			}
		}
	}

	private OccurrenceTable constructCandidateOccurrences(ArrayList<ReferrableCloneSet> referrableCloneSets) {
		ReferrableCloneSet rcs = referrableCloneSets.get(0);
		String[][] occurrenceTable = new String[rcs.getCloneSet().size()][configurationPoints.size()];
		CloneInstance[] instanceArray = rcs.getCloneSet().toArray(new CloneInstance[0]);
		
		for(int i=0; i<instanceArray.length; i++){
			CloneInstance instance = instanceArray[i];
			
			for(int j=0; j<configurationPoints.size(); j++){
				ConfigurationPoint cp = configurationPoints.get(j);
				TokenSeq tokenSeq = cp.getSeqMultiset().findTokenSeqByCloneInstance(instance.getFileLocation(), 
						instance.getStartLine(), instance.getEndLine());
				if(tokenSeq != null){
					occurrenceTable[i][j] = tokenSeq.getText();
				}
			}
		}
		
		return new OccurrenceTable(occurrenceTable);
	}

	private void adjustCandidateRanking(ArrayList<ConfigurationPoint> configurationPoints,
			OccurrenceTable occurrences) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the configurationPoints
	 */
	public ArrayList<ConfigurationPoint> getConfigurationPoints() {
		return configurationPoints;
	}

	/**
	 * @param configurationPoints the configurationPoints to set
	 */
	public void setConfigurationPoints(
			ArrayList<ConfigurationPoint> configurationPoints) {
		this.configurationPoints = configurationPoints;
	}
}
