package ccdemon.test.main;

import java.util.ArrayList;
import java.util.Iterator;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;
import ccdemon.model.ReferrableCloneSet;
import ccdemon.test.model.CPWrapper;
import ccdemon.test.model.CPWrapperList;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class CloneRecoverer {
	
	class CollectedData{
		
	}
	
	public void trial(CloneSets sets){
		for(clonepedia.model.ontology.CloneSet clonepdiaSet: sets.getCloneList()){
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepdiaSet);
			
			SeqMCIDiff diff = new SeqMCIDiff();
			IJavaProject proj = CCDemonUtil.retrieveWorkingJavaProject();
			ArrayList<SeqMultiset> diffList = diff.diff(set, proj);
			
			/**
			 * choose the target clone instance to be recovered
			 */
			for(CloneInstance targetInstance: set.getInstances()){
				
				ArrayList<SeqMultiset> typeIDiffs = findTypeIDiff(diffList, targetInstance);
				ArrayList<SeqMultiset> typeIIDiffs = findTypeIIDiff(diffList, targetInstance);
				
				/**
				 * choose the source clone instance as the copied instance
				 */
				for(CloneInstance sourceInstance: set.getInstances()){
					if(sourceInstance.equals(targetInstance)){
						continue;
					}

					CPWrapperList wrapperList = 
							constructConfigurationPoints(sourceInstance, targetInstance, typeIIDiffs);
					ArrayList<ConfigurationPoint> pointList = wrapperList.getConfigurationPoints();
					
					ConfigurationPointSet cps = identifyConfigurationPointSet(proj, 
							pointList, targetInstance, sourceInstance, set);
					
					CollectedData data = simulate(cps, wrapperList);
				}
			}
		}
	}
	
	private CollectedData simulate(ConfigurationPointSet cps,
			CPWrapperList wrapperList) {
		// TODO Auto-generated method stub
		return null;
	}

	private ConfigurationPointSet identifyConfigurationPointSet(IJavaProject proj, ArrayList<ConfigurationPoint> pointList,
			CloneInstance targetInstance, CloneInstance sourceInstance, CloneSet set){
		CompilationUnit cu = findCompilationUnitOfTargetInstance(proj, targetInstance);
		int startPosition = cu.getPosition(targetInstance.getStartLine(), 0);
		
		ConfigurationPointSet cpSet = new ConfigurationPointSet(pointList, set, cu, startPosition);
		CloneSet newSet = cloneCloneSet(set, targetInstance);
		ReferrableCloneSet rcs = new ReferrableCloneSet(newSet, sourceInstance);
		ArrayList<ReferrableCloneSet> rcsList = new ArrayList<>();
		rcsList.add(rcs);
		
		cpSet.prepareForInstallation(rcsList);
		
		return cpSet;
	}
	
	private CloneSet cloneCloneSet(CloneSet set, CloneInstance instance){
		CloneSet newSet = new CloneSet(set.getId());
		for(CloneInstance ins: set.getInstances()){
			if(!ins.equals(instance)){
				newSet.addInstance(ins);
			}
		}
		
		return newSet;
	}
	
	private CompilationUnit findCompilationUnitOfTargetInstance(IJavaProject proj, CloneInstance targetInstance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Given a target instance, the diffs can be divided into two categories: 
	 * Type I: The token sequence for target instance is different from the token sequences for other instances, 
	 * in this case, those token sequences in other instances are exactly the same.
	 * Type II: The token sequences for other instances are different with each other.
	 * 
	 * By differentiating these differences, I am able to identify the correct candidate for each configuration
	 * point.
	 * 
	 * @param diffList
	 * @return
	 */
	private ArrayList<SeqMultiset> findTypeIDiff(ArrayList<SeqMultiset> diffList, CloneInstance targetInstance){
		ArrayList<SeqMultiset> typeOneDiffList = new ArrayList<>();
		//TODO
		
		return typeOneDiffList;
	}
	
	/**
	 * See the doc for {@code findTypeIDiff()} method.
	 * @param diffList
	 * @param targetInstance
	 * @return
	 */
	private ArrayList<SeqMultiset> findTypeIIDiff(ArrayList<SeqMultiset> diffList, CloneInstance targetInstance){
		ArrayList<SeqMultiset> typeTwoDiffList = new ArrayList<>();
		//TODO
		
		return typeTwoDiffList;
	}

	/**
	 * Create a configuration point for each diff, and take the value in source instance as the copied
	 * token sequence in configuration point. In addition, the value in target instance is removed so that 
	 * it will not be considered in rule generation and association mining.
	 * 
	 * @param sourceInstance
	 * @param targetInstance
	 * @param diffList
	 * @return
	 */
	private CPWrapperList constructConfigurationPoints(
			CloneInstance sourceInstance, CloneInstance targetInstance, ArrayList<SeqMultiset> diffList) {
		ArrayList<CPWrapper> cpList = new ArrayList<>();
		for(SeqMultiset multiset: diffList){
			TokenSeq copiedSeq = null;
			TokenSeq correctSeq = null;
			
			Iterator<TokenSeq> iterator = multiset.getSequences().iterator();
			while(iterator.hasNext()){
				TokenSeq tokenSeq = iterator.next();
				
				mcidiff.model.CloneInstance ins = tokenSeq.getCloneInstance();
				
				if(ins.equals(sourceInstance)){
					copiedSeq = tokenSeq;
				}
				else if(ins.equals(targetInstance)){
					correctSeq = tokenSeq;
					iterator.remove();
				}
			}
			
			ConfigurationPoint point = new ConfigurationPoint(copiedSeq, multiset);
			//TODO check whether it is correct
			point.setModifiedTokenSeq(copiedSeq);
			CPWrapper wrapper = new CPWrapper(point, correctSeq);
			cpList.add(wrapper);
		}
		
		return new CPWrapperList(cpList);
	}
}
