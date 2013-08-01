package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;

public class Interface extends VarType implements ComplexType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8826805904323619852L;
	private String interfaceId;
	private Project project;
	private ArrayList<Interface> superInterfaces = new ArrayList<Interface>();
	
	private ArrayList<Method> methods = new ArrayList<Method>();
	private ArrayList<Field> fields = new ArrayList<Field>();
	
	private boolean isMerged = false;
	private ArrayList<ProgrammingElement> supportingElements = new ArrayList<ProgrammingElement>();
	
	
	public Interface(String id){
		this.interfaceId = id;
	}
	
	public Interface(Project project, String interfaceFullName){
		this.project = project;
		this.fullName = interfaceFullName;
	}

	public Interface(String interfaceId, Project project,String interfaceFullName) {
		this.interfaceId = interfaceId;
		this.project = project;
		this.fullName = interfaceFullName;
	}

	public Interface(String fullName, ArrayList<Method> methods,
			ArrayList<Field> fields, boolean isMerged) {
		this.fullName = fullName;
		this.methods = methods;
		this.fields = fields;
		this.isMerged = isMerged;
	}

	public int hashCode(){
		try{
			return this.fullName.hashCode();
		}
		catch(NullPointerException e){
			return "nullInterface".hashCode();
		}
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Interface){
			Interface referInterface = (Interface)obj;
			
			try{
				return referInterface.getId().equals(this.getId());
			}
			catch(NullPointerException e){
				return false;
			}
		}
		return false;
	}
	
	public String getSimpleName(){
		if(fullName == null)
			return "*";
		return fullName.substring(fullName.lastIndexOf(".")+1, fullName.length());
	}
	
	public void addSuperInterface(Interface interf){
		superInterfaces.add(interf);
	}
	
	public String toString(){
		return this.fullName;
	}
	
	public String getId() {
		return interfaceId;
	}

	public void setId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String interfaceFullName) {
		this.fullName = interfaceFullName;
	}

	public Project getProject() {
		return project;
	}

	public ArrayList<Method> getMethods() {
		return methods;
	}

	public void setMethods(ArrayList<Method> methods) {
		this.methods = methods;
	}

	public ArrayList<Interface> getSuperInterfaces() {
		return superInterfaces;
	}

	public void setSuperInterfaces(ArrayList<Interface> superInterfaces) {
		this.superInterfaces = superInterfaces;
	}
	
	public boolean isClass() {
		return false;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Interface;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();

		successors.addAll(superInterfaces);
		
		return successors;
	}

	public ArrayList<Field> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Interface){
			Interface referInterface = (Interface)element;
			try{
				return referInterface.getId().equals(this.getId());
			}
			catch(NullPointerException e){
				return false;
			}
		}
		return false;
	}

	@Override
	public int getConcreteType() {
		return VarType.InterfaceType;
	}

	@Override
	public String getSimpleElementName() {
		return getSimpleName();
	}
	
	@Override
	public void addSuperClassOrInterface(ComplexType complexType) {
		if(complexType instanceof Interface)
			this.superInterfaces.add((Interface)complexType);
	}
	
	@Override
	public boolean isGeneralType() {
		if(fullName.startsWith("java.lang.") || fullName.startsWith("org.w3c."))
			return true;
		else
			return false;
	}

	@Override
	public boolean isCompatibleWith(ComplexType complextType) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isMerged(){
		return this.isMerged;
	}
	
	@Override
	public ArrayList<ProgrammingElement> getSupportingElements(){
		return this.supportingElements;
	}
	
	@Override
	public void addDistinctField(Field field) {
		for(Field f: this.fields){
			if(f.equals(field)){
				return;
			}
		}
		this.fields.add(field);
	}

	@Override
	public void addDistinctMethod(Method method) {
		for(Method m: this.methods){
			if(m.equals(method)){
				return;
			}
		}
		this.methods.add(method);
	}
	
	@Override
	public HashSet<ComplexType> getParents() {
		HashSet<ComplexType> parents = new HashSet<ComplexType>();
		for(Interface interf: this.superInterfaces){
			parents.add(interf);
		}
		
		collectParents(parents);
		
		return parents;
	}
	
	private void collectParents(HashSet<ComplexType> parents){
		
		if(parents.size() == 0){
			return;
		}
		else{
			for(ComplexType type: parents){
				parents.addAll(type.getParents());
			}
		}
	}
}
