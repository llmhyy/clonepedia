package mcidiff.model;

import java.util.ArrayList;


public class CloneInstance {
	
	private CloneSet set;
	
	/**
	 * @return the set
	 */
	public CloneSet getSet() {
		return set;
	}

	/**
	 * @param set the set to set
	 */
	public void setSet(CloneSet set) {
		this.set = set;
	}

	private String fileName;
	private int startLine;
	private int endLine;
	
	private ArrayList<Token> tokenList = new ArrayList<>();
	
	/**
	 * @return the tokenList
	 */
	public ArrayList<Token> getTokenList() {
		return tokenList;
	}

	/**
	 * @param tokenList the tokenList to set
	 */
	public void setTokenList(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}

	/**
	 * @param fileName
	 * @param startLine
	 * @param endLine
	 */
	public CloneInstance(String fileName, int startLine, int endLine) {
		super();
		this.fileName = fileName;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	public CloneInstance(CloneSet set, String fileName, int startLine, int endLine) {
		super();
		this.set = set;
		this.fileName = fileName;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	@Override
	public String toString() {
		return "CloneInstance [fileName=" + fileName + ", startLine="
				+ startLine + ", endLine=" + endLine + "]";
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof CloneInstance){
			CloneInstance cloneInstance = (CloneInstance)obj;
			return cloneInstance.getFileName().equals(getFileName()) &&
					cloneInstance.getStartLine() == getStartLine() &&
					cloneInstance.getEndLine() == getEndLine();
		}
		
		return false;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the startLine
	 */
	public int getStartLine() {
		return startLine;
	}

	/**
	 * @param startLine the startLine to set
	 */
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	/**
	 * @return the endLine
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * @param endLine the endLine to set
	 */
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	
}
