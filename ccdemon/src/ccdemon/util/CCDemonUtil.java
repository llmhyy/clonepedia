package ccdemon.util;

import java.util.ArrayList;

import mcidiff.model.CloneInstance;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.internal.handlers.WidgetMethodHandler;

import ccdemon.model.ReferrableCloneSet;
import ccdemon.model.SelectedCodeRange;
import ccdemon.model.rule.PatternMatchingComponent;
import ccdemon.model.rule.TemplateMatch;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;

@SuppressWarnings("restriction")
public class CCDemonUtil {
	
	public static void callBackDefaultEvent(String name, ExecutionEvent event) throws ExecutionException{
		WidgetMethodHandler handler = new WidgetMethodHandler();
		handler.setInitializationData(null, null, name);
		handler.execute(event);
	}
	
	/**
	 * Whenever a clone instance overlapping with the given code fragment, its clone set
	 * will be returned.
	 * @param fileName
	 * @param startLine
	 * @param endLine
	 * @return
	 */
	public static ArrayList<ReferrableCloneSet> findCodeTemplateMaterials(CloneSets cloneSets, SelectedCodeRange range){
		ArrayList<ReferrableCloneSet> materials = new ArrayList<>();
		for(CloneSet set: cloneSets.getCloneList()){
			
			if(set.getId().equals("aa") || set.getId().equals("bb") /*|| set.size() != 5*/){
				continue;
			}
			
			for(clonepedia.model.ontology.CloneInstance instance: set){
				if(instance.getFileLocation().equals(range.getFileName())){
					if(instance.getStartLine()<=range.getEndLine() && instance.getEndLine()>=range.getStartLine()){
						ReferrableCloneSet material = new ReferrableCloneSet(set, instance);
						
						materials.add(material);
						continue;
					}
				}
			}
		}
		
		return materials;
	}
	
	public static TemplateMatch matchPattern(String[] patternArray, String[] instanceArray){
		PatternMatchingComponent[] patternList = transferToPatternComponentList(patternArray);
		PatternMatchingComponent[] instanceList = transferToPatternComponentList(instanceArray);
		
		double[][] scoreTable = new double[patternArray.length+1][instanceArray.length+1];
		for(int i=1; i<scoreTable.length; i++){
			for(int j=1; j<scoreTable[0].length; j++){
				double replaceValue = patternList[i-1].computeSimilarity(instanceList[j-1]);
				
				double replaceV = scoreTable[i-1][j-1] + replaceValue;
				double addV = scoreTable[i-1][j];
				double delV = scoreTable[i][j-1];
				
				double value = getLargestValue(replaceV, addV, delV);
				scoreTable[i][j] = value;
			}
		}

		TemplateMatch match = new TemplateMatch(patternArray.length);
		
		double optimalValue = scoreTable[patternArray.length][instanceArray.length];
		if(optimalValue >= Settings.patternMatchingThreshold){
			match.setMatchable(true);
			for(int i=patternArray.length, j=instanceArray.length; i>0&&j>0;){
				double replaceValue = patternList[i-1].computeSimilarity(instanceList[j-1]);
				double replaceScore = scoreTable[i][j] - scoreTable[i-1][j-1];
				
				if(Math.abs(replaceValue-replaceScore)<0.01){
					match.setValue(i-1, j-1);
					i--;
					j--;
				}
				else{
					if(scoreTable[i][j-1] >= scoreTable[i-1][j]){
						j--;
					}
					else{
						i--;
					}
				}
			}
		}
		else{
			match.setMatchable(false);
		}
		
		return match;
	}
	
	public static double getLargestValue(double entry1, double entry2, double entry3){
		double value = (entry1 > entry2)? entry1 : entry2;
		return (value > entry3)? value : entry3;
	}
	
	private static PatternMatchingComponent[] transferToPatternComponentList(String[] list){
		PatternMatchingComponent[] comList = new PatternMatchingComponent[list.length];
		for(int i=0; i<list.length; i++){
			PatternMatchingComponent com = new PatternMatchingComponent(list[i], (i+1)/(double)list.length);
			comList[i] = com;
		}
		
		return comList;
	}
	
	public static mcidiff.model.CloneSet adaptMCIDiffModel(CloneSet set0) {
		mcidiff.model.CloneSet set = new mcidiff.model.CloneSet();
		set.setId(set0.getId());
		for(clonepedia.model.ontology.CloneInstance ins: set0){
			mcidiff.model.CloneInstance instance = 
					new CloneInstance(set, ins.getFileLocation(), ins.getStartLine(), ins.getEndLine());
			set.addInstance(instance);
		}
		return set;
	}
	
	public static clonepedia.model.ontology.CloneSet adaptClonepediaModel(mcidiff.model.CloneSet set0) {
		clonepedia.model.ontology.CloneSet set = new clonepedia.model.ontology.CloneSet(set0.getId());
		for(mcidiff.model.CloneInstance ins: set0.getInstances()){
			clonepedia.model.ontology.CloneInstance instance = 
					new clonepedia.model.ontology.CloneInstance(set, ins.getFileName(), ins.getStartLine(), ins.getEndLine());
			set.add(instance);
		}
		return set;
	}

}
