package clonepedia.dialog;

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
import clonepedia.wizard.CodeSkeletonGenerationUtil;
import clonepedia.wizard.SkeletonGenerationWizard;

public class SkeletonGenerationDialog extends TitleAreaDialog {
	
	private ClonePatternGroupWrapper cpgWrapper;
	
	private Table candiateIntraPatternList;
	private Table selectedIntraPatternList;
	
	private Button addPatternButton;
	private Button removePatternButton;
	
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

	/**
	 * Class used in stub creation routines to add needed imports to a
	 * compilation unit.
	 */
	public static class ImportsManager {
	
		private final CompilationUnit fAstRoot;
		private final ImportRewrite fImportsRewrite;
	
		/* package */ImportsManager(CompilationUnit astRoot) {
			fAstRoot = astRoot;
			fImportsRewrite = StubUtility.createImportRewrite(astRoot, true);
		}
	
		/* package */ICompilationUnit getCompilationUnit() {
			return fImportsRewrite.getCompilationUnit();
		}
	
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an
		 * import of an other type with the same simple name, the import is not
		 * added.
		 * 
		 * @param qualifiedTypeName
		 *            The fully qualified name of the type to import (dot
		 *            separated).
		 * @return Returns the simple type name that can be used in the code or
		 *         the fully qualified type name if an import conflict prevented
		 *         the import.
		 */
		public String addImport(String qualifiedTypeName) {
			return fImportsRewrite.addImport(qualifiedTypeName);
		}
	
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an
		 * import of an other type with the same simple name, the import is not
		 * added.
		 * 
		 * @param qualifiedTypeName
		 *            The fully qualified name of the type to import (dot
		 *            separated).
		 * @param insertPosition
		 *            the offset where the import will be used
		 * @return Returns the simple type name that can be used in the code or
		 *         the fully qualified type name if an import conflict prevented
		 *         the import.
		 * 
		 * @since 3.8
		 */
		public String addImport(String qualifiedTypeName, int insertPosition) {
			ImportRewriteContext context = new ContextSensitiveImportRewriteContext(
					fAstRoot, insertPosition, fImportsRewrite);
			return fImportsRewrite.addImport(qualifiedTypeName, context);
		}
	
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an
		 * import of an other type with the same simple name, the import is not
		 * added.
		 * 
		 * @param typeBinding
		 *            the binding of the type to import
		 * 
		 * @return Returns the simple type name that can be used in the code or
		 *         the fully qualified type name if an import conflict prevented
		 *         the import.
		 */
		public String addImport(ITypeBinding typeBinding) {
			return fImportsRewrite.addImport(typeBinding);
		}
	
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an
		 * import of an other type with the same simple name, the import is not
		 * added.
		 * 
		 * @param typeBinding
		 *            the binding of the type to import
		 * @param insertPosition
		 *            the offset where the import will be used
		 * 
		 * @return Returns the simple type name that can be used in the code or
		 *         the fully qualified type name if an import conflict prevented
		 *         the import.
		 * 
		 * @since 3.8
		 */
		public String addImport(ITypeBinding typeBinding, int insertPosition) {
			ImportRewriteContext context = new ContextSensitiveImportRewriteContext(
					fAstRoot, insertPosition, fImportsRewrite);
			return fImportsRewrite.addImport(typeBinding, context);
		}
	
		/**
		 * Adds a new import declaration for a static type that is sorted in the
		 * existing imports. If an import already exists or the import would
		 * conflict with an import of an other static import with the same
		 * simple name, the import is not added.
		 * 
		 * @param declaringTypeName
		 *            The qualified name of the static's member declaring type
		 * @param simpleName
		 *            the simple name of the member; either a field or a method
		 *            name.
		 * @param isField
		 *            <code>true</code> specifies that the member is a field,
		 *            <code>false</code> if it is a method.
		 * @return returns either the simple member name if the import was
		 *         successful or else the qualified name if an import conflict
		 *         prevented the import.
		 * 
		 * @since 3.2
		 */
		public String addStaticImport(String declaringTypeName,
				String simpleName, boolean isField) {
			return fImportsRewrite.addStaticImport(declaringTypeName,
					simpleName, isField);
		}
	
		/* package */void create(boolean needsSave, IProgressMonitor monitor)
				throws CoreException {
			TextEdit edit = fImportsRewrite.rewriteImports(monitor);
			JavaModelUtil.applyEdit(fImportsRewrite.getCompilationUnit(), edit,
					needsSave, null);
		}
	
		/* package */void removeImport(String qualifiedName) {
			fImportsRewrite.removeImport(qualifiedName);
		}
	
		/* package */void removeStaticImport(String qualifiedName) {
			fImportsRewrite.removeStaticImport(qualifiedName);
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
				ICompilationUnit unit = createType(null, clazz);
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
			
			Method m = targetClass.findMethod(location.methodName, location.methodParameterNames);
			if(null == m){
				ArrayList<Variable> parameters = new ArrayList<Variable>();
				for(String paramType: location.methodParameterNames){
					String paramName = paramType.substring(paramType.lastIndexOf(".")+1, paramType.length());
					paramName = paramName.toLowerCase();
					
					Variable var = new Variable(paramName, new Class(paramType, null, paramType), false);
					
					parameters.add(var);
				}
				m = new Method(targetClass, location.methodName, new Class(location.methodReturnTypeName, null, location.methodReturnTypeName), 
						parameters);
				targetClass.getMethods().add(m);
			}
			
			targetClassList.put(className, targetClass);
		}
		
		return targetClassList;
	}
	
	// ---- creation ----------------

		/**
		 * Creates the new type using the entered field values.
		 * 
		 * @param monitor
		 *            a progress monitor to report progress.
		 * @throws CoreException
		 *             Thrown when the creation failed.
		 * @throws InterruptedException
		 *             Thrown when the operation was canceled.
		 */
		@SuppressWarnings("restriction")
		public ICompilationUnit createType(IProgressMonitor monitor, Class clazz) throws CoreException,
				InterruptedException {
			
			String classFullName = clazz.getFullName();
			String packageName = classFullName.substring(0, classFullName.lastIndexOf("."));
			
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}

			monitor.beginTask("Create Code from Clone...", 8);
			
			IPackageFragmentRoot root = CodeSkeletonGenerationUtil.getPackageFragmentRoot();
			IPackageFragment pack = root.getPackageFragment(packageName);
			if (pack == null) {
				pack = root.getPackageFragment(""); //$NON-NLS-1$
			}

			if (!pack.exists()) {
				String packName = pack.getElementName();
				pack = root.createPackageFragment(packName, true,
						new SubProgressMonitor(monitor, 1));
			} else {
				monitor.worked(1);
			}

			boolean needsSave = true;
			ICompilationUnit connectedCU = null;

			try {
				String typeName = getTypeNameWithoutParameters(clazz);

				boolean isInnerClass = false;//isEnclosingTypeSelected();
				boolean isClassExist = true;

				IType createdType = null;
				ImportsManager imports = null;
				int indent = 0;

				Set<String> existingImports = null;

				String lineDelimiter = null;
				if (!isInnerClass) {
					lineDelimiter = StubUtility.getLineDelimiterUsed(pack.getJavaProject());

					String cuName = getCompilationUnitName(typeName);
					
					ICompilationUnit parentCU = pack.getCompilationUnit(cuName);
					
					if(!parentCU.exists()){
						parentCU = pack.createCompilationUnit(cuName,
								"", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$	
						isClassExist = false;
					}
					
					// create a working copy with a new owner

					needsSave = true;
					parentCU.becomeWorkingCopy(new SubProgressMonitor(monitor, 1)); // cu
																					// is
																					// now
																					// a
																					// (primary)
																					// working
																					// copy
					connectedCU = parentCU;

					IBuffer buffer = parentCU.getBuffer();

					CompilationUnit astRoot = createASTForImports(parentCU);
					existingImports = getExistingImports(astRoot);
					imports = new ImportsManager(astRoot);
					// add an import that will be removed again. Having this import solves 14661
					imports.addImport(JavaModelUtil.concatenateName(pack.getElementName(), typeName));
					
					if(!isClassExist){
						
						/**
						 * create initial class skeleton such as "public class A{}"
						 */
						String simpleTypeStub = constructSimpleTypeStub(clazz);
						
						/**
						 * create location packages and corresponding AST.
						 */
						String cuContent = constructCUContent(parentCU, simpleTypeStub, lineDelimiter, clazz);
						buffer.setContents(cuContent);
						
						
						
						String typeContent = constructTypeStub(parentCU, imports, lineDelimiter, clazz, createdType, pack);
						
						int index = cuContent.lastIndexOf(simpleTypeStub);
						if (index == -1) {
							AbstractTypeDeclaration typeNode = (AbstractTypeDeclaration) astRoot.types().get(0);
							int start = ((ASTNode) typeNode.modifiers().get(0)).getStartPosition();
							int end = typeNode.getStartPosition() + typeNode.getLength();
							buffer.replace(start, end - start, typeContent);
						} else {
							buffer.replace(index, simpleTypeStub.length(), typeContent);
						}
					}

					createdType = parentCU.getType(typeName);
					
				} 
				/**
				 * add it back
				 */
				
				
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}

				// add imports for superclass/interfaces, so types can be resolved
				// correctly

				ICompilationUnit cu = createdType.getCompilationUnit();
				
				//cu.createPackageDeclaration(packageName, null);

				imports.create(false, new SubProgressMonitor(monitor, 1));

				JavaModelUtil.reconcile(cu);

				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}

				// set up again
				CompilationUnit astRoot = createASTForImports(imports.getCompilationUnit());
				imports = new ImportsManager(astRoot);

				createTypeMembers(createdType, imports, new SubProgressMonitor(monitor, 1), clazz);

				// add imports
				imports.create(false, new SubProgressMonitor(monitor, 1));

				removeUnusedImports(cu, existingImports, false);

				//JavaModelUtil.reconcile(cu);

				ISourceRange range = createdType.getSourceRange();

				IBuffer buf = cu.getBuffer();
				String originalContent = buf.getText(range.getOffset(),range.getLength());

				String formattedContent = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, originalContent,
						indent, lineDelimiter, pack.getJavaProject());
				formattedContent = Strings.trimLeadingTabsAndSpaces(formattedContent);
				buf.replace(range.getOffset(), range.getLength(), formattedContent);
				if (!isInnerClass) {
					String fileComment = getFileComment(cu);
					if (fileComment != null && fileComment.length() > 0) {
						buf.replace(0, 0, fileComment + lineDelimiter);
					}
				}
				
				//fCreatedType = createdType;

				if (needsSave) {
					cu.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}

			} finally {
				if (connectedCU != null) {
					connectedCU.discardWorkingCopy();
				}
				monitor.done();
			}
			
			return connectedCU;
		}

		private CompilationUnit createASTForImports(ICompilationUnit cu) {
			ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
			parser.setSource(cu);
			parser.setResolveBindings(true);
			parser.setFocalPosition(0);
			return (CompilationUnit) parser.createAST(null);
		}

		private Set<String> getExistingImports(CompilationUnit root) {
			List<ImportDeclaration> imports = root.imports();
			Set<String> res = new HashSet<String>(imports.size());
			for (int i = 0; i < imports.size(); i++) {
				res.add(ASTNodes.asString(imports.get(i)));
			}
			return res;
		}

		private void removeUnusedImports(ICompilationUnit cu,
				Set<String> existingImports, boolean needsSave)
				throws CoreException {
			ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
			parser.setSource(cu);
			parser.setResolveBindings(true);

			CompilationUnit root = (CompilationUnit) parser.createAST(null);
			if (root.getProblems().length == 0) {
				return;
			}

			List<ImportDeclaration> importsDecls = root.imports();
			if (importsDecls.isEmpty()) {
				return;
			}
			ImportsManager imports = new ImportsManager(root);

			int importsEnd = ASTNodes.getExclusiveEnd(importsDecls.get(importsDecls.size() - 1));
			IProblem[] problems = root.getProblems();
			for (int i = 0; i < problems.length; i++) {
				IProblem curr = problems[i];
				if (curr.getSourceEnd() < importsEnd) {
					int id = curr.getID();
					if (id == IProblem.UnusedImport
							|| id == IProblem.NotVisibleType) { // not visible
																// problems hide
																// unused -> remove
																// both
						int pos = curr.getSourceStart();
						for (int k = 0; k < importsDecls.size(); k++) {
							ImportDeclaration decl = importsDecls.get(k);
							if (decl.getStartPosition() <= pos
									&& pos < decl.getStartPosition()
											+ decl.getLength()) {
								if (existingImports.isEmpty()
										|| !existingImports.contains(ASTNodes
												.asString(decl))) {
									String name = decl.getName()
											.getFullyQualifiedName();
									if (decl.isOnDemand()) {
										name += ".*"; //$NON-NLS-1$
									}
									if (decl.isStatic()) {
										imports.removeStaticImport(name);
									} else {
										imports.removeImport(name);
									}
								}
								break;
							}
						}
					}
				}
			}
			imports.create(needsSave, null);
		}

		/**
		 * Uses the New Java file template from the code template page to generate a
		 * compilation unit with the given type content.
		 * 
		 * @param cu
		 *            The new created compilation unit
		 * @param typeContent
		 *            The content of the type, including signature and type body.
		 * @param lineDelimiter
		 *            The line delimiter to be used.
		 * @return String Returns the result of evaluating the new file template
		 *         with the given type content.
		 * @throws CoreException
		 *             when fetching the file comment fails or fetching the content
		 *             for the new compilation unit fails
		 * @since 2.1
		 */
		protected String constructCUContent(ICompilationUnit cu,
				String typeContent, String lineDelimiter, Class clazz) throws CoreException {
			String fileComment = getFileComment(cu, lineDelimiter);
			String typeComment = getTypeComment(cu, lineDelimiter, clazz);
			IPackageFragment pack = (IPackageFragment) cu.getParent();
			String content = CodeGeneration.getCompilationUnitContent(cu,
					fileComment, typeComment, typeContent, lineDelimiter);
			if (content != null) {
				ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
				parser.setProject(cu.getJavaProject());
				parser.setSource(content.toCharArray());
				CompilationUnit unit = (CompilationUnit) parser.createAST(null);
				if ((pack.isDefaultPackage() || unit.getPackage() != null)
						&& !unit.types().isEmpty()) {
					return content;
				}
			}
			StringBuffer buf = new StringBuffer();
			if (!pack.isDefaultPackage()) {
				buf.append("package ").append(pack.getElementName()).append(';'); //$NON-NLS-1$
			}
			buf.append(lineDelimiter).append(lineDelimiter);
			if (typeComment != null) {
				buf.append(typeComment).append(lineDelimiter);
			}
			buf.append(typeContent);
			return buf.toString();
		}

		// ---- construct CU body----------------

		private void writeSuperClass(StringBuffer buf, ImportsManager imports, Class clazz,
				String superClass, IType fCurrType, IPackageFragment pack) {
			//String superclass = getSuperClass();
			buf.append(" extends "); //$NON-NLS-1$

			ITypeBinding binding = null;
			if (fCurrType != null) {
				binding = TypeContextChecker.resolveSuperClass(superClass,
						fCurrType, getSuperClassStubTypeContext(clazz, fCurrType, pack));
			}
			if (binding != null) {
				buf.append(imports.addImport(binding));
			} else {
				buf.append(imports.addImport(superClass));
			}
		}

		private void writeSuperInterfaces(StringBuffer buf, ImportsManager imports, Class clazz, 
				List<String> interfaces, IType fCurrType, IPackageFragment pack) {
			//List<String> interfaces = getSuperInterfaces();
			int last = interfaces.size() - 1;
			if (last >= 0) {
				buf.append(" implements ");
				String[] intfs = interfaces.toArray(new String[interfaces.size()]);
				ITypeBinding[] bindings;
				if (fCurrType != null) {
					bindings = TypeContextChecker.resolveSuperInterfaces(intfs,
							fCurrType, getSuperInterfacesStubTypeContext(clazz, fCurrType, pack));
				} else {
					bindings = new ITypeBinding[intfs.length];
				}
				for (int i = 0; i <= last; i++) {
					ITypeBinding binding = bindings[i];
					if (binding != null) {
						buf.append(imports.addImport(binding));
					} else {
						buf.append(imports.addImport(intfs[i]));
					}
					if (i < last) {
						buf.append(',');
					}
				}
			}
		}

		private String constructSimpleTypeStub(Class clazz) {
			StringBuffer buf = new StringBuffer("public class "); //$NON-NLS-1$
			buf.append(clazz.getSimpleName());
			buf.append("{ }"); //$NON-NLS-1$
			return buf.toString();
		}

		/*
		 * Called from createType to construct the source for this type
		 */
		private String constructTypeStub(ICompilationUnit parentCU,
				ImportsManager imports, String lineDelimiter, Class clazz, IType fCurType, IPackageFragment pack) throws CoreException {
			StringBuffer buf = new StringBuffer();

			int modifiers = 1;
			buf.append(Flags.toString(modifiers));
			if (modifiers != 0) {
				buf.append(' ');
			}
			String type = ""; //$NON-NLS-1$
			String templateID = ""; //$NON-NLS-1$
			type = "class "; //$NON-NLS-1$
			templateID = CodeGeneration.CLASS_BODY_TEMPLATE_ID;
			buf.append(type);
			buf.append(clazz.getSimpleName());
			
			if(clazz.getSuperClass() != null){
				writeSuperClass(buf, imports, clazz, clazz.getSuperClass().getFullName(), fCurType, pack);
			}
			
			List<String> interfaceList = new ArrayList<String>();
			for(Interface interf: clazz.getImplementedInterfaces()){
				interfaceList.add(interf.getFullName());
			}
			
			writeSuperInterfaces(buf, imports, clazz, interfaceList, fCurType, pack);

			System.out.println();
			
			buf.append(" {").append(lineDelimiter); //$NON-NLS-1$
			String typeBody = CodeGeneration.getTypeBody(templateID, parentCU,
					clazz.getSimpleName(), lineDelimiter);
			if (typeBody != null) {
				buf.append(typeBody);
			} else {
				buf.append(lineDelimiter);
			}
			buf.append('}').append(lineDelimiter);
			return buf.toString();
		}

		/**
		 * Hook method that gets called from <code>createType</code> to support
		 * adding of unanticipated methods, fields, and inner types to the created
		 * type.
		 * <p>
		 * Implementers can use any methods defined on <code>IType</code> to
		 * manipulate the new type.
		 * </p>
		 * <p>
		 * The source code of the new type will be formatted using the platform's
		 * formatter. Needed imports are added by the wizard at the end of the type
		 * creation process using the given import manager.
		 * </p>
		 * 
		 * @param newType
		 *            the new type created via <code>createType</code>
		 * @param imports
		 *            an import manager which can be used to add new imports
		 * @param monitor
		 *            a progress monitor to report progress. Must not be
		 *            <code>null</code>
		 * @throws CoreException
		 *             thrown when creation of the type members failed
		 * 
		 * @see #createType(IProgressMonitor)
		 */
		protected void createTypeMembers(IType type,
				final ImportsManager imports, IProgressMonitor monitor, Class clazz)
				throws CoreException {
			
			//createInheritedMethods(type, false, true, imports, new SubProgressMonitor(monitor, 1));

			for(Method method: clazz.getMethods()){
				
				String returnType = method.getReturnType().getFullName();
				String methodName = method.getMethodName();
				
				String[] params = new String[method.getParameters().size()];
				for(int i=0; i<params.length; i++){
					params[i] = method.getParameters().get(i).getVariableType().toString();
				}
				
				
				StringBuffer buf= new StringBuffer();
				final String lineDelim= "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
				String comment= CodeGeneration.getMethodComment(type.getCompilationUnit(), 
						type.getTypeQualifiedName('.'), methodName, params, new String[0], Signature.createTypeSignature(returnType, true), null, lineDelim); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (comment != null) {
					buf.append(comment);
					buf.append(lineDelim);
				}
				buf.append("public " + returnType + " " + methodName + "("); //$NON-NLS-1$
				for(int i=0; i<params.length; i++){
					buf.append(params[i]); //$NON-NLS-1$
					
					if(params[i].contains(".")){
						imports.addImport(params[i]);						
					}
					
					buf.append(" ");
					
					buf.append("param"+i+", ");
				}
				
				if(params.length != 0){
					
					String tempString = buf.toString();
					tempString = tempString.substring(0, tempString.length()-2);
					buf = new StringBuffer(tempString);
					
				}
				buf.append("){");
				buf.append(lineDelim);
				final String content= CodeGeneration.getMethodBodyContent(type.getCompilationUnit(), type.getTypeQualifiedName('.'), "main", false, "", lineDelim); //$NON-NLS-1$ //$NON-NLS-2$
				if (content != null && content.length() != 0)
					buf.append(content);
				buf.append(lineDelim);
				buf.append("}"); //$NON-NLS-1$
				type.createMethod(buf.toString(), null, false, null);
			}
			

			/*IDialogSettings dialogSettings= getDialogSettings();
			if (dialogSettings != null) {
				IDialogSettings section= dialogSettings.getSection(PAGE_NAME);
				if (section == null) {
					section= dialogSettings.addNewSection(PAGE_NAME);
				}
				section.put(SETTINGS_CREATEMAIN, isCreateMain());
				section.put(SETTINGS_CREATECONSTR, isCreateConstructors());
				section.put(SETTINGS_CREATEUNIMPLEMENTED, isCreateInherited());
			}*/

			if (monitor != null) {
				monitor.done();
			}
		}

		/**
		 * @param parentCU
		 *            the current compilation unit
		 * @return returns the file template or <code>null</code>
		 * @deprecated Instead of file templates, the new type code template
		 *             specifies the stub for a compilation unit.
		 */
		protected String getFileComment(ICompilationUnit parentCU) {
			return null;
		}

		/**
		 * Hook method that gets called from <code>createType</code> to retrieve a
		 * file comment. This default implementation returns the content of the
		 * 'file comment' template or <code>null</code> if no comment should be
		 * created.
		 * 
		 * @param parentCU
		 *            the parent compilation unit
		 * @param lineDelimiter
		 *            the line delimiter to use
		 * @return the file comment or <code>null</code> if a file comment is not
		 *         desired
		 * @throws CoreException
		 *             when fetching the file comment fails
		 * 
		 * @since 3.1
		 */
		protected String getFileComment(ICompilationUnit parentCU,
				String lineDelimiter) throws CoreException {
			
			return CodeGeneration.getFileComment(parentCU, lineDelimiter);

		}

		private boolean isValidComment(String template) {
			IScanner scanner = ToolFactory.createScanner(true, false, false, false);
			scanner.setSource(template.toCharArray());
			try {
				int next = scanner.getNextToken();
				while (TokenScanner.isComment(next)) {
					next = scanner.getNextToken();
				}
				return next == ITerminalSymbols.TokenNameEOF;
			} catch (InvalidInputException e) {
			}
			return false;
		}

		/**
		 * Hook method that gets called from <code>createType</code> to retrieve a
		 * type comment. This default implementation returns the content of the
		 * 'type comment' template.
		 * 
		 * @param parentCU
		 *            the parent compilation unit
		 * @param lineDelimiter
		 *            the line delimiter to use
		 * @return the type comment or <code>null</code> if a type comment is not
		 *         desired
		 * 
		 * @since 3.0
		 */
		protected String getTypeComment(ICompilationUnit parentCU,
				String lineDelimiter, Class clazz) {
			try {
				StringBuffer typeName = new StringBuffer();
				/*if (isEnclosingTypeSelected()) {
					typeName.append(
							getEnclosingType().getTypeQualifiedName('.'))
							.append('.');
				}*/
				typeName.append(getTypeNameWithoutParameters(clazz.getSimpleName()));
				String[] typeParamNames = new String[0];
				String comment = CodeGeneration.getTypeComment(parentCU,
						typeName.toString(), typeParamNames, lineDelimiter);
				if (comment != null && isValidComment(comment)) {
					return comment;
				}
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
			return null;
		}

		/**
		 * @param parentCU
		 *            the current compilation unit
		 * @return returns the template or <code>null</code>
		 * @deprecated Use getTypeComment(ICompilationUnit, String)
		 */
		protected String getTypeComment(ICompilationUnit parentCU, Class clazz) {
			if (StubUtility.doAddComments(parentCU.getJavaProject()))
				return getTypeComment(parentCU,
						StubUtility.getLineDelimiterUsed(parentCU), clazz);
			return null;
		}

		/**
		 * @param name
		 *            the name of the template
		 * @param parentCU
		 *            the current compilation unit
		 * @return returns the template or <code>null</code>
		 * @deprecated Use getTemplate(String,ICompilationUnit,int)
		 */
		protected String getTemplate(String name, ICompilationUnit parentCU) {
			return getTemplate(name, parentCU, 0);
		}

		/**
		 * Returns the string resulting from evaluation the given template in the
		 * context of the given compilation unit. This accesses the normal template
		 * page, not the code templates. To use code templates use
		 * <code>constructCUContent</code> to construct a compilation unit stub or
		 * getTypeComment for the comment of the type.
		 * 
		 * @param name
		 *            the template to be evaluated
		 * @param parentCU
		 *            the templates evaluation context
		 * @param pos
		 *            a source offset into the parent compilation unit. The template
		 *            is evaluated at the given source offset
		 * @return return the template with the given name or <code>null</code> if
		 *         the template could not be found.
		 */
		protected String getTemplate(String name, ICompilationUnit parentCU, int pos) {
			try {
				Template template = JavaPlugin.getDefault().getTemplateStore()
						.findTemplate(name);
				if (template != null) {
					return JavaContext.evaluateTemplate(template, parentCU, pos);
				}
			} catch (CoreException e) {
				JavaPlugin.log(e);
			} catch (BadLocationException e) {
				JavaPlugin.log(e);
			} catch (TemplateException e) {
				JavaPlugin.log(e);
			}
			return null;
		}

		/**
		 * Creates the bodies of all unimplemented methods and constructors and adds
		 * them to the type. Method is typically called by implementers of
		 * <code>NewTypeWizardPage</code> to add needed method and constructors.
		 * 
		 * @param type
		 *            the type for which the new methods and constructor are to be
		 *            created
		 * @param doConstructors
		 *            if <code>true</code> unimplemented constructors are created
		 * @param doUnimplementedMethods
		 *            if <code>true</code> unimplemented methods are created
		 * @param imports
		 *            an import manager to add all needed import statements
		 * @param monitor
		 *            a progress monitor to report progress
		 * @return the created methods.
		 * @throws CoreException
		 *             thrown when the creation fails.
		 */
		protected IMethod[] createInheritedMethods(IType type, boolean doConstructors, boolean doUnimplementedMethods,
				ImportsManager imports, IProgressMonitor monitor) throws CoreException {
			final ICompilationUnit cu = type.getCompilationUnit();
			JavaModelUtil.reconcile(cu);
			if(type.exists()){
				IMethod[] typeMethods = type.getMethods();
				Set<String> handleIds = new HashSet<String>(typeMethods.length);
				for (int index = 0; index < typeMethods.length; index++)
					handleIds.add(typeMethods[index].getHandleIdentifier());
				ArrayList<IMethod> newMethods = new ArrayList<IMethod>();
				CodeGenerationSettings settings = JavaPreferencesSettings
						.getCodeGenerationSettings(type.getJavaProject());
				settings.createComments = true/*isAddComments()*/;
				ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
				parser.setResolveBindings(true);
				parser.setSource(cu);
				CompilationUnit unit = (CompilationUnit) parser
						.createAST(new SubProgressMonitor(monitor, 1));
				final ITypeBinding binding = ASTNodes.getTypeBinding(unit, type);
				if (binding != null) {
					if (doUnimplementedMethods) {
						AddUnimplementedMethodsOperation operation = new AddUnimplementedMethodsOperation(
								unit, binding, null, -1, false, true, false);
						operation.setCreateComments(true/*isAddComments()*/);
						operation.run(monitor);
						createImports(imports, operation.getCreatedImports());
					}
					if (doConstructors) {
						AddUnimplementedConstructorsOperation operation = new AddUnimplementedConstructorsOperation(
								unit, binding, null, -1, false, true, false);
						operation.setOmitSuper(true);
						operation.setCreateComments(true/*isAddComments()*/);
						operation.run(monitor);
						createImports(imports, operation.getCreatedImports());
					}
				}
				JavaModelUtil.reconcile(cu);
				typeMethods = type.getMethods();
				for (int index = 0; index < typeMethods.length; index++)
					if (!handleIds.contains(typeMethods[index].getHandleIdentifier()))
						newMethods.add(typeMethods[index]);
				IMethod[] methods = new IMethod[newMethods.size()];
				newMethods.toArray(methods);
				return methods;
				
			}
			
			return null;
		}

		private void createImports(ImportsManager imports, String[] createdImports) {
			for (int index = 0; index < createdImports.length; index++)
				imports.addImport(createdImports[index]);
		}
		
		

		// ---- creation ----------------
		
		/**
		 * Returns the runnable that creates the type using the current settings.
		 * The returned runnable must be executed in the UI thread.
		 * 
		 * @return the runnable to create the new type
		 */
		public IRunnableWithProgress getRunnable(final Class clazz) {
			return new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						if (monitor == null) {
							monitor = new NullProgressMonitor();
						}
						createType(monitor, clazz);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
		}
		
		//The following is code clones
		private String getTypeNameWithoutParameters(Class clazz) {
			return getTypeNameWithoutParameters(clazz.getSimpleName());
		}

		private static String getTypeNameWithoutParameters(
				String typeNameWithParameters) {
			int angleBracketOffset = typeNameWithParameters.indexOf('<');
			if (angleBracketOffset == -1) {
				return typeNameWithParameters;
			} else {
				return typeNameWithParameters.substring(0, angleBracketOffset);
			}
		}
		
		protected String getCompilationUnitName(String typeName) {
			return typeName + JavaModelUtil.DEFAULT_CU_SUFFIX;
		}
		
		private StubTypeContext getSuperClassStubTypeContext(Class clazz, IType fCurrType, IPackageFragment pack) {
			String typeName;
			if (fCurrType != null) {
				typeName = clazz.getSimpleName();
			} else {
				typeName = JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
			}
			StubTypeContext fSuperClassStubTypeContext = TypeContextChecker
					.createSuperClassStubTypeContext(typeName,
							null/*getEnclosingType()*/, pack);
			return fSuperClassStubTypeContext;
		}
		
		private StubTypeContext getSuperInterfacesStubTypeContext(Class clazz, IType fCurrType, IPackageFragment pack) {
			String typeName;
			if (fCurrType != null) {
				typeName = clazz.getSimpleName();
			} else {
				typeName = JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
			}
			StubTypeContext fSuperInterfaceStubTypeContext = TypeContextChecker
					.createSuperInterfaceStubTypeContext(typeName,
							null/*getEnclosingType()*/, pack);
			return fSuperInterfaceStubTypeContext;
		}
}
