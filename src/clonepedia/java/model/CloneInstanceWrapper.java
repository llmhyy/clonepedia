package clonepedia.java.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import clonepedia.java.CompilationUnitPool;
import clonepedia.java.visitor.MethodsDeclarationVisitor;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.util.MinerProperties;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class CloneInstanceWrapper{
	
	private CompilationUnitPool pool;
	
	private CloneInstance cloneInstance;
	private ASTNode minimumContainingASTNode;
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
	
	public CloneInstanceWrapper(CloneInstance cloneInstance, CompilationUnitPool pool, boolean isLimitToMethod, ASTNode node) {
		
		this.cloneInstance = cloneInstance;
		this.pool = pool;
		
		String filePath = cloneInstance.getFileLocation();
		File javaFile = new File(filePath);
		String javaFileContent = MinerUtil.getFileConent(javaFile);
		
		int packageStartIndex = javaFileContent.indexOf("package ")+8;
		int packageEndIndex = javaFileContent.indexOf(";", packageStartIndex);
		String packageName = javaFileContent.substring(packageStartIndex, packageEndIndex);
		
		String packagePrefix = null;
		if(packageName.contains(".")){
			packagePrefix = packageName.substring(0, packageName.indexOf("."));			
		}
		else{
			packagePrefix = packageName;
		}
		className = filePath.substring(filePath.indexOf(packagePrefix));
		className = className.replace("\\", "/");
		
		if(isLimitToMethod){
			try{
				initializeCloneResidingMethod();
			}catch(CoreException e){
				e.printStackTrace();
			}			
		}
		else{
			minimumContainingASTNode = node;
		}
	}
	
	private void initializeCloneResidingMethod() throws CoreException{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		String projectName = Settings.projectName;
		/*if(this.getCloneInstance().getFileLocation().contains("jarp")){
			projectName = "jarp";
		}
		else if(this.getCloneInstance().getFileLocation().contains("joone")){
			projectName = "joone";
		}*/
		
		IProject project = root.getProject(projectName);
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
			
			ASTNode node = visitor.getCloneResidingMethod();
			
			if(node != null){
				this.minimumContainingASTNode = node;				
			}
			else{
				this.minimumContainingASTNode = cu;
			}
			
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

	public ASTNode getMinimumContainingASTNode() {
		return minimumContainingASTNode;
	}

	public void setMinimumContainingASTNode(ASTNode containingASTNode) {
		this.minimumContainingASTNode = containingASTNode;
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
