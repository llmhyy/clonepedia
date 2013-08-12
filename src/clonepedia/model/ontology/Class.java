package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Class extends VarType implements ComplexType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1782670082339703936L;
	private String classId;
	private Project project;
	//private String classFullName;
	private Class superClass;
	private Class outerClass;
	private ArrayList<Interface> implementedInterfaces = new ArrayList<Interface>();
	private ArrayList<Method> methods = new ArrayList<Method>();
	private ArrayList<Field> fields = new ArrayList<Field>();
	
	private boolean isMerged = false;
	private ArrayList<ProgrammingElement> supportingElements = new ArrayList<ProgrammingElement>();
	
	public Class(String id){
		this.classId = id;
	}
	
	/*public Class(Project project, String classFullName){
		this.project = project;
		this.fullName = classFullName;
	}*/
	
	public Class(String classId, Project project, String classFullName){
		this.classId = classId;
		this.project = project;
		this.fullName = classFullName;
	}
	
	public Class(String fullName, ArrayList<Method> methods, ArrayList<Field> fields, boolean isMerged){
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
			return "nullClass".hashCode();
		}
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Class){
			Class referClass = (Class)obj;
			return referClass.getFullName().equals(this.getFullName());
		}
		return false;
	}
	
	public String getSimpleName(){
		if(fullName == null)
			return "*";
		return fullName.substring(fullName.lastIndexOf(".")+1, fullName.length());
	}
	
	public void addInterface(Interface interf){
		implementedInterfaces.add(interf);
	}
	
	public String toString(){
		return this.fullName;
	}
	
	public String getId() {
		return classId;
	}

	public void setId(String classId) {
		this.classId = classId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String classFullName) {
		this.fullName = classFullName;
	}

	public Class getSuperClass() {
		return superClass;
	}

	public void setSuperClass(Class superClass) {
		this.superClass = superClass;
	}

	public Class getOuterClass() {
		return outerClass;
	}

	public void setOuterClass(Class outerClass) {
		this.outerClass = outerClass;
	}

	public ArrayList<Interface> getImplementedInterfaces() {
		return implementedInterfaces;
	}

	public void setImplementedInterfaces(ArrayList<Interface> implementedInterfaces) {
		this.implementedInterfaces = implementedInterfaces;
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

	public boolean isClass() {
		return true;
	}
	
	public Method findMethod(String methodName, List<String> parameterList){
		for(Method method: methods){
			if(method.getMethodName().equals(methodName)){
				List<Variable> varList = method.getParameters();
				if(varList.size() == parameterList.size()){
					for(int i=0; i<varList.size(); i++){
						String mParamTypeName = varList.get(i).getVariableType().getFullName();
						String paramTypeName = parameterList.get(i);
						if(!mParamTypeName.equals(paramTypeName)){
							return null;
						}
					}
					return method;
				}
			}
		}
		return null;
	}
	
	public Interface findImplementedInterface(String interfaceName){
		for(Interface interf: implementedInterfaces){
			if(interf.getFullName().equals(interfaceName)){
				return interf;
			}
		}
		return null;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Class;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		if(null != outerClass)
			successors.add(outerClass);
		if(null != superClass)
			successors.add(superClass);
		successors.addAll(implementedInterfaces);
		
		return successors;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Class){
			Class referClass = (Class)element;
			try{
				return referClass.getId().equals(this.getId());
			}
			catch(NullPointerException e){
				return false;
			}
			
		}
		return false;
	}

	@Override
	public int getConcreteType() {
		return VarType.ClassType;
	}

	@Override
	public String getSimpleElementName() {
		return getSimpleName();
	}

	@Override
	public void addSuperClassOrInterface(ComplexType complexType) {
		if(complexType instanceof Class)
			superClass = (Class) complexType;
		else if(complexType instanceof Interface)
			this.implementedInterfaces.add((Interface)complexType);
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
		for(Interface interf: this.getImplementedInterfaces()){
			parents.add(interf);
		}
		
		if(superClass != null){
			parents.add(superClass);			
		}
		
		collectParents(parents);
		
		return parents;
	}
	
	
	
	private void collectParents(HashSet<ComplexType> parents){
		
		if(parents.size() == 0){
			return;
		}
		else{
			ArrayList<HashSet<ComplexType>> setList = new ArrayList<HashSet<ComplexType>>();
			for(ComplexType type: parents){
				HashSet<ComplexType> newParents = type.getParents();
				setList.add(newParents);
			}
			
			for(HashSet<ComplexType> set: setList){
				parents.addAll(set);
			}
		}
	}
	
}
