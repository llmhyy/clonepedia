package mcidiff.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

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
	
	public static CompilationUnit generateCompilationUnit(String path){
		
		String content = retrieveContent(path);
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//parser.setResolveBindings(isNeedBinding);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return cu;
	}
}
