package clonepedia.java.model;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * an element in counter diff
 * @author linyun
 *
 */
public class DiffInstanceElementRelationEmulator {

	private CloneInstanceWrapper instanceWrapper;
	private ASTNode node;

	public DiffInstanceElementRelationEmulator(
			CloneInstanceWrapper instanceWrapper, ASTNode node) {
		super();
		this.instanceWrapper = instanceWrapper;
		this.node = node;
	}

	public CloneInstanceWrapper getInstanceWrapper() {
		return instanceWrapper;
	}

	public void setInstanceWrapper(CloneInstanceWrapper instanceWrapper) {
		this.instanceWrapper = instanceWrapper;
	}

	public ASTNode getNode() {
		return node;
	}

	public void setNode(ASTNode node) {
		this.node = node;
	}

}
