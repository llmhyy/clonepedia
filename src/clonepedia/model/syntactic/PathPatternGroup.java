package clonepedia.model.syntactic;

import java.util.ArrayList;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.PrimiType;
import clonepedia.syntactic.util.abstractor.PathAbstractor;

public class PathPatternGroup extends ArrayList<Path>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1601248845273028017L;
	private PathSequence abstractPathSequence = new PathSequence();
	private CloneSet cloneSet;
	
	public PathPatternGroup(CloneSet set){
		this.cloneSet = set;
	}
	
	public void updateAbstractPathSequence() throws Exception{
		this.abstractPathSequence = new PathAbstractor().extractAbstractPathFromPathPatternGroup(this);
	}
	
	public void updateAbstractPathSequenceByJustOneComparison()  throws Exception{
		
		if(this.size() == 1)
			this.setAbstractPathSequence(this.get(0).transferToSequence());
		else{
			PathSequence[] sequences = new PathSequence[2];
			
			PathSequence newPath = this.get(this.size()-1).transferToSequence();
			
			sequences[0] = newPath;
			sequences[1] = this.abstractPathSequence;
			
			int averageLength = this.getCloneSet().getCloneSets().getAveragePathSequenceLength();
			
			this.setAbstractPathSequence(new PathAbstractor().extractAbstractPathFromPathSequences(sequences, averageLength));
		}
	}
	
	public void addPath(Path path){
		this.add(path);
	}
	
	public String toString(){
		String str = "pattern:\n";
		int count = 1;
		for(Path p: this){
			str += count++ + ": " + p + '\n';
		}
		return str;
	}

	public double getContaningOntologicalElementNumber(OntologicalElement node) {
		int count = 0;
		for(Path p: this){
			count += p.getContaningOntologicalElementNumber(node);
		}
		return count;
	}

	public double getContainingOntologicalRelationNumber(
			OntologicalRelationType edgeType) throws Exception {
		int count = 0;
		for(Path p: this){
			count += p.getContainingOntologicalRelationNumber(edgeType);
		}
		return count;
	}
	
	
	
	/**
	 * The pattern containing "useConstant" relation should not be involved in
	 * structural clustering because it has little structural meaning.
	 * 
	 * The location pattern containing "hasParameterType" will add much noise
	 * @return
	 * @throws Exception 
	 */
	public boolean isClusterable() throws Exception{
		if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.define))
			return false;
		else if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.useConstant))
			return false;
		else if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.define) ||
				this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.refer) || 
				this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.access)){
			if(abstractPathSequence.size() <= 3)
				return false;
		}
		else if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.hasParameterType) && isLocationPattern())
			return false;
		else if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.hasType) && isLocationPattern())
			return false;
		return true;
	}
	
	public boolean isLocationPattern() throws Exception{
		if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.resideIn))
			return true;
		return false;
	}
	
	public boolean isUsagePattern() throws Exception{
		return !isLocationPattern();
	}
	
	public boolean isMethodFieldVariableUseagePattern() throws Exception{
		if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.call) 
				|| this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.access) 
				|| this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.refer))
			return true;
		else return false;
	}
	
	public boolean isComplexTypeUseagePattern(){
		if(this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.useClass) || this.abstractPathSequence.isOfSpecificType(OntologicalRelationType.useInterface))
			return true;
		return false;
	}

	public PathSequence getAbstractPathSequence() {
		return abstractPathSequence;
	}

	public void setAbstractPathSequence(PathSequence abstractPathSequence) {
		this.abstractPathSequence = abstractPathSequence;
	}

	public CloneSet getCloneSet() {
		return cloneSet;
	}

	public void setCloneSet(CloneSet set) {
		this.cloneSet = set;
	}
	
	public String getStyle() throws Exception{
		return this.abstractPathSequence.getStyle();
	}
}
