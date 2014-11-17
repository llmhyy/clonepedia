package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jface.text.BadLocationException;
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
	
	public ConfigurationPointSet getCps() {
		return cps;
	}

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
				for(int i = index + 1; i < configurationPoints.size(); i++){
					ConfigurationPoint cp = configurationPoints.get(i);
					TokenSeq modifiedTokenSeq = cp.getModifiedTokenSeq();
					ArrayList<Token> tokens = modifiedTokenSeq.getTokens();
					for(Token token : tokens){
						token.setStartPosition(token.getStartPosition() + currentLength - formerLength);
						token.setEndPosition(token.getEndPosition() + currentLength - formerLength);
					}
				}
				
				PasteHandler.installConfigurationPointsOnCode(cps);
			}
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
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
