package clonepedia.model.viewer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import clonepedia.Activator;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.viewer.comparator.AverageCodeFragmentLengthAscComparator;
import clonepedia.model.viewer.comparator.AverageCodeFragmentLengthDescComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperDiffInCodeAscComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperDiffInCodeDescComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberAscComparator;
import clonepedia.model.viewer.comparator.CloneSetWrapperInstanceNumberDescComparator;
import clonepedia.model.viewer.comparator.ContainedClonePatternNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedClonePatternNumberDescComparator;
import clonepedia.model.viewer.comparator.ContainedCloneSetNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedTopicNumberAscComparator;
import clonepedia.model.viewer.comparator.ContainedTopicNumberDescComparator;
import clonepedia.summary.SummaryUtil;

public class CloneSetWrapper implements IContent{
	
	public static String ADJ_SIZE_SMALL = "small";
	public static String ADJ_SIZE_MEDIUM = "medium";
	public static String ADJ_SIZE_LARGE = "large";
	public static String ADJ_DIFF_LOW = "almost identical";
	public static String ADJ_DIFF_MEDIUM = "partially different";
	public static String ADJ_DIFF_HIGH = "very different";
	public static String ADJ_PATTERN_LESS = "a few";
	public static String ADJ_PATTERN_MEDIUM = "some";
	public static String ADJ_PATTERN_MORE = "many";
	public static String ADJ_TOPIC_LESS = "a few";
	public static String ADJ_TOPIC_MEDIUM = "some";
	public static String ADJ_TOPIC_MORE = "many";
	public static String ADJ_LENGTH_LONG = "short";
	public static String ADJ_LENGTH_MEDIUM = "medium";
	public static String ADJ_LENGTH_SHORT = "long";
	
	private CloneSet cloneSet;
	private IContainer container;
	private clonepedia.java.model.CloneSetWrapper syntacticSetWrapper;
	
	public String toString(){
		return cloneSet.getId();
	}
	
	public CloneSetWrapper(CloneSet cloneSet){
		this.cloneSet = cloneSet;
	}

	public CloneSet getCloneSet() {
		return cloneSet;
	}

	public void setCloneSet(CloneSet cloneSet) {
		this.cloneSet = cloneSet;
	}

	public IContainer getContainer() {
		return container;
	}

	public void setContainer(IContainer container) {
		this.container = container;
	}

	@Override
	public int hashCode() {
		return this.cloneSet.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CloneSetWrapper){
			CloneSetWrapper setW = (CloneSetWrapper)obj;
			return this.cloneSet.equals(setW.getCloneSet());
		}
		return false;
	}

	public ArrayList<IContent> getContent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int computeAverageCodeFragmentLength(){
		int sum = 0;
		for(CloneInstance instance: this.cloneSet)
			sum += (instance.getEndLine()-instance.getStartLine()+1);
		return sum/this.cloneSet.size();
	}
	
	public double differenceInCodeFragments(){
		NumberFormat format = new DecimalFormat("#0.000");
		String diffNumString = format.format((double)this.getCloneSet().getCounterRelationGroups().size()/this.computeAverageCodeFragmentLength());
		return Double.valueOf(diffNumString);
	}
	
	
	
	public String getAdjectiveAccordingToSize(){
		ArrayList<CloneSetWrapper> list = SummaryUtil.getCloneSetWrapperList(Activator.sets.getCloneList());
		Collections.sort(list, new CloneSetWrapperInstanceNumberAscComparator());
		
		int delimit1 = list.get((int)(list.size()*0.25)).getCloneSet().size();
		int delimit2 = list.get((int)(list.size()*0.75)).getCloneSet().size();
		
		if(this.cloneSet.size() <= delimit1)
			return ADJ_SIZE_SMALL;
		else if(this.cloneSet.size()>delimit1 && this.cloneSet.size() <= delimit2)
			return ADJ_SIZE_MEDIUM;
		else
			return ADJ_SIZE_LARGE;
	}
	
	
	
	public String getAdjectiveAccordingToLength(){
		ArrayList<CloneSetWrapper> list = SummaryUtil.getCloneSetWrapperList(Activator.sets.getCloneList());
		Collections.sort(list, new AverageCodeFragmentLengthAscComparator());
		
		int delimit1 = list.get((int)(list.size()*0.25)).computeAverageCodeFragmentLength();
		int delimit2 = list.get((int)(list.size()*0.75)).computeAverageCodeFragmentLength();
		
		if(this.computeAverageCodeFragmentLength() <= delimit1)
			return ADJ_LENGTH_LONG;
		else if(this.computeAverageCodeFragmentLength()>delimit1 && this.computeAverageCodeFragmentLength() <= delimit2)
			return ADJ_LENGTH_MEDIUM;
		else
			return ADJ_LENGTH_SHORT;
	}
	
	public String getAdjectiveAccordingToDiffRatio(){
		ArrayList<CloneSetWrapper> list = SummaryUtil.getCloneSetWrapperList(Activator.sets.getCloneList());
		Collections.sort(list, new CloneSetWrapperDiffInCodeAscComparator());
		
		double delimit1 = list.get((int)(list.size()*0.25)).differenceInCodeFragments();
		double delimit2 = list.get((int)(list.size()*0.75)).getCloneSet().size();
		
		if(this.differenceInCodeFragments() <= delimit1)
			return ADJ_DIFF_LOW;
		else if(this.differenceInCodeFragments()>delimit1 && this.differenceInCodeFragments() <= delimit2)
			return ADJ_DIFF_MEDIUM;
		else
			return ADJ_DIFF_HIGH;
	}
	
	public String getAdjective(Comparator<CloneSetWrapper> comparator){
		if(comparator instanceof AverageCodeFragmentLengthDescComparator)
			return getAdjectiveAccordingToLength();
		else if(comparator instanceof CloneSetWrapperDiffInCodeDescComparator)
			return getAdjectiveAccordingToDiffRatio();
		else if(comparator instanceof ContainedClonePatternNumberDescComparator)
			return getAdjectiveAccordingToClonePattern();
		else if(comparator instanceof ContainedTopicNumberDescComparator)
			return getAdjectiveAccordingToTopic();
		else if(comparator instanceof CloneSetWrapperInstanceNumberDescComparator)
			return getAdjectiveAccordingToSize();
		return "";
	}
	
	public String getAdjectiveAccordingToClonePattern(){
		ArrayList<CloneSetWrapper> list = SummaryUtil.getCloneSetWrapperList(Activator.sets.getCloneList());
		Collections.sort(list, new ContainedClonePatternNumberAscComparator());
		
		int delimit1 = list.get((int)(list.size()*0.25)).getCloneSet().getPatternLabels().size();
		int delimit2 = list.get((int)(list.size()*0.75)).getCloneSet().getPatternLabels().size();
		
		if(this.cloneSet.getPatternLabels().size() <= delimit1)
			return ADJ_PATTERN_LESS;
		else if(this.cloneSet.getPatternLabels().size()>delimit1 && this.cloneSet.getPatternLabels().size() <= delimit2)
			return ADJ_PATTERN_MEDIUM;
		else
			return ADJ_PATTERN_MORE;
	}
	
	public String getAdjectiveAccordingToTopic(){
		ArrayList<CloneSetWrapper> list = SummaryUtil.getCloneSetWrapperList(Activator.sets.getCloneList());
		Collections.sort(list, new ContainedTopicNumberAscComparator());
		
		int delimit1 = list.get((int)(list.size()*0.25)).getCloneSet().getTopicLabels().size();
		int delimit2 = list.get((int)(list.size()*0.75)).getCloneSet().getTopicLabels().size();
		
		if(this.cloneSet.getTopicLabels().size() <= delimit1)
			return ADJ_TOPIC_LESS;
		else if(this.cloneSet.getTopicLabels().size()>delimit1 && this.cloneSet.getTopicLabels().size() <= delimit2)
			return ADJ_TOPIC_MEDIUM;
		else
			return ADJ_TOPIC_MORE;
	}

	public clonepedia.java.model.CloneSetWrapper getSyntacticSetWrapper() {
		return syntacticSetWrapper;
	}

	public void setSyntacticSetWrapper(clonepedia.java.model.CloneSetWrapper syntacticSetWrapper) {
		this.syntacticSetWrapper = syntacticSetWrapper;
	}
	
}
