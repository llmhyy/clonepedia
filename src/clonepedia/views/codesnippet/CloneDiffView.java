package clonepedia.views.codesnippet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.CompilationUnitPool;
import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.CloneSetWrapper;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.Method;
import clonepedia.views.util.ViewUtil;

public class CloneDiffView extends ViewPart {

	private clonepedia.java.model.CloneSetWrapper set;
	private ScrolledComposite scrolledComposite;
	private SashForm sashForm;
	private DiffCounterRelationGroupEmulator relationGroup;
	
	public CloneDiffView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		this.scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	}
	
	public void showCodeSnippet(CloneSet set, DiffCounterRelationGroupEmulator relationGroup){
		CompilationUnitPool pool = new CompilationUnitPool();
		CloneSetWrapper setWrapper = new clonepedia.java.model.CloneSetWrapper(set, pool);
		this.set = new CloneInformationExtractor().extractCounterRelationalDifferencesWithinSyntacticBoundary(setWrapper);
		this.relationGroup = relationGroup;
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
		
		int widgetHeight = overallHeight - 50;
		int widgetWidth = (set.size() <= 4)? (overallWidth/set.size() - 20) : overallWidth/4;
		
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
		labelLayoutData.widthHint = widgetWidth;
		label.setLayoutData(labelLayoutData);
		label.setText(instanceWrapper.getCloneInstance().getResidingMethod().getFullName());
		
		GridData scrollCodeLayoutDdata = new GridData();
		scrollCodeLayoutDdata.heightHint = widgetHeight; 
		scrollCodeLayoutDdata.widthHint = widgetWidth;
		
		ScrolledComposite sc = new ScrolledComposite(codeComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setLayoutData(scrollCodeLayoutDdata);
		
		Composite com = new Composite(sc, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		com.setLayout(gridLayout);
		
		generateCodeText(instanceWrapper, com);
		
		sc.setContent(com);
		sc.setMinSize(com.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.layout();
		codeComposite.layout();
	}
	
	private void generateCodeText(CloneInstanceWrapper instanceWrapper, Composite parent){
		StyledText text = new StyledText(parent, SWT.WRAP);
		
		String content = null;
		
		CloneInstance instance = instanceWrapper.getCloneInstance();
		
		ICompilationUnit iunit = ViewUtil.getCorrespondingCompliationUnit(instance);
		if(iunit != null){
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(iunit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			int startClonePosition = cu.getPosition(instance.getStartLine(), 0);
			int endClonePosition = cu.getPosition(instance.getEndLine()+1, 0);
			
			MethodDeclaration methodDeclaration = instanceWrapper.getMethodDeclaration();
			int methodStartPosition = methodDeclaration.getStartPosition();
			int methodEndPosition = methodStartPosition + methodDeclaration.getLength();
			
			try {
				content = iunit.getSource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
			content = content.substring(methodStartPosition, methodEndPosition);
			text.setText(content);
			
			//cloneStyleRange.fontStyle = SWT.BOLD;
			
			/*StyleRange cloneStyleRange = new StyleRange();
			cloneStyleRange.start = startClonePosition - methodStartPosition;
			cloneStyleRange.length = endClonePosition - startClonePosition;
			cloneStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
			text.setStyleRange(cloneStyleRange);*/
			
			int startCloneLineNumber = cu.getLineNumber(startClonePosition) - cu.getLineNumber(methodStartPosition);
			int lineCount =  cu.getLineNumber(endClonePosition) - cu.getLineNumber(startClonePosition);
			
			text.setLineBackground(startCloneLineNumber, lineCount,
					new Color(Display.getCurrent(), 150, 250, 250));
			
			ASTNode doc = methodDeclaration.getJavadoc();
			if(doc != null){
				StyleRange commentStyleRange = new StyleRange();
				commentStyleRange.start = doc.getStartPosition() - methodStartPosition;
				commentStyleRange.length = doc.getLength();
				commentStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
				text.setStyleRange(commentStyleRange);	
			}
			/**
			 * for counter relationally difference
			 */
			for(DiffCounterRelationGroupEmulator relationGroup: this.set.getRelationGroups()){
				for(DiffInstanceElementRelationEmulator relation: relationGroup.getRelations()){
					CloneInstanceWrapper referInstance = relation.getInstanceWrapper();
					if(referInstance.equals(instanceWrapper)){
						ASTNode node = relation.getNode();
						int startNodePosition = node.getStartPosition();
						int length = node.getLength();
						
						StyleRange CRDStyleRange = new StyleRange();
						CRDStyleRange.start = startNodePosition - methodStartPosition;
						CRDStyleRange.length = length;
						CRDStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
						
						text.setStyleRange(CRDStyleRange);
					}
				}
			}
			
			/**
			 * for uncounter relationally difference
			 */
			for(ASTNode node: instanceWrapper.getUncounterRelationalDifferenceNodes()){
				int startNodePosition = node.getStartPosition();
				int length = node.getLength();
				
				StyleRange CRDStyleRange = new StyleRange();
				CRDStyleRange.start = startNodePosition - methodStartPosition;
				CRDStyleRange.length = length;
				CRDStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
				
				text.setStyleRange(CRDStyleRange);
			}
			
			/**
			 * for selected counter relational difference
			 */
			if(relationGroup != null){
				for(DiffInstanceElementRelationEmulator relation: relationGroup.getRelations()){
					if(relation.getInstanceWrapper().equals(instanceWrapper)){
						ASTNode node = relation.getNode();
						
						StyleRange selectedStyleRange = getStyleRangeFromASTNode(node,
								methodStartPosition, Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						selectedStyleRange.fontStyle = SWT.BOLD;
						
						text.setStyleRange(selectedStyleRange);
					}
				}
			}
		}
		
		if(content == null){
			content = instanceWrapper.getMethodDeclaration().toString();
			text.setText(content);
		}
		
		/*GridData gData = new GridData();
		gData.heightHint = 600;
		gData.widthHint = 600;
		text.setLayoutData(gData);*/
	}
	
	private StyleRange getStyleRangeFromASTNode(ASTNode node, int methodStartPosition, Color color){
		
		int startNodePosition = node.getStartPosition();
		int length = node.getLength();
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = startNodePosition - methodStartPosition;
		styleRange.length = length;
		styleRange.foreground = color;
		
		return styleRange;
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
