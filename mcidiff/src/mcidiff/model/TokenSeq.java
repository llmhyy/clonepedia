package mcidiff.model;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * TokenSeq contains consecutive differential tokens.
 * 
 * @author linyun
 *
 */
public class TokenSeq {
	private ArrayList<Token> tokens = new ArrayList<>();
	private String text;

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(Token token: tokens){
			buffer.append(token.getTokenName() + " ");
		}
		
		return buffer.toString();
	}
	
	public boolean isSingleToken(){
		return this.tokens.size() == 1;
	}
	
	public void retrieveTextFromDoc(){
		if(isEpisolonTokenSeq()){
			setText("");
		}
		else{
			Token token = getTokens().get(0);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath location = Path.fromOSString(token.getCloneInstance().getFileName());
			IFile file = workspace.getRoot().getFileForLocation(location);
			TextFileDocumentProvider provider = new TextFileDocumentProvider();
			
			try {
				provider.connect(file);
				IDocument doc = provider.getDocument(file);
				String content = doc.get(getStartPosition(), getPositionLength());
				this.text = content;
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getText(){
		return text;
	}
	
	public void setText(String text){
		this.text = text;
	}

	@Override
	public int hashCode() {
		return getTokens().toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TokenSeq){
			TokenSeq seq = (TokenSeq)obj;
			if(seq.toString().equals(toString())){
				return true;
			}
		}
		return false;
	}

	public boolean isEpisolonTokenSeq(){
		if(getTokens().size() > 0){
			for(Token t: getTokens()){
				if(!t.isEpisolon()){
					return false;
				}
			}
			
			return true;
		}
		
		return true;
	}
	
	public int getPositionLength(){
		return getEndPosition() - getStartPosition();
	}
	
	/**
	 * @return the tokens
	 */
	public ArrayList<Token> getTokens() {
		return tokens;
	}

	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(Token t){
		if(!t.isEpisolon()){
			this.tokens.add(t);			
		}
		else if(this.tokens.size() == 0){
			this.tokens.add(t);	
		}
	}
	
	public CloneInstance getCloneInstance(){
		if(getTokens().size() != 0){
			return getTokens().get(0).getCloneInstance();
		}
		
		return null;
	}
	
	public int getStartPosition(){
		if(getTokens().size() != 0){
			return getTokens().get(0).getStartPosition();
		}
		
		return -1;
	}
	
	public int getEndPosition(){
		if(getTokens().size() != 0){
			return getTokens().get(getTokens().size()-1).getEndPosition();
		}
		
		return -1;
	}
}
