package clonepedia.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedConstructorsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedMethodsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.TokenScanner;
import org.eclipse.jdt.internal.corext.refactoring.StubTypeContext;
import org.eclipse.jdt.internal.corext.refactoring.TypeContextChecker;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaTypeCompletionProcessor;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.wizard.DetermineElementWizardPage.MethodParameterWrapper;

public class SkeletonGenerationDialog extends TitleAreaDialog {
	
	private ClonePatternGroupWrapper cpgWrapper;
	
	private Table candiateIntraPatternList;
	private Table selectedIntraPatternList;
	
	private Button addPatternButton;
	private Button removePatternButton;
	
	private HashMap<Method, PathPatternGroupWrapper> methodAndCloneMap 
		= new HashMap<Method, PathPatternGroupWrapper>();
	
	private ArrayList<PathPatternGroupWrapper> buildingWrapperList
		= new ArrayList<PathPatternGroupWrapper>();

	public SkeletonGenerationDialog(Shell parentShell, ClonePatternGroupWrapper cpgWrapper) {
		super(parentShell);
		this.cpgWrapper = cpgWrapper;
	}
	
	public void create() {
		setHelpAvailable(false);
		super.create();
		
		try {
			// Set the title
			setTitle("Code Skeleton Generation:" + this.cpgWrapper.getEpitomise3());
			// Set the message
			setMessage("Please customize your skeleton generating process", 
					IMessageProvider.INFORMATION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = new Composite(parent, SWT.BORDER);
		area.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout areaLayout = new GridLayout();
		areaLayout.numColumns = /*1*/3;
		areaLayout.horizontalSpacing = 10;
		areaLayout.marginHeight = 10;
		areaLayout.marginBottom = 10;
		areaLayout.marginLeft = 10;
		areaLayout.marginRight = 10;
		areaLayout.verticalSpacing = 10;
		area.setLayout(areaLayout);
		
		GridData candidateGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		candidateGridData.widthHint = 150;
		candiateIntraPatternList = new Table(area, SWT.BORDER | SWT.FULL_SELECTION);
		candiateIntraPatternList.setLayoutData(candidateGridData);
		candiateIntraPatternList.setHeaderVisible(true);
		candiateIntraPatternList.setLinesVisible(true);
		TableColumn candidateColumn = new TableColumn(candiateIntraPatternList, SWT.NONE);
		candidateColumn.setText("Candidate Intra Pattern");
		for(PathPatternGroup ppg: this.cpgWrapper.getClonePattern()){
			PathPatternGroupWrapper ppgWrapper = new PathPatternGroupWrapper(ppg);
			
			TableItem item = new TableItem(candiateIntraPatternList, SWT.NONE);
			item.setText(ppgWrapper.getEpitomise3());
			item.setData(ppgWrapper);
		}
		candidateColumn.pack();
		
		Composite buttonComposite = new Composite(area, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));
		
		addPatternButton = new Button(buttonComposite, SWT.NONE);
		addPatternButton.setText(">>");
		addPatternButton.addMouseListener(new AddPatternListener());
		removePatternButton = new Button(buttonComposite, SWT.NONE);
		removePatternButton.setText("<<");
		removePatternButton.addMouseListener(new RemovePatternListener());
		
		GridData selectedGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		selectedGridData.widthHint = 150;
		selectedIntraPatternList = new Table(area, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		selectedIntraPatternList.setLayoutData(selectedGridData);
		selectedIntraPatternList.setHeaderVisible(true);
		selectedIntraPatternList.setLinesVisible(true);
		TableColumn selectedColumn = new TableColumn(selectedIntraPatternList, SWT.NONE);
		selectedColumn.setText("Selected Intra Pattern                                   ");
		selectedColumn.pack();
		
		return area;
	}
	
	class AddPatternListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			int index = candiateIntraPatternList.getSelectionIndex();
			if(index != -1){
				TableItem tobeRemovedItem = candiateIntraPatternList.getItem(index);
				
				String name = tobeRemovedItem.getText();
				PathPatternGroupWrapper wrapper = (PathPatternGroupWrapper)tobeRemovedItem.getData();
				
				SkeletonGenerationWizard wizard = new SkeletonGenerationWizard(wrapper);
				
				WizardDialog skeletonDialog = new WizardDialog(PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getShell(), wizard);
				
				
				if(skeletonDialog.open() == Window.OK){
					PathPatternGroupWrapper.PotentialLocation location = wrapper.new
							PotentialLocation(wizard.getPackageName(), wizard.getResidingClass(), 
									wizard.getSuperClass(), wizard.getInterfaces(), 
									wizard.getMethodName(), wizard.getMethodReturnType(), wizard.getMethodParamters());
					
					wrapper.setLocation(location);
					
					candiateIntraPatternList.remove(index);
					
					wrapper.setWizard(wizard);
					
					TableItem addedItem = new TableItem(selectedIntraPatternList, SWT.NONE);
					addedItem.setText(name);					
					addedItem.setData(wrapper);
					selectedIntraPatternList.getColumn(0).pack();
					
					buildingWrapperList.add(wrapper);
				}
				
				
			}
			
		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RemovePatternListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			int index = selectedIntraPatternList.getSelectionIndex();
			
			if(index != -1){
				TableItem tobeRemovedItem = selectedIntraPatternList.getItem(index);
				
				String name = tobeRemovedItem.getText();
				PathPatternGroupWrapper wrapper = (PathPatternGroupWrapper)tobeRemovedItem.getData();
				
				selectedIntraPatternList.remove(index);
				
				TableItem addedItem = new TableItem(candiateIntraPatternList, SWT.NONE);
				addedItem.setText(name);
				addedItem.setData(wrapper);
				candiateIntraPatternList.getColumn(0).pack();
				
				buildingWrapperList.remove(wrapper);
			}
			
			
		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	protected void createButtonsForButtonBar(Composite parent) {
		GridData parentGridData = new GridData();
		parentGridData.grabExcessHorizontalSpace = true;
		parentGridData.grabExcessVerticalSpace = true;
		parentGridData.horizontalAlignment = GridData.FILL;
		parentGridData.verticalAlignment = GridData.FILL;
		parent.setData(parentGridData);

		parent.setLayoutData(parentGridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Cofirm", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = 
				createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	
	protected Button createOkButton(Composite parent, int id, String label, boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	protected Point getInitialSize() {
		return new Point(800, 575);
	}
	
	private boolean isValidInput() {
		boolean valid = true;
		
		return valid;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		List<ICompilationUnit> unitList = generateCodeSkeleton();
	
		for(ICompilationUnit unit: unitList){
			
			IResource resource= unit.getResource();
			if (resource != null) {
				IEditorPart javaEditor;
				try {
					javaEditor = JavaUI.openInEditor(unit);
					JavaUI.revealInEditor(javaEditor,
							(IJavaElement) unit);
					//goToLine(javaEditor, instance);
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	private List<ICompilationUnit> generateCodeSkeleton() {
		
		HashMap<String, Class> targetClassList = buildGenerationModel();
		
		List<ICompilationUnit> unitList = new ArrayList<ICompilationUnit>();
		
		for(String key: targetClassList.keySet()){
			Class clazz = targetClassList.get(key);
			
			try {
				ICompilationUnit unit = new SkeletonGenerationAction().createType(null, clazz, this.methodAndCloneMap);
				unitList.add(unit);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return unitList;
	}
	
	private HashMap<String, Class> buildGenerationModel(){
		HashMap<String, clonepedia.model.ontology.Class> targetClassList = 
				new HashMap<String, clonepedia.model.ontology.Class>();
		
		for(PathPatternGroupWrapper wrapper: buildingWrapperList){
			PathPatternGroupWrapper.PotentialLocation location = wrapper.getLocation();
			
			/**
			 * inner class has not been supported here
			 */
			String className = location.packageName + "." + location.className;
			Class targetClass = targetClassList.get(className);
			if(null == targetClass){
				targetClass = new Class(className, null, className);
			}
			
			if(null == targetClass.getSuperClass() && location.superClassName != null && !location.superClassName.equals("")){
				Class superClass = new Class(location.superClassName, null, location.superClassName);
				targetClass.addSuperClassOrInterface(superClass);
			}
			
			for(String interfaceName: location.interfaceNames){
				Interface interf = targetClass.findImplementedInterface(interfaceName);
				if(null == interf){
					Interface interfaze = new Interface(interfaceName, null, interfaceName);
					targetClass.addInterface(interfaze);
				}
			}
			
			List<String> methodParameterTypes = new ArrayList<String>();
			for(MethodParameterWrapper wrapper0: location.methodParameterNames){
				methodParameterTypes.add(wrapper0.parameterType);
			}
			
			Method m = targetClass.findMethod(location.methodName, methodParameterTypes);
			if(null == m){
				ArrayList<Variable> parameters = new ArrayList<Variable>();
				for(String paramType: methodParameterTypes){
					String paramName = paramType.substring(paramType.lastIndexOf(".")+1, paramType.length());
					paramName = paramName.toLowerCase();
					
					Variable var = new Variable(paramName, new Class(paramType, null, paramType), false);
					
					parameters.add(var);
				}
				m = new Method(targetClass, location.methodName, new Class(location.methodReturnTypeName, null, location.methodReturnTypeName), 
						parameters);
				targetClass.getMethods().add(m);
			}
			
			this.methodAndCloneMap.put(m, wrapper);
			
			targetClassList.put(className, targetClass);
		}
		
		return targetClassList;
	}
	
	
}
