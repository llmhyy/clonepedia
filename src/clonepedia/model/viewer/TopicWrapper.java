package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.model.ontology.CloneSet;
import clonepedia.model.semantic.Topic;
import clonepedia.summary.SummaryUtil;

public class TopicWrapper implements IContent, IContainer{
	private Topic topic;
	private ArrayList<IContent> content = new ArrayList<IContent>();
	private IContainer container;
	private int cloneSetSize;
	private int diversity;
	
	public TopicWrapper(Topic topic){
		this.topic = topic;
	}
	
	public String toString(){
		return topic.getTopicString();
	}
	
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	public ArrayList<IContent> getContent() {
		return content;
	}
	public void setContent(ArrayList<IContent> content) {
		this.content = content;
	}
	public IContainer getContainer() {
		return container;
	}
	public void setContainer(IContainer container) {
		this.container = container;
	}

	@Override
	public HashSet<CloneSetWrapper> getAllContainedCloneSet() {
		HashSet<CloneSetWrapper> set = new HashSet<CloneSetWrapper>();
		recursivelyFindCloneSets(set, this);
		
		return set;
	}
	
	private void recursivelyFindCloneSets(HashSet<CloneSetWrapper> basicWrapperSet, IContainer root) {
		for(IContent content: root.getContent())
			if(content instanceof IContainer)
				recursivelyFindCloneSets(basicWrapperSet, (IContainer)content);
			else if(content instanceof CloneSetWrapper)
				basicWrapperSet.add((CloneSetWrapper)content);
	}
	
	@Override
	public HashSet<CloneSetWrapper> getIntersectionWith(IContainer container) {
		HashSet<CloneSetWrapper> intersectionSet = new HashSet<CloneSetWrapper>();
		for(CloneSetWrapper cloneSetWrapper1: this.getAllContainedCloneSet())
			for(CloneSetWrapper cloneSetWrapper2: container.getAllContainedCloneSet())
				if(cloneSetWrapper1.equals(cloneSetWrapper2))
					intersectionSet.add(cloneSetWrapper1);
		return intersectionSet;
	}

	@Override
	public double value() {
		return topic.getScore()*size()/diversity();
	}

	@Override
	public int size() {
		if(this.cloneSetSize == 0)
			this.cloneSetSize = getAllContainedCloneSet().size();
		
		return this.cloneSetSize;
	}

	@Override
	public int diversity() {
		if(diversity == 0){
			ArrayList<CloneSet> sets = new ArrayList<CloneSet>();
			for(CloneSetWrapper setWrapper: getAllContainedCloneSet())
				sets.add(setWrapper.getCloneSet());
			
			this.diversity = SummaryUtil.generateClonePatternOrientedSimpleTree(sets).size();
		}
		return this.diversity;
	}

	public int getCloneSetSize() {
		return cloneSetSize;
	}

	public void setCloneSetSize(int cloneSetSize) {
		this.cloneSetSize = cloneSetSize;
	}

	public int getDiversity() {
		return diversity;
	}

	public void setDiversity(int diversity) {
		this.diversity = diversity;
	}
}
