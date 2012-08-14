package clonepedia.syntactic.util.comparator;

/*import cloneminer.model.Interface;
import cloneminer.model.Class;*/
import java.io.Serializable;
import java.util.HashMap;

import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.VarType;
import clonepedia.syntactic.util.DistanceComputationPool;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;

public abstract class OntologicalElementComparator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7805123020406857610L;
	private static int savedComparingTime = 0;

	public double compute(OntologicalElement e1, OntologicalElement e2) throws Exception{
		if(e1.getOntologicalType() != e2.getOntologicalType())
			return 1.0;
		else{
			OntologicalComparingPair pair = new OntologicalComparingPair(e1, e2);
			Double distance;
			if(pair.isComplexTypePair()){
				//Double distance = null;
				//long start = System.currentTimeMillis();
				distance = DistanceComputationPool.ontoDisMap.get(pair);
				//long end = System.currentTimeMillis();
				//long time = end - start;
				//System.out.println("Time used in hash get:" + time);
				if(null == distance){
					//start = System.currentTimeMillis();
					distance = computeDistanceOfConcreteType(e1, e2);
					//end = System.currentTimeMillis();
					//time = end - start;
					/*if(time > 20){
						System.out.println("Time used in computeDistanceOfConcreteType:" + time);
						start = System.currentTimeMillis();
						distance = computeDistanceOfConcreteType(e1, e2);
						end = System.currentTimeMillis();
						System.out.println("Time used in computeDistanceOfConcreteType:" + time);
						//distance = computeDistanceOfConcreteType(e1, e2);
					}*/
					
					DistanceComputationPool.ontoDisMap.put(pair, distance);
				}
				else
					savedComparingTime++;
			}
			else{
				distance = computeDistanceOfConcreteType(e1, e2);
			}
			return distance;
		}
	}
	
	protected abstract double computeDistanceOfConcreteType(OntologicalElement e1, OntologicalElement e2) throws Exception;
	
	protected double computeStringDistance(String str1, String str2){
		
		if(str1 == null || str2 ==null)return 1;
		
		if(str1.length() == 0 || str2.length() == 0) return 1;
		
		char[] name1 = str1.toCharArray();
		char[] name2 = str2.toCharArray();
		
		Character[] nameChars1 = MinerUtil.convertChar(name1);
		Character[] nameChars2 = MinerUtil.convertChar(name2);
		
		Object[] commonChars = MinerUtil.generateCommonNodeList(nameChars1, nameChars2, new DefaultComparator());
		return 1-(commonChars.length/((name1.length + name2.length)/2.0d));
	}
	
	protected double computeVarTypeDistance(VarType type1, VarType type2) throws Exception{
		
		if(type1 == null || type2 == null)return 1;
		
		if(type1.getConcreteType() != type2.getConcreteType())
			return 1;
		else if(type1.getConcreteType() != VarType.ClassType && type1.getConcreteType() != VarType.InterfaceType){
			if(type1.equals(type2))
				return 0;	
		}
		else{
			if(!type1.equals(type2))
				return 1;	
			else
				return 0;
			/*if(type1.getConcreteType() == VarType.ClassType){
				return new ClassComparator().compute((Class)type1, (Class)type2);
			}
			else if(type1.getConcreteType() == VarType.InterfaceType){
				return new InterfaceCompatrator().compute((Interface)type1, (Interface)type2);
			}*/
		}
		
		return 1;
	}
}
