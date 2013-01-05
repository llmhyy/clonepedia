package clonepedia.java.model;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import clonepedia.java.CompilationUnitPool;
import clonepedia.java.visitor.MethodsDeclarationVisitor;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.util.MinerProperties;

public class CloneInstanceWrapper{
	
	private CompilationUnitPool pool;
	
	private CloneInstance cloneInstance;
	private MethodDeclaration methodDeclaration;
	private ArrayList<ASTNode> uncounterRelationalDifferenceNodes = new ArrayList<ASTNode>();
	
	private String className;
	
	private ASTNode[] astNodeList;
	public int startContextIndex = 0;
	public int endContextIndex = 0;
	public int comparePointer = 0;
	private HashSet<Integer> markedIndexes = new HashSet<Integer>();
	private HashSet<Integer> markedStatementIndexes = new HashSet<Integer>();
	
	private Statement[] statementList; 
	public int startStatementContextIndex = 0;
	public int endStatementContextIndex = 0;
	public int compareStatementPointer = 0;
	//private HashSet<Integer> markedStatementIndexes = new HashSet<Integer>();

	public CloneInstanceWrapper(CloneInstance cloneInstance, CompilationUnitPool pool) {
		
		this.cloneInstance = cloneInstance;
		this.pool = pool;
		
		String filePath = cloneInstance.getFileLocation();
		
		className = filePath.substring(filePath.indexOf("\\src\\")+5);
		className = className.replace("\\", "/");
		
		try{
			initializeCloneResidingMethod();
		}catch(CoreException e){
			e.printStackTrace();
		}
	}
	
	private void initializeCloneResidingMethod() throws CoreException{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(cloneInstance.getCloneSet().getProject().getProjectName());
		if (project.isNatureEnabled(MinerProperties.javaNatureName)) {
			IJavaProject javaProject = JavaCore.create(project);
			IPath filePath = Path.fromPortableString(className);
			IJavaElement element = javaProject.findElement(filePath);

			ICompilationUnit unit = (ICompilationUnit) element;

			/*//Document doc = new Document(unit.getSource());
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			//parser.setSource(doc.get().toCharArray());
			parser.setSource(unit);
			parser.setResolveBindings(true);
			
			
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);*/
			
			CompilationUnit cu = pool.getComilationUnit(unit);
			
			MethodsDeclarationVisitor visitor = new MethodsDeclarationVisitor(getStartLine(), getEndLine(), cu);
			cu.accept(visitor);
			//System.out.println();
			this.methodDeclaration = visitor.getCloneResidingMethod();
			
		}
	}

	public boolean equals(Object object){
		
		if(!(object instanceof CloneInstanceWrapper))return false;
		
		CloneInstanceWrapper instance = (CloneInstanceWrapper)object;
		if(getFilePath().equals(instance.getFilePath()) 
				&& getStartLine() == instance.getStartLine()
				&& getEndLine() == instance.getEndLine())
			return true;
		return false;
	}
	
	public String getSimpleClassName(){
		String simpleClassName = getFilePath().substring(getFilePath().lastIndexOf("\\")+1, getFilePath().length()-5);
		return simpleClassName;
	}
	
	public int hashCode(){
		return getStartLine()*getEndLine()*getFilePath().hashCode();
	}
	
	public int getStartLine(){
		return cloneInstance.getStartLine();
	}
	
	public int getEndLine(){
		return cloneInstance.getEndLine();
	}

	public String getFilePath(){
		return cloneInstance.getFileLocation();
	}
	
	public CloneInstance getCloneInstance() {
		return cloneInstance;
	}

	public void setCloneInstance(CloneInstance cloneInstance) {
		this.cloneInstance = cloneInstance;
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public ASTNode[] getAstNodeList() {
		return astNodeList;
	}

	public void setAstNodeList(ASTNode[] astNodeList) {
		this.astNodeList = astNodeList;
	}
	
	public Statement[] getStatementList() {
		return statementList;
	}

	public void setStatementList(Statement[] statementList) {
		this.statementList = statementList;
	}

	public void markIndex(int index){
		markedIndexes.add(index);
	}

	public boolean isMarked(int index){
		return markedIndexes.contains(index);
	}
	
	public void markStatementIndex(int index){
		this.markedStatementIndexes.add(index);
	}
	
	public boolean isStatementMarked(int index){
		return this.markedStatementIndexes.contains(index);
	}
	
	public void clearMarkedIndexes(){
		this.markedIndexes.clear();
	}

	public ArrayList<ASTNode> getUncounterRelationalDifferenceNodes() {
		return uncounterRelationalDifferenceNodes;
	}

	public void setUncounterRelationalDifferenceNodes(
			ArrayList<ASTNode> uncounterRelationalDifferenceNodes) {
		this.uncounterRelationalDifferenceNodes = uncounterRelationalDifferenceNodes;
	}
}
