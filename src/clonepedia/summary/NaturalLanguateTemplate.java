package clonepedia.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;


import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.MergeableSimpleConcreteElement;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Variable;
import clonepedia.model.semantic.Topic;
import clonepedia.model.semantic.Word;
import clonepedia.model.semantic.WordSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.comparator.AverageCodeFragmentLengthDescComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperDiffInCodeDescComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberAscComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberDescComparator;
import clonepedia.model.viewer.comparator.ContainedClonePatternNumberDescComparator;
import clonepedia.model.viewer.comparator.ContainedTopicNumberDescComparator;
import clonepedia.model.viewer.comparator.DefaultValueDescComparator;
import clonepedia.views.util.ViewUtil;

public class NaturalLanguateTemplate {
	
	private final static String counterUsagePatternExplanation = 
			"<p>Tip: Some differences occurs in similar places of the clone code fragements in a clone set, " +
			"if such differences has syntactic commonality, they form Counter Usage Pattern.</p>";

	public static String generateCloneInstancesDescription(Object obj){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		buffer.append("<p>");
		if(obj instanceof CloneSetWrapper){
			for(CloneInstance instance: ((CloneSetWrapper)obj).getCloneSet()){
				buffer.append("<a href=\"CloneInstance\">");
				String instanceString = instance.toString();
				instanceString = instanceString.substring(instanceString.indexOf("(")+1, instanceString.lastIndexOf(")"));
				buffer.append(instanceString);
				buffer.append("</a><br/>");
			}
		}
		
		buffer.append("</p>");
		buffer.append("</form>");
		return buffer.toString();
	}
	
	public static String generateTextDescriptionSummary(Object obj) {
		if(obj instanceof TopicWrapper){
			return generateTextDescriptionSummaryFromTopic((TopicWrapper)obj);
		}
		else if(obj instanceof CloneInstance){
			return generateTextDescriptionSummaryFromCloneInstance((CloneInstance)obj);
		}
		else if(obj instanceof ClonePatternGroupWrapper){
			return generateTextDescriptionSummaryFromClonePattern((ClonePatternGroupWrapper)obj);
		}
		else if(obj instanceof CloneSetWrapper)
			return generateTextDescriptionSummaryFromCloneSet((CloneSetWrapper)obj);
		return null;
	}
	
	private static String generateTextDescriptionSummaryFromCloneSet(
			CloneSetWrapper set) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<p>");
		buffer.append("This clone set is ");
		buffer.append("<b>");
		buffer.append(set.getAdjectiveAccordingToSize());
		buffer.append("</b>");
		buffer.append(". ");
		buffer.append("The cloned code fragments are ");
		buffer.append("<b>");
		buffer.append(set.getAdjectiveAccordingToLength());
		buffer.append("</b>");
		buffer.append(", ");
		buffer.append("and they are ");
		buffer.append("<b>");
		buffer.append(set.getAdjectiveAccordingToDiffRatio());
		buffer.append("</b>");
		buffer.append(". ");
		buffer.append("These clones share ");
		buffer.append("<b>");
		buffer.append(set.getAdjectiveAccordingToClonePattern());
		buffer.append("</b>");
		buffer.append(" syntactic clone patterns and ");
		buffer.append("<b>");
		buffer.append(set.getAdjectiveAccordingToTopic());
		buffer.append("</b>");
		buffer.append(" topics.");
		buffer.append("</p>");
		/*buffer.append("There are ");
		buffer.append(String.valueOf(set.getCloneSet().size()));
		buffer.append(" clone instances in this clone set");
		buffer.append("and its average length of clone code fragments is ");
		buffer.append(String.valueOf(set.computeAverageCodeFragmentLength()));
		buffer.append(" lines. ");
		buffer.append("In addition, the clone set is involved in ");
		buffer.append(String.valueOf(set.getCloneSet().getPatternLabels().size()));
		buffer.append(" clone patterns and ");
		buffer.append(String.valueOf(set.getCloneSet().getTopicLabels().size()));
		buffer.append(" topics.");
		buffer.append("</p>");*/
		return "<form>" + buffer.toString() + "</form>";
	}

	private static String generateTextDescriptionSummaryFromClonePattern(
			ClonePatternGroupWrapper clonePattern) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		buffer.append("<p>");
		buffer.append("There are ");
		buffer.append(String.valueOf(clonePattern.getAllContainedCloneSet().size()));
		buffer.append(" clone sets involved in this pattern and ");
		buffer.append(String.valueOf(clonePattern.diversity()));
		buffer.append(" topics are related to it.");
		buffer.append("</p>");
		buffer.append("</form>");
		
		return buffer.toString();
	}

	private static String generateTextDescriptionSummaryFromCloneInstance(
			CloneInstance instance) {
		return generateSummaryFromCloneInstance(instance);
	}

	private static String generateTextDescriptionSummaryFromTopic(
			TopicWrapper topic) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		buffer.append("<p>");
		buffer.append("There are ");
		buffer.append(String.valueOf(topic.getAllContainedCloneSet().size()));
		buffer.append(" clone sets involved in this pattern and ");
		buffer.append(String.valueOf(topic.diversity()));
		buffer.append(" clone patterns are related to it.");
		buffer.append("</p>");
		buffer.append("</form>");
		
		return buffer.toString();
	}

	public static String generateRelatedTopicSummary(Object obj){
		if(obj instanceof TopicWrapper){
			return generateRelatedTopicSummaryFromTopic((TopicWrapper)obj);
		}
		else if(obj instanceof CloneInstance){
			return generateRelatedTopicSummaryFromCloneInstance((CloneInstance)obj);
		}
		else if(obj instanceof ClonePatternGroupWrapper){
			return generateRelatedTopicSummaryFromClonePattern((ClonePatternGroupWrapper)obj);
		}
		else if(obj instanceof CloneSetWrapper)
			return generateRelatedTopicSummaryFromCloneSet((CloneSetWrapper)obj);
		return null;
	}
	
	private static String generateRelatedTopicSummaryFromCloneSet(CloneSetWrapper set) {
		return "<form>" + getTopicDescriptionForCloneSet(set) + "</form>";
	}

	private static String generateRelatedTopicSummaryFromClonePattern(
			ClonePatternGroupWrapper clonePattern) {
		//return "<form>" + getTopicDescriptionForClonePattern(clonePattern) + "</form>";
		
		HashSet<CloneSetWrapper> setWrappers = clonePattern.getAllContainedCloneSet();
		ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
		for(CloneSetWrapper sw: setWrappers)
			setList.add(sw.getCloneSet());
		
		ArrayList<TopicWrapper> twList = SummaryUtil.generateTopicOrientedSimpleTree(setList);
		
		Collections.sort(twList, new DefaultValueDescComparator());
		
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		
		buffer.append("The clone sets sharing this clone pattern are relevant to ");
		buffer.append(String.valueOf(clonePattern.diversity()));
		buffer.append(" topics. ");
		buffer.append("You may be interested in the following topics: ");
		
		for(int i=0; i<3 && i<twList.size(); i++){
			buffer.append("<li>");
			buffer.append("<a href=\"Topic\">");
			buffer.append(twList.get(i).getTopic().getTopicString());
			buffer.append("</a>");
			buffer.append("</li>");
		}
		
		
		buffer.append("</form>");
		
		return buffer.toString();
	}

	private static String generateRelatedTopicSummaryFromCloneInstance(
			CloneInstance obj) {
		return "<form></form>";
	}

	private static String generateRelatedTopicSummaryFromTopic(TopicWrapper topic) {
		return "<form>" + topic.getTopic().getTopicString() + "</form>";
	}

	public static String generateRelatedClonePatternSummary(Object obj){
		if(obj instanceof TopicWrapper){
			return generateRelatedClonePatternSummaryFromTopic((TopicWrapper)obj);
		}
		else if(obj instanceof CloneInstance){
			return generateRelatedClonePatternSummaryFromCloneInstance((CloneInstance)obj);
		}
		else if(obj instanceof ClonePatternGroupWrapper){
			return generateRelatedClonePatternSummaryFromClonePattern((ClonePatternGroupWrapper)obj);
		}
		else if(obj instanceof CloneSetWrapper)
			return generateRelatedClonePatternSummaryFromCloneSet((CloneSetWrapper)obj);
		return null;
	}
	
	private static String generateRelatedClonePatternSummaryFromCloneSet(
			CloneSetWrapper set) {
		return "<form>" + getClonePatternDescriptionForCloneSet(set) + "</form>";
	}

	private static String generateRelatedClonePatternSummaryFromClonePattern(
			ClonePatternGroupWrapper clonePattern) {
		return "<form>" + clonePattern.toString() + "</form>";
	}

	private static String generateRelatedClonePatternSummaryFromCloneInstance(
			CloneInstance obj) {
		return "<form></form>";
	}

	private static String generateRelatedClonePatternSummaryFromTopic(
			TopicWrapper topic) {
		/*try {
			return "<form>" + getClonePatternDescriptionForTopic(topic) + "</form>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";*/
		
		HashSet<CloneSetWrapper> setWrappers = topic.getAllContainedCloneSet();
		ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
		for(CloneSetWrapper sw: setWrappers)
			setList.add(sw.getCloneSet());
		
		ArrayList<ClonePatternGroupWrapper> list = SummaryUtil.generateClonePatternOrientedSimpleTree(setList);
		
		Collections.sort(list, new DefaultValueDescComparator());
		
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		
		buffer.append("The clone sets sharing this topic are relevant to ");
		buffer.append(String.valueOf(topic.diversity()));
		buffer.append(" clone patterns. ");
		buffer.append("You may be interested in the following syntactic clone patten: ");
		
		for(int i=0; i<3 && i<list.size(); i++){
			buffer.append("<li>");
			buffer.append("<a href=\"ClonePattern\">");
			try {
				buffer.append(list.get(i).toString() + "[#" + list.get(i).getClonePattern().getUniqueId() + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
			buffer.append("</a>");
			buffer.append("</li>");
		}
		
		
		buffer.append("</form>");
		
		return buffer.toString();
	}

	public static String generateInvolvedCloneSetsSummary(Object obj){
		if(obj instanceof TopicWrapper){
			return generateInvolvedCloneSetsSummaryFromTopic((TopicWrapper)obj);
		}
		else if(obj instanceof CloneInstance){
			return generateInvolvedCloneSetsSummaryFromCloneInstance((CloneInstance)obj);
		}
		else if(obj instanceof ClonePatternGroupWrapper){
			return generateInvolvedCloneSetsSummaryFromClonePattern((ClonePatternGroupWrapper)obj);
		}
		else if(obj instanceof CloneSetWrapper)
			return generateInvolvedCloneSetsSummaryFromCloneSet((CloneSetWrapper)obj);
		return null;
	}

	private static String generateInvolvedCloneSetsSummaryFromCloneSet(
			CloneSetWrapper set) {
		return "<form><p><a href=\"CloneSet\">" + set.getCloneSet().getId() + "</a></p></form>";
	}

	private static String generateInvolvedCloneSetsSummaryFromClonePattern(
			ClonePatternGroupWrapper clonePattern) {
		return "<form>" + getDescriptionForIncludedCloneSets(clonePattern) + "</form>";
	}

	private static String generateInvolvedCloneSetsSummaryFromCloneInstance(
			CloneInstance instance) {
		return "<form><p><a href=\"CloneSet\">" + instance.getCloneSet().getId() + "</a></p></form>";
	}

	private static String generateInvolvedCloneSetsSummaryFromTopic(
			TopicWrapper topic) {
		return "<form>" + getDescriptionForIncludedCloneSets(topic) + "</form>";
	}

	private static String generateSummaryFromCloneSet(CloneSetWrapper set){
		StringBuffer buffer = new StringBuffer();
		String str1 = "There are ";
		String str2 = " clone instances in this clone set.";
		String str3 = "Among them, ";
		String str4 = "are seperately used by ";
		String str5 = " clone instances";
		String str6 = "The related topic to this clone sets are: ";
		String str7 = "The related clone pattern to this clone sets are: ";
		String str8 = " for ";
		String str9 = " times";
		
		buffer.append("<form>");
		buffer.append("<p>");
		buffer.append(str1);
		buffer.append(String.valueOf(set.getCloneSet().size()));
		buffer.append(str2);
		buffer.append("</p>");
		buffer.append("<p>");
		buffer.append(str3);
		buffer.append("<br/>");
		WordSet ws = new WordSet();
		for(CounterRelationGroup group: set.getCloneSet().getCounterRelationGroups()){
			StringBuffer counterBuffer = new StringBuffer();
			for(InstanceElementRelation relation: group.getRelationList()){
				ProgrammingElement element = relation.getElement();
				counterBuffer.append("<span color=\"red\">");
				counterBuffer.append(element.getOntologicalType());
				counterBuffer.append("</span>");
				counterBuffer.append(" ");
				counterBuffer.append("<span color=\"green\">");
				counterBuffer.append(element.getSimpleElementName());
				counterBuffer.append("</span>");
				counterBuffer.append(",");
			}
			counterBuffer.append(str4);
			counterBuffer.append(String.valueOf(group.getRelationList().size()));
			counterBuffer.append(str5);
			ws.addWord(new Word(counterBuffer.toString()));
		}
		for(Word counterDescription: ws){
			buffer.append(counterDescription.getName());
			buffer.append(str8);
			buffer.append(String.valueOf(counterDescription.count));
			buffer.append(str9);
			buffer.append("<br/>");
		}
		buffer.append("</p>");
		buffer.append(getTopicDescriptionForCloneSet(set));
		buffer.append("<p>");
		buffer.append(str7);
		buffer.append("<br/>");
		for(ClonePatternGroup pattern: set.getCloneSet().getPatternLabels()){
			ClonePatternGroupWrapper patternWrapper = new ClonePatternGroupWrapper(pattern);
			buffer.append("<a href=\"ClonePattern\">");
			try {
				buffer.append(patternWrapper.getEpitomise(/*6*/)+"[#" + pattern.getUniqueId() + "]");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			buffer.append("</a>");
			buffer.append("<br/>");
		}
		buffer.append("</p>");
		buffer.append("</form>");
		return buffer.toString();
	}
	
	private static String generateSummaryFromTopic(TopicWrapper topic) {
		
		StringBuffer buffer = new StringBuffer();
		
		HashSet<CloneSetWrapper> basicWrapperSet = topic.getAllContainedCloneSet();
		int relatedCloneSetNumber = basicWrapperSet.size();
		
		String str1 = "There are ";
		String str2 = " clone sets related to the concept of \"";
		String str3 = "\":";
		String str4 = "";
		for(CloneSetWrapper set: basicWrapperSet)
			str4 += "<a href=\"CloneSet\">" + set.getCloneSet().getId() + "</a>,  ";
		
		buffer.append("<form><p>");
		buffer.append(str1);
		buffer.append(String.valueOf(relatedCloneSetNumber));
		buffer.append(str2);
		buffer.append(topic.getTopic().getTopicString());
		buffer.append(str3);
		buffer.append(str4);
		buffer.append("</p>");
		
		try {
			buffer.append(getClonePatternDescriptionForTopic(topic));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		buffer.append("</form>");
		
		return buffer.toString();
	}

	private static String generateSummaryFromCloneInstance(CloneInstance obj) {
		
		StringBuffer buffer = new StringBuffer();
		
		CloneInstance instance = (CloneInstance)obj;
		String str1 = "It is one of the ";
		String str2 = " clone instances of the clone set ";
		String str3 = ", which resides in the method \"";
		String str4 = "\". In addition, it is located in the file \"";
		String str5 = "\" and start from line ";
		String str6 = " to line ";
		String str7 = ".";
		
		buffer.append("<form><p>");
		buffer.append(str1);
		buffer.append(String.valueOf(instance.getCloneSet().size()));
		buffer.append(str2);
		buffer.append("<a href=\"CloneSet\">");
		buffer.append(instance.getCloneSet().getId());
		buffer.append("</a>");
		buffer.append(str3);
		buffer.append(instance.getResidingMethod());
		buffer.append(str4);
		buffer.append(instance.getFileLocation());
		buffer.append(str5);
		buffer.append(String.valueOf(instance.getStartLine()));
		buffer.append(str6);
		buffer.append(String.valueOf(instance.getEndLine()));
		buffer.append(str7);
		buffer.append("</p></form>");
		
		return buffer.toString();
	}
	
	public static String generateDetailsFromClonePattern0(ClonePatternGroupWrapper obj) {
		ClonePatternGroupWrapper cpgw = (ClonePatternGroupWrapper)obj;
		PathSequence path = cpgw.getClonePattern().getAbstractPathSequence();
		
		Object[] nodeEdgeList = path.toArray(new Object[0]);
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<form>");
		buffer.append("<p>");
		String str = "";
		if(obj.getClonePattern().getStyle().equals(ClonePatternGroup.LOCATION))
			str = "The clones appear in ";
		else if(obj.getClonePattern().getStyle().equals(ClonePatternGroup.DIFF_USAGE))
			str = "The differential parts of clones use ";
		else if(obj.getClonePattern().getStyle().equals(ClonePatternGroup.COMMON_USAGE))
			str = "The common parts of clones use ";
		
		buffer.append(str);
		buffer.append(" ");
		if((OntologicalElement)nodeEdgeList[2] instanceof MergeableSimpleConcreteElement)
			buffer.append("method/field/variable");
		else
			buffer.append(((OntologicalElement)nodeEdgeList[2]).getOntologicalType().toString().toLowerCase());
		buffer.append(" ");
		buffer.append(getOntologicalTypeString((OntologicalElement)nodeEdgeList[2]));
		buffer.append(" ");
		
		if(nodeEdgeList.length > 3 && !(nodeEdgeList[2] instanceof ComplexType)){
			buffer.append(getDescOfOntologicalRelation((OntologicalRelationType)nodeEdgeList[3]));
			buffer.append(" ");
			buffer.append(((OntologicalElement)nodeEdgeList[4]).getOntologicalType().toString().toLowerCase());
			buffer.append(" ");
			buffer.append(getOntologicalTypeString((OntologicalElement)nodeEdgeList[4]));
			buffer.append(" ");
			
			if(nodeEdgeList.length >= 7){
				buffer.append("whose super type is: ");
				
				for(int i=6; i<nodeEdgeList.length; i = i+2){
					buffer.append(((OntologicalElement)nodeEdgeList[i]).getOntologicalType().toString().toLowerCase());
					buffer.append(" ");
					buffer.append(getOntologicalTypeString((OntologicalElement)nodeEdgeList[i]));
					buffer.append(",");
				}
				buffer.setCharAt(buffer.length()-2, '.');
				buffer.setCharAt(buffer.length()-1, ' ');
			}
			else
				buffer.append(".");
		}
		else if(nodeEdgeList.length > 3){
			
			buffer.append("whose super type is: ");
			for(int i=4; i<nodeEdgeList.length; i = i+2){
				buffer.append(((OntologicalElement)nodeEdgeList[i]).getOntologicalType().toString().toLowerCase());
				buffer.append(" ");
				buffer.append(getOntologicalTypeString((OntologicalElement)nodeEdgeList[i]));
				buffer.append(",");
			}
			buffer.setCharAt(buffer.length()-2, '.');
			buffer.setCharAt(buffer.length()-1, ' ');
		}
		
		buffer.append("</p>");
		buffer.append("</form>");
		return buffer.toString();
	}
	
	private static String getOntologicalTypeString(OntologicalElement element){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<span color=\"");
		
		if(element instanceof Class)
			buffer.append("Class");
		else if(element instanceof Interface)
			buffer.append("Interface");
		else if(element instanceof Method)
			buffer.append("Method");
		else if(element instanceof Field)
			buffer.append("Field");
		else if(element instanceof Variable)
			buffer.append("Variable");
		else if(element instanceof MergeableSimpleConcreteElement)
			buffer.append("MergeableElement");
		else
			buffer.append("noColor");
		
		buffer.append("\">");
		buffer.append(element.getSimpleElementName());
		buffer.append(getSupportingElementDescription((ProgrammingElement)element));
		buffer.append("</span> ");
		
		return buffer.toString();
	}
	
	private static String getDescOfOntologicalRelation(OntologicalRelationType relation){
		if(relation.equals(OntologicalRelationType.hasType))
			return "having type of";
		else if(relation.equals(OntologicalRelationType.hasParameterType))
			return "having parameter of";
		else if(relation.equals(OntologicalRelationType.declaredIn))
			return "declared in";
		else if(relation.equals(OntologicalRelationType.isInnerClassOf))
			return "being inner class of";
		
		return "";
	}

	public static String generateDetailsFromClonePattern(ClonePatternGroupWrapper obj) {
		ClonePatternGroupWrapper cpgw = (ClonePatternGroupWrapper)obj;
		PathSequence path = cpgw.getClonePattern().getAbstractPathSequence();
		
		Object[] nodeEdgeList = path.toArray(new Object[0]);
		String str = "<form>";
		String str1 = "";
		boolean isLocationPath = isLocationPath(path);
		if(isLocationPath)
			str1 = "<p>The clone sets contained have similar location pattern as \"";
		else
			str1 = "<p>The clone sets contained have similar counter usage pattern as \"";
		String str2 = "<span color=\"OntologicalRelation\">" + (OntologicalRelationType)nodeEdgeList[1] + "</span> a ";
		String str3 = "<span color=\"OntologicalType\">" + 
				((OntologicalElement)nodeEdgeList[2]).getOntologicalType().toString() + 
				"</span>";
		str3 += " having name like " + "<span color=\"Name\">" + 
				((OntologicalElement)nodeEdgeList[2]).getSimpleElementName() + "</span>";
		
		str3 += getSupportingElementDescription((ProgrammingElement)nodeEdgeList[2]);
		
		str3 += ", ";
		String str4 = "\".\n";
		
		str += str1 + str2 + str3;
		
		//boolean flag = false;
		for(int i=2; i<nodeEdgeList.length-2; i+=2){
			//flag = true;
			str += getDecriptionFromRelation((OntologicalElement)nodeEdgeList[i], (OntologicalRelationType)nodeEdgeList[i+1],
					(OntologicalElement)nodeEdgeList[i+2]);
		}
		
		/*if(flag)
			str = str.substring(0, str.length()-2);*/
		str += str4 + "</p>";
		//str += getDescriptionForIncludedCloneSets(cpgw);
		//str += getTopicDescriptionForClonePattern(cpgw);
		
		if(!isLocationPath)
			str += counterUsagePatternExplanation;
		
		str += "</form>";
		return str;
	}
	
	public static String getSupportingElementDescription(ProgrammingElement element){
		if(element.getSimpleElementName().contains("*")){
			StringBuffer buffer = new StringBuffer();
			buffer.append("(e.g. ");
			
			ArrayList<ProgrammingElement> supportingElements = element.getSupportingElements();
			ProgrammingElement ele1 = supportingElements.get(0);
			
			buffer.append(ele1.getSimpleElementName());
			buffer.append(", ");
			for(int i=1; i<supportingElements.size(); i++){
				ProgrammingElement ele2 = supportingElements.get(i);
				if(!ele2.getSimpleElementName().equals(ele1.getSimpleElementName())){
					buffer.append(ele2.getSimpleElementName());
					buffer.append(",");
					break;
				}
			}
			
			buffer.append("...) ");
			
			return buffer.toString();
		}
		return "";
	}
	
	private static String getTopicDescriptionForCloneSet(CloneSetWrapper set){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<p>");
		//buffer.append(str6);
		for(Topic topic: set.getCloneSet().getTopicLabels()){
			buffer.append("<a href=\"Topic\">");
			buffer.append(topic.getTopicString());
			buffer.append("</a>, ");
		}
		
		buffer.setCharAt(buffer.length()-2, ' ');
		buffer.append("</p>");
		
		return buffer.toString();
	}

	private static String getTopicDescriptionForClonePattern(ClonePatternGroupWrapper clonePattern){
		HashSet<CloneSetWrapper> setWrappers = clonePattern.getAllContainedCloneSet();
		ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
		for(CloneSetWrapper sw: setWrappers)
			setList.add(sw.getCloneSet());
		
		ArrayList<TopicWrapper> twList = SummaryUtil.generateTopicOrientedSimpleTree(setList);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<p>Above all, <br/>");
		for(TopicWrapper topic: twList){
			buffer.append("<a href=\"Topic\">"/*"<span font=\"bold\">"*/ + topic.getTopic().getTopicString()+/*"</span>"*/"</a>:");
			for(CloneSetWrapper set: topic.getAllContainedCloneSet()){
				buffer.append("<a href=\"CloneSet\">");
				buffer.append(set.getCloneSet().getId());
				buffer.append("</a>, ");
			}
			buffer.append("<br/>");
		}
		buffer.append("</p>");
		return buffer.toString();
	}

	private static String getClonePatternDescriptionForTopic(TopicWrapper topic) throws Exception{
		HashSet<CloneSetWrapper> setWrappers = topic.getAllContainedCloneSet();
		ArrayList<CloneSet> setList = new ArrayList<CloneSet>();
		for(CloneSetWrapper sw: setWrappers)
			setList.add(sw.getCloneSet());
		
		ArrayList<ClonePatternGroupWrapper> clonePatternList = SummaryUtil.generateClonePatternOrientedSimpleTree(setList);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<p>Above all, <br/>");
		for(ClonePatternGroupWrapper clonePattern: clonePatternList){
			for(CloneSetWrapper set: clonePattern.getAllContainedCloneSet()){
				buffer.append("<a href=\"CloneSet\">");
				buffer.append(set.getCloneSet().getId());
				buffer.append("</a>, ");
			}
			buffer.append("are related to structure\"");
			buffer.append("<a href=\"ClonePattern\">" + clonePattern.getEpitomise(/*5*/) + "[#" + clonePattern.getClonePattern().getUniqueId() + "]"+"</a>\"<br/>");
		}
		buffer.append("</p>");
		return buffer.toString();
	}

	private static String getClonePatternDescriptionForCloneSet(CloneSetWrapper set){
		StringBuffer buffer = new StringBuffer();
		//buffer.append("<p>");
		//buffer.append(str7);
		ArrayList<ClonePatternGroup> locationPatterns = new ArrayList<ClonePatternGroup>();
		ArrayList<ClonePatternGroup> usagePatterns = new ArrayList<ClonePatternGroup>();
		
		for(ClonePatternGroup pattern: set.getCloneSet().getPatternLabels())
			if(pattern.getStyle().equals(ClonePatternGroup.LOCATION))
				locationPatterns.add(pattern);
			else
				usagePatterns.add(pattern);
		buffer.append(getSpecificStyleClonePatternDescription(locationPatterns, "Location"));
		buffer.append("<br/>");
		buffer.append(getSpecificStyleClonePatternDescription(usagePatterns, "Usage"));
		
		//buffer.append("</p>");
		return buffer.toString();
	}
	
	private static String getSpecificStyleClonePatternDescription(ArrayList<ClonePatternGroup> patterns, String style){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<li>");
		buffer.append("<b>");
		buffer.append(style + " Clone Patterns:");
		buffer.append("</b>");
		buffer.append("</li>");
		buffer.append("<p>");
		for(ClonePatternGroup pattern: patterns){
			ClonePatternGroupWrapper patternWrapper = new ClonePatternGroupWrapper(pattern);
			buffer.append("<a href=\"ClonePattern\">");
			try {
				buffer.append(patternWrapper.getEpitomise(/*6*/)+"[#" + pattern.getUniqueId() + "]");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			buffer.append("</a>");
			buffer.append("<br/>");
		}
		buffer.append("</p>");
		return buffer.toString();
	}

	private static String getDescriptionForIncludedCloneSets(IContainer container){
		StringBuffer buffer = new StringBuffer();
		HashSet<CloneSetWrapper> hs = container.getAllContainedCloneSet();
		
		ArrayList<CloneSetWrapper> list = new ArrayList<CloneSetWrapper>();
		for(CloneSetWrapper setWrapper: hs)
			list.add(setWrapper);
		
		buffer.append("This topic is present in ");
		buffer.append(String.valueOf(container.getAllContainedCloneSet().size()));
		buffer.append(" clone sets. ");
		buffer.append("You may be interested in the following clone sets:");
		buffer.append("<br/>");
		
		buffer.append(getDescriptionOfTopCloneSets(list, new CloneSetWrapperInstanceNumberDescComparator()));
		buffer.append(getDescriptionOfTopCloneSets(list, new AverageCodeFragmentLengthDescComparator()));
		buffer.append(getDescriptionOfTopCloneSets(list, new ContainedClonePatternNumberDescComparator()));
		buffer.append(getDescriptionOfTopCloneSets(list, new ContainedTopicNumberDescComparator()));
		buffer.append(getDescriptionOfTopCloneSets(list, new CloneSetWrapperDiffInCodeDescComparator()));
		
		return buffer.toString();
	}
	
	private static String getDescriptionOfTopCloneSets(ArrayList<CloneSetWrapper> list, 
			Comparator<CloneSetWrapper> comparator){
		Collections.sort(list, comparator);
		
		String propertyString = "";
		//String unitString = "";
		
		ArrayList<CloneSetWrapper> lowList = new ArrayList<CloneSetWrapper>();
		ArrayList<CloneSetWrapper> mediumList = new ArrayList<CloneSetWrapper>();
		ArrayList<CloneSetWrapper> highList = new ArrayList<CloneSetWrapper>();
		
		
		String startWords = "";
		String endWords = "";
		
		//int limitNum = 3;
		
		//int num = 0;
		if(comparator instanceof AverageCodeFragmentLengthDescComparator){
			propertyString = "clone code fragment length";
			for(CloneSetWrapper setWrapper: list){
				String adj = setWrapper.getAdjectiveAccordingToLength();
				if(adj.equals(CloneSetWrapper.ADJ_LENGTH_SHORT))
					lowList.add(setWrapper);
				else if(adj.equals(CloneSetWrapper.ADJ_LENGTH_MEDIUM))
					mediumList.add(setWrapper);
				else
					highList.add(setWrapper);
					
			}
			startWords = "contain ";
			endWords = " code fragments";
			
			//unitString = "lines";
			
			
			/*num = (list.size() > limitNum)? 
					list.get(2).computeAverageCodeFragmentLength() : 
					list.get(list.size()-1).computeAverageCodeFragmentLength();*/
		}
		else if(comparator instanceof CloneSetWrapperInstanceNumberDescComparator){
			propertyString = "number of clone instances";
			for(CloneSetWrapper setWrapper: list){
				String adj = setWrapper.getAdjectiveAccordingToSize();
				if(adj.equals(CloneSetWrapper.ADJ_SIZE_SMALL))
					lowList.add(setWrapper);
				else if(adj.equals(CloneSetWrapper.ADJ_SIZE_MEDIUM))
					mediumList.add(setWrapper);
				else
					highList.add(setWrapper);
					
			}
			
			startWords = "";
			endWords = " clone sets";
			
			/*num = (list.size() > limitNum)? 
					list.get(2).getCloneSet().size() : 
					list.get(list.size()-1).getCloneSet().size();*/
		}
		else if(comparator instanceof ContainedClonePatternNumberDescComparator){
			propertyString = "number of syntactic clone pattern";
			for(CloneSetWrapper setWrapper: list){
				String adj = setWrapper.getAdjectiveAccordingToClonePattern();
				if(adj.equals(CloneSetWrapper.ADJ_PATTERN_LESS))
					lowList.add(setWrapper);
				else if(adj.equals(CloneSetWrapper.ADJ_PATTERN_MEDIUM))
					mediumList.add(setWrapper);
				else
					highList.add(setWrapper);
					
			}
			startWords = "contain ";
			endWords = " clone pattern";
			
			/*num = (list.size() > limitNum)? 
					list.get(2).getCloneSet().getPatternLabels().size() : 
					list.get(list.size()-1).getCloneSet().getPatternLabels().size();*/
		}
		else if(comparator instanceof CloneSetWrapperDiffInCodeDescComparator){
			propertyString = "differences per line";
			for(CloneSetWrapper setWrapper: list){
				String adj = setWrapper.getAdjectiveAccordingToDiffRatio();
				if(adj.equals(CloneSetWrapper.ADJ_DIFF_LOW))
					lowList.add(setWrapper);
				else if(adj.equals(CloneSetWrapper.ADJ_DIFF_MEDIUM))
					mediumList.add(setWrapper);
				else
					highList.add(setWrapper);
					
			}
			startWords = "contain ";
			endWords = " codes";
			
			/*num = (list.size() > limitNum)? 
					list.get(2).getCloneSet().getTopicLabels().size() : 
					list.get(list.size()-1).getCloneSet().getTopicLabels().size();*/
		}
		else if(comparator instanceof ContainedTopicNumberDescComparator){
			propertyString = "number of topics";
			for(CloneSetWrapper setWrapper: list){
				String adj = setWrapper.getAdjectiveAccordingToTopic();
				if(adj.equals(CloneSetWrapper.ADJ_TOPIC_LESS))
					lowList.add(setWrapper);
				else if(adj.equals(CloneSetWrapper.ADJ_TOPIC_MEDIUM))
					mediumList.add(setWrapper);
				else
					highList.add(setWrapper);
					
			}
			startWords = "contain ";
			endWords = " topics";
			
			/*num = (list.size() > limitNum)? 
					list.get(2).getCloneSet().getTopicLabels().size() : 
					list.get(list.size()-1).getCloneSet().getTopicLabels().size();*/
		}
		propertyString = "<b>" + propertyString + "</b>";
		
		StringBuffer buffer = new StringBuffer();

		buffer.append("<li>");
		buffer.append("In terms of ");
		buffer.append(propertyString);
		buffer.append(":</li>");
		/*buffer.append("<li>The " + propertyString + " is over ");
		buffer.append("<b>");
		buffer.append(String.valueOf(num));
		buffer.append("</b>");
		buffer.append(" " + unitString + ": ");*/
		
		buffer.append(getSubListOfCloneSetInPatternDescription(startWords, endWords, comparator, highList));
		buffer.append(getSubListOfCloneSetInPatternDescription(startWords, endWords, comparator, mediumList));
		buffer.append(getSubListOfCloneSetInPatternDescription(startWords, endWords, comparator, lowList));
		
		//buffer.append(" whose number is over " + num + " " + unitString);
		
		//buffer.append("</li>");
		return buffer.toString();
	}
	
	private static String getSubListOfCloneSetInPatternDescription(String startWords, String endWords,
			Comparator<CloneSetWrapper> comparator, ArrayList<CloneSetWrapper> list){
		StringBuffer buffer = new StringBuffer();
		if(list.size() > 0){
			buffer.append("<li bindent=\"20\">");
			buffer.append(startWords + list.get(0).getAdjective(comparator) + endWords + ": ");
			for(int i=0; i<3 && i<list.size(); i++){
				buffer.append("<a href=\"CloneSet\">");
				buffer.append(list.get(i).getCloneSet().getId());
				buffer.append("</a>, ");
			}
			buffer.append("</li>");
		}
		return buffer.toString();
	}

	private static String getDecriptionFromRelation(OntologicalElement e1, OntologicalRelationType type, OntologicalElement e2){
		String str = "the ";
		str += "<span color=\"OntologicalType\">" + e1.getOntologicalType() + "</span> ";
		//str += getSupportingElementDescription((ProgrammingElement)e1);
		str += "<span color=\"OntologicalRelation\">" + type.toString() + "</span> ";
		str += "<span color=\"OntologicalType\">" + e2.getOntologicalType() + "</span>";
		
		str += /*" having name like " +*/ " <span color=\"Name\">" + e2.getSimpleElementName() + "</span>";
		str += getSupportingElementDescription((ProgrammingElement)e2);
		str += ", ";
		
		return str;
	}

	private static boolean isLocationPath(PathSequence path){
		return "resideIn".equals(((OntologicalRelationType)path.get(1)).toString());
	}
}
