package clonepedia.model.ontology;

import java.util.ArrayList;

public class Method implements MergeableSimpleOntologicalElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6111598614301308276L;
	private String methodId;
	private ComplexType owner;
	private String methodName;
	private VarType returnType;
	private ArrayList<Variable> parameters = new ArrayList<Variable>();
	
	private String fullName;
	
	private boolean isMerged = false;
	private ArrayList<ProgrammingElement> supportingElements = new ArrayList<ProgrammingElement>();

	public Method(String methodId){
		this.methodId = methodId;
	}
	
	public int hashCode(){
		return toString().hashCode();
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

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof Method){
			Method referMethod = (Method)element;
			try{
				return referMethod.getMethodId().equals(this.getMethodId());
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
}
