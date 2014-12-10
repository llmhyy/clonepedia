package ccdemon.model;


import java.util.ArrayList;
import java.util.HashMap;

import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class ConfigurationPoint {
	/**
	 * The token sequence that need to be changed.
	 */
	private TokenSeq modifiedTokenSeq;
	/**
	 * The copied token sequence, which is different from (@code modifiedTokenSeq) in that
	 * their position and AST nodes are different.
	 */
	private TokenSeq copiedTokenSeq;
	/**
	 * candidate token sequences to replace {@code tokenSeq} field
	 */
	private SeqMultiset seqMultiset;
	
	private ArrayList<Candidate> candidates = new ArrayList<>();
	/**
	 * @param tokenSeq
	 * @param seqMultiset
	 */
	public ConfigurationPoint(TokenSeq tokenSeq, SeqMultiset seqMultiset) {
		super();
		this.copiedTokenSeq = tokenSeq;
		this.seqMultiset = seqMultiset;
		
		organizeHistoricalCandidate(getSeqMultiset());
	}
	
	public boolean contains(String candidateString){
		for(Candidate candidate: this.candidates){
			if(candidate.getText().equals(candidateString)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isType(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof ITypeBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isVariableOrField(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isMethod(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IMethodBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private void organizeHistoricalCandidate(SeqMultiset seqMultiset) {
		// TODO may need to collect more information to adjust the score of candidate to indidate candidate ranking.
		HashMap<TokenSeq, Integer> map = new HashMap<>();
		for(TokenSeq seq: seqMultiset.getSequences()){
			Integer count = map.get(seq);
			if(count == null){
				count = new Integer(1);
			}
			else{
				count++;
			}
			
			map.put(seq, count);
		}
		
		for(TokenSeq seq: map.keySet()){
			Candidate candidate = new Candidate(seq.getText(), map.get(seq), Candidate.HISTORY);
			candidates.add(candidate);
		}
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Class> getSuperClasses(){
		ArrayList<Class> classList = new ArrayList<Class>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof ITypeBinding){
						ITypeBinding typeBinding = (ITypeBinding) binding;
						try {
							Class<?> c = Class.forName(typeBinding.getSuperclass().getQualifiedName());
							if(!c.equals(Object.class) && !classList.contains(c)){
								classList.add(c);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return classList;
	}
	
	public ArrayList<String> getVariableOrFieldTypes(){
		ArrayList<String> typeList = new ArrayList<String>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						if(!typeList.contains(((IVariableBinding)binding).getType().getQualifiedName())){
							typeList.add(((IVariableBinding)binding).getType().getQualifiedName());
						}
					}
				}
			}
		}
		
		return typeList;
	}

	/**
	 * @return the tokenSeq
	 */
	public String toString(){
		return copiedTokenSeq.toString();
	}
	
	public TokenSeq getCopiedTokenSeq() {
		return copiedTokenSeq;
	}
	/**
	 * @return the candidates
	 */
	public ArrayList<Candidate> getCandidates() {
		return candidates;
	}

	/**
	 * @param candidates the candidates to set
	 */
	public void setCandidates(ArrayList<Candidate> candidates) {
		this.candidates = candidates;
	}

	/**
	 * @param tokenSeq the tokenSeq to set
	 */
	public void setCopiedTokenSeq(TokenSeq tokenSeq) {
		this.copiedTokenSeq = tokenSeq;
	}
	/**
	 * @return the seqMultiset
	 */
	public SeqMultiset getSeqMultiset() {
		return seqMultiset;
	}
	/**
	 * @param seqMultiset the seqMultiset to set
	 */
	public void setSeqMultiset(SeqMultiset seqMultiset) {
		this.seqMultiset = seqMultiset;
	}
	/**
	 * @return the modifiedTokenSeq
	 */
	public TokenSeq getModifiedTokenSeq() {
		return modifiedTokenSeq;
	}
	/**
	 * @param modifiedTokenSeq the modifiedTokenSeq to set
	 */
	public void setModifiedTokenSeq(TokenSeq modifiedTokenSeq) {
		this.modifiedTokenSeq = modifiedTokenSeq;
	}
	
	
}
