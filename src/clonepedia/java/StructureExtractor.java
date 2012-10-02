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

import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.java.visitor.TypeVisitor;
import clonepedia.model.ontology.Project;
import clonepedia.util.MinerProperties;


public class StructureExtractor {
	
	private Project project;
	
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
	private void parseCompilationUnit(ICompilationUnit unit, CompilationUnitPool pool) throws Exception{
		/*ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);*/
		CompilationUnit cu = pool.getComilationUnit(unit);
		TypeVisitor typeVisitor = new TypeVisitor(cu);
		typeVisitor.setProject(project);
		cu.accept(typeVisitor);
	}
	
	public void extractProjectContent() throws JavaModelException, CoreException{
		
		new OntologicalDataFetcher().storeProject(project);
		
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
							parseCompilationUnit(unit, pool);
						} catch (Exception e) {
							e.printStackTrace();
						}	
						//System.out.println(types);
					}
				}
			}
			
			/*IPath filePath = Path.fromPortableString(className);
			IJavaElement element = javaProject.findElement(filePath);

			ICompilationUnit unit = (ICompilationUnit) element;

			//Document doc = new Document(unit.getSource());
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			//parser.setSource(doc.get().toCharArray());
			parser.setSource(unit);
			parser.setResolveBindings(true);
			
			
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);*/
			
		}
	}
	
	
}
