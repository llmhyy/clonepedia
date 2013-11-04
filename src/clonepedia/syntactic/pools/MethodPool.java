package clonepedia.syntactic.pools;

import java.util.HashSet;

import clonepedia.model.ontology.Method;

public class MethodPool extends HashSet<Method> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4697556275542819008L;
	public Method getMethod(String id){
		for(Method method: this){
			if(method.getMethodId().equals(id))
				return method;
		}
		return null;
	}
}
