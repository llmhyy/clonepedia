package ccdemon.handlers;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;

public class CustomLinkedModeUI extends LinkedModeUI {

	public CustomLinkedModeUI(LinkedModeModel model, ITextViewer viewer) {
		super(model, viewer);
	}

	public void setPositionListener(ILinkedModeUIFocusListener listener) {
		super.setPositionListener(listener);
	}
}
