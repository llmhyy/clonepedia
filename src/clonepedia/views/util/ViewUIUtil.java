package clonepedia.views.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.syntactic.util.SyntacticUtil;

public class ViewUIUtil {
	
	public static void createTreeItemsForSemanticCluster(Tree tree, Object obj){
		String label;
		if(obj instanceof SemanticCluster)
			label = ((SemanticCluster)obj).getLabel();
		else if(obj instanceof TopicWrapper)
			label = ((TopicWrapper)obj).getTopic().getTopicString();
		else 
			return;
		
		createSimpleTreeItem(tree, "Labels:", label, 0, obj);
	}
	
	public static void createTreeItemsForCloneInstance(Tree tree, Object obj){
		
		CloneInstance instance = (CloneInstance)obj;
		
		createSimpleTreeItem(tree, "Instance Id:", instance.getId(), 0);
		createSimpleTreeItem(tree, "Residing Method:", instance.getResidingMethod().toString(), 1);
		createSimpleTreeItem(tree, "File Location:", instance.getFileLocation(), 2);
		createSimpleTreeItem(tree, "Start Line:", String.valueOf(instance.getStartLine()), 3);
		createSimpleTreeItem(tree, "End Line:", String.valueOf(instance.getEndLine()), 4);
	}

	public static void createTreeItemsForClonePattern(Tree tree, Object obj){
		TreeItem item = createSimpleTreeItem(tree, "Patterns", "Corresponding Clone Sets", new Object());
		
		ClonePatternGroup cp;
		if(obj instanceof ClonePatternGroup)
			cp = (ClonePatternGroup)obj;
		else if(obj instanceof ClonePatternGroupWrapper)
			cp = ((ClonePatternGroupWrapper)obj).getClonePattern();
		else
			return;
		HashMap<String, HashSet<CloneSet>> map = generatePathPatternCloneSetsMap(cp);
		for(String keyPath: map.keySet()){
			
			HashSet<CloneSet> sets = map.get(keyPath);
			String content = "";
			for(CloneSet set: sets)
				content += set.getId() + ", ";
			content = content.substring(0, content.length()-2);
			
			createSubTreeItem(item, keyPath, content, sets);
		}
		
		item.setExpanded(true);
	}
	
	public static void createTreeItemsForCloneSet(Tree tree, Object obj) throws Exception{
		
		CloneSet set;
		if(obj instanceof CloneSet)
			set = (CloneSet)obj;
		else if(obj instanceof CloneSetWrapper)
			set = ((CloneSetWrapper)obj).getCloneSet();
		else
			return;
		
		createSimpleTreeItem(tree, "Instance Number:", String.valueOf(set.size()), new Object());
		TreeItem patternsItem = createSimpleTreeItem(tree, "Patterns:", "", new Object());
		
		for(PathPatternGroup ppg: set.getPatterns()){
			TreeItem abSeqItem = createSubTreeItem(patternsItem, "Pattern:", ppg.getAbstractPathSequence().toString(), ppg);
			abSeqItem.setFont(1, new Font(Display.getCurrent(), "Arial",9, SWT.BOLD));

			for(Path path: ppg){
				createSubTreeItem(abSeqItem, "Path:", path.toString(), path);
			}
		}
		
		patternsItem.setExpanded(true);
		
		TreeItem countersItem = createSimpleTreeItem(tree, "Counter Relations:", "", new Object());
		for(CounterRelationGroup group: set.getCounterRelationGroups()){
			TreeItem counterItem = createSubTreeItem(countersItem, "Counter Relation:", "", group);
			int index = 0;
			for(InstanceElementRelation relation: group.getRelationList()){
				String content = relation.getCloneInstance().getId() + " " + 
						SyntacticUtil.identifyOntologicalRelation(relation.getCloneInstance(), relation.getElement()).toString() + " " +
						relation.getElement().toString();
				
				createSubTreeItem(counterItem, String.valueOf(++index), content, relation);
			}
		}
	}
	
	private static TreeItem createSimpleTreeItem(Tree tree, String title, String content, int index, Object domainObj){
		TreeItem item = new TreeItem(tree, SWT.NONE, index);
		item.setText(new String[]{title, content});
		item.setData(domainObj);
		return item;
	}
	
	private static TreeItem createSimpleTreeItem(Tree tree, String title, String content, Object domainObj){
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText(new String[]{title, content});
		item.setData(domainObj);
		return item;
	}
	
	private static TreeItem createSubTreeItem(TreeItem item, String title, String content, Object domainObj){
		TreeItem i = new TreeItem(item, SWT.NONE);
		i.setText(new String[]{title, content});
		i.setData(domainObj);
		return i;
	}
	
	private static TreeItem createSubTreeItem(TreeItem item, String title, String content, int index, Object domainObj){
		TreeItem i = new TreeItem(item, SWT.NONE, index);
		i.setText(new String[]{title, content});
		i.setData(domainObj);
		return i;
	}
	
	private static HashMap<String, HashSet<CloneSet>> generatePathPatternCloneSetsMap(ClonePatternGroup pc){
		HashMap<String, HashSet<CloneSet>> map = new HashMap<String, HashSet<CloneSet>>();
		for(PathPatternGroup ppg: pc){
			String keyPath = ppg.getAbstractPathSequence().toString();
			HashSet<CloneSet> sets = map.get(keyPath);
			if(sets == null){
				HashSet<CloneSet> setList = new HashSet<CloneSet>();
				setList.add(ppg.getCloneSet());
				map.put(keyPath	, setList);
			}
			else
				sets.add(ppg.getCloneSet());
		}
		return map;
	}
}
