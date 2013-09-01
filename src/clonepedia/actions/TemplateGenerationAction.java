package clonepedia.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.featuretemplate.CandidateTemplateBuilder;
import clonepedia.featuretemplate.TMGBuilder;
import clonepedia.featuretemplate.TMGBuilder2;
import clonepedia.featuretemplate.TemplateBuilder;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.template.CandidateTemplate;
import clonepedia.model.template.TMGList;
import clonepedia.model.template.Template;
import clonepedia.model.template.SubCandidateTemplate;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.model.template.CandidateTemplateList;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class TemplateGenerationAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Job job = new Job("building template"){
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Template generation start", 100);
				
				CloneSets sets = (CloneSets)MinerUtil.deserialize(Settings.ontologyFile, true);
				sets.setPathComparator(new LevenshteinPathComparator());
				
				TMGBuilder2 builder = new TMGBuilder2(sets);
				builder.build(monitor);
				HashSet<TemplateMethodGroup> templateMethodGroupList = builder.getMethodGroupList();
				
				ArrayList<TemplateMethodGroup> list = new ArrayList<TemplateMethodGroup>();
				
				ArrayList<TemplateMethodGroup> sigificantTMGList = new ArrayList<TemplateMethodGroup>();
				for(TemplateMethodGroup tmg: templateMethodGroupList){
					list.add(tmg);
					if(tmg.getMethods().size() > 2){
						sigificantTMGList.add(tmg);
					}
				}
				
				System.out.println("significant TMG: " + sigificantTMGList.size());
				
				CandidateTemplateBuilder featureBuilder = new CandidateTemplateBuilder(list);
				featureBuilder.generateTemplateFeatures(monitor);
				CandidateTemplateList featureGroups = featureBuilder.getCandidateTemplateList();
				
				try {
					MinerUtil.serialize(featureGroups, "featureGroups", false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				System.out.println("cadidate template list: " + featureGroups.size());
				
				//CandidateTemplateList featureGroups = (CandidateTemplateList)MinerUtil.deserialize("featureGroups", false);
				
				CandidateTemplateList significantGroups = new CandidateTemplateList();
				for(CandidateTemplate feature: featureGroups){
					int count = 0;
					for(SubCandidateTemplate tfg: feature){
						count += tfg.getTemplateMethodGroupList().size();
					}
					if(count > 3){
						
						if(feature.toString().contains("Main")){
							System.out.println();
						}
						
						TemplateBuilder templateBuilder = new TemplateBuilder(feature);
						Template template = templateBuilder.buildTemplate();
						
						feature.setTemplate(template);
						significantGroups.add(feature);
					}
				}
				
				//System.out.println("cadidate template list: " + featureGroups.size());
				
				monitor.worked(30);
				
				//TMGList tmgList = new TMGList(sets, list);
				//TFGList tfgList = new TFGList(sets, featureGroups);
				
				try {
					//MinerUtil.serialize(tmgList, "tmgList", false);
					MinerUtil.serialize(significantGroups, "totalTFGs", false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//sets.setTemplateMethodGroup(list);
				//sets.setFeatureGroupList(featureGroups);
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
