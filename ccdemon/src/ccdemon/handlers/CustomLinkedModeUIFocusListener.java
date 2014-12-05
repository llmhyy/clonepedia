package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jface.text.link.LinkedModeUI.ILinkedModeUIFocusListener;
import org.eclipse.jface.text.link.LinkedModeUI.LinkedModeUITarget;
import org.eclipse.jface.text.link.LinkedPosition;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;
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
	private ArrayList<Integer> configuredNum = new ArrayList<>();
	
	private ConfigurationPoint currentPoint;
	private int formerLength;
	private int currentLength;
	
	public CustomLinkedModeUIFocusListener(ArrayList<RankedProposalPosition> positionList, 
			ConfigurationPointSet configurationPointSet){
		this.positionList = positionList;
		this.configurationPointSet = configurationPointSet;
	}
	
	public void setConfigurationPointSet(ConfigurationPointSet cps) {
		this.configurationPointSet = cps;
	}

	@Override
	public void linkingFocusLost(LinkedPosition position, LinkedModeUITarget target) {
		
		currentLength = position.length;
		
		if(currentLength != formerLength){
			ArrayList<ConfigurationPoint> configurationPoints = configurationPointSet.getConfigurationPoints();
			int index = configurationPoints.indexOf(currentPoint);
			
			for(int i = index + 1; i < configurationPoints.size(); i++){
				ConfigurationPoint cp = configurationPoints.get(i);
				TokenSeq modifiedTokenSeq = cp.getModifiedTokenSeq();
				ArrayList<Token> tokens = modifiedTokenSeq.getTokens();
				for(Token token : tokens){
					token.setStartPosition(token.getStartPosition() + currentLength - formerLength);
					token.setEndPosition(token.getEndPosition() + currentLength - formerLength);
				}
			}
			
			//the way to modify proposals' content
			/*int positionIndex = CCDemonUtil.positions.indexOf(position);
			for(int i = positionIndex + 1; i < CCDemonUtil.positions.size(); i++){
				RankedProposalPosition pp = (RankedProposalPosition) CCDemonUtil.positions.get(i);
				ICompletionProposal[] proposals = new ICompletionProposal[3]; 
				proposals[0] = new RankedCompletionProposal("test1", pp.offset, pp.length, 0 , 0);
				((RankedCompletionProposal) proposals[0]).setPosition(pp);
				proposals[1] = new RankedCompletionProposal("test2", pp.offset, pp.length, 0 , 0);
				((RankedCompletionProposal) proposals[1]).setPosition(pp);
				proposals[2] = new RankedCompletionProposal("test3", pp.offset, pp.length, 0 , 0);
				((RankedCompletionProposal) proposals[2]).setPosition(pp);
				
				pp.setChoices(proposals);
			}*/
		}
	}

	@Override
	public void linkingFocusGained(LinkedPosition position, LinkedModeUITarget target) {
		//find current configuration point that has the same offset and length
		
		formerLength = position.length;
		currentPoint = configurationPointSet.getConfigurationPoints().get(positionList.indexOf(position));
		
	}

}
