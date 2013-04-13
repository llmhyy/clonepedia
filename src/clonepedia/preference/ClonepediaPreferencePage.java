package clonepedia.preference;


import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

import clonepedia.Activator;
import clonepedia.util.Settings;

public class ClonepediaPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Combo projectCombo;
	private Text cloneFileText;
	
	private String defaultTargetProject;
	private String defaultCloneFilePath;
	
	public static final String TARGET_PORJECT = "targetProjectName";
	public static final String CLONE_PATH = "cloneFilePath";
	
	public ClonepediaPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		//Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		
		this.defaultTargetProject = Activator.getDefault().getPreferenceStore().getString(TARGET_PORJECT);
		this.defaultCloneFilePath = Activator.getDefault().getPreferenceStore().getString(CLONE_PATH);
		
		//this.defaultTargetProject = preferences.get(TARGET_PORJECT, "");
		//this.defaultCloneFilePath = preferences.get(CLONE_PATH, "");

	}

	private String[] getProjectsInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		String[] projectStrings = new String[projects.length];
		for(int i=0; i<projects.length; i++){
			projectStrings[i] = projects[i].getName();
		}
		
		return projectStrings;
	}
	
	/**
	 * The default layout of parent is GridLayout
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		composite.setLayout(layout);
		
		Label projectLabel = new Label(composite, SWT.NONE);
		projectLabel.setText("target project");
		
		projectCombo = new Combo(composite, SWT.BORDER);
		projectCombo.setItems(getProjectsInWorkspace());
		projectCombo.setText(this.defaultTargetProject);
		GridData comboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		comboData.horizontalSpan = 2;
		projectCombo.setLayoutData(comboData);
		
		
		Label cloneFileLabel = new Label(composite, SWT.NONE);
		cloneFileLabel.setText("clone file");
		cloneFileText = new Text(composite, SWT.BORDER);
		cloneFileText.setText(this.defaultCloneFilePath);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false);
		cloneFileText.setLayoutData(textData);
		Button cloneFileButton = new Button(composite, SWT.NONE);
		cloneFileButton.setText("Browser");
		cloneFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getShell(), SWT.NULL);
				
				  String path = dialog.open();
				  if (path != null) {

					  File file = new File(path);
					  if (file.isFile()){
						  cloneFileText.setText(file.toString());
					  }
				  }	  
			}
		});
		
		Group cloneGroup = new Group(composite, SWT.NONE);
		cloneGroup.setText("parameters for pattern generation");
		GridData cloneGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		cloneGroupData.horizontalSpan = 3;
		cloneGroup.setLayoutData(cloneGroupData);
		
		return composite;
	}
	
	public boolean performOk(){
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		preferences.put(TARGET_PORJECT, this.projectCombo.getText());
		preferences.put(CLONE_PATH, this.cloneFileText.getText());
		
		Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(CLONE_PATH, this.cloneFileText.getText());
		
		confirmChanges();
		
		return true;
		
	}
	
	private void confirmChanges(){
		Settings.projectName = this.projectCombo.getText();
		Settings.inputCloneFile = this.cloneFileText.getText();
	}
	
	public ClonepediaPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public ClonepediaPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

}
