package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;

public interface ComplexType extends StructuralProgrammingElement{
	public abstract boolean isClass();

	public abstract String getSimpleName();

	public abstract String toString();

	public abstract String getId();

	public abstract String getFullName();

	public abstract Project getProject();

	public abstract ArrayList<Method> getMethods();
	
	public abstract ArrayList<Field> getFields();

	public abstract void setMethods(ArrayList<Method> methods);
	
	/**
	 * If the inheriting type is class, this method will add a superclass
	 * If the inheriting type is interface, this method will add a super interface
	 * @param complexType
	 */
	public abstract void addSuperClassOrInterface(ComplexType complexType);
	
	public abstract boolean isGeneralType();
	
	public abstract boolean isCompatibleWith(ComplexType complextType);
	
	public abstract void addDistinctField(Field field);
	
	public abstract void addDistinctMethod(Method method);
	
	public abstract HashSet<ComplexType> getParents();

}
