package clonepedia.syntactic.util.comparator;

import java.io.Serializable;

import clonepedia.model.syntactic.PathSequence;

public class PathSequenceComparingPair implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4591578140202725914L;
	private PathSequence seq1;
	private PathSequence seq2;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq1 == null) ? 0 : seq1.hashCode());
		result = prime * result + ((seq2 == null) ? 0 : seq2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathSequenceComparingPair other = (PathSequenceComparingPair) obj;
		if((other.getSeq1().equals(seq1) && other.getSeq2().equals(seq2)) 
				|| (other.getSeq2().equals(seq1) && other.getSeq1().equals(seq2)))
			return true;
		return false;
	}

	public PathSequenceComparingPair(PathSequence seq1, PathSequence seq2) {
		super();
		this.seq1 = seq1;
		this.seq2 = seq2;
	}

	public PathSequence getSeq1() {
		return seq1;
	}

	public void setSeq1(PathSequence seq1) {
		this.seq1 = seq1;
	}

	public PathSequence getSeq2() {
		return seq2;
	}

	public void setSeq2(PathSequence seq2) {
		this.seq2 = seq2;
	}

}
