package clonepedia.model.ontology;

import java.util.ArrayList;

public class CloneInstance implements RegionalOwner{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2663267917552058953L;
	private String cloneInstanceId;
	private CloneSet cloneSet;
	private Method residingMethod;
	private String fileLocation;
	private int startLine;
	private int endLine;
	
	private String fullName;
	
	/*private ArrayList<Method> callingMethods = new ArrayList<Method>();
	private ArrayList<Field> accessingFields = new ArrayList<Field>();
	private ArrayList<Variable> definingVariables = new ArrayList<Variable>();
	private ArrayList<Variable> referingVariables = new ArrayList<Variable>();
	private ArrayList<Constant> usingConstants = new ArrayList<Constant>();
	private ArrayList<Class> usingClasses = new ArrayList<Class>();
	private ArrayList<Interface> usingInterfaces = new ArrayList<Interface>();*/

	public CloneInstance(CloneSet cloneSet, String fileLocation, int startLine,
			int endLine) {
		super();
		this.cloneSet = cloneSet;
		this.fileLocation = fileLocation;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	public String getFullName(){
		try{
			if(this.fullName == null){
				if(fileLocation.length() == 0)
					return "instance";
				
				ComplexType residingType = residingMethod.getOwner();
				if(residingType.isClass()){
					Class residingClass = (Class)residingType;
					String residingPath = residingClass.getSimpleElementName() + "." + residingMethod.getSimpleElementName() + "(...)[" + startLine + "," + endLine + "]";
					Class outerClass = residingClass.getOuterClass();
					while(null != outerClass){
						residingPath = outerClass.getSimpleElementName() + "." + residingPath;
						outerClass = outerClass.getOuterClass();
					}
					return "@" + cloneSet.getId() + "(" + residingPath + ")";
				}
				else{				
					String fileName = fileLocation.substring(fileLocation.lastIndexOf("\\")+1, fileLocation.lastIndexOf("."));
					return "@" + cloneSet.getId()+ "(" + fileName + "." + residingMethod.getSimpleElementName() + "(...)[" + startLine + "," + endLine + "])";
				}
			}
			return this.fullName;
		}
		catch(NullPointerException e){
			//e.printStackTrace();
			return "instance";
		}
		catch(StringIndexOutOfBoundsException e){
			e.printStackTrace();
			return "instance";
		}
	}
	
	public String toString(){
		return getFullName();
	}

	public CloneInstance(String cloneInstanceId, CloneSet cloneSet,
			String fileLocation, int startLine, int endLine) {
		super();
		this.cloneInstanceId = cloneInstanceId;
		this.cloneSet = cloneSet;
		this.fileLocation = fileLocation;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public CloneInstance() {
		// TODO Auto-generated constructor stub
	}

	public boolean equals(Object object) {

		if (!(object instanceof CloneInstance))
			return false;

		CloneInstance instance = (CloneInstance) object;
		if (fileLocation.equals(instance.getFileLocation())
				&& startLine == instance.getStartLine()
				&& endLine == instance.getEndLine())
			return true;
		return false;
	}

	public int hashCode() {
		if(fileLocation == null)
			fileLocation = "";
		return startLine * endLine * fileLocation.hashCode();
	}

	public String getId() {
		return cloneInstanceId;
	}

	public void setId(String cloneInstanceId) {
		this.cloneInstanceId = cloneInstanceId;
	}

	public CloneSet getCloneSet() {
		return cloneSet;
	}

	public void setCloneSet(CloneSet cloneSet) {
		this.cloneSet = cloneSet;
	}

	public Method getResidingMethod() {
		return residingMethod;
	}

	public void setResidingMethod(Method residingMethod) {
		this.residingMethod = residingMethod;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.CloneInstance;
	}
	
	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		successors.add(residingMethod);
		/*successors.addAll(callingMethods);
		successors.addAll(accessingFields);
		successors.addAll(definingVariables);
		successors.addAll(referingVariables);
		successors.addAll(usingConstants);
		successors.addAll(usingClasses);
		successors.addAll(usingInterfaces);*/
		
		return successors;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof CloneInstance){
			CloneInstance referInstance = (CloneInstance)element;
			return referInstance.getId().equals(this.getId());
		}
		return false;
	}

	@Override
	public String getSimpleElementName() {
		String str = toString();
		if(!str.equals("instance")){
			String fileName = str.substring(str.indexOf("(")+1, str.indexOf("."));
			String methodName = str.substring(str.indexOf(".")+1, str.lastIndexOf("("));
			
			char[] methChars = methodName.toCharArray();
			methChars[0] = Character.toUpperCase(methChars[0]);
			methodName = String.valueOf(methChars);
			
			return fileName+methodName;
		}
		return str;
	}

}
