package clonepedia.model.ontology;

import java.util.ArrayList;

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
	
	public abstract void addSuperClassOrInterface(ComplexType complexType);
	
	public abstract boolean isGeneralType();
	
	public abstract boolean isCompatibleWith(ComplexType complextType);
	
	public abstract void addDistinctField(Field field);
	
	public abstract void addDistinctMethod(Method method);

}
