package clonepedia.views.codesnippet;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import clonepedia.Activator;
import clonepedia.java.ASTComparator;
import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.Diff;
import clonepedia.java.model.DiffElement;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.perspective.CloneDiffPerspective;
import clonepedia.util.ImageUI;
import clonepedia.views.DiffPropertyView;
import clonepedia.views.util.ViewUtil;
import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

public class CloneDiffView extends ViewPart {

	private clonepedia.java.model.CloneSetWrapper set;
	private Diff diff;
//	private CloneSet set;
//	private Multiset diffList;
	
	private ScrolledComposite scrolledComposite;
	private SashForm sashForm;
	
	/**
	 * Show which diff to highlight
	 */
	private int diffIndex = -1;
	
	public void setDiffIndex(int diffIndex) {
		this.diffIndex = diffIndex;
	}

	public CloneDiffView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		this.scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL /*| SWT.V_SCROLL*/);
		
		hookActionsOnToolBar();
	}
	
	private void hookActionsOnToolBar(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		Action nextDiffAction = new Action("Check Next Diff"){
			public void run(){
				
				if(set == null) return;
				
				ArrayList<Diff> list = set.getDiffs();
				if(diffIndex < list.size()-1){
					diffIndex++;
				}
				
				if(diffIndex >= 0){	
					if(diffIndex > list.size()-1){
						diffIndex = 0;
					}
					diff = list.get(diffIndex);
				}
				
				showCodeSnippet(diff);
				
				DiffPropertyView propertyViewPart = (DiffPropertyView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.DIFF_PROPERTY_VIEW);
				if(propertyViewPart != null){
					propertyViewPart.showDiffInformation(diff);
					propertyViewPart.setFocus();
				}
			}
		};
		nextDiffAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.DOWN_ARROW));
		
		Action prevDiffAction = new Action("Check Previous Diff"){
			public void run(){
				
				if(set == null) return;
				
				ArrayList<Diff> list = set.getDiffs();
				if(diffIndex > 0){
					diffIndex--;
				}
				
				if(diffIndex >= 0){					
					diff = list.get(diffIndex);
				}
				
				showCodeSnippet(diff);
				
				DiffPropertyView propertyViewPart = (DiffPropertyView)getSite().getWorkbenchWindow().getActivePage().findView(CloneDiffPerspective.DIFF_PROPERTY_VIEW);
				if(propertyViewPart != null){
					propertyViewPart.showDiffInformation(diff);
				}
			}
		};
		prevDiffAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.UP_ARROW));
		
		
		toolBar.add(nextDiffAction);
		toolBar.add(prevDiffAction);
	}
	
	public void showCodeSnippet(Diff relationGroup){
		if(this.set != null){
			showCodeSnippet(this.set, relationGroup);
		}
		
		for(DiffElement d: relationGroup.getElements()){
			System.out.println(d.getSeq());
		}
	}
	
	/**
	 * {@code diff} is the diff to be highlighted in syntacticCloneSetWrapper
	 * 
	 * @param syntacticCloneSetWrapper
	 * @param diff
	 */
	public void showCodeSnippet(clonepedia.java.model.CloneSetWrapper syntacticCloneSetWrapper, Diff diff){
		//CompilationUnitPool pool = new CompilationUnitPool();
		//CloneSetWrapper setWrapper = new clonepedia.java.model.CloneSetWrapper(set, pool);
		//this.set = new CloneInformationExtractor().extractCounterRelationalDifferencesWithinSyntacticBoundary(setWrapper);
		this.set = syntacticCloneSetWrapper;
		this.diff = diff;
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
		//scrolledComposite.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.layout();
	}

	private void generateCodeComposite(Composite parent, CloneInstanceWrapper instanceWrapper){
		
		int overallHeight = parent.getParent().getClientArea().height;
		int overallWidth = parent.getParent().getClientArea().width;
		
		int widgetHeight = overallHeight - 20;
		int widgetWidth = (set.size() <= 4)? (overallWidth/set.size() - 20) : overallWidth/4;
		
		Composite codeComposite = new Composite(parent, SWT.BORDER);
		GridLayout overGridLayout = new GridLayout();
		overGridLayout.numColumns = 1;
		overGridLayout.verticalSpacing = 1;
		overGridLayout.marginLeft = 0;
		overGridLayout.marginRight = 0;
		codeComposite.setLayout(overGridLayout);
		
		Label label = new Label(codeComposite, SWT.NONE);
		GridData labelLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		labelLayoutData.heightHint = 20;
		labelLayoutData.widthHint = widgetWidth;
		/*labelLayoutData.grabExcessHorizontalSpace = true;
		labelLayoutData.grabExcessVerticalSpace = true;*/
		label.setLayoutData(labelLayoutData);
		String ownerName; 
		String methodName;
		if(instanceWrapper.getCloneInstance().getResidingMethod() != null){
			ownerName = instanceWrapper.getCloneInstance().getResidingMethod().getOwner().getFullName();
			methodName = instanceWrapper.getCloneInstance().getResidingMethod().getMethodName();
		}
		else{
			ownerName = instanceWrapper.getCloneInstance().getSimpleFileName();
			methodName = "";
		}
		String typeName = ownerName.substring(ownerName.lastIndexOf("/")+1, ownerName.length());
		label.setText(typeName);
		label.setToolTipText(ownerName + ":" + methodName);
		
		GridData scrollCodeLayoutData = new GridData(GridData.FILL_BOTH);
		scrollCodeLayoutData.heightHint = widgetHeight; 
		scrollCodeLayoutData.widthHint = widgetWidth;
		//scrollCodeLayoutDdata.grabExcessHorizontalSpace = true;
		//scrollCodeLayoutDdata.grabExcessVerticalSpace = true;
		
		/*ScrolledComposite sc = new ScrolledComposite(codeComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setLayoutData(scrollCodeLayoutDdata);*/
		
		/*Composite com = new Composite(sc, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		com.setLayout(gridLayout);*/
		
		generateCodeText(instanceWrapper, codeComposite, scrollCodeLayoutData);
		
		//sc.setContent(com);
		//sc.setMinSize(com.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//sc.layout();
		codeComposite.layout();
	}
	
	@SuppressWarnings("deprecation")
	private void generateCodeText(CloneInstanceWrapper instanceWrapper, Composite parent, GridData scrollCodeLayoutData){
		final StyledText text = new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		
		text.setLayoutData(scrollCodeLayoutData);
		
		Menu menu = new Menu(parent);
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText("Show in java editor");
		text.setMenu(menu);
		
		String content = null;
		
		CloneInstance instance = instanceWrapper.getCloneInstance();
		File file = new File(instance.getFileLocation());
		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile= workspace.getRoot().getFileForLocation(location);
		
		final ICompilationUnit iunit = JavaCore.createCompilationUnitFrom(ifile);
		if(iunit != null){
			
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(iunit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			//int startClonePosition = cu.getPosition(instance.getStartLine()-1, 0);
			//int endClonePosition = cu.getPosition(instance.getEndLine(), 0);
			
			ASTNode containingNode = instanceWrapper.getMinimumContainingASTNode();
			int methodStartPosition = containingNode.getStartPosition();
			
			try {
				content = iunit.getSource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
			text.setText(content);
			
			final int startCloneLineNumber = instance.getStartLine();
			final int lineCount =  instance.getLength();

			menuItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					ViewUtil.openJavaEditorForCloneInstace(iunit, startCloneLineNumber, startCloneLineNumber+lineCount-1);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			
			text.setTopIndex(startCloneLineNumber - 3);
			
			Color disposableColoar = new Color(Display.getCurrent(), 150, 250, 250);
			text.setLineBackground(startCloneLineNumber-1, lineCount, disposableColoar);
			
			ASTNode doc = null;
			if(containingNode instanceof BodyDeclaration){
				doc = ((BodyDeclaration)containingNode).getJavadoc();				
			}
			
			ArrayList<StyleRange> rangeList = new ArrayList<>();
			
			//ArrayList<StyleRange> keywordList = generateKeywordsStyle(text);
			//rangeList.addAll(keywordList);
			
			if(doc != null){
				ArrayList<StyleRange> list = generateStyleRangeFromASTNode(text, doc, methodStartPosition, 
						CloneDiffView.DOC_STYLE, 0, false);
				rangeList.addAll(list);
			}
			/**
			 * for counter relationally difference
			 */
			for(Diff diff: this.set.getDiffs()){
				for(DiffElement diffElement: diff.getElements()){
					CloneInstanceWrapper referInstance = diffElement.getInstanceWrapper();
					if(referInstance.equals(instanceWrapper)){
						if(!diffElement.getSeq().isEpisolonTokenSeq()){
							ArrayList<StyleRange> list = generateStyleRangeFromASTNode(text, diffElement.getSeq(), 
									methodStartPosition, getDiffStyle(diff), diff.getElements().size(), false);
							rangeList.addAll(list);							
						}
						
					}
				}
			}
			
			/**
			 * for uncounter relationally difference
			 */
//			for(ASTNode node: instanceWrapper.getUncounterRelationalDifferenceNodes()){
//				ArrayList<StyleRange> list = generateStyleRangeFromASTNode(text, node, methodStartPosition, 
//						CloneDiffView.GAP_DIFF_STYLE, 1, false);
//				rangeList.addAll(list);
//			}
			
			/**
			 * for selected counter relational difference
			 */
			if(diff != null){
				for(DiffElement diffElement: diff.getElements()){
					if(!diffElement.getSeq().isEpisolonTokenSeq()){
						if(diffElement.getInstanceWrapper().equals(instanceWrapper)){
							ArrayList<StyleRange> list = generateStyleRangeFromASTNode(text, diffElement.getSeq(),
									methodStartPosition, getDiffStyle(diff), diff.getElements().size(), true);
							rangeList.addAll(list);
							
							text.setTopIndex(cu.getLineNumber(diffElement.getSeq().getStartPosition()) - 3);
							text.setHorizontalIndex(cu.getColumnNumber(diffElement.getSeq().getStartPosition()) - 20);
						}
					}
				}
			}
			
			text.addLineStyleListener(new CustomizedLineStyleListener(text, rangeList));
		}
		
		if(content == null){
			content = instanceWrapper.getMinimumContainingASTNode().toString();
			text.setText(content);
		}
		
	}
	
	public class CustomizedLineStyleListener implements LineStyleListener{
		private ArrayList<StyleRange> rangeList;
		private StyledText text;
		
		public CustomizedLineStyleListener(StyledText text, ArrayList<StyleRange> rangeList){
			this.rangeList = rangeList;
			this.text = text;
		}
		
		public void lineGetStyle(LineStyleEvent e)
	    {
	    	
	    	e.styles = sortList();
	        //Set the line number
	        e.bulletIndex = text.getLineAtOffset(e.lineOffset);

	        //Set the style, 12 pixles wide for each digit
	        StyleRange style = new StyleRange();
	        style.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	        style.metrics = new GlyphMetrics(0, 0, Integer.toString(text.getLineCount()+1).length()*12);

	        //Create and set the bullet
	        e.bullet = new Bullet(ST.BULLET_NUMBER, style);
	    }
		
		/**
		 * Bubble sort
		 * @return
		 */
		private StyleRange[] sortList(){
			StyleRange[] rangeArray = this.rangeList.toArray(new StyleRange[0]);
			for(int i=0; i<rangeArray.length; i++){
				for(int j=1; j<rangeArray.length-i; j++){
					int prev = rangeArray[j-1].start;
					int post = rangeArray[j].start;
					if(prev > post){
						StyleRange temp = rangeArray[j];
						rangeArray[j] = rangeArray[j-1];
						rangeArray[j-1] = temp;
					}
				}
			}
			
			System.currentTimeMillis();
			
			return rangeArray;
		}
	}
	
	private int getDiffStyle(Diff diff){
		
		SeqMultiset multiset = new SeqMultiset();
		for(DiffElement ele: diff.getElements()){
			multiset.addTokenSeq(ele.getSeq());
		}
		
		
		if(multiset.isGapped()){
			return COUNTER_GAP_DIFF_STYPE;
		}
		else{
			return multiset.containsDuplicate() ? COUNTER_PARTIAL_DIFF_STYLE : COUNTER_EVEN_DIFF_STYLE ;
		}
		
	}
	
	private boolean isAllTheElementDifferent(ASTNode[] nodeList){
		for(int i=0; i<nodeList.length; i++){
			for(int j=i+1; j<nodeList.length; j++){
				if(new ASTComparator().isMatch(nodeList[i], nodeList[j])){
					return false;
				}
			}
		}
		return true;
	}
	
	private ArrayList<StyleRange> generateKeywordsStyle(StyledText text){
		ArrayList<StyleRange> rangeList = new ArrayList<>();
		
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		int style = SWT.BOLD;
		
		String codeText = text.getText();
		
		int startPosition = 0;
		int length = 0;
		
		for(String keyword: this.keywords){
			
			startPosition = codeText.indexOf(keyword);
			
			while(startPosition != -1){
				length = keyword.length();
				
				StyleRange styleRange = new StyleRange();
				////styleRange.start = startNodePosition - methodStartPosition;
				styleRange.start = startPosition;
				styleRange.length = length;
				styleRange.foreground = color;
				styleRange.fontStyle = style;	
				
				rangeList.add(styleRange);
				text.setStyleRange(styleRange);

				startPosition = codeText.indexOf(keyword, startPosition+length);
			}
			
		}
		
		return rangeList;
	}
	
	private String[] keywords = {"package ", "import ", "private ", "public ", "protected ", "class ", "interface ", "new ", 
			"final ", "static ", "int ", "double ", "short ", "long ", "char ", "boolean ", "void ", "instanceof ", 
			"switch", "case", "for(", "for ", "if(", "if ", "else", "while(", "while ", "try{", "try ", "catch(", "catch ", "finally",
			"return", "throw", "throws", "null"};
	
	private final static int DOC_STYLE = 1;
	private final static int GAP_DIFF_STYLE = 2;
	//private final static int COUNTER_DIFF_STYLE = 3;
	private static final int COUNTER_EVEN_DIFF_STYLE = 3;
	private static final int COUNTER_PARTIAL_DIFF_STYLE = 4;
	private static final int COUNTER_GAP_STYLE = 5;
	private static final int COUNTER_GAP_DIFF_STYPE = 6;
	
	private ArrayList<StyleRange> generateStyleRangeFromASTNode(StyledText text, ASTNode node, 
			int methodStartPosition, int codeTextStyle, int relationGroupSize, boolean highlight){
		ArrayList<StyleRange> rangeList = new ArrayList<>();
		
		int style = SWT.NORMAL;
		
		Color color = determineColor(codeTextStyle);
		
		if(highlight){
			style = SWT.BOLD;
		}
		
		int startNodePosition = node.getStartPosition();
		int length = node.getLength();
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = startNodePosition;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.fontStyle = style;	
		
		rangeList.add(styleRange);
		text.setStyleRange(styleRange);
		
		return rangeList;
	}
	
	private ArrayList<StyleRange> generateStyleRangeFromASTNode(StyledText text, TokenSeq seq, 
			int methodStartPosition, int codeTextStyle, int relationGroupSize, boolean highlight){
		ArrayList<StyleRange> rangeList = new ArrayList<>();
		
		int style = SWT.NORMAL;
		
		Color color = determineColor(codeTextStyle);
		
		if(highlight){
			style = SWT.BOLD;
		}
		
		int startNodePosition = seq.getStartPosition();
		int length = seq.getPositionLength();
		if(seq.isEpisolonTokenSeq()){
			Token previousToken = seq.getTokens().get(0).getPreviousToken();
			if(previousToken != null){
				startNodePosition = previousToken.getStartPosition();
			}
			length = 0;
		}
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = startNodePosition;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.fontStyle = style;	
		
		rangeList.add(styleRange);
		text.setStyleRange(styleRange);
		
		return rangeList;
	}

	private Color determineColor(int codeTextStyle) {
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		switch(codeTextStyle){
		case DOC_STYLE:
			color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
			break;
		case GAP_DIFF_STYLE:
			color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
			break;
		case COUNTER_EVEN_DIFF_STYLE:
			//int s = (relationGroupSize < set.size())? SWT.COLOR_MAGENTA : SWT.COLOR_RED;
			color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			break;
		case COUNTER_PARTIAL_DIFF_STYLE:
			color = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
			break;
		case COUNTER_GAP_STYLE:
			color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
			break;
		case COUNTER_GAP_DIFF_STYPE:
			color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
			break;
		}
		return color;
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
