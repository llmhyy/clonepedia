package clonepedia.java.util;

import java.util.ArrayList;
import java.util.HashSet;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.VarType;
import clonepedia.util.MinerUtil;

public class JavaMetricComputingUtil {
	
	public static final double maxScore = 1;
	
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
			return maxScore;
		}
		else if(!(varType1 != null && varType2 != null)){
			return 0;
		}
		
		return compareType(varType1, varType2);
	}
	
	public static double compareParameterList(ArrayList<String> paramList1, ArrayList<String> paramList2){
		if(paramList1.size() == 0 && paramList2.size() == 0){
			return maxScore;
		}
		else
			return compareList(paramList1, paramList2);
	}
	
	private static ArrayList<String> convert(String[] strList){
		ArrayList<String> packList = new ArrayList<String>();
		for(int i=0; i<strList.length; i++){
			packList.add(strList[i]);
		}
		
		return packList;
	}
	
	private static double compareType(VarType varType1, VarType varType2){
		if(varType1.getConcreteType() == varType2.getConcreteType()){
			if(varType1.getConcreteType() != VarType.ClassType && 
					varType1.getConcreteType() != VarType.InterfaceType){
				return (varType1.getFullName().equals(varType2.getFullName()))? maxScore : 0;
			}
			else{
				
				if(varType1.getFullName().equals(varType2.getFullName())){
					return maxScore;
				}
				
				HashSet<ComplexType> parents1 = convertVarTypeToComplexType(varType1).getParents();
				HashSet<ComplexType> parents2 = convertVarTypeToComplexType(varType2).getParents();

				ArrayList<String> typeStringList1 = convertTypeToString(parents1);
				ArrayList<String> typeStringList2 = convertTypeToString(parents2);
				
				return compareList(typeStringList1, typeStringList2);
			}
		}
		else{
			if((varType1 instanceof clonepedia.model.ontology.Class && varType2 instanceof Interface) ||
					(varType1 instanceof Interface && varType2 instanceof clonepedia.model.ontology.Class)){
				
				HashSet<ComplexType> parents1 = convertVarTypeToComplexType(varType1).getParents();
				HashSet<ComplexType> parents2 = convertVarTypeToComplexType(varType2).getParents();

				if(varType1 instanceof Interface){
					parents1.add((Interface)varType1);
				}
				else{
					parents2.add((Interface)varType2);
				}
				
				ArrayList<String> typeStringList1 = convertTypeToString(parents1);
				ArrayList<String> typeStringList2 = convertTypeToString(parents2);
				
				return compareList(typeStringList1, typeStringList2);
			}
		}
		
		return 0;
	}
	
	private static ComplexType convertVarTypeToComplexType(VarType varType){
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
	public static double compareList(ArrayList<String> list1, ArrayList<String> list2){
		
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
