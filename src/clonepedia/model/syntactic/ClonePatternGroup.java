package clonepedia.model.syntactic;

import java.util.ArrayList;

import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.MergeableSimpleConcreteElement;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.Variable;
import clonepedia.syntactic.util.abstractor.PathAbstractor;


public class ClonePatternGroup extends ArrayList<PathPatternGroup>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5768376571179989012L;
	private PathSequence abstractPathSequence = new PathSequence();
	
	/**
	 * this id is used to identify a unique clone pattern group. Currently(2013/1/31),
	 * it is still possible to generate two clone patterns with the same name. Therefore,
	 * we need to identify them in UI.
	 */
	private String uniqueId;
	
	/**
	 * "location", "diffUsage" and "commonUsage" are the valid options for
	 * style in clone pattern.
	 */
	private String style;

	/**
	 * ppg is the first path pattern group (intra clone set pattern) added into the clone pattern group
	 * (inter clone set pattern).
	 * @param ppg
	 * @param style
	 */
	public ClonePatternGroup(PathPatternGroup ppg, String style){
		this.add(ppg);
		this.setStyle(style);
		this.setAbstractPathSequence(this.get(0).getAbstractPathSequence());
	}
	
	public void updateAbstractPathSequence() throws Exception{
		this.setAbstractPathSequence(new PathAbstractor().extractAbstractPathFromClonePatternGroup(this));
	}
	
	/**
	 * Assume that an incremental path pattern is added into this group, the abstracted path updating
	 * work could be done just by comparing new added path and current abstracted path.
	 * @throws Exception
	 */
	public void updateAbstractPathSequenceByJustOneComparison()  throws Exception{
		
		if(this.size() == 1)
			this.setAbstractPathSequence(this.get(0).getAbstractPathSequence());
		else{
			PathSequence[] sequences = new PathSequence[2];
			
			PathSequence newPath = this.get(this.size()-1).getAbstractPathSequence();
			
			sequences[0] = newPath;
			sequences[1] = this.abstractPathSequence;
			
			int averageLength = this.get(0).getCloneSet().getCloneSets().getAveragePathSequenceLength();
			
			this.setAbstractPathSequence(new PathAbstractor().extractAbstractPathFromPathSequences(sequences, averageLength));
		}
		
	}

	public PathSequence getAbstractPathSequence() {
		return abstractPathSequence;
	}

	public void setAbstractPathSequence(PathSequence abstractPathSequence) {
		this.abstractPathSequence = abstractPathSequence;
	}

	public boolean isLocationPattern() throws Exception{
		return this.style.equals(PathSequence.LOCATION);
	}
	
	public boolean isUsagePattern() throws Exception{
		return this.style.equals(PathSequence.DIFF_USAGE) || this.style.equals(PathSequence.COMMON_USAGE);
	}
	
	public boolean isDiffUsagePattern() throws Exception{
		return this.style.equals(PathSequence.DIFF_USAGE);
	}
	
	public boolean isCommonUsagePattern() throws Exception{
		return this.style.equals(PathSequence.COMMON_USAGE);
	}

	public double getContainingOntologicalRelationNumber(
			OntologicalRelationType edgeType) throws Exception {
		int count = 0;
		for(PathPatternGroup ppg: this){
			OntologicalRelationType type = (OntologicalRelationType) ppg.getAbstractPathSequence().get(1);
			if(type.equals(OntologicalRelationType.resideIn))
				count++;
		}
		return count;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
