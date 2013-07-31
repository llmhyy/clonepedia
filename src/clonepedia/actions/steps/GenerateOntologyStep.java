package clonepedia.actions.steps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;

import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.StructureExtractor;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Project;
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
			
			this.sets = sets;
			
			MinerUtil.serialize(sets, "ontological_model");
			
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
