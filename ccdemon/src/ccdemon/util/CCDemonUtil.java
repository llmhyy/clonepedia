package ccdemon.util;

import java.util.ArrayList;

import mcidiff.model.CloneInstance;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.handlers.WidgetMethodHandler;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ccdemon.model.ReferrableCloneSet;
import ccdemon.model.SelectedCodeRange;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;

@SuppressWarnings("restriction")
public class CCDemonUtil {
	
	public static ArrayList<LinkedPosition> positions = new ArrayList<LinkedPosition>();
	
	private static AbstractTextEditor activeEditor;

	public static AbstractTextEditor getActiveEditor() {
		return activeEditor;
	}

	public static void setActiveEditor(ExecutionEvent event) {
		activeEditor = (AbstractTextEditor) HandlerUtil.getActiveEditor(event);
	}
	
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
	
	public static mcidiff.model.CloneSet adaptClonepediaModel(CloneSet set0) {
		mcidiff.model.CloneSet set = new mcidiff.model.CloneSet();
		set.setId(set0.getId());
		for(clonepedia.model.ontology.CloneInstance ins: set0){
			mcidiff.model.CloneInstance instance = 
					new CloneInstance(set, ins.getFileLocation(), ins.getStartLine(), ins.getEndLine());
			set.addInstance(instance);
		}
		return set;
	}
	
	public static clonepedia.model.ontology.CloneSet adaptMCIDiffModel(mcidiff.model.CloneSet set0) {
		clonepedia.model.ontology.CloneSet set = new clonepedia.model.ontology.CloneSet(set0.getId());
		for(mcidiff.model.CloneInstance ins: set0.getInstances()){
			clonepedia.model.ontology.CloneInstance instance = 
					new clonepedia.model.ontology.CloneInstance(set, ins.getFileName(), ins.getStartLine(), ins.getEndLine());
			set.add(instance);
		}
		return set;
	}

}
