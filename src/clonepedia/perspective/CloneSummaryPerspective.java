package clonepedia.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CloneSummaryPerspective implements IPerspectiveFactory {

	public static String PLAIN_CLONESET_VIEW = "Clonepedia.PlainCloneSetView";
	public static String PATTERN_ORIENTED_VIEW = "Clonepedia.PatternOrientedView";
	public static String TOPIC_ORIENTED_VIEW = "Clonepedia.TopicOrientedView";
	public static String CODE_SNIPPET_VIEW = "Clonepedia.CloneCodeSnippetView";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.addView(PLAIN_CLONESET_VIEW, IPageLayout.LEFT, 0.25f, editorArea);
		
		layout.addView(PATTERN_ORIENTED_VIEW, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addView(TOPIC_ORIENTED_VIEW, IPageLayout.RIGHT, 0.5f, PATTERN_ORIENTED_VIEW);
		
		layout.addView(CODE_SNIPPET_VIEW, IPageLayout.RIGHT, 0.75f, editorArea);

	}

}
