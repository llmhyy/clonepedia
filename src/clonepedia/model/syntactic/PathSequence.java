package clonepedia.model.syntactic;

import java.util.ArrayList;

import clonepedia.model.ontology.OntologicalRelationType;

public class PathSequence extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -701511380513530626L;
	
	public static final String LOCATION = "location";
	public static final String DIFF_USAGE = "diffUsage";
	public static final String COMMON_USAGE = "commonUsage";
	
	public String toString(){
		return super.toString();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof PathSequence){
			PathSequence ps = (PathSequence)obj;
			if(ps.toString().equals(toString()))
				return true;
		}
		return false;
	}
	
	public int hashCode(){
		return toString().hashCode();
	}
	
	public boolean isOfSpecificType(OntologicalRelationType type){
		if(this != null){
			for(int i=1 ;i<this.size(); i++){
				Object ontoType = this.get(i);
				if(ontoType instanceof OntologicalRelationType){
					OntologicalRelationType relationType = (OntologicalRelationType)ontoType;
					if(relationType.equals(type))
						return true;
				}
			}
		}
		return false;
	}
	
	public boolean isLocationPath() throws Exception{
		if(this.isOfSpecificType(OntologicalRelationType.resideIn))
			return true;
		return false;
	}
	
	public String getStyle() throws Exception{
		if(isLocationPath()){
			return PathSequence.LOCATION;
		}
		else{
			return PathSequence.DIFF_USAGE;
		}
	}
}
