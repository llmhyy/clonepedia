package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import clonepedia.java.util.JavaMetricComputingUtil;
import clonepedia.model.cluster.IClusterable;
import clonepedia.util.MinerUtil;

public class Class extends ComplexType implements VarType, IClusterable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1782670082339703936L;
	private String classId;
	private String packageName;
	private Class superClass;
	private Class outerClass;
	private ArrayList<Class> innerClassList = new ArrayList<Class>();
	private ArrayList<Interface> implementedInterfaces = new ArrayList<Interface>();
	
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
		if(obj instanceof Class){
			Class referClass = (Class)obj;
			if(referClass.isMerged == this.isMerged){				
				if(isMerged){
					List<String> list1 = new ArrayList<String>();
					List<String> list2 = new ArrayList<String>();
					
					for(ProgrammingElement element: this.getSupportingElements()){
						list1.add(element.getFullName());
					}
					for(ProgrammingElement element: referClass.getSupportingElements()){
						list2.add(element.getFullName());
					}
					
					return MinerUtil.isTwoStringSetEqual(list1, list2);
				}
				else{
					return referClass.getFullName().equals(this.getFullName());				
				}
			}
		}
		return false;
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
	
	public boolean isInnerClass(){
		return this.getOuterClass() != null;
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
		for(Interface interf: this.getImplementedInterfaces()){
			parents.add(interf);
		}
		
		if(superClass != null){
			parents.add(superClass);			
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
			ArrayList<HashSet<ComplexType>> setList = new ArrayList<HashSet<ComplexType>>();
			for(ComplexType type: parents){
				HashSet<ComplexType> newParents = type.getAllParents();
				setList.add(newParents);
			}
			
			for(HashSet<ComplexType> set: setList){
				parents.addAll(set);
			}
		}
	}

	@Override
	public double computeDistanceWith(IClusterable clusterable) {
		if(clusterable instanceof Class){
			
			
			Class toBeComparedClass = (Class)clusterable;
			if(this.getFullName().contains("Main") && toBeComparedClass.getFullName().contains("Main")){
				System.out.print("");
			}
			
			double typeScore = JavaMetricComputingUtil.compareClassLocation(toBeComparedClass, this);
			double contentScore = JavaMetricComputingUtil.compareClassContent(toBeComparedClass, this);
			
			//System.out.print("");
			
			return 1 - (3*typeScore + 7*contentScore)/10;
		}
		
		return 1.0;
	}

	public ArrayList<Class> getInnerClassList() {
		return innerClassList;
	}

	public void setInnerClassList(ArrayList<Class> innerClassList) {
		this.innerClassList = innerClassList;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
