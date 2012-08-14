package clonepedia.debug;

import java.util.ArrayList;

import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.semantic.util.SemanticUtil;
import clonepedia.util.MinerUtil;

public class Logger {
	
	/*public static void logSemanticClusterForCloneSets(ArrayList<SemanticCluster> clusters, String fileName){
		StringBuffer buffer = new StringBuffer();
		for(SemanticCluster clu: clusters){
			
		}
	}*/
	
	public static void log1(ArrayList<SemanticCluster> clusters, String fileName){
		StringBuffer buffer = new StringBuffer();
		for(SemanticCluster semanticCluster: clusters){
			buffer.append("=====================" + ":\n");
			buffer.append("semantic cluster: " + semanticCluster.getLabel() + ":\n");
			for(ICluster unit: semanticCluster.getUnits()){
				
				SyntacticCluster synCluster = (SyntacticCluster)unit;
				buffer.append("syntactic cluster:");
				buffer.append("[");
				
				for(CloneSet set: synCluster){
					buffer.append(set.getId() + ",");
					/*for(PatternPathGroup ppg: set.getPatterns()){
						buffer.append(ppg.toString());
					}*/
				}
				buffer.append("]\n");
				buffer.append("\n");
				//buffer.append(SemanticUtil.combineWord(synCluster.getRelatedWords()));
				//buffer.append("\n");
				
			}
			buffer.append("\n");
			buffer.append("=====================" + ":\n");
			
		}
		
		MinerUtil.logMessage(buffer.toString(), fileName);
	}
	
	public static void log2(ArrayList<SemanticCluster> clusters, String fileName){
		StringBuffer buffer = new StringBuffer();
		for(SemanticCluster semanticCluster: clusters){
			buffer.append("=====================" + ":\n");
			buffer.append("semantic cluster: " + semanticCluster.getLabel() + ":\n");
			for(ICluster unit: semanticCluster.getUnits()){
				
				SyntacticCluster synCluster = (SyntacticCluster)unit;
				buffer.append("  syntactic cluster:\n");
				
				
				for(ICluster u: synCluster.getUnits()){
					SemanticCluster semClu = (SemanticCluster)u;
					buffer.append("    semantic cluster: " + semClu.getLabel() + ":");
					buffer.append("[");
					for(CloneSet set: semClu){
						buffer.append(set.getId() + ",");
						/*for(PatternPathGroup ppg: set.getPatterns()){
							buffer.append(ppg.toString());
						}*/
					}
					buffer.append("]\n");
				}
				
				buffer.append("\n");
				//buffer.append(SemanticUtil.combineWord(synCluster.getRelatedWords()));
				//buffer.append("\n");
				
			}
			buffer.append("\n");
			buffer.append("=====================" + ":\n");
			
		}
		
		MinerUtil.logMessage(buffer.toString(), fileName);
	}
}
