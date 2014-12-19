package clonepedia.preference;


import java.io.File;
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
	
	/*private Text templateMethodCallStrengthText;
	private Text abstractMethodCallStrengthText;
	
	private Text thresholdForTMGFormingText;
	private Text thresholdForTMGLocationText;
	private Text thresholdForTCGFormingText;*/
	
	private Combo levelCombo;
	private Combo skipPatternCombo;
	
	private String defaultTargetProject;
	private String defaultCloneFilePath;
	private String defaultOntologyFilePath;
	private String defaultIntraSetFilePath;
	private String defaultInterSetFilePath;
	
	private String defaultDiffLevel;
	private String defaultSkipPattern;
	
	/*private String defaultTemplateMethodCallStrength;
	private String defaultAbstractMethodCallStrength;
	
	private String defaultThresholdForTMGForming;
	private String defaultThresholdForTMGLocation;
	private String defaultThresholdForTCGForming;*/
	
	
	public static final String TARGET_PORJECT = "targetProjectName";
	public static final String CLONE_PATH = "cloneFilePath";
	public static final String ONTOLOGY_PATH = "ontologyFilePath";
	public static final String INTRA_SET_PATH = "intraSetFilePath";
	public static final String INTER_SET_PATH = "interSetFilePath";
	
	public static final String DIFF_LEVEL = "diffLevel";
	public static final String SKIP_PATTERN = "skipPattern";
	
	/*public static final String TEMPLATE_METHOD_STRENGTH = "templateMethodCallStrength";
	public static final String ABSTRACT_METHOD_STRENGTH = "abstractMethodCallStrength";
	
	public static final String THRESHOLD_FOR_TMG_FORMING = "thresholdForTMGForming";
	public static final String THRESHOLD_FOR_TMG_LOCATION = "thresholdForTMGLocation";
	public static final String THRESHOLD_FOR_TCG_FORMING = "hresholdForTCGForming";*/
	
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
		
		/*this.defaultTemplateMethodCallStrength = Activator.getDefault().getPreferenceStore().getString(TEMPLATE_METHOD_STRENGTH);
		this.defaultAbstractMethodCallStrength = Activator.getDefault().getPreferenceStore().getString(ABSTRACT_METHOD_STRENGTH);
		
		this.defaultThresholdForTMGForming = Activator.getDefault().getPreferenceStore().getString(THRESHOLD_FOR_TMG_FORMING);
		this.defaultThresholdForTMGLocation = Activator.getDefault().getPreferenceStore().getString(THRESHOLD_FOR_TMG_LOCATION);
		this.defaultThresholdForTCGForming = Activator.getDefault().getPreferenceStore().getString(THRESHOLD_FOR_TCG_FORMING);
		*/
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
				
				String cloneFileName = getPersistentFileName(projectName, "clones.xml"); 
				String ontologyFileName = getPersistentFileName(projectName, "ontological_model");
				String intraSetFileName = getPersistentFileName(projectName, "intra_pattern_sets");
				String interSetFileName = getPersistentFileName(projectName, "inter_pattern_sets");
				
				cloneFileText.setText(cloneFileName);
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
		
		//createTemplateGroup(composite);
		
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
	
	/*private void createTemplateGroup(Composite parent){
		Group templateGroup = new Group(parent, SWT.NONE);
		templateGroup.setText("parameters for template generation");
		GridData diffGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		diffGroupData.horizontalSpan = 3;
		templateGroup.setLayoutData(diffGroupData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		templateGroup.setLayout(layout);
		
		createTemplateMethodCallStrengthText(templateGroup);
		createAbstractMethodCallStrengthText(templateGroup);
		createThresholdForTMGFormingText(templateGroup);
		createThresholdForTMGLocationText(templateGroup);
		createThresholdForTCGFormingText(templateGroup);
	}*/
	
	/*private void createTemplateGenerationText(Group group, Text text, String labelString, String defaultString){
		Label label = new Label(group, SWT.NONE);
		label.setText(labelString);
		text = new Text(group, SWT.BORDER);
		text.setText(String.valueOf(defaultString));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		text.setLayoutData(gridData);
	}*/
	
	/*private void createTemplateMethodCallStrengthText(Group group){
		Label templateMethodCallStrengthLabel = new Label(group, SWT.NONE);
		templateMethodCallStrengthLabel.setText("TMG call strength");
		
		templateMethodCallStrengthText = new Text(group, SWT.BORDER);
		templateMethodCallStrengthText.setText(String.valueOf(this.defaultTemplateMethodCallStrength));
		GridData templateMethodData = new GridData(SWT.FILL, SWT.FILL, true, false);
		templateMethodData.horizontalSpan = 2;
		templateMethodCallStrengthText.setLayoutData(templateMethodData);
	}
	
	private void createAbstractMethodCallStrengthText(Group group){
		Label abstractMethodCallStrengthLabel = new Label(group, SWT.NONE);
		abstractMethodCallStrengthLabel.setText("abstract method call strength");
		
		abstractMethodCallStrengthText = new Text(group, SWT.BORDER);
		abstractMethodCallStrengthText.setText(String.valueOf(this.defaultAbstractMethodCallStrength));
		GridData abstractMethodData = new GridData(SWT.FILL, SWT.FILL, true, false);
		abstractMethodData.horizontalSpan = 2;
		abstractMethodCallStrengthText.setLayoutData(abstractMethodData);
	}
	
	private void createThresholdForTMGFormingText(Group group){
		Label label = new Label(group, SWT.NONE);
		label.setText("TMG spliting threshold");
		
		thresholdForTMGFormingText = new Text(group, SWT.BORDER);
		thresholdForTMGFormingText.setText(String.valueOf(this.defaultThresholdForTMGForming));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		thresholdForTMGFormingText.setLayoutData(data);
	}
	
	private void createThresholdForTMGLocationText(Group group){
		Label label = new Label(group, SWT.NONE);
		label.setText("TMG location threshold");
		
		thresholdForTMGLocationText = new Text(group, SWT.BORDER);
		thresholdForTMGLocationText.setText(String.valueOf(this.defaultThresholdForTMGLocation));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		thresholdForTMGLocationText.setLayoutData(data);
	}
	
	private void createThresholdForTCGFormingText(Group group){
		Label label = new Label(group, SWT.NONE);
		label.setText("abstract class threshold");
		
		thresholdForTCGFormingText = new Text(group, SWT.BORDER);
		thresholdForTCGFormingText.setText(String.valueOf(this.defaultThresholdForTCGForming));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		thresholdForTCGFormingText.setLayoutData(data);
	}*/
	
	
	
	public boolean performOk(){
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Clonepedia");
		preferences.put(TARGET_PORJECT, this.projectCombo.getText());
		preferences.put(CLONE_PATH, this.cloneFileText.getText());
		preferences.put(ONTOLOGY_PATH, this.ontologyFileText.getText());
		preferences.put(INTRA_SET_PATH, this.intraSetFileText.getText());
		preferences.put(INTER_SET_PATH, this.interSetFileText.getText());
		preferences.put(DIFF_LEVEL, this.levelCombo.getText());
		preferences.put(SKIP_PATTERN, this.skipPatternCombo.getText());
		/*preferences.put(TEMPLATE_METHOD_STRENGTH, this.templateMethodCallStrengthText.getText());
		preferences.put(ABSTRACT_METHOD_STRENGTH, this.abstractMethodCallStrengthText.getText());
		preferences.put(THRESHOLD_FOR_TMG_FORMING, this.thresholdForTMGFormingText.getText());
		preferences.put(THRESHOLD_FOR_TMG_LOCATION, this.thresholdForTMGLocationText.getText());
		preferences.put(THRESHOLD_FOR_TCG_FORMING, this.thresholdForTCGFormingText.getText());*/
		
		Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(CLONE_PATH, this.cloneFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(ONTOLOGY_PATH, this.ontologyFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(INTRA_SET_PATH, this.intraSetFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(INTER_SET_PATH, this.interSetFileText.getText());
		Activator.getDefault().getPreferenceStore().putValue(DIFF_LEVEL, this.levelCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(SKIP_PATTERN, this.skipPatternCombo.getText());
		/*Activator.getDefault().getPreferenceStore().putValue(TEMPLATE_METHOD_STRENGTH, this.templateMethodCallStrengthText.getText());
		Activator.getDefault().getPreferenceStore().putValue(ABSTRACT_METHOD_STRENGTH, this.abstractMethodCallStrengthText.getText());
		Activator.getDefault().getPreferenceStore().putValue(THRESHOLD_FOR_TMG_FORMING, this.thresholdForTMGFormingText.getText());
		Activator.getDefault().getPreferenceStore().putValue(THRESHOLD_FOR_TMG_LOCATION, this.thresholdForTMGLocationText.getText());
		Activator.getDefault().getPreferenceStore().putValue(THRESHOLD_FOR_TCG_FORMING, this.thresholdForTCGFormingText.getText());
		*/
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
			
			if(Activator.getCloneSets() != null){
				
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
	}
	
	private void confirmChanges(){
		Settings.projectName = this.projectCombo.getText();
		Settings.inputCloneFile = this.cloneFileText.getText();
		Settings.ontologyFile = this.ontologyFileText.getText();
		Settings.diffComparisonMode = this.levelCombo.getText();
		Settings.skipPattern = this.skipPatternCombo.getText();
		
		/*Settings.templateMethodGroupCallingStrength = Integer.valueOf(this.templateMethodCallStrengthText.getText());
		Settings.abstractMethodGroupCallingStrength = Integer.valueOf(this.abstractMethodCallStrengthText.getText());
		
		Settings.thresholdDistanceForTMGFilteringAndSplitting = Double.valueOf(this.thresholdForTMGFormingText.getText());
		Settings.thresholdDistanceForTMGLocationClustering = Double.valueOf(this.thresholdForTMGLocationText.getText());
		Settings.thresholdDistanceForDeclaringClassClustering = Double.valueOf(this.thresholdForTCGFormingText.getText());*/
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
