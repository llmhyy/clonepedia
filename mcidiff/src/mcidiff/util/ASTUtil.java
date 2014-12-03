package mcidiff.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import mcidiff.model.Multiset;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class ASTUtil {
	public static String retrieveContent(String absolutePath){
		String everything = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(absolutePath));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		return everything;
	}
	
	public static String retrieveContent(String absolutePath, int startLine, int endLine){
		
		if(startLine > endLine){
			System.err.print("start line is larger than end line");
			return null;
		}
		
		 int count = 1;
		String everything = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(absolutePath));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        
	        while (line != null) {
	        	
	        	if(count >= startLine && count <= endLine){
	        		sb.append(line);
	        		sb.append(System.lineSeparator());
	        	}
	        	
	        	line = br.readLine();
	        	count++;
	        }
	        everything = sb.toString();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		if(startLine > count || endLine > count){
			System.err.print("start line or end line is larger the total line number");
			return null;
		}
		
		return everything;
	}
	
	public static CompilationUnit generateCompilationUnit(String path, IJavaProject project){
		
		String content = retrieveContent(path);
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		if(project != null){
			String unitName = path.substring(path.indexOf(project.getProject().getName()));
			unitName = unitName.replace("\\", "/");
			unitName = "/" + unitName;
			
			parser.setProject(project);
			parser.setUnitName(unitName);
			parser.setResolveBindings(true);			
		}
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return cu;
	}
	
	public static void sort(ArrayList<Multiset> list, Comparator<Multiset> comparator){
		for(int i=0; i<list.size(); i++){
			int targetPosition = list.size()-i-1;
			int replacePosition = 0;
			for(int j=1; j<=targetPosition; j++){
				Multiset set = list.get(j);
				if(comparator.compare(list.get(replacePosition), set)<0){
					replacePosition = j;
				}
			}
			
			Multiset tmp = list.get(replacePosition);
			list.set(replacePosition, list.get(targetPosition));
			list.set(targetPosition, tmp);
		}
	}
	
	public static boolean isSimpleNameDeclaration(IBinding binding, SimpleName name){
		if(binding.getKind() == IBinding.METHOD){
			ASTNode node = name.getParent();
			while(!(node instanceof MethodDeclaration || node instanceof MethodInvocation)){
				node = node.getParent();
				if(node == null){
					break;
				}
			}
			
			if(node != null){
				return node instanceof MethodDeclaration;
			}
			return false;
		}
		else if(binding.getKind() == IBinding.TYPE){
			ASTNode node = name.getParent();
			while(!(node instanceof SimpleType || node instanceof TypeDeclaration)){
				node = node.getParent();
				if(node == null){
					break;
				}
			}
			
			if(node != null){
				return node instanceof TypeDeclaration;
			}
			return false;
		}
		else if(binding.getKind() == IBinding.VARIABLE){
			ASTNode node = name.getParent();
			while(!(node instanceof VariableDeclaration)){
				node = node.getParent();
				if(node == null){
					break;
				}
			}
			
			if(node != null){
				return node instanceof VariableDeclaration;
			}
			return false;
		}
		
		return false;
	}
}
