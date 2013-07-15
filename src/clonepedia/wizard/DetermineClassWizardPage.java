package clonepedia.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.syntactic.Path;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;


public class DetermineClassWizardPage extends DetermineElementWizardPage {
	
	//private final static String PAGE_NAME= "DetermineClassWizardPage";
	private PathPatternGroupWrapper wrapper;
	
	public DetermineClassWizardPage(PathPatternGroupWrapper wrapper, String pageName) {
		super(true, pageName);
		
		this.wrapper = wrapper;
		
		setTitle("Determine the class");
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
		initClass();
		initSuperClass();
		initInterfaces();
		
		doStatusUpdate();
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

	private void initClass() {
		Class clazz = (Class)wrapper.getPathPattern().getAbstractPathSequence().get(4);
		fTypeNameDialogField.setText(clazz.getSimpleName());
		
	}

	private void initSuperClass() {
		
		HashMap<String, Integer> classMap = new HashMap<String, Integer>();
		
		for(Path path: wrapper.getPathPattern()){
			OntologicalElement element = path.get(2);
			if(element instanceof clonepedia.model.ontology.Class){
				Class clazz = (Class)element;
				Class superClass = clazz.getSuperClass();
				
				if(superClass != null){
					Integer i = classMap.get(superClass.getFullName());
					if(null != i){
						i++;
						classMap.put(superClass.getFullName(), i);
					}
					else{
						classMap.put(superClass.getFullName(), 1);
					}
					
				}
			}
		}
		
		String bestSuperClassName = "";
		int count = 0;
		for(String name: classMap.keySet()){
			if(classMap.get(name) > count){
				count = classMap.get(name);
				bestSuperClassName = name;
			}
		}
		
		setSuperClass(bestSuperClassName, true);
	}

	private void initInterfaces() {
		HashSet<String> interfaceSet = new HashSet<String>();
		
		for(Path path: wrapper.getPathPattern()){
			OntologicalElement element = path.get(2);
			if(element instanceof clonepedia.model.ontology.Class){
				Class clazz = (Class)element;
				ArrayList<Interface> interfaceList = clazz.getImplementedInterfaces();
				
				for(Interface interf: interfaceList){
					interfaceSet.add(interf.getFullName());
				}
			}
		}
		
		ArrayList<String> interfaces = new ArrayList<String>();
		for(String interfaceName: interfaceSet){
			interfaces.add(interfaceName);
		}
		
		setSuperInterfaces(interfaces, true);
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		doStatusUpdate();
	}

	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
			fTypeNameStatus,
			fModifierStatus,
			fSuperClassStatus,
			fSuperInterfacesStatus
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
		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		setControl(composite);
		
		init(wrapper);
	}

	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			setFocus();
	}
}
