package clonepedia.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CloneDiffPerspective implements IPerspectiveFactory {

	public static String PLAIN_CLONESET_VIEW = "Clonepedia.PlainCloneSetView";
	public static String CLONE_DIFF_VIEW = "Clonepedia.CloneDiffView";
	public static String DIFF_PROPERTY_VIEW = "Clonepedia.DiffPropertyView";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.addView(PLAIN_CLONESET_VIEW, IPageLayout.LEFT, 0.25f, editorArea);
		
		layout.addView(CLONE_DIFF_VIEW, IPageLayout.BOTTOM, 0.55f, editorArea);
		
		layout.addView(DIFF_PROPERTY_VIEW, IPageLayout.RIGHT, 0.5f, editorArea);

	}

}
