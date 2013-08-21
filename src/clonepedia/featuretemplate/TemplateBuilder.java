package clonepedia.featuretemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import clonepedia.model.cluster.IClusterable;
import clonepedia.model.cluster.NormalCluster;
import clonepedia.model.cluster.hierarchy.HierarchyClusterAlgorithm;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.template.TFGList;
import clonepedia.model.template.Template;
import clonepedia.model.template.TemplateFeatureGroup;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class TemplateBuilder {
	private TFGList tfgList;
	
	private ArrayList<Class> abstractClassList = new ArrayList<Class>();
	
	public TemplateBuilder(TFGList tfgList){
		this.tfgList = tfgList;
	}
	
	public Template buildTemplate(){
		
		init();
		
		abstractClasses();
		
		return null;
	}
	
	/**
	 * list all the classes declaring methods in TMGs and cluster them to make sure
	 * that only similar classes can take part in abstraction.
	 */
	private void init(){
		HashSet<Class> declaringClassSet = new HashSet<Class>();
		for(TemplateFeatureGroup tfg: tfgList){
			for(TemplateMethodGroup tmg: tfg.getTemplateMethodGroupList()){
				for(Method method: tmg.getMethods()){
					ComplexType owner = method.getOwner();
					if(owner.isClass()){
						Class declaringClass = (Class)owner;
						declaringClassSet.add(declaringClass);
					}
				}
			}
		}
		
		Class[] declaringClassArray = declaringClassSet.toArray(new Class[0]);
		HierarchyClusterAlgorithm algorithm = new HierarchyClusterAlgorithm(declaringClassArray, 
				/*0.7*/Settings.thresholdDistanceForDeclaringClassClustering, HierarchyClusterAlgorithm.CompleteLinkage);
		
		ArrayList<NormalCluster> clusterList;
		try {
			clusterList = algorithm.doClustering();
			for(NormalCluster cluster: clusterList){
				Class abstractedClass = new Class("", null, "");
				if(cluster.size() > 1){
					abstractedClass.setMerge(true);
					for(IClusterable clusterable: cluster){
						Class declaringClass = (Class)clusterable;
						
						abstractedClass.setProject(declaringClass.getProject());
						abstractedClass.getSupportingElements().add(declaringClass);
					}					
				}
				else{
					abstractedClass = (Class)cluster.get(0);
				}
				this.abstractClassList.add(abstractedClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	private void abstractClasses(){
		for(Class abstractedClass: this.abstractClassList){
			if(abstractedClass.isMerged()){
				abstractedClass.setFullName(mergeName(abstractedClass));
				mergeSuperType(abstractedClass);
				//mergeInterfaces(abstractedClass);
				mergeMethods(abstractedClass);				
			}
		}
	}

	private void mergeMethods(Class abstractedClass) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * compute the weight of each possible super type and find the largest one
	 * @param abstractedClass
	 */
	private void mergeSuperType(Class abstractedClass) {
		
		
		HashMap<ComplexType, Double> superTypeFrequencyMap = new HashMap<ComplexType, Double>();
		
		for(ProgrammingElement element: abstractedClass.getSupportingElements()){
			Class supportingClass = (Class)element;
			double weight = 1.0;
			
			assignWeightsForParents(weight, superTypeFrequencyMap, supportingClass);
		}
		
		/**
		 * find the class with largest weight over threshold
		 * find the interfaces over threshold
		 */
		double largestWeight = 0;
		Class candidateSuperClass = null;
		for(ComplexType type: superTypeFrequencyMap.keySet()){
			Double weight = superTypeFrequencyMap.get(type);
			if(type instanceof Class && weight > largestWeight){
				largestWeight = weight;
				candidateSuperClass = (Class)type;
			}
			else if(type instanceof Interface && weight/abstractedClass.getSupportingElements().size() >= Settings.thresholdSimilarityForDecidingAbstractedInterface){
				abstractedClass.addInterface((Interface)type);
			}
		}
		
		if(largestWeight/abstractedClass.getSupportingElements().size() >= Settings.thresholdSimilarityForDecidingAbstractedClass){
			abstractedClass.setSuperClass(candidateSuperClass);			
		}
	}
	
	private void assignWeightsForParents(double weight, HashMap<ComplexType, Double> map, ComplexType type){
		if(type.getDirectParents().size() != 0){
			for(ComplexType superType: type.getDirectParents()){
				Double frequency = map.get(superType);
				if(frequency == null){
					frequency = new Double(weight);
					map.put(superType, frequency);
				}
				else{
					frequency += weight;
					map.put(superType, frequency);
				}
				
				assignWeightsForParents(weight/2, map, superType);
			}
		}
	}
	
	private String mergeName(ProgrammingElement element){
		ArrayList<ProgrammingElement> elementList = element.getSupportingElements();
		
		ArrayList<String> packageStringList = new ArrayList<String>();
		ArrayList<String> nameStringList = new ArrayList<String>();
		for(ProgrammingElement ele: elementList){
			if(element instanceof Class){
				String classString =  ((Class)ele).getFullName();
				String packageString = classString.substring(0, classString.lastIndexOf("."));
				packageStringList.add(packageString);				
			}
			nameStringList.add(ele.getSimpleElementName());
		}
		
		String packageString = "";
		if(packageStringList.size() > 0){
			packageString = MinerUtil.generateAbstractString(packageStringList, MinerUtil.DotSplitting);
		}
		
		String abstractName = MinerUtil.generateAbstractString(nameStringList, MinerUtil.CamelSplitting);
		
		return packageString + "." + abstractName;
	}
	
	
}
