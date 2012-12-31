package clonepedia.views.codesnippet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class CloneDiffView extends ViewPart {

	public CloneDiffView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		/**
		 * If there is no these two statements, the following sash form will not present in UI.
		 */
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		SashForm sashForm = new SashForm(scrolledComposite, SWT.HORIZONTAL);
		for(int i=0; i<5; i++){
			generateCodeComposite(sashForm);
		}
		//textList[3].dispose();
		scrolledComposite.setContent(sashForm);
		scrolledComposite.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.layout();
	}

	private void generateCodeComposite(Composite parent){
		
		Composite codeComposite = new Composite(parent, SWT.BORDER);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.wrap = false;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		codeComposite.setLayout(rowLayout);
		
		Label label = new Label(codeComposite, SWT.NONE);
		label.setText("method");
		RowData rD = new RowData(60, 20);
		//rD.exclude = true;
		label.setLayoutData(rD);
		
		RowData rD1 = new RowData(300, 300);
		
		ScrolledComposite sc = new ScrolledComposite(codeComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setLayoutData(rD1);
		//sc.setLayout(new FillLayout());
		Composite com = new Composite(sc, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		com.setLayout(gridLayout);
		Text text = new Text(com, SWT.WRAP);
		text.setText("!ENTRY org.eclipse.osgi 2 1 2012-12-31 15:16:30.726!MESSAGE NLS missing message: " +
				"hudsonMonitor_unknown_build_error in: com.sonatype.buildserver.eclipse.ui.messages15:16:30.807 " +
				"[Worker-0] DEBUG o.m.i.e.a.i.AuthenticationPlugin - Starting the AuthenticationPlugin...");
		GridData gData = new GridData(600, 400);
		text.setLayoutData(gData);
		
		sc.setContent(com);
		//sc.setMinSize(10, 10);
		sc.setMinSize(com.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//sc.setMinSize(com.computeSize(SWT.DEFAULT, ));
		sc.layout();
		codeComposite.layout();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
