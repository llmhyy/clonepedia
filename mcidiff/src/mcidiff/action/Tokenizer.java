package mcidiff.action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.Token;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Tokenizer {
	/**
	 * assign the input set with its token list.
	 * @param set
	 */
	public void tokenize(CloneSet set){
		for(CloneInstance instance: set.getInstances()){
			ArrayList<Token> tokenList = parseTokens(instance);
			
			System.currentTimeMillis();
			
			instance.setTokenList(tokenList);
		}
	}

	/**
	 * The fileName should be the absolute path for the target file.
	 * For convenience of following process, each sequence has an empty start and an empty end.
	 * @param fileName
	 * @param startLine
	 * @param endLine
	 * @return
	 */
	private ArrayList<Token> parseTokens(CloneInstance instance) {
		ArrayList<Token> tokenList = new ArrayList<>();
		tokenList.add(new Token("e*", null, instance, -1, -1));
		
		String fileContent = retrieveContent(instance.getFileName(), 
				instance.getStartLine(), instance.getEndLine());
		
		CompilationUnit cu = generateCompilationUnit(instance.getFileName());
		int baseLinePosition = cu.getPosition(instance.getStartLine(), 0);
		
		IScanner scanner = ToolFactory.createScanner(false, false, false, false);
		scanner.setSource(fileContent.toCharArray());
		
		Token previous = null;
		while(true){
			try {
				int t = scanner.getNextToken();
				if(t == ITerminalSymbols.TokenNameEOF){
					break;
				}
				String tokenName = new String(scanner.getCurrentTokenSource());
				
				/*if(tokenName.equals("b")){
					System.currentTimeMillis();
				}*/
				
				int startPosition = baseLinePosition + scanner.getCurrentTokenStartPosition();
				int endPosition = baseLinePosition + scanner.getCurrentTokenEndPosition()+1;
				
				NodeVisitor visitor = new NodeVisitor(startPosition, endPosition);
				cu.accept(visitor);
				ASTNode node = visitor.getNode();
				
				Token token = new Token(tokenName, node, instance, startPosition, endPosition);
				tokenList.add(token);
				
				token.setPreviousToken(previous);
				if(previous != null){
					previous.setPostToken(token);
				}
				previous = token;
			} catch (InvalidInputException e) {
				e.printStackTrace();
			}
			
		}
		
		tokenList.add(new Token("e*", null, instance, -1, -1));
		
		return tokenList;
	}
	
	private String retrieveContent(String absolutePath){
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
	
	private String retrieveContent(String absolutePath, int startLine, int endLine){
		
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
	
	private CompilationUnit generateCompilationUnit(String path){
		
		String content = retrieveContent(path);
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return cu;
	}
	
	public class NodeVisitor extends ASTVisitor {
		private ASTNode node;
		private double bias = -1;
		//private CompilationUnit cu;
		
		private int startPosition;
		private int endPosition;
		
		/**
		 * @param range
		 */
		public NodeVisitor(int startPosition, int endPosition) {
			super();
			//this.cu = cu;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}

		/**
		 * @return the diffNode
		 */
		public ASTNode getNode() {
			return node;
		}

		/**
		 * @param diffNode the diffNode to set
		 */
		public void setNode(ASTNode node) {
			this.node = node;
		}

		public void preVisit(ASTNode node){
			
			if(isContainRange(node, startPosition, endPosition)){
				double preBias = startPosition - node.getStartPosition();
				double postBias = node.getStartPosition()+node.getLength()-endPosition;
				
				double currentBias = preBias + postBias;
				if(bias == -1){
					bias = currentBias;
					setNode(node);
				}
				else{
					if(bias >= currentBias){
						bias = currentBias;
						setNode(node);
					}
				}
			}
		}
		
		private boolean isContainRange(ASTNode node, int startPosition, int endPosition){
			return node.getStartPosition() <= startPosition &&
					node.getStartPosition()+node.getLength() >= endPosition;
		}
		
//		private boolean isOutsideRange(ASTNode node, int startPosition, int endPosition){
//			return node.getStartPosition() > endPosition ||
//					node.getStartPosition()+node.getLength() < startPosition;
//		}
//		
//		private boolean isContainedByRange(ASTNode node, int startPosition, int endPosition){
//			return node.getStartPosition() >= startPosition 
//					&& node.getStartPosition()+node.getLength() <= endPosition;
//		}
//		
//		private boolean isOverlappedWithRange(ASTNode node, int startPosition, int endPosition){
//			return !isContainRange(node, startPosition, endPosition) &&
//					!isOutsideRange(node, startPosition, endPosition) &&
//					!isContainedByRange(node, startPosition, endPosition);
//		}
		
//		@SuppressWarnings("rawtypes")
//		public Object[] getChildren(ASTNode node) {
//		    List list= node.structuralPropertiesForType();
//		    for (int i= 0; i < list.size(); i++) {
//		        StructuralPropertyDescriptor curr= (StructuralPropertyDescriptor) list.get(i);
//		        Object child = node.getStructuralProperty(curr);
//		        if (child instanceof List) {
//		                return ((List) child).toArray();
//		        } else if (child instanceof ASTNode) {
//		            return new Object[] { child };
//		        }
//		    }
//		    
//		    return new Object[0];
//		    //return null;
//		}
	}
}