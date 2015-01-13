package ccdemon.test.main;

import java.util.ArrayList;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

import org.eclipse.jdt.core.IJavaProject;

import ccdemon.model.ConfigurationPoint;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class CloneRecover {
	public void trial(CloneSets sets){
		for(clonepedia.model.ontology.CloneSet clonepdiaSet: sets.getCloneList()){
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepdiaSet);
			
			/**
			 * choose the target clone instance to be recovered
			 */
			for(CloneInstance targetInstance: set.getInstances()){
				CloneSet trialSet = CCDemonUtil.adaptMCIDiffModel(clonepdiaSet);
				trialSet.getInstances().remove(targetInstance);
				
				SeqMCIDiff diff = new SeqMCIDiff();
				IJavaProject proj = CCDemonUtil.retrieveWorkingJavaProject();
				ArrayList<SeqMultiset> diffList = diff.diff(trialSet, proj);
				
				/**
				 * choose the source clone instance as the copied instance
				 */
				for(CloneInstance sourceInstance: trialSet.getInstances()){

					ArrayList<ConfigurationPoint> configurationPoints = 
							constructConfigurationPoints(sourceInstance, diffList);
					
					//ArrayList<Token> targetTokenList = copy(targetCloneInstance)
					
					
					//appendConfigurationPointsWithPastedSeq(configurationPoints, targetInstance);
				}
			}
		}
	}
	
	private ArrayList<ConfigurationPoint> constructConfigurationPoints(
			CloneInstance sourceCloneInstance, ArrayList<SeqMultiset> diffList) {
		ArrayList<ConfigurationPoint> cpList = new ArrayList<>();
		for(SeqMultiset multiset: diffList){
			for(TokenSeq tokenSeq: multiset.getSequences()){
				mcidiff.model.CloneInstance ins = tokenSeq.getCloneInstance();
				if(ins.equals(sourceCloneInstance)){
					ConfigurationPoint point = new ConfigurationPoint(tokenSeq, multiset);
					cpList.add(point);
					continue;
				}
			}
		}
		
		return cpList;
	}
}
