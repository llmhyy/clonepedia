package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeUI.ILinkedModeUIFocusListener;
import org.eclipse.jface.text.link.LinkedModeUI.LinkedModeUITarget;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.ProposalPosition;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;
import ccdemon.proposal.RankedCompletionProposal;
import ccdemon.util.CCDemonUtil;

public class CustomLinkedModeUIFocusListener implements
		ILinkedModeUIFocusListener {
	
	ConfigurationPointSet cps;
	ConfigurationPoint currentCP;
	int formerLength;
	int currentLength;
	
	public void setCps(ConfigurationPointSet cps) {
		this.cps = cps;
	}

	@Override
	public void linkingFocusLost(LinkedPosition position,
			LinkedModeUITarget target) {
		try {
			System.out.println("lost: " + position.getContent() + ", length: " + position.length + ", offset: " + position.offset);		

			currentLength = position.length;
			
			if(currentLength != formerLength){
				ArrayList<ConfigurationPoint> configurationPoints = cps.getConfigurationPoints();
				int index = configurationPoints.indexOf(currentCP);
//				ProposalPosition currentPosition = (ProposalPosition) PasteHandler.positions.get(index);
//				currentPosition.setLength(currentLength);
				
				for(int i = index + 1; i < configurationPoints.size(); i++){
					ConfigurationPoint cp = configurationPoints.get(i);
					TokenSeq modifiedTokenSeq = cp.getModifiedTokenSeq();
					ArrayList<Token> tokens = modifiedTokenSeq.getTokens();
					for(Token token : tokens){
//						System.out.println("1: " + token.toString() + " " + token.getStartPosition());
//						System.out.println("1: " + token.toString() + " " + token.getEndPosition());
						token.setStartPosition(token.getStartPosition() + currentLength - formerLength);
						token.setEndPosition(token.getEndPosition() + currentLength - formerLength);
//						System.out.println("2: " + token.toString() + " " + token.getStartPosition());
//						System.out.println("2: " + token.toString() + " " + token.getEndPosition());
					}
					
//					ProposalPosition lp = (ProposalPosition) PasteHandler.positions.get(i);
//					for(ICompletionProposal proposal : lp.getChoices()){
//						RankedCompletionProposal rankedProposal = (RankedCompletionProposal) proposal;
//
//						System.out.println("1: " + rankedProposal.getDisplayString() + " " + rankedProposal.getOffset());
//						
//						rankedProposal.setOffset(rankedProposal.getOffset() + currentLength - formerLength);
//						
//						System.out.println("2: " + rankedProposal.getDisplayString() + " " + rankedProposal.getOffset());
//					}
				}
				
				int positionIndex = CCDemonUtil.positions.indexOf(position);
				for(int i = positionIndex + 1; i < CCDemonUtil.positions.size(); i++){
					ProposalPosition pp = (ProposalPosition) CCDemonUtil.positions.get(i);
					
					for(ICompletionProposal proposal : pp.getChoices()){
						RankedCompletionProposal rankedProposal = (RankedCompletionProposal) proposal;

						System.out.println("1: " + rankedProposal.getDisplayString() + " " + rankedProposal.getfReplacementString() + " " + rankedProposal.getOffset());
						
						rankedProposal.setfReplacementString("test" + i);

						System.out.println("2: " + rankedProposal.getDisplayString() + " " + rankedProposal.getfReplacementString() + " " + rankedProposal.getOffset());
					}
				}
				
				//PasteHandler.installConfigurationPointsOnCode(cps);
			}
			

			System.out.println("currentLength: " + currentLength);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void linkingFocusGained(LinkedPosition position,
			LinkedModeUITarget target) {
		try {
			System.out.println("gained: " + position.getContent() + ", length: " + position.length + ", offset: " + position.offset);
			
			int offset = position.offset;
			formerLength = position.length;
			ArrayList<ConfigurationPoint> configurationPoints = cps.getConfigurationPoints();
			for(ConfigurationPoint cp : configurationPoints){
				if(cp.getModifiedTokenSeq().getStartPosition() == offset && cp.getModifiedTokenSeq().getPositionLength() == formerLength){
					currentCP = cp;
					break;
				}
			}
			
			System.out.println("formerLength: " + formerLength);
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
