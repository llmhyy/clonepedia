package mcidiff.model;

import java.util.ArrayList;

public class SeqMultiset {
	private ArrayList<TokenSeq> sequences = new ArrayList<>();

	@Override
	public String toString(){
		return this.sequences.toString();
	}
	
	/**
	 * @return the sequences
	 */
	public ArrayList<TokenSeq> getSequences() {
		return sequences;
	}

	/**
	 * @param sequences the sequences to set
	 */
	public void setSequences(ArrayList<TokenSeq> sequences) {
		this.sequences = sequences;
	}
	
	public void addTokenSeq(TokenSeq seq){
		this.sequences.add(seq);
	}
	
	public int getSize(){
		return getSequences().size();
	}
}
