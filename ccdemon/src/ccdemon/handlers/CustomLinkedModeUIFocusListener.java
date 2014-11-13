package ccdemon.handlers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.link.LinkedModeUI.ILinkedModeUIFocusListener;
import org.eclipse.jface.text.link.LinkedModeUI.LinkedModeUITarget;
import org.eclipse.jface.text.link.LinkedPosition;

public class CustomLinkedModeUIFocusListener implements
		ILinkedModeUIFocusListener {
	
	private LinkedPosition testPosition;

	@Override
	public void linkingFocusLost(LinkedPosition position,
			LinkedModeUITarget target) {
		try {
			System.out.println("lost: " + position.getContent());		
			if(position.getContent().equals("stati")){
				target.getViewer().getDocument().replace(testPosition.getOffset(), 
						testPosition.getLength(), position.getContent() + "Test");
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
			System.out.println("gained: " + position.getContent());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the testPosition
	 */
	public LinkedPosition getTestPosition() {
		return testPosition;
	}

	/**
	 * @param testPosition the testPosition to set
	 */
	public void setTestPosition(LinkedPosition testPosition) {
		this.testPosition = testPosition;
	}
	
	

}
