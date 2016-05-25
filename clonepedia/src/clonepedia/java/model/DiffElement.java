package clonepedia.java.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import mcidiff.model.TokenSeq;

/**
 * an element in counter diff
 * @author linyun
 *
 */
public class DiffElement {

	private CloneInstanceWrapper instanceWrapper;
	private List<ASTNode> nodeList = new ArrayList<>();
	
	private TokenSeq seq;

	public DiffElement(
			CloneInstanceWrapper instanceWrapper, ASTNode node) {
		super();
		this.instanceWrapper = instanceWrapper;
		this.nodeList.add(node);
	}
	
	public DiffElement(
			CloneInstanceWrapper instanceWrapper, TokenSeq seq) {
		super();
		this.instanceWrapper = instanceWrapper;
		this.seq = seq;
	}
	
	public DiffElement(
			CloneInstanceWrapper instanceWrapper, List<ASTNode> nodeList) {
		super();
		this.instanceWrapper = instanceWrapper;
		this.nodeList = nodeList;
	}

	public CloneInstanceWrapper getInstanceWrapper() {
		return instanceWrapper;
	}

	public void setInstanceWrapper(CloneInstanceWrapper instanceWrapper) {
		this.instanceWrapper = instanceWrapper;
	}

	public List<ASTNode> getNodeList() {
		return this.nodeList;
	}
	
	public ASTNode getNode(){
		if(!this.nodeList.isEmpty()){
			return this.nodeList.get(0);
		}
		
		return null;
	}

	public void setNodeList(List<ASTNode> nodeList) {
		this.nodeList = nodeList;
	}

	public TokenSeq getSeq() {
		return seq;
	}

	public void setSeq(TokenSeq seq) {
		this.seq = seq;
	}

}
