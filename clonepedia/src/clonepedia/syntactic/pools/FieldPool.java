package clonepedia.syntactic.pools;

import java.util.HashSet;

import clonepedia.model.ontology.Field;


public class FieldPool extends HashSet<Field>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 79037829027313535L;

	public Field getField(String fieldName, String ownerId){
		for(Field field: this){
			if(field.getFieldName().equals(fieldName)
					&& field.getOwnerType().getId().equals(ownerId))
				return field;
		}
		return null;
	}
}
