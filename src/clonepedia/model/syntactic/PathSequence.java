package clonepedia.model.syntactic;

import java.util.ArrayList;

public class PathSequence extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -701511380513530626L;
	
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
}
