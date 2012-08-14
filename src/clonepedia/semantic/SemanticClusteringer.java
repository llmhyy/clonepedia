package clonepedia.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;

import com.google.common.collect.Maps;

import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.ISemanticClusterable;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.semantic.util.SemanticUtil;

public class SemanticClusteringer {
	
	public ArrayList<SemanticCluster> doClustering(ArrayList<? extends ISemanticClusterable> list){
		HashMap<Document, ISemanticClusterable> map = new HashMap<Document, ISemanticClusterable>();
		ArrayList<Document> documents = extractWords(list, map);
		/*for (String row : data)
        {
            documents.add(new Document(row));
        }*/
		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(AttributeNames.DOCUMENTS, documents);
		Controller controller = ControllerFactory.createSimple();
		
		ProcessingResult byTopicClusters = controller.process(attributes, LingoClusteringAlgorithm.class);
		
		List<Cluster> clusters = byTopicClusters.getClusters();
		ArrayList<SemanticCluster> semanticClusterList = new ArrayList<SemanticCluster>();
		for(Cluster c: clusters){
			SemanticCluster semCluster = new SemanticCluster(map, c);
			semanticClusterList.add(semCluster);
		}
		
		return semanticClusterList;
	}
	
	private ArrayList<Document> extractWords(ArrayList<? extends ISemanticClusterable> list, HashMap<Document, ISemanticClusterable> map){
		ArrayList<Document> docs = new ArrayList<Document>();
		for(ISemanticClusterable unit: list){
			String content = SemanticUtil.combineWord(unit.getRelatedWords());
			//String content = cluster.getRelatedWords();
			Document doc = new Document(content);
			
			map.put(doc, unit);
			
			docs.add(doc);
		}
		return docs;
	}
}
