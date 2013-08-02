package clonepedia.test.patternextraction;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.featuretemplate.TemplateFeatureBuilder;
import clonepedia.featuretemplate.TemplateMethodBuilder;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.template.TemplateFeature;
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
		
		/*CloneSets sets = (CloneSets) MinerUtil.deserialize("D://linyun//eclipse_for_clone//configurations//JHotDraw7_0_6//ontological_model", true);
		sets.setPathComparator(new LevenshteinPathComparator());
		ArrayList<TemplateMethodGroup> templateMethodGroupList = sets.getTemplateMethodGroup();
		
		TemplateFeatureBuilder featureBuilder = new TemplateFeatureBuilder(templateMethodGroupList);
		ArrayList<TemplateFeature> features = featureBuilder.generateTemplateFeatures();*/
		
		/*HashSet<ATest> aList = new HashSet<ATest>();
		for(int i=0; i<10; i++){
			ATest a = new ATest(String.valueOf(i));
			aList.add(a);
		}
		MinerUtil.serialize(aList, "test");*/
		CloneSets sets = (CloneSets)MinerUtil.deserialize("D://linyun//eclipse_for_clone//configurations//JHotDraw7_0_6//ontological_model", true);
		
		System.out.print("");
	}
}
