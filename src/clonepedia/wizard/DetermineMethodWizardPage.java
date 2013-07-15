package clonepedia.wizard;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.VarType;
import clonepedia.model.syntactic.Path;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class DetermineMethodWizardPage extends DetermineElementWizardPage {
	
	private PathPatternGroupWrapper wrapper;

	public DetermineMethodWizardPage(PathPatternGroupWrapper wrapper, String pageName) {
		super(true, pageName);
		
		this.wrapper = wrapper;
		
		setTitle("Determine the method");
	}
	
	private void init(PathPatternGroupWrapper selection) {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject project = root.getProject(Settings.projectName);
		IJavaProject jProject = JavaCore.create(project);
		
		if (jProject.exists()) {
			IPackageFragmentRoot initRoot = null;
			IPackageFragmentRoot[] roots;
			try {
				roots = jProject.getPackageFragmentRoots();
				for (int i= 0; i < roots.length; i++) {
					if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
						initRoot = roots[i];
						setPackageFragmentRoot(initRoot, true);

						break;
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		initPackage();
		initMethodReturnType();
		initMethodName();
		initMethodParameters();
		
		doStatusUpdate();
	}
	
	private void initMethodReturnType() {
		String abstractReturnType = null;
		
		for(Path path: wrapper.getPathPattern()){
			OntologicalElement element = path.get(1);
			if(element instanceof Method){
				Method method = (Method)element;
				VarType returnType = method.getReturnType();
				
				String returnTypeString;
				
				if(null == returnType){
					abstractReturnType = "";
					break;
				}
				else{
					returnTypeString = returnType.getFullName();
				}
				
				if(null == abstractReturnType || abstractReturnType.equals(returnType.getFullName())){
					abstractReturnType = returnType.getFullName();
				}
				else{
					String[] abstractArray = MinerUtil.splitCamelString(abstractReturnType);
					String[] specialArray = MinerUtil.splitCamelString(returnTypeString);
					
					Object[] commonObjects = MinerUtil.generateAbstractCommonNodeList(abstractArray, 
							specialArray, new DefaultComparator());
					
					String abString = "";
					for(int i=0; i<commonObjects.length; i++){
						abString += commonObjects[i].toString();
					}
					
					abstractReturnType = abString;
				}
			}
		}
		
		setMethodReturnType(abstractReturnType, true);
		
	}

	private void initMethodParameters() {
		// TODO Auto-generated method stub
		
	}

	private void initMethodName() {
		// TODO Auto-generated method stub
		
	}

	private void initPackage() {
		
		String abstractPackage = null;
		
		for(Path path: wrapper.getPathPattern()){
			OntologicalElement element = path.get(2);
			if(element instanceof clonepedia.model.ontology.Class){
				Class clazz = (Class)element;
				String classFullName = clazz.getFullName();
				
				String packageName = classFullName.substring(0, classFullName.lastIndexOf("."));
				
				if(abstractPackage == null || abstractPackage.equals(packageName)){
					abstractPackage = packageName;
				}
				else{
					String[] abstractArray = abstractPackage.split("\\.");
					String[] specialArray = packageName.split("\\.");
					
					Object[] commonObjects = MinerUtil.generateAbstractCommonNodeList(abstractArray, 
							specialArray, new DefaultComparator());
					
					String abstractString = "";
					for(int i=0; i<commonObjects.length; i++){
						abstractString += commonObjects[i].toString() + ".";
					}
					
					abstractPackage = abstractString.substring(0, abstractString.length()-1);
				}
				
			}
		}
		
		fPackageDialogField.setText(abstractPackage);
	}
	
	protected void handleFieldChanged(String fieldName) {
		if (fieldName == CONTAINER) {
			fPackageStatus = packageChanged();
			fMethodReturnTypeStatus = methodReturnTypeChanged();
			fMethodNameStatus = methodNameChanged();
			fMethodParametersStatus = methodParametersChanged();
		}
		else if(fieldName == METHODS){
			fMethodNameStatus = methodNameChanged();			
		}

		doStatusUpdate();
	}

	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fMethodReturnTypeStatus,
			fMethodNameStatus,
			fMethodParametersStatus
		};

		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		
		createContainerControls(composite, nColumns);
		
		createPackageControls(composite, nColumns);
		
		createMethodReturnTypeControls(composite, nColumns);
		createMethodNameControls(composite, nColumns);
		createMethodParametersControls(composite, nColumns);
		
		setControl(composite);
		
		init(wrapper);
	}

	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			setFocus();
	}

}
