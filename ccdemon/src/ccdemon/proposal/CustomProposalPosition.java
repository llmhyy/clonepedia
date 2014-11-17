package ccdemon.proposal;

import java.util.Arrays;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;

public class CustomProposalPosition extends LinkedPosition {
	/**
	 * The proposals
	 */
	private ICompletionProposal[] fProposals;

	public ICompletionProposal[] getfProposals() {
		return fProposals;
	}

	public void setfProposals(ICompletionProposal[] fProposals) {
		this.fProposals = fProposals;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param document the document
	 * @param offset the offset of the position
	 * @param length the length of the position
	 * @param sequence the iteration sequence rank
	 * @param proposals the proposals to be shown when entering this position
	 */
	public CustomProposalPosition(IDocument document, int offset, int length, int sequence, ICompletionProposal[] proposals) {
		super(document, offset, length, sequence);
		fProposals= copy(proposals);
	}

	/**
	 * Creates a new instance, with no sequence number.
	 *
	 * @param document the document
	 * @param offset the offset of the position
	 * @param length the length of the position
	 * @param proposals the proposals to be shown when entering this position
	 */
	public CustomProposalPosition(IDocument document, int offset, int length, ICompletionProposal[] proposals) {
		super(document, offset, length, LinkedPositionGroup.NO_STOP);
		fProposals= copy(proposals);
	}
	
	/*
	 * @since 3.1
	 */
	private ICompletionProposal[] copy(ICompletionProposal[] proposals) {
		if (proposals != null) {
			ICompletionProposal[] copy= new ICompletionProposal[proposals.length];
			System.arraycopy(proposals, 0, copy, 0, proposals.length);
			return copy;
		}
		return null;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof ProposalPosition) {
			if (super.equals(o)) {
				return Arrays.equals(fProposals, ((CustomProposalPosition)o).fProposals);
			}
		}
		return false;
	}

	/**
	 * Returns the proposals attached to this position. The returned array is owned by
	 * this <code>ProposalPosition</code> and may not be modified by clients.
	 *
	 * @return an array of choices, including the initial one. Callers must not
	 *         modify it.
	 */
	public ICompletionProposal[] getChoices() {
		return fProposals;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.link.LinkedPosition#hashCode()
	 */
	public int hashCode() {
		return super.hashCode() | (fProposals == null ? 0 : fProposals.hashCode());
	}
}
