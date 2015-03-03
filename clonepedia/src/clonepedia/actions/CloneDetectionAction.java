package clonepedia.actions;

import java.util.ArrayList;
import java.util.Iterator;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SimilarityGroupManager;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.GeneralizeArrayInitializers;
import org.eposoft.jccd.preprocessors.java.GeneralizeClassDeclarationNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodArgumentTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodCallNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodReturnTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableDeclarationTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableNames;

import clonepedia.filepraser.CloneDetectionFileWriter;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.MinerProperties;
import clonepedia.util.Settings;

public class CloneDetectionAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		
		
		Job job = new Job("detecting code clones"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				detectClone();
				//detectMultiProjectClone();
				
				
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
	}
	
	private void detectClone(){
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject proj = root.getProject(Settings.projectName);
			if (proj.isNatureEnabled(MinerProperties.javaNatureName)) {
				IJavaProject javaProject = JavaCore.create(proj);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				
				ArrayList<JCCDFile> fileList = new ArrayList<JCCDFile>();
				
				/*fileList.add(new JCCDFile(("F:\\git_space\\JHotDraw7\\jhotdraw7\\src\\main\\java\\"
						+ "org\\jhotdraw\\gui\\plaf\\palette\\PaletteToolBarBorder.java")));*/
				
				for(IPackageFragment pack: packages){
					if(pack.getKind() == IPackageFragmentRoot.K_SOURCE 
							/*&& pack.getHandleIdentifier().contains("sample")*/){
						for(ICompilationUnit iunit: pack.getCompilationUnits()){
							IResource resource = iunit.getResource();
							
							fileList.add(new JCCDFile(resource.getRawLocation().toFile()));
							
							System.currentTimeMillis();
						}
					}
				}
				
				APipeline detector = new ASTDetector();
				JCCDFile[] files = fileList.toArray(new JCCDFile[0]);
				detector.setSourceFiles(files);
				
				detector.addOperator(new GeneralizeArrayInitializers());
				detector.addOperator(new GeneralizeClassDeclarationNames());
				detector.addOperator(new GeneralizeMethodArgumentTypes());
				detector.addOperator(new GeneralizeMethodReturnTypes());
				detector.addOperator(new GeneralizeVariableDeclarationTypes());
				detector.addOperator(new GeneralizeMethodCallNames());
				detector.addOperator(new GeneralizeVariableDeclarationTypes());
				detector.addOperator(new GeneralizeVariableNames());
				
				SimilarityGroupManager manager = detector.process();
				SimilarityGroup[] simGroups = manager.getSimilarityGroups();
				
				CloneSets sets = convertToCloneSets(simGroups);
				
				filterCloneSetByCriteria(proj, javaProject, sets);
				
				CloneDetectionFileWriter writer = new CloneDetectionFileWriter();
				writer.writeToXML(sets);
				
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param proj
	 * @param javaProject
	 * @param sets
	 */
	private void filterCloneSetByCriteria(IProject proj,
			IJavaProject javaProject, CloneSets sets) {
		//for collecting test data
		SeqMCIDiff mcidiff = new SeqMCIDiff();
//				ArrayList<CloneSet> toRemove = new ArrayList<CloneSet>();
		Iterator<CloneSet> iterator = sets.getCloneList().iterator();
		int count = 0;
		int type2Count = 0;
		int type3Count = 0;
		int filteredCount = 0;
		int size = sets.getCloneList().size();
		while(iterator.hasNext()){
			CloneSet set = iterator.next();
			if(set.size()<3 || set.size()>30){
				iterator.remove();
				filteredCount++;
			}else{
				mcidiff.model.CloneSet diffset = new mcidiff.model.CloneSet(set.getId());
				for(CloneInstance ins : set){
					mcidiff.model.CloneInstance diffins = new mcidiff.model.CloneInstance(diffset, ins.getFileLocation(), ins.getStartLine(), ins.getEndLine());
					diffset.addInstance(diffins);
				}
				ArrayList<SeqMultiset> diffList;
				try {
					diffList = mcidiff.diff(diffset, javaProject);
					if(diffList.size() == 0){
						iterator.remove();
						filteredCount++;
					}
					else if(containEpisolon(diffList)){
						type3Count++;
					}
					else{
						type2Count++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			count++;
			System.out.println("=============== Current: " + count + ", Size: " + size + " ================");
		}
		
		
//				for(CloneSet set : sets.getCloneList()){
//					if(set.size()<3 || set.size()>8){
//						toRemove.add(set);
//					}else{
//						mcidiff.model.CloneSet diffset = new mcidiff.model.CloneSet();
//						diffset.setId(set.getId());
//						for(CloneInstance ins : set){
//							mcidiff.model.CloneInstance diffins = new mcidiff.model.CloneInstance(diffset, ins.getFileLocation(), ins.getStartLine(), ins.getEndLine());
//							diffset.addInstance(diffins);
//						}
//						ArrayList<SeqMultiset> diffList = mcidiff.diff(diffset, javaProject);
//						if(diffList.size() == 0){
//							toRemove.add(set);
//						}
//					}
//				}
//				sets.getCloneList().removeAll(toRemove);
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("Project:" + proj.getName() + ", clone set size:" + sets.getCloneList().size() +
				", type 2: " + type2Count + ", type 3: " + type3Count);
		System.out.println("Total clone set size (include filtered): " + size + ", filtered size: " + filteredCount);
		System.out.println("----------------------------------------------------------------------------------------------");
	}
	
	private boolean containEpisolon(ArrayList<SeqMultiset> diffList) {
		for(SeqMultiset set: diffList){
			for(TokenSeq seq: set.getSequences()){
				if(seq.isEpisolonTokenSeq()){
					return true;
				}
			}
		}
		return false;
	}

	private CloneSets convertToCloneSets(SimilarityGroup[] simGroups){
		CloneSets sets = new CloneSets();
		
		for (int i = 0; i < simGroups.length; i++) {
			final ASourceUnit[] nodes = simGroups[i].getNodes();
			
			CloneSet set = new CloneSet(String.valueOf(simGroups[i].getGroupId()));
			
			
			for (int j = 0; j < nodes.length; j++) {
				
				final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) nodes[j]);
				final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) nodes[j]);

				ANode fileNode = (ANode) nodes[j];
				while (fileNode.getType() != NodeTypes.FILE.getType()) {
					fileNode = fileNode.getParent();
				}
				
				CloneInstance cloneInstance = new CloneInstance(set, fileNode.getText(), 
						minPos.getLine(), maxPos.getLine());
				/*System.out.println(cloneInstance.getFileLocation());
				System.out.println(minPos.getLine());
				System.out.println(maxPos.getLine());*/
				
				if(cloneInstance.getLength() >= 5){
					set.add(cloneInstance);					
				}
				
			}
			
			if(set.size() >= 2){
				sets.add(set);				
			}
		}
		
		return sets;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		
	}

}
