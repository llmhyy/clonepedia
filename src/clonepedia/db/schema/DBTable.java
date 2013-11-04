package clonepedia.db.schema;

import java.lang.reflect.Modifier;

public abstract class DBTable {
	protected String value;
	
	public String toString(){
		return this.value;
	}
	
	public String getName(){
		return this.value;
	}
	
	protected boolean isInputValid(String inputName, @SuppressWarnings("rawtypes") Class tableClass){
		for(java.lang.reflect.Field field: tableClass.getFields()){
			int modifier = field.getModifiers();
			if(Modifier.isFinal(modifier) && Modifier.isStatic(modifier)){
				if(field.getName().equals(inputName))
					return true;
			}
		}
		
		return false;
	} 
}
