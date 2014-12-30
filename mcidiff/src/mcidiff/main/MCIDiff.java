package mcidiff.main;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.IJavaProject;

import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.CorrespondentListAndSet;
import mcidiff.model.Multiset;
import mcidiff.model.TokenMultiset;

public abstract class MCIDiff {
	
	public abstract ArrayList<? extends Multiset> diff(CloneSet set, IJavaProject project);
	
	protected abstract ArrayList<? extends Multiset> computeDiff(CorrespondentListAndSet cls, TokenSequence[] sequences);
	
	protected TokenSequence[] transferToModel(CloneSet set){
		TokenSequence[] sequence = new TokenSequence[set.getInstances().size()];
		for(int i=0; i<sequence.length; i++){
			CloneInstance instance = set.getInstances().get(i);
			sequence[i] = new TokenSequence(instance);
		}
		return sequence;
	}
	
	protected void filterCommonSet(ArrayList<? extends Multiset> results){
		Iterator<? extends Multiset> iterator = results.iterator();
		while(iterator.hasNext()){
			Multiset obj = iterator.next();
			if(obj instanceof TokenMultiset){
				TokenMultiset set = (TokenMultiset)obj;
				if(set.isCommon()){
					iterator.remove();
				}
			}
		}
	}
}
