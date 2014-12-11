package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedModeUI.ILinkedModeUIFocusListener;
import org.eclipse.jface.text.link.LinkedModeUI.LinkedModeUITarget;
import org.eclipse.jface.text.link.LinkedPosition;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;
import ccdemon.proposal.RankedCompletionProposal;
import ccdemon.proposal.RankedProposalPosition;

public class CustomLinkedModeUIFocusListener implements
		ILinkedModeUIFocusListener {
	
	/**
	 * the following two fields, {@code positionList} and {@code configurationPointSet} represents the model
	 * and UI part of configuration points. The list of proposal and the list of configuration points share
	 * the same order.
	 */
	private ArrayList<RankedProposalPosition> positionList;
	private ConfigurationPointSet configurationPointSet;
	
	/**
	 * record which configuration point/position has been configured.
	 */
	//private ArrayList<Integer> configuredIndexList = new ArrayList<>();
	
	private ConfigurationPoint currentPoint;
	private String formerContent;
	private String currentContent;
	
	public CustomLinkedModeUIFocusListener(ArrayList<RankedProposalPosition> positionList, 
			ConfigurationPointSet configurationPointSet){
		this.positionList = positionList;
		this.configurationPointSet = configurationPointSet;
	}

	@Override
	public void linkingFocusLost(LinkedPosition position, LinkedModeUITarget target) {
		try {
			currentContent = position.getContent();
			if(!currentContent.equals(formerContent)){
				((RankedProposalPosition) position).setConfigured(true);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		currentPoint.setCurrentValue(currentContent);
		
		ArrayList<ConfigurationPoint> configurationPoints = configurationPointSet.getConfigurationPoints();
		if(currentContent.length() != formerContent.length()){
			int index = configurationPoints.indexOf(currentPoint);
			
			for(int i = index + 1; i < configurationPoints.size(); i++){
				ConfigurationPoint cp = configurationPoints.get(i);
				TokenSeq modifiedTokenSeq = cp.getModifiedTokenSeq();
				ArrayList<Token> tokens = modifiedTokenSeq.getTokens();
				for(Token token : tokens){
					token.setStartPosition(token.getStartPosition() + currentContent.length() - formerContent.length());
					token.setEndPosition(token.getEndPosition() + currentContent.length() - formerContent.length());
				}
			}
		}
		
		//Step 1: change configuration point set
		configurationPointSet.getRule().applyRule(currentContent, currentPoint);
		configurationPointSet.adjustCandidateRanking();
		
		//Step 2: update the position list w.r.t configuration point set.
		for(int i = 0; i < configurationPoints.size(); i++){
			ConfigurationPoint cp = configurationPoints.get(i);				
			RankedProposalPosition pp = positionList.get(i);
			
			RankedCompletionProposal[] proposals = new RankedCompletionProposal[cp.getCandidates().size()]; 
			for(int j = 0; j<proposals.length; j++){
				proposals[j] = new RankedCompletionProposal(cp.getCandidates().get(j).getText(), 
						cp.getModifiedTokenSeq().getStartPosition(), cp.getModifiedTokenSeq().getPositionLength(), 0, 0);
				proposals[j].setPosition(pp);
			}
			pp.setChoices(proposals);
			
			//Step 3: update the code by ranking
			//only update when the position is not configured
			if(!pp.isConfigured()){
				IDocument document = target.getViewer().getDocument();
				proposals[0].apply(document);
			}
		}
	}

	@Override
	public void linkingFocusGained(LinkedPosition position, LinkedModeUITarget target) {
		//find current configuration point that has the same offset and length
		try {
			formerContent = position.getContent();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		currentPoint = configurationPointSet.getConfigurationPoints().get(positionList.indexOf(position));
	}

}
