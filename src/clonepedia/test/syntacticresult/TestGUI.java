package clonepedia.test.syntacticresult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TestGUI {
	public static void main(String[] args) {
		Display display = new Display();
		// Image image = new Image(display, "yourFile.gif");

		final Shell shell = new Shell(display);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(new GridLayout());
		
		final Tree tree = new Tree(shell, SWT.H_SCROLL|SWT.V_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for(int i=0; i<20; i++){
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(String.valueOf(i));
		}
		
		final CTabFolder folder = new CTabFolder(shell, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		folder.setSimple(false);
		folder.setUnselectedImageVisible(false);
		folder.setUnselectedCloseVisible(false);

		for (int i = 0; i < 8; i++) {
			CTabItem item = new CTabItem(folder, SWT.CLOSE);
			item.setText("Item " + i);
			// item.setImage(image);
			Text text = new Text(folder, SWT.MULTI | SWT.V_SCROLL
					| SWT.H_SCROLL);
			text.setText("Text for item " + i);
			item.setControl(text);
		}

		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		// image.dispose();
		display.dispose();

	}
}
