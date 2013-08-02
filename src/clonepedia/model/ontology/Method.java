package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.java.util.JavaMetricComputingUtil;
import clonepedia.model.cluster.IClusterable;
import clonepedia.model.template.TemplateMethodGroup;

public class Method implements MergeableSimpleOntologicalElement, IClusterable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6111598614301308276L;
	private String methodId;
	private ComplexType owner;
	private String methodName;
	private VarType returnType;
	private ArrayList<Variable> parameters = new ArrayList<Variable>();
	private HashSet<CloneInstance> cloneInstances = new HashSet<CloneInstance>();
	
	private HashSet<Method> callerMethods = new HashSet<Method>();
	private HashSet<Method> calleeMethods = new HashSet<Method>();
	private ArrayList<TemplateMethodGroup> templateGroupList = new ArrayList<TemplateMethodGroup>();
	
	private String fullName;
	
	private boolean isMerged = false;
	private ArrayList<ProgrammingElement> supportingElements = new ArrayList<ProgrammingElement>();

	public Method(String methodId){
		this.methodId = methodId;
	}
	
	public Method(ComplexType owner, String methodName, VarType returnType,
			ArrayList<Variable> parameters) {
		super();
		this.owner = owner;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameters = parameters;
	}
	
	public Method(String methodName, VarType returnType,
			ArrayList<Variable> parameters, boolean isMerged) {
		super();
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameters = parameters;
		this.isMerged = isMerged;
	}

	public Method(String methodId, ComplexType owner, String methodName,
			VarType returnType, ArrayList<Variable> parameters) {
		super();
		this.methodId = methodId;
		this.owner = owner;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameters = parameters;
	}
	
	public int hashCode(){
		return super.hashCode();
		//return toString().hashCode();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof OntologicalElement){
			OntologicalElement ele = (OntologicalElement)obj;
			return this.equals(ele);
		}
		return false;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Method){
			Method referMethod = (Method)element;
			try{
				return this.getOwner().getFullName().equals(referMethod.getOwner().getFullName())
						&& this.toString().equals(referMethod.toString());
			}
			catch(NullPointerException e){
				return false;
			}
			
		}
		return false;
	}

	public String toString() {
		String methodString = (returnType == null) ? "" : returnType.toString()
				+ " ";
		return methodString + getNonReturnTypeExpression();
	}
	
	public String getNonReturnTypeExpression(){
		String methodString = methodName + "(";
		if (0 != parameters.size()) {
			for (Variable variable : parameters)
				methodString += variable.getVariableType() + " " + variable.getVariableName() + ", ";
			methodString = methodString.substring(0, methodString.length() - 2);
		}

		methodString += ")";
		return methodString;
	}

	public boolean isDeclaredInClass() {
		return owner.getId().substring(0, 2).equals("cl");
	}

	public void addParameterType(Variable varible) {
		this.parameters.add(varible);
	}

	public ComplexType getOwner() {
		return owner;
	}

	public void setOwner(ComplexType owner) {
		this.owner = owner;
	}

	public VarType getReturnType() {
		return returnType;
	}

	public void setReturnType(VarType returnType) {
		this.returnType = returnType;
	}

	public String getMethodId() {
		if(methodId == null){
			methodId = getFullName();
		}
		return methodId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}

	public ArrayList<Variable> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Variable> parameters) {
		this.parameters = parameters;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.Method;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		successors.add(owner);
		
		if(returnType instanceof ComplexType)
			successors.add((ComplexType)returnType);

		for(Variable v: parameters){
			VarType vType = v.getVariableType();
			if(vType instanceof ComplexType){
				successors.add((ComplexType)vType);
			}
		}
		
		return successors;
	}
	
	public String getFullName(){
		if(this.fullName == null){
			ComplexType type = getOwner();
			this.fullName = type.getFullName() + "." + this.getMethodName() + "(";
			for(Variable v: this.getParameters()){
				this.fullName += v.getVarType().getFullName() + " ";
			}
			this.fullName += ")";
		}
		
		return this.fullName;
	}

	@Override
	public VarType getVarType() {
		return this.returnType;
	}

	@Override
	public String getName() {
		return this.methodName;
	}

	@Override
	public String getSimpleElementName() {
		if(this.methodName != null)
			return /*"m:" +*/ this.methodName;
		else
			return "*";
	}
	
	@Override
	public boolean isMerged(){
		return this.isMerged;
	}
	
	@Override
	public ArrayList<ProgrammingElement> getSupportingElements(){
		return this.supportingElements;
	}

	public void addCloneInstance(CloneInstance instance){
		this.cloneInstances.add(instance);
	}
	
	public HashSet<CloneInstance> getCloneInstances() {
		return cloneInstances;
	}

	public void setCloneInstances(HashSet<CloneInstance> cloneInstances) {
		this.cloneInstances = cloneInstances;
	}

	@Override
	public double computeDistanceWith(IClusterable clusterable) {
		if(clusterable instanceof Method){
			Method tobeComparedMethod = (Method)clusterable;
			
			String thisLocation = this.owner.getFullName();
			String thatLocation = tobeComparedMethod.getOwner().getFullName();
			
			double packageLocationScore = JavaMetricComputingUtil.
					comparePackageLocation(thisLocation, thatLocation);
			
			//System.out.println();
			
			double classLocationScore = JavaMetricComputingUtil.
					compareClassLocation((Class)this.getOwner(), (Class)tobeComparedMethod.getOwner());
			
			double returnTypeScore = JavaMetricComputingUtil.
					compareReturnType(this.getReturnType(), tobeComparedMethod.getReturnType());
			
			double paramScore = JavaMetricComputingUtil.
					compareParameterList(this.getParamTypeStringList(), tobeComparedMethod.getParamTypeStringList());
			
			return 1 - (packageLocationScore + classLocationScore + returnTypeScore + paramScore)/4;
		}
		
		else return 0;
	}
	
	public ArrayList<String> getParamTypeStringList(){
		ArrayList<String> paramList = new ArrayList<String>();
		for(Variable v: this.parameters){
			paramList.add(v.getVarType().getFullName());
		}
		
		return paramList;
	}

	public HashSet<Method> getCallerMethods() {
		return callerMethods;
	}


	public void addCallerMethod(Method m){
		this.callerMethods.add(m);
	}


	public HashSet<Method> getCalleeMethod() {
		return this.calleeMethods;
	}
	
	public void addCalleeMethod(Method m){
		this.calleeMethods.add(m);
	}

	public ArrayList<TemplateMethodGroup> getTemplateGroupList() {
		return templateGroupList;
	}

	public void setTemplateGroupList(ArrayList<TemplateMethodGroup> templateGroupList) {
		this.templateGroupList = templateGroupList;
	}
	
	public void addTemplateGroup(TemplateMethodGroup group){
		this.templateGroupList.add(group);
	}
}
