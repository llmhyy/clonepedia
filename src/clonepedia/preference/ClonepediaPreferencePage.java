package clonepedia.preference;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.viewer.ClonePatternGroupCategoryList;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.model.viewer.comparator.DefaultValueAscComparator;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;
import clonepedia.perspective.CloneSummaryPerspective;
import clonepedia.summary.SummaryUtil;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;
import clonepedia.views.multiorient.PatternOrientedView;
import clonepedia.views.multiorient.PlainCloneSetView;
import clonepedia.views.multiorient.TopicOrientedView;

public class ClonepediaPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Combo projectCombo;
	private Text cloneFileText;
	private Text ontologyFileText;
	private Text intraSetFileText;
	private Text interSetFileText;
	
	private Combo levelCombo;
	private Combo skipPatternCombo;
	
	private String defaultTargetProject;
	private String defaultCloneFilePath;
	private String defaultOntologyFilePath;
	private String defaultIntraSetFilePath;
	private String defaultInterSetFilePath;
	
	private String defaultDiffLevel;
	private String defaultSkipPattern;
	
	public static final String TARGET_PORJECT = "targetProjectName";
	public static final String CLONE_PATH = "cloneFilePath";
	public static final String ONTOLOGY_PATH = "ontologyFilePath";
	public static final String INTRA_SET_PATH = "intraSetFilePath";
	public static final String INTER_SET_PATH = "interSetFilePath";
	
	public static final String DIFF_LEVEL = "diffLevel";
	public static final String SKIP_PATTERN = "skipPattern";
	
	public ClonepediaPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		//Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		
		this.defaultTargetProject = Activator.getDefault().getPreferenceStore().getString(TARGET_PORJECT);
		this.defaultCloneFilePath = Activator.getDefault().getPreferenceStore().getString(CLONE_PATH);
		this.defaultOntologyFilePath = Activator.getDefault().getPreferenceStore().getString(ONTOLOGY_PATH);
		this.defaultIntraSetFilePath = Activator.getDefault().getPreferenceStore().getString(INTRA_SET_PATH);
		this.defaultInterSetFilePath = Activator.getDefault().getPreferenceStore().getString(INTER_SET_PATH);
		this.defaultDiffLevel = Activator.getDefault().getPreferenceStore().getString(DIFF_LEVEL);
		this.defaultSkipPattern = Activator.getDefault().getPreferenceStore().getString(SKIP_PATTERN);
		//this.defaultTargetProject = preferences.get(TARGET_PORJECT, "");
		//this.defaultCloneFilePath = preferences.get(CLONE_PATH, "");
		//Activator.setCloneSets((CloneSets) MinerUtil.deserialize("sets"));

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
		projectCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String projectName = projectCombo.getText();
				
				String ontologyFileName = getPersistentFileName(projectName, "ontological_model");
				String intraSetFileName = getPersistentFileName(projectName, "intra_pattern_sets");
				String interSetFileName = getPersistentFileName(projectName, "inter_pattern_sets");
				
				ontologyFileText.setText(ontologyFileName);
				intraSetFileText.setText(intraSetFileName);
				interSetFileText.setText(interSetFileName);
			}
		});
		
		createCloneFileText(composite);
		createOntologyFileText(composite);
		createIntraCloneSetPatternFileText(composite);
		createInterCloneSetPatternFileText(composite);
		
		createPatternGroup(composite);
		
		createDiffGroup(composite);
		
		return composite;
	}
	
	private String getPersistentFileName(String projectName, String fileName){
		File ontologyFile = new File("configurations" + File.separator
				+ projectName + File.separator + fileName);
		return ontologyFile.getAbsolutePath();
	}
	
	class FileSelectionAdapter extends SelectionAdapter{
		
		private Text text;
		
		public FileSelectionAdapter(Text text){
			this.text = text;
		}
		
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(), SWT.NULL);
			
			  String path = dialog.open();
			  if (path != null) {

				  File file = new File(path);
				  if (file.isFile()){
					  text.setText(file.toString());
				  }
			  }	  
		}
	}
	
	private void createCloneFileText(Composite composite){
		Label cloneFileLabel = new Label(composite, SWT.NONE);
		cloneFileLabel.setText("clone file:");
		cloneFileText = new Text(composite, SWT.BORDER);
		cloneFileText.setText(this.defaultCloneFilePath);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false);
		cloneFileText.setLayoutData(textData);
		Button cloneFileButton = new Button(composite, SWT.NONE);
		cloneFileButton.setText("Browse");
		cloneFileButton.addSelectionListener(new FileSelectionAdapter(cloneFileText));
	}
	
	private void createOntologyFileText(Composite composite){
		Label ontologyFileLabel = new Label(composite, SWT.NONE);
		ontologyFileLabel.setText("ontology file:");
		ontologyFileText = new Text(composite, SWT.BORDER);
		ontologyFileText.setText(this.defaultOntologyFilePath);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false);
		ontologyFileText.setLayoutData(textData);
		Button ontologyFileButton = new Button(composite, SWT.NONE);
		ontologyFileButton.setText("Browse");
		ontologyFileButton.addSelectionListener(new FileSelectionAdapter(ontologyFileText));
	}
	
	private void createIntraCloneSetPatternFileText(Composite composite){
		Label intraSetFileLabel = new Label(composite, SWT.NONE);
		intraSetFileLabel.setText("intra set file:");
		intraSetFileText = new Text(composite, SWT.BORDER);
		intraSetFileText.setText(this.defaultIntraSetFilePath);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false);
		intraSetFileText.setLayoutData(textData);
		Button intraSetFileButton = new Button(composite, SWT.NONE);
		intraSetFileButton.setText("Browse");
		intraSetFileButton.addSelectionListener(new FileSelectionAdapter(intraSetFileText));
	}
	
	private void createInterCloneSetPatternFileText(Composite composite){
		Label interSetFileLabel = new Label(composite, SWT.NONE);
		interSetFileLabel.setText("inter set file:");
		interSetFileText = new Text(composite, SWT.BORDER);
		interSetFileText.setText(this.defaultInterSetFilePath);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false);
		interSetFileText.setLayoutData(textData);
		Button interSetFileButton = new Button(composite, SWT.NONE);
		interSetFileButton.setText("Browse");
		interSetFileButton.addSelectionListener(new FileSelectionAdapter(interSetFileText));
	}
	
	private void createPatternGroup(Composite parent){
		Group patternGroup = new Group(parent, SWT.NONE);
		patternGroup.setText("parameters for pattern generation");
		GridData patternGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		patternGroupData.horizontalSpan = 3;
		patternGroup.setLayoutData(patternGroupData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		patternGroup.setLayout(layout);
		
		Label skipLabel = new Label(patternGroup, SWT.NONE);
		skipLabel.setText("skip pattern or not");
		
		skipPatternCombo = new Combo(patternGroup, SWT.BORDER);
		skipPatternCombo.setItems(new String[]{"Yes", "No"});
		skipPatternCombo.setText(this.defaultSkipPattern);
		GridData comboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		comboData.horizontalSpan = 2;
		skipPatternCombo.setLayoutData(comboData);
	}
	
	private void createDiffGroup(Composite parent){
		Group diffGroup = new Group(parent, SWT.NONE);
		diffGroup.setText("parameters for clone diff");
		GridData diffGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		diffGroupData.horizontalSpan = 3;
		diffGroup.setLayoutData(diffGroupData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		diffGroup.setLayout(layout);
		
		Label levelLabel = new Label(diffGroup, SWT.NONE);
		levelLabel.setText("level");
		
		levelCombo = new Combo(diffGroup, SWT.BORDER);
		levelCombo.setItems(new String[]{"ASTNode_Based", "Statement_Based"});
		levelCombo.setText(this.defaultDiffLevel);
		GridData comboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		comboData.horizontalSpan = 2;
		levelCombo.setLayoutData(comboData);
	}
	
	public boolean performOk(){
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		preferences.put(TARGET_PORJECT, this.projectCombo.getText());
		preferences.put(CLONE_PATH, this.cloneFileText.getText());
		preferences.put(ONTOLOGY_PATH, this.ontologyFileText.getText());
		preferences.put(INTRA_SET_PATH, this.intraSetFileText.getText());
		preferences.put(INTER_SET_PATH, this.interSetFileText.getText());
		preferences.put(DIFF_LEVEL, this.levelCombo.getText());
		preferences.put(SKIP_PATTERN, this.skipPatternCombo.getText());
		
		Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(CLONE_PATH, this.cloneFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(ONTOLOGY_PATH, this.ontologyFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(INTRA_SET_PATH, this.intraSetFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(INTER_SET_PATH, this.interSetFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(DIFF_LEVEL, this.levelCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(SKIP_PATTERN, this.skipPatternCombo.getText());
		
		if(!Settings.projectName.equals(this.projectCombo.getText()) ){
			UIRefresh();			
		}
		
		confirmChanges();
		
		return true;
		
	}
	
	@SuppressWarnings("unchecked")
	private void UIRefresh(){
		String targetDir = "configurations" + File.separator + this.projectCombo.getText();
		File dir = new File(targetDir);
		if(dir.exists()){
			
			Settings.projectName = this.projectCombo.getText();
			Activator.setCloneSets((CloneSets) MinerUtil.deserialize("sets", false));
			
			CloneSetWrapperList cloneSets = SummaryUtil.wrapCloneSets(Activator.getCloneSets().getCloneList());
			
			PlainCloneSetView cloneSetView = (PlainCloneSetView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PLAIN_CLONESET_VIEW);
			//cloneSetView.restoreInput(Activator.getCloneSets());
			
			
			PatternOrientedView patternView = (PatternOrientedView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.PATTERN_ORIENTED_VIEW);
			TopicOrientedView topicView = (TopicOrientedView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView(CloneSummaryPerspective.TOPIC_ORIENTED_VIEW);
			
			try {
				if(cloneSetView != null){
					cloneSetView.setCloneSets(cloneSets);
					cloneSetView.restoreInput(cloneSets);
				}
				
				if(patternView != null){
					ClonePatternGroupCategoryList clonePatternCategories = (ClonePatternGroupCategoryList)SummaryUtil.generateClonePatternSimplifiedCategories(Activator.getCloneSets().getCloneList());
					for(PatternGroupCategory category: clonePatternCategories){
						Collections.sort(category.getPatternList(), new DefaultValueDescComparator());
					}
					
					patternView.setCategories(clonePatternCategories);
					patternView.restoreInput(clonePatternCategories);
					
				}

				if(topicView != null){
					TopicWrapperList topics = SummaryUtil.generateTopicOrientedSimpleTree(Activator.getCloneSets().getCloneList());
					Collections.sort(topics, new DefaultValueAscComparator());
					
					topicView.restoreInput(topics);
					topicView.setTopics(topics);
					
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void confirmChanges(){
		Settings.projectName = this.projectCombo.getText();
		Settings.inputCloneFile = this.cloneFileText.getText();
		Settings.diffComparisonMode = this.levelCombo.getText();
		Settings.skipPattern = this.skipPatternCombo.getText();
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
