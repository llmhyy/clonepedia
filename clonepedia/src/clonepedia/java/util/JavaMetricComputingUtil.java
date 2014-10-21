package clonepedia.java.util;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.VarType;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;

public class JavaMetricComputingUtil {
	
	public static final double maxSimilarScore = 1;
	
	public static double compareClassContent(Class class1, Class class2){
		
		if(class1.getMethods().size() == 0 && class2.getMethods().size() == 0){
			return maxSimilarScore;
		}
		
		double count = 0;
		
		for(Method method: class1.getMethods()){
			ArrayList<TemplateMethodGroup> tmgList = method.getTemplateGroupList();
			
			label:
			for(TemplateMethodGroup tmg: tmgList){
				for(Method candidateMethod: tmg.getMethods()){
					if(candidateMethod.getOwner().getFullName().equals(class2.getFullName())){
						count++;
						break label;
					}
				}
			}
		}
		
		return 2*count/(class1.getMethods().size() + class2.getMethods().size());
	}
	
	public static double compareMethodName(String methodName1, String methodName2){
		String[] array1 = MinerUtil.splitCamelString(methodName1);
		String[] array2 = MinerUtil.splitCamelString(methodName2);
		
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();
		
		for(int i=0; i<array1.length; i++){
			list1.add(array1[i]);
		}
		
		for(int i=0; i<array2.length; i++){
			list2.add(array2[i]);
		}
		
		try {
			return 1 - MinerUtil.computeAdjustedLevenshteinDistance(list1, list2, new DefaultComparator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static double comparePackageLocation(String package1, String package2){
		String[] packs1 = package1.split("\\.");
		String[] packs2 = package2.split("\\.");
		
		ArrayList<String> packList1 = convert(packs1);
		ArrayList<String> packList2 = convert(packs2);
		
		try {
			return 1 - MinerUtil.computeAdjustedLevenshteinDistance(packList1, packList2, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static double compareClassLocation(Class clazz1, Class clazz2){
		return compareType(clazz1, clazz2);
	}
	
	public static double compareReturnType(VarType varType1, VarType varType2){
		
		/**
		 * This means constructor
		 */
		if(null == varType1 && null == varType2){
			return maxSimilarScore;
		}
		else if(!(varType1 != null && varType2 != null)){
			return 0;
		}
		else if(varType1.getArrayLevel() != varType2.getArrayLevel()){
			return 0;
		}
		
		return compareType(varType1.getPureVarType(), varType2.getPureVarType());
	}
	
	public static double compareParameterList(ArrayList<String> paramList1, ArrayList<String> paramList2){
		if(paramList1.size() == 0 && paramList2.size() == 0){
			return maxSimilarScore;
		}
		else
			return compareStringList(paramList1, paramList2);
	}
	
	public static double compareMethodContent(Method m1, Method m2){
		HashSet<CloneInstance> set1 = m1.getCloneInstances();
		HashSet<CloneInstance> set2 = m2.getCloneInstances();
		
		ArrayList<CloneSet> list = new ArrayList<CloneSet>();
		for(CloneInstance instance1: set1){
			for(CloneInstance instance2: set2){
				if(instance1.getCloneSet().getId().equals(instance2.getCloneSet().getId())){
					list.add(instance1.getCloneSet());
					break;
				}
			}
		}
		
		double commonLength = 0;
		for(CloneSet set: list){
			CloneInstance instance = set.toArray(new CloneInstance[0])[0];
			double len = instance.getEndLine() - instance.getStartLine() + 1;
			if(len > commonLength){
				commonLength = len;
			}
		}
		
		int totalLengh = m1.getLength() + m2.getLength();
		if(totalLengh != 0){
			return 2*commonLength/totalLengh;			
		}
		else{
			return 0;
		}
	}
	
	private static ArrayList<String> convert(String[] strList){
		ArrayList<String> packList = new ArrayList<String>();
		for(int i=0; i<strList.length; i++){
			packList.add(strList[i]);
		}
		
		return packList;
	}
	
	private static double compareType(PureVarType varType1, PureVarType varType2){
		if(varType1.getConcreteType() == varType2.getConcreteType()){
			if(varType1.getConcreteType() != PureVarType.ClassType && 
					varType1.getConcreteType() != PureVarType.InterfaceType){
				return (varType1.getFullName().equals(varType2.getFullName()))? maxSimilarScore : 0;
			}
			else{
				
				if(varType1.getFullName().equals(varType2.getFullName())){
					return maxSimilarScore;
				}
				
				HashSet<ComplexType> parents1 = convertVarTypeToComplexType(varType1).getAllParents();
				HashSet<ComplexType> parents2 = convertVarTypeToComplexType(varType2).getAllParents();

				ArrayList<String> typeStringList1 = convertTypeToString(parents1);
				ArrayList<String> typeStringList2 = convertTypeToString(parents2);
				
				return compareStringList(typeStringList1, typeStringList2);
			}
		}
		else{
			if((varType1 instanceof clonepedia.model.ontology.Class && varType2 instanceof Interface) ||
					(varType1 instanceof Interface && varType2 instanceof clonepedia.model.ontology.Class)){
				
				HashSet<ComplexType> parents1 = convertVarTypeToComplexType(varType1).getAllParents();
				HashSet<ComplexType> parents2 = convertVarTypeToComplexType(varType2).getAllParents();

				if(varType1 instanceof Interface){
					parents1.add((Interface)varType1);
				}
				else{
					parents2.add((Interface)varType2);
				}
				
				ArrayList<String> typeStringList1 = convertTypeToString(parents1);
				ArrayList<String> typeStringList2 = convertTypeToString(parents2);
				
				return compareStringList(typeStringList1, typeStringList2);
			}
		}
		
		return 0;
	}
	
	private static ComplexType convertVarTypeToComplexType(PureVarType varType){
		if(varType instanceof Class)
			return (Class)varType;
		else
			return (Interface)varType;
	}
	
	private static ArrayList<String> convertTypeToString(HashSet<ComplexType> typeList){
		ArrayList<String> strList = new ArrayList<String>();
		for(ComplexType type: typeList){
			strList.add(type.getFullName());
		}
		
		return strList;
	}
	
	/**
	 * Jaccard coefficient
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static double compareStringList(ArrayList<String> list1, ArrayList<String> list2){
		
		if(list1.size() == 0 && list2.size() == 0){
			return 0;
		}
		
		if(null == list1 || null == list2)
			return 0;
		
		double count = 0.0;
		for(String str: list1){
			if(list2.contains(str))
				count++;
		}
		
		return count/(list1.size() + list2.size() - count);
	}
}
