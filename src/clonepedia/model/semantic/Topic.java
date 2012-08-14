package clonepedia.model.semantic;

import java.io.Serializable;

public class Topic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8892654269157758809L;
	
	private String topic;
	private double score;
	
	public Topic(String topic, double score) {
		super();
		this.topic = topic;
		this.score = score;
	}
	
	public String getTopicString() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Topic)
			return ((Topic)obj).getTopicString().equals(this.topic);
		else return false;
	}
	
	public int hashCode(){
		return this.topic.hashCode();
	}
}
