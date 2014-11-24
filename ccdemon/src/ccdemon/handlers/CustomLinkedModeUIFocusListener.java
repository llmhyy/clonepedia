package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jface.text.link.LinkedModeUI.ILinkedModeUIFocusListener;
import org.eclipse.jface.text.link.LinkedModeUI.LinkedModeUITarget;
import org.eclipse.jface.text.link.LinkedPosition;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;

public class CustomLinkedModeUIFocusListener implements
		ILinkedModeUIFocusListener {
	
	ConfigurationPointSet cps;
	ConfigurationPoint currentCP;
	int formerLength;
	int currentLength;
	
	public void setConfigurationPointSet(ConfigurationPointSet cps) {
		this.cps = cps;
	}

	@Override
	public void linkingFocusLost(LinkedPosition position, LinkedModeUITarget target) {
		
		currentLength = position.length;
		
		if(currentLength != formerLength){
			ArrayList<ConfigurationPoint> configurationPoints = cps.getConfigurationPoints();
			int index = configurationPoints.indexOf(currentCP);
			
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
		
		formerLength = position.length;
		
		//find current configuration point that has the same offset and length
		int offset = position.offset;
		ArrayList<ConfigurationPoint> configurationPoints = cps.getConfigurationPoints();
		for(ConfigurationPoint cp : configurationPoints){
			if(cp.getModifiedTokenSeq().getStartPosition() == offset && cp.getModifiedTokenSeq().getPositionLength() == formerLength){
				currentCP = cp;
				break;
			}
		}
	}

}
