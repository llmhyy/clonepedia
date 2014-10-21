package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class ComplexType extends StructuralProgrammingElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2557585228189787686L;

	protected Project project;
	protected ArrayList<Method> methods = new ArrayList<Method>();
	protected ArrayList<Field> fields = new ArrayList<Field>();
	
	public abstract boolean isClass();

	public abstract String toString();

	public abstract String getId();
	
	/**
	 * If the inheriting type is class, this method will add a superclass
	 * If the inheriting type is interface, this method will add a super interface
	 * @param complexType
	 */
	public abstract void addSuperClassOrInterface(ComplexType complexType);
	
	public abstract void addDistinctMethod(Method method);
	
	public abstract HashSet<ComplexType> getAllParents();
	
	public abstract HashSet<ComplexType> getDirectParents();

	public String getSimpleName(){
		if(fullName == null)
			return "*";
		return fullName.substring(fullName.lastIndexOf(".")+1, fullName.length());
	}
	
	public boolean isGeneralType() {
		if(fullName.startsWith("java.lang.") || fullName.startsWith("org.w3c."))
			return true;
		else
			return false;
	}

	public boolean isCompatibleWith(ComplexType complextType) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void addDistinctField(Field field) {
		for(Field f: this.fields){
			if(f.equals(field)){
				return;
			}
		}
		this.fields.add(field);
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public ArrayList<Method> getMethods() {
		return methods;
	}

	public void setMethods(ArrayList<Method> methods) {
		this.methods = methods;
	}
	
	public ArrayList<Field> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}
}
