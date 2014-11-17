package ccdemon.proposal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;

public class RankedProposalPosition extends ProposalPosition {

	public RankedProposalPosition(IDocument document, int offset, int length,
			ICompletionProposal[] proposals) {
		super(document, offset, length, proposals);
	}
	
	public void sort(){
		ICompletionProposal[] fProposals = getChoices();
	}

}
