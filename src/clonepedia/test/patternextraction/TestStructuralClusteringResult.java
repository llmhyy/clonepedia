package clonepedia.test.patternextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import clonepedia.featuretemplate.CandidateTemplateBuilder;
import clonepedia.featuretemplate.TMGBuilder;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.template.SubCandidateTemplate;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.syntactic.OntologicalModelGenerator;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;
import junit.framework.TestCase;

public class TestStructuralClusteringResult extends TestCase {
	public void testClusteringResult() throws Exception{
		
		//ArrayList<SyntacticCluster> list = ((SyntacticClusteringer)MinerUtil.deserialize("all_clusterer", false)).doClustering();
		
		//System.out.print("");
		
		//OntologicalModelGenerator generator = (OntologicalModelGenerator)MinerUtil.deserialize("generator", false);
		//SyntacticClusteringer allCluster = (SyntacticClusteringer)MinerUtil.deserialize("all_clusterer_new", false);
		//ArrayList<SyntacticCluster> allList = allCluster.doClustering();
		/*StructuralClusterer locationCluster = (StructuralClusterer)MinerUtil.deserialize("location_clusterer_new");
		ArrayList<CloneSetCluster> locationList = locationCluster.doClustering();
		StructuralClusterer usageCluster = (StructuralClusterer)MinerUtil.deserialize("usage_clusterer_new");
		ArrayList<CloneSetCluster> usageList = usageCluster.doClustering();*/
		//System.out.print("");
		
		
		/*TemplateMethodBuilder builder = new TemplateMethodBuilder(sets);
		builder.build();
		ArrayList<TemplateMethodGroup> templateMethodGroupList = builder.getMethodGroupList();*/
		CloneSets sets = (CloneSets)MinerUtil.deserialize("D://linyun//eclipse_for_clone//configurations//JHotDraw7_0_6//ontological_model", true);
		sets.setPathComparator(new LevenshteinPathComparator());
		
		TMGBuilder builder = new TMGBuilder(sets);
		builder.build();
		/*TreeSet<TemplateMethodGroup> templateMethodGroupList = builder.getMethodGroupList();
		
		sets.setTemplateMethodGroup(templateMethodGroupList);*/
		
		
		//ArrayList<TemplateMethodGroup> templateMethodGroupList = sets.getTemplateMethodGroup();
		
		/*TemplateFeatureBuilder featureBuilder = new TemplateFeatureBuilder(templateMethodGroupList);
		ArrayList<TemplateFeature> features = featureBuilder.generateTemplateFeatures();
		
		ArrayList<TemplateFeature> validateFeatures = new ArrayList<TemplateFeature>();
		for(TemplateFeature feature: features){
			if(feature.getTemplateMethodGroupList().size() > 1){
				validateFeatures.add(feature);
			}
		}*/
		
		System.out.print("");
	}
}
