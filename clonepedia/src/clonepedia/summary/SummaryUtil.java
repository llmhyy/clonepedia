package clonepedia.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.semantic.Topic;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.ClonePatternGroupCategoryList;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.PatternGroupCategoryList;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.ClonePatternGroupWrapperList;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.CloneSetWrapperList;
import clonepedia.model.viewer.IContent;
import clonepedia.model.viewer.PatternGroupWrapper;
import clonepedia.model.viewer.PatternGroupWrapperList;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberDescComparator;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;

public class SummaryUtil {
	private static HashMap<Topic, ArrayList<CloneSet>> getTopicCloneRelationsFromCloneSets(
			ArrayList<CloneSet> sets) {
		HashMap<Topic, ArrayList<CloneSet>> topicToCloneSetMap = new HashMap<Topic, ArrayList<CloneSet>>();
		for (CloneSet set : sets) {
			for (Topic topic : set.getTopicLabels()) {
				if (topicToCloneSetMap.containsKey(topic)) {
					ArrayList<CloneSet> setList = topicToCloneSetMap.get(topic);
					if (null != setList)
						setList.add(set);
					else {
						setList = new ArrayList<CloneSet>();
						setList.add(set);
						topicToCloneSetMap.put(topic, setList);
					}
				} else {
					ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
					setList.add(set);
					topicToCloneSetMap.put(topic, setList);
				}
			}
		}
		return topicToCloneSetMap;
	}

	private static HashMap<ClonePatternGroup, ArrayList<CloneSet>> getClonePatternCloneRelationsFromCloneSets(
			ArrayList<CloneSet> sets) {
		HashMap<ClonePatternGroup, ArrayList<CloneSet>> patternToCloneSetMap = new HashMap<ClonePatternGroup, ArrayList<CloneSet>>();
		for (CloneSet set : sets) {
			for (ClonePatternGroup clonePattern : set.getPatternLabels()) {
				if (patternToCloneSetMap.containsKey(clonePattern)) {
					ArrayList<CloneSet> setList = patternToCloneSetMap
							.get(clonePattern);
					if (null != setList)
						setList.add(set);
					else {
						setList = new ArrayList<CloneSet>();
						setList.add(set);
						patternToCloneSetMap.put(clonePattern, setList);
					}
				} else {
					ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
					setList.add(set);
					patternToCloneSetMap.put(clonePattern, setList);
				}
			}
		}
		return patternToCloneSetMap;
	}

	
	public static ArrayList<CloneSetWrapper> getCloneSetWrapperList(ArrayList<CloneSet> sets){
		ArrayList<CloneSetWrapper> setWrapperList = new ArrayList<CloneSetWrapper>();
		for(CloneSet set: sets)
			setWrapperList.add(new CloneSetWrapper(set));
		return setWrapperList;
	}
	
	public static TopicWrapperList generateTopicOrientedSimpleTree(
			ArrayList<CloneSet> sets) {

		TopicWrapperList twList = new TopicWrapperList();

		HashMap<Topic, ArrayList<CloneSet>> map = getTopicCloneRelationsFromCloneSets(sets);
		for (Topic topic : map.keySet()) {
			TopicWrapper tw = new TopicWrapper(topic);
			for (CloneSet set : map.get(topic)) {
				CloneSetWrapper csw = new CloneSetWrapper(set);
				csw.setContainer(tw);
				tw.getContent().add(csw);
			}
			tw.setCloneSetSize(tw.getContent().size());
			twList.add(tw);
		}

		//Collections.sort(twList, new DefaultValueDescComparator());

		return twList;
	}
	
	public static TopicWrapperList generateTopicOrientedSimpleList(ArrayList<CloneSetWrapper> setWrappers){
		ArrayList<CloneSet> list = new ArrayList<CloneSet>();
		for(CloneSetWrapper setWrapper: setWrappers)
			list.add(setWrapper.getCloneSet());
		return generateTopicOrientedSimpleTree(list);
	}

	public static TopicWrapperList generateTopicOrientedComplexTree(
			ArrayList<CloneSet> sets) {

		TopicWrapperList twList = new TopicWrapperList();

		HashMap<Topic, ArrayList<CloneSet>> tmap = getTopicCloneRelationsFromCloneSets(sets);
		for (Topic topic : tmap.keySet()) {
			TopicWrapper tw = new TopicWrapper(topic);

			ArrayList<CloneSet> subSetList = tmap.get(topic);
			HashMap<ClonePatternGroup, ArrayList<CloneSet>> pmap = getClonePatternCloneRelationsFromCloneSets(subSetList);
			for (ClonePatternGroup pc : pmap.keySet()) {
				ClonePatternGroupWrapper cpw = new ClonePatternGroupWrapper(pc);

				for (CloneSet set : pmap.get(pc)) {
					CloneSetWrapper csw = new CloneSetWrapper(set);
					csw.setContainer(cpw);
					cpw.getContent().add(csw);
				}

				cpw.setContainer(tw);
				tw.getContent().add(cpw);
			}

			twList.add(tw);
		}

		return twList;
	}
	
	public static TopicWrapperList generateTopicOrientedComplexList(ArrayList<CloneSetWrapper> setWrappers){
		ArrayList<CloneSet> list = new ArrayList<CloneSet>();
		for(CloneSetWrapper setWrapper: setWrappers)
			list.add(setWrapper.getCloneSet());
		return generateTopicOrientedComplexTree(list);
	}

	public static ClonePatternGroupWrapperList generateClonePatternOrientedSimpleTree(
			ArrayList<CloneSet> sets) {
		ClonePatternGroupWrapperList cpwList = new ClonePatternGroupWrapperList();

		HashMap<ClonePatternGroup, ArrayList<CloneSet>> pmap = getClonePatternCloneRelationsFromCloneSets(sets);
		for (ClonePatternGroup clonePattern : pmap.keySet()) {
			ClonePatternGroupWrapper cpw = new ClonePatternGroupWrapper(
					clonePattern);
			for (CloneSet set : pmap.get(clonePattern)) {
				CloneSetWrapper csw = new CloneSetWrapper(set);
				csw.setContainer(cpw);
				cpw.getContent().add(csw);
			}
			
			cpw.setCloneSetSize(cpw.getContent().size());
			cpwList.add(cpw);
		}

		//Collections.sort(cpwList, new DefaultValueDescComparator());
		return cpwList;
	}
	
	public static ClonePatternGroupWrapperList generateClonePatternOrientedSimpleList(ArrayList<CloneSetWrapper> setWrappers){
		ArrayList<CloneSet> list = new ArrayList<CloneSet>();
		for(CloneSetWrapper setWrapper: setWrappers)
			list.add(setWrapper.getCloneSet());
		return generateClonePatternOrientedSimpleTree(list);
	}

	public static ClonePatternGroupWrapperList generateClonePatternOrientedComplexTree(
			ArrayList<CloneSet> sets) {
		ClonePatternGroupWrapperList cpwList = new ClonePatternGroupWrapperList();

		HashMap<ClonePatternGroup, ArrayList<CloneSet>> pmap = getClonePatternCloneRelationsFromCloneSets(sets);
		for (ClonePatternGroup clonePattern : pmap.keySet()) {
			ClonePatternGroupWrapper cpw = new ClonePatternGroupWrapper(
					clonePattern);

			ArrayList<CloneSet> subSetList = pmap.get(clonePattern);
			HashMap<Topic, ArrayList<CloneSet>> tmap = getTopicCloneRelationsFromCloneSets(subSetList);
			for (Topic topic : tmap.keySet()) {
				TopicWrapper tw = new TopicWrapper(topic);
				for (CloneSet set : tmap.get(topic)) {
					CloneSetWrapper csw = new CloneSetWrapper(set);
					csw.setContainer(tw);
					tw.getContent().add(csw);
				}

				tw.setContainer(cpw);
				cpw.getContent().add(tw);
			}

			cpwList.add(cpw);
		}

		return cpwList;
	}
	
	public static ClonePatternGroupWrapperList generateClonePatternOrientedComplexList(ArrayList<CloneSetWrapper> setWrappers){
		ArrayList<CloneSet> list = new ArrayList<CloneSet>();
		for(CloneSetWrapper setWrapper: setWrappers)
			list.add(setWrapper.getCloneSet());
		return generateClonePatternOrientedComplexTree(list);
	}
	
	public static ArrayList reduceTopClonePatternFromLayer(ClonePatternGroupWrapperList clonePatterns){
		HashSet<CloneSetWrapper> contentSet = new HashSet<CloneSetWrapper>();
		ArrayList<CloneSetWrapper> list = new ArrayList<CloneSetWrapper>();
		
		ClonePatternGroupWrapper cpw = (ClonePatternGroupWrapper)clonePatterns.get(0);
		Object obj = cpw.getContent().get(0);
		if(obj instanceof CloneSetWrapper){
			for(PatternGroupWrapper pattern: clonePatterns)
				contentSet.addAll(((ClonePatternGroupWrapper)pattern).getAllContainedCloneSet());
			list.addAll(contentSet);
			return list;
		}
		else if(obj instanceof TopicWrapper){
			for(PatternGroupWrapper clonePattern: clonePatterns)
				for(IContent content: ((ClonePatternGroupWrapper)clonePattern).getContent()){
					TopicWrapper topic = (TopicWrapper)content;
					contentSet.addAll(topic.getAllContainedCloneSet());
				}
			list.addAll(contentSet);
			
			return generateTopicOrientedSimpleList(list);
		}
			
		return null;	
	}
	
	public static ArrayList reduceTopTopicFromLayer(ArrayList<TopicWrapper> topics){
		HashSet<CloneSetWrapper> contentSet = new HashSet<CloneSetWrapper>();
		ArrayList<CloneSetWrapper> list = new ArrayList<CloneSetWrapper>();
		
		Object obj = topics.get(0).getContent().get(0);
		if(obj instanceof CloneSetWrapper){
			for(TopicWrapper topic: topics)
				contentSet.addAll(topic.getAllContainedCloneSet());
			list.addAll(contentSet);
			return list;
		}
		else if(obj instanceof ClonePatternGroupWrapper){
			for(TopicWrapper topic: topics)
				for(IContent content: topic.getContent()){
					ClonePatternGroupWrapper clonePattern = (ClonePatternGroupWrapper)content;
					contentSet.addAll(clonePattern.getAllContainedCloneSet());
				}
			list.addAll(contentSet);
			
			return generateClonePatternOrientedSimpleList(list);
		}
			
		return null;	
	}
	
	public static TopicWrapperList transferTopicFromSimpleToComplex(TopicWrapperList topicList){
		for (TopicWrapper wrapper : topicList) {
			HashSet<CloneSetWrapper> setWrappers = wrapper
					.getAllContainedCloneSet();
			ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
			for (CloneSetWrapper sw : setWrappers)
				setList.add(sw.getCloneSet());

			ClonePatternGroupWrapperList cpgwList = generateClonePatternOrientedSimpleTree(setList);
			wrapper.getContent().clear();
			for (PatternGroupWrapper clonePattern : cpgwList){
				wrapper.getContent().add((ClonePatternGroupWrapper)clonePattern);
			}
				
		}
		
		return topicList;
	}
	
	public static TopicWrapperList transferTopicFromComplexToSimple(
			TopicWrapperList wrappers) {
		for (TopicWrapper wrapper : wrappers) {
			HashSet<CloneSetWrapper> setWrappers = wrapper
					.getAllContainedCloneSet();
			wrapper.getContent().clear();
			for (CloneSetWrapper setWrapper : setWrappers)
				wrapper.getContent().add(setWrapper);
		}

		return wrappers;
	}

	public static ClonePatternGroupWrapperList transferPatternFromSimpleToComplex(
			PatternGroupWrapperList wrappers) {
		for (PatternGroupWrapper pWrapper : wrappers) {
			ClonePatternGroupWrapper wrapper = (ClonePatternGroupWrapper)pWrapper;
			HashSet<CloneSetWrapper> setWrappers = wrapper
					.getAllContainedCloneSet();
			ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
			for (CloneSetWrapper sw : setWrappers)
				setList.add(sw.getCloneSet());

			TopicWrapperList twList = generateTopicOrientedSimpleTree(setList);
			wrapper.getContent().clear();
			for (TopicWrapper topic : twList)
				wrapper.getContent().add(topic);
		}

		return (ClonePatternGroupWrapperList)wrappers;
	}

	public static ClonePatternGroupWrapperList transferPatternFromComplexToSimple(
			PatternGroupWrapperList wrappers) {
		for (PatternGroupWrapper pWrapper : wrappers) {
			ClonePatternGroupWrapper wrapper = (ClonePatternGroupWrapper)pWrapper;
			HashSet<CloneSetWrapper> setWrappers = wrapper
					.getAllContainedCloneSet();
			wrapper.getContent().clear();
			for (CloneSetWrapper setWrapper : setWrappers)
				wrapper.getContent().add(setWrapper);
		}

		return (ClonePatternGroupWrapperList)wrappers;
	}

	public static PatternGroupCategoryList generateClonePatternSimplifiedCategories(
			ArrayList<CloneSet> sets) throws Exception {
		PatternGroupCategory locationCategory = new PatternGroupCategory(PathSequence.LOCATION, new ClonePatternGroupWrapperList());
		PatternGroupCategory diffUsageCategory = new PatternGroupCategory(PathSequence.DIFF_USAGE, new ClonePatternGroupWrapperList());
		PatternGroupCategory commonUsageCategory = new PatternGroupCategory(PathSequence.COMMON_USAGE, new ClonePatternGroupWrapperList());

		HashMap<ClonePatternGroup, ArrayList<CloneSet>> pmap = getClonePatternCloneRelationsFromCloneSets(sets);
		for (ClonePatternGroup clonePattern : pmap.keySet()) {
			ClonePatternGroupWrapper cpw = new ClonePatternGroupWrapper(
					clonePattern);
			for (CloneSet set : pmap.get(clonePattern)) {
				CloneSetWrapper csw = new CloneSetWrapper(set);
				csw.setContainer(cpw);
				cpw.getContent().add(csw);
			}
			
			cpw.setCloneSetSize(cpw.getContent().size());

			if (cpw.getClonePattern().isLocationPattern())
				locationCategory.addPatternGroupWrapper(cpw);
			else if (cpw.getClonePattern().isDiffUsagePattern())
				diffUsageCategory.addPatternGroupWrapper(cpw);
			else if(cpw.getClonePattern().isCommonUsagePattern())
				commonUsageCategory.addPatternGroupWrapper(cpw);
			 
		}

		/*Collections.sort(locationCategory.getPatterns(),
				new DefaultValueDescComparator());
		Collections.sort(usageCategory.getPatterns(),
				new DefaultValueDescComparator());*/
		// Collections.sort(mixedCategory.getPatterns(), new
		// DefaultValueDescComparator());

		ClonePatternGroupCategoryList categories = new ClonePatternGroupCategoryList();
		categories.add(locationCategory);
		categories.add(diffUsageCategory);
		categories.add(commonUsageCategory);

		return categories;
	}

	public static PatternGroupCategoryList generateClonePatternComplexCategories(
			ArrayList<CloneSet> sets) throws Exception {
		PatternGroupCategory locationCategory = new PatternGroupCategory(PathSequence.LOCATION, new ClonePatternGroupWrapperList());
		PatternGroupCategory diffUsageCategory = new PatternGroupCategory(PathSequence.DIFF_USAGE, new ClonePatternGroupWrapperList());
		PatternGroupCategory commonUsageCategory = new PatternGroupCategory(PathSequence.COMMON_USAGE, new ClonePatternGroupWrapperList());

		HashMap<ClonePatternGroup, ArrayList<CloneSet>> pmap = getClonePatternCloneRelationsFromCloneSets(sets);
		for (ClonePatternGroup clonePattern : pmap.keySet()) {
			ClonePatternGroupWrapper cpw = new ClonePatternGroupWrapper(
					clonePattern);

			ArrayList<CloneSet> subSetList = pmap.get(clonePattern);
			HashMap<Topic, ArrayList<CloneSet>> tmap = getTopicCloneRelationsFromCloneSets(subSetList);
			for (Topic topic : tmap.keySet()) {
				TopicWrapper tw = new TopicWrapper(topic);
				for (CloneSet set : tmap.get(topic)) {
					CloneSetWrapper csw = new CloneSetWrapper(set);
					csw.setContainer(tw);
					tw.getContent().add(csw);
				}

				tw.setContainer(cpw);
				cpw.getContent().add(tw);
			}

			if (cpw.getClonePattern().isLocationPattern())
				locationCategory.addPatternGroupWrapper(cpw);
			else if (cpw.getClonePattern().isDiffUsagePattern())
				diffUsageCategory.addPatternGroupWrapper(cpw);
			else if(cpw.getClonePattern().isCommonUsagePattern())
				commonUsageCategory.addPatternGroupWrapper(cpw);
			 
		}

		PatternGroupCategoryList categories = new PatternGroupCategoryList();
		categories.add(locationCategory);
		categories.add(diffUsageCategory);
		categories.add(commonUsageCategory);

		return categories;
	}

	public static PatternGroupCategoryList transferPatternFromSimpleToComplexInCatetory(
			PatternGroupCategoryList categories) {

		for (PatternGroupCategory category : categories)
			transferPatternFromSimpleToComplex(category.getPatterns());

		return categories;
	}

	public static PatternGroupCategoryList transferPatternFromComplexToSimpleInCatetory(
			PatternGroupCategoryList categories) {

		for (PatternGroupCategory category : categories)
			transferPatternFromComplexToSimple(category.getPatterns());

		return categories;
	}

	public static PatternGroupCategoryList filterClonePatternByNumberInCategory(
			PatternGroupCategoryList categories, int minimum, int maximum) {
		for (PatternGroupCategory category : categories) {
			ClonePatternGroupWrapperList list = filterClonePatternByNumber(
					category.getPatterns(), minimum, maximum);
			category.setPatterns(list);
		}

		return categories;
	}

	public static ClonePatternGroupWrapperList filterClonePatternByNumber(
			PatternGroupWrapperList clonePatterns, int minimum, int maximum) {
		ClonePatternGroupWrapperList list = new ClonePatternGroupWrapperList();
		for (PatternGroupWrapper pWrapper : clonePatterns) {
			ClonePatternGroupWrapper wrapper = (ClonePatternGroupWrapper)pWrapper;
			int size = wrapper.getAllContainedCloneSet().size();
			if (size >= minimum && size <= maximum)
				list.add(wrapper);
		}

		return list;
	}

	public static CloneSetWrapperList wrapCloneSets(ArrayList<CloneSet> sets) {
		CloneSetWrapperList setWrappers = new CloneSetWrapperList();
		for (CloneSet set : sets)
			setWrappers.add(new CloneSetWrapper(set));
		return setWrappers;
	}
}
