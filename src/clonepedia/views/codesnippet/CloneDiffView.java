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

import clonepedia.java.CompilationUnitPool;
import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Method;

public class CloneDiffView extends ViewPart {

	private clonepedia.java.model.CloneSetWrapper set;
	private ScrolledComposite scrolledComposite;
	private SashForm sashForm;
	
	public CloneDiffView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		this.scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	}
	
	public void showCodeSnippet(CloneSet set){
		CompilationUnitPool pool = new CompilationUnitPool();
		this.set = new clonepedia.java.model.CloneSetWrapper(set, pool);
		/**
		 * If there is no these two statements, the following sash form will not present in UI.
		 */
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		this.sashForm = new SashForm(scrolledComposite, SWT.HORIZONTAL);
		for(CloneInstanceWrapper instanceWrapper: this.set){
			generateCodeComposite(sashForm, instanceWrapper);
		}
		
		scrolledComposite.setContent(sashForm);
		scrolledComposite.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.layout();
	}

	private void generateCodeComposite(Composite parent, CloneInstanceWrapper instanceWrapper){
		
		int overallHeight = parent.getParent().getClientArea().height;
		int overallWidth = parent.getParent().getClientArea().width;
		
		Composite codeComposite = new Composite(parent, SWT.BORDER);
		GridLayout overGridLayout = new GridLayout();
		overGridLayout.numColumns = 1;
		overGridLayout.verticalSpacing = 1;
		overGridLayout.marginLeft = 0;
		overGridLayout.marginRight = 0;
		codeComposite.setLayout(overGridLayout);
		
		Label label = new Label(codeComposite, SWT.NONE);
		GridData labelLayoutData = new GridData();
		labelLayoutData.heightHint = 20;
		labelLayoutData.grabExcessHorizontalSpace = true;
		label.setLayoutData(labelLayoutData);
		label.setText(instanceWrapper.getCloneInstance().getResidingMethod().getFullName());
		
		GridData scrollCodeLayoutDdata = new GridData();
		scrollCodeLayoutDdata.heightHint = overallHeight - 50;
		if(set.size() <= 4){
			scrollCodeLayoutDdata.widthHint = overallWidth/set.size() - 20;			
		}
		else{
			scrollCodeLayoutDdata.widthHint = overallWidth/4;
		}
		
		ScrolledComposite sc = new ScrolledComposite(codeComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setLayoutData(scrollCodeLayoutDdata);
		
		Composite com = new Composite(sc, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		com.setLayout(gridLayout);
		Text text = new Text(com, SWT.WRAP);
		text.setText(instanceWrapper.getMethodDeclaration().toString());
		GridData gData = new GridData();
		gData.heightHint = 600;
		gData.widthHint = 600;
		text.setLayoutData(gData);
		
		sc.setContent(com);
		sc.setMinSize(com.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.layout();
		codeComposite.layout();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public clonepedia.java.model.CloneSetWrapper getSet() {
		return set;
	}

	public void setSet(clonepedia.java.model.CloneSetWrapper set) {
		this.set = set;
	}

}
