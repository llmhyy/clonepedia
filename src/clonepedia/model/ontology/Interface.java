package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;

public class Interface extends ComplexType implements VarType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8826805904323619852L;
	private String interfaceId;
	private ArrayList<Interface> superInterfaces = new ArrayList<Interface>();
	
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
		if(isMerged){
			int hashCode = 0;
			for(ProgrammingElement element: this.supportingElements){
				hashCode += element.getFullName().hashCode();
			}
			return hashCode;
		}
		else{
			return this.fullName.hashCode();
		}
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Interface){
			Interface referInterface = (Interface)obj;
			if(referInterface.isMerged == this.isMerged){				
				if(isMerged){
					return compareMergedElement(referInterface);
				}
				else{
					return referInterface.getFullName().equals(this.getFullName());				
				}
			}
		}
		return false;
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
	public void addDistinctMethod(Method method) {
		for(Method m: this.methods){
			if(m.equals(method)){
				return;
			}
		}
		this.methods.add(method);
	}
	
	@Override
	public HashSet<ComplexType> getDirectParents() {
		HashSet<ComplexType> parents = new HashSet<ComplexType>();
		for(Interface interf: this.superInterfaces){
			parents.add(interf);
		}
		
		return parents;
	}

	@Override
	public HashSet<ComplexType> getAllParents() {
		HashSet<ComplexType> parents = getDirectParents();
		
		collectParents(parents);
		
		return parents;
	}
	
	private void collectParents(HashSet<ComplexType> parents){
		
		if(parents.size() == 0){
			return;
		}
		else{
			for(ComplexType type: parents){
				parents.addAll(type.getAllParents());
			}
		}
	}
}
