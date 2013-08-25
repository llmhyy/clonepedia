package clonepedia.actions.steps;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;

import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.featuretemplate.TFGBuilder;
import clonepedia.featuretemplate.TMGBuilder;
import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.StructureExtractor;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Project;
import clonepedia.model.template.TemplateFeatureGroup;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.util.MinerUtil;

public class GenerateOntologyStep implements Step{
	
	private Project project;
	private StructureExtractor extractor;
	
	private CloneSets sets;
	private CloneDetectionFileParser parser = new CloneDetectionFileParser(false, "");
	
	public GenerateOntologyStep(Project project, StructureExtractor extractor){
		this.project = project;
		this.extractor = extractor;
	}
	
	public void run(IProgressMonitor monitor){
		try {
			OntologicalModelDataFetcher modelFetcher = 
					(OntologicalModelDataFetcher)extractor.extractProjectContent(monitor);
			
			modelFetcher = (OntologicalModelDataFetcher) new CloneInformationExtractor(
					parser, project, modelFetcher).extract(monitor);
			
			
			CloneSets sets = new CloneSets();
			for(CloneSet set: modelFetcher.getCloneSetMap().values()){
				sets.add(set);
				set.setCloneSets(sets);
			}
			
			sets.setDataFetcher(modelFetcher);
			
			this.sets = sets;
			
			/*TemplateMethodBuilder builder = new TemplateMethodBuilder(sets);
			builder.build();
			ArrayList<TemplateMethodGroup> templateMethodGroupList = builder.getMethodGroupList();
			
			sets.setTemplateMethodGroup(templateMethodGroupList);*/
			
			/*TemplateFeatureBuilder featureBuilder = new TemplateFeatureBuilder(templateMethodGroupList);
			ArrayList<TemplateFeature> features = featureBuilder.generateTemplateFeatures();*/
			
			MinerUtil.serialize(sets, "ontological_model", false);
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getTotolEffort(){
		return extractor.getTotalCompilationUnitNumber() + 
				parser.getCloneSets().getCloneList().size();
	}
	
	public CloneSets getCloneSets(){
		return this.sets;
	}
}
