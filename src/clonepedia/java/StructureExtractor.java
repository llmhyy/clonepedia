package clonepedia.java;



import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.java.visitor.GlobalTypeVisitor;
import clonepedia.model.ontology.Project;
import clonepedia.util.MinerProperties;


public class StructureExtractor {
	
	private Project project;
	private OntologicalDataFetcher fetcher;
	
	public StructureExtractor(Project project) {
		super();
		this.project = project;
	}

	/*public void extractProjectOutline(){
		
		DBOperator dbOp = new DBOperator();
		dbOp.storeProject(project);
	}*/
	
	/**
	 * Parse the content of a compilation unit including its declared class/interface together
	 * with their super class/interface, declared methods and fields. For class, its probably 
	 * inner class is also parsed. 
	 * @param unit
	 * @throws Exception 
	 */
	private void parseCompilationUnit(ICompilationUnit unit, CompilationUnitPool pool, boolean isDebug) throws Exception{
		/*ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);*/
		CompilationUnit cu = pool.getComilationUnit(unit);
		GlobalTypeVisitor typeVisitor = new GlobalTypeVisitor(cu, isDebug);
		typeVisitor.setProject(project);
		cu.accept(typeVisitor);
		
		this.fetcher = typeVisitor.getFetcher();
	}
	
	public OntologicalDataFetcher extractProjectContent(boolean isDebug) throws JavaModelException, CoreException{
		
		if(isDebug){
			new OntologicalDBDataFetcher().storeProject(project);			
		}
		
		CompilationUnitPool pool = new CompilationUnitPool();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject proj = root.getProject(project.getProjectName());
		if (proj.isNatureEnabled(MinerProperties.javaNatureName)) {
			IJavaProject javaProject = JavaCore.create(proj);
			
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for(IPackageFragment pack: packages){
				if(pack.getKind() == IPackageFragmentRoot.K_SOURCE){
					for(ICompilationUnit unit: pack.getCompilationUnits()){
						
						try {
							parseCompilationUnit(unit, pool, isDebug);
						} catch (Exception e) {
							e.printStackTrace();
						}	
						//System.out.println(types);
					}
				}
			}
			
		}
		
		return this.fetcher;
	}
	
	
}
