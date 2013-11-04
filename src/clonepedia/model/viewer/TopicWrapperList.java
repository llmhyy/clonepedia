package clonepedia.model.viewer;

import java.util.ArrayList;

public class TopicWrapperList extends ArrayList<TopicWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1987100884789656323L;
	
	public TopicWrapper searchSpecificTopic(String topicContent){
		for(TopicWrapper topic: this){
			if(topic.getTopic().getTopicString().equals(topicContent))
				return topic;
		}
		return null;
	}
	
	public TopicWrapperList searchContent(String content){
		content = content.toLowerCase();
		TopicWrapperList list = new TopicWrapperList();
		
		for(TopicWrapper topic: this)
			if(topic.getTopic().getTopicString().toLowerCase().contains(content))
				list.add(topic);
		
		return list;
	}
	
	
}
