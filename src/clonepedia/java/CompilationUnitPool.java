package clonepedia.java;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CompilationUnitPool {
	private ArrayList<CompilationUnit> unitList = new ArrayList<CompilationUnit>();

	public CompilationUnitPool() {
	}

	public ArrayList<CompilationUnit> getUnitList() {
		return unitList;
	}

	public void setUnitList(ArrayList<CompilationUnit> unitList) {
		this.unitList = unitList;
	}
	
	public CompilationUnit getComilationUnit(ICompilationUnit referedUnit){
		
		String referedName;
		try {
			referedName = referedUnit.getPackageDeclarations()[0].getElementName()+ "." + referedUnit.getElementName();
			for(CompilationUnit unit: this.unitList){
				String name = unit.getPackage().getName().toString() + "." + unit.getJavaElement().getElementName();
				if(referedName.equals(name)){
					//System.out.println("yes");
					return unit;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		//Document doc = new Document(unit.getSource());
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//parser.setSource(doc.get().toCharArray());
		parser.setSource(referedUnit);
		parser.setResolveBindings(true);
		
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		this.unitList.add(cu);
		
		return cu;
	}
}
