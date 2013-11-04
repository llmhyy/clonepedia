package clonepedia.syntactic.util.abstractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import clonepedia.model.ontology.MergeableSimpleOntologicalElement;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.PureVarType;
import clonepedia.model.ontology.VarType;
import clonepedia.syntactic.util.SimilarPairDistanceMatrixComputer;
import clonepedia.syntactic.util.SimilarPairDistanceMatrixComputer.Distance;
import clonepedia.syntactic.util.comparator.OntologicalElementComparator;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;

public abstract class OntologicalElementAbstractor implements Serializable{
	
	//private SimilarPairDistanceMatrixComputer spdmc = new SimilarPairDistanceMatrixComputer();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1167265096383893423L;

	public OntologicalElement abstractElement(OntologicalElement e1, OntologicalElement e2) throws Exception{
		if((e1.getOntologicalType() != e2.getOntologicalType())
				&& !(e1 instanceof MergeableSimpleOntologicalElement && e2 instanceof MergeableSimpleOntologicalElement))
			throw new Exception("cannot abstract different type of ontological elements");
		else
			return abstractOntologicalElement(e1, e2);
	};
	
	protected abstract OntologicalElement abstractOntologicalElement(OntologicalElement e1, OntologicalElement e2) throws Exception;

	protected VarType abstractVarType(VarType type1, VarType type2){
		if (type1 == null || type2 == null)
			return null;

		if (type1.getPureVarType().getConcreteType() != type2.getPureVarType().getConcreteType())
			return null;
		else {
			if (!type1.equals(type2))
				return null;
			else
				return type1;
			
		}
	}
	
	protected String abstractName(String str1, String str2){
		
		if(str1 == null || str2 == null)
			return null;
		
		if(str1.equals(str2))
			return str1;
		
		/*char[] name1 = str1.toCharArray();
		char[] name2 = str2.toCharArray();
		Character[] nameChars1 = MinerUtil.convertChar(name1);
		Character[] nameChars2 = MinerUtil.convertChar(name2);
		
		Object[] commonChars = MinerUtil.generateCommonNodeList(nameChars1, nameChars2, new DefaultComparator());
		Character[] name = MinerUtil.convertCharacter(commonChars);
		if(name.length != 0)
			return MinerUtil.convertCharacterArrayToString(name);
		else
			return null;*/
		
		String[] nameStrings1 = MinerUtil.splitCamelString(str1);
		String[] nameStrings2 = MinerUtil.splitCamelString(str2);
		
		MinerUtil.changeEachHeadOfStringIntoUpperCase(nameStrings1);
		MinerUtil.changeEachHeadOfStringIntoUpperCase(nameStrings2);
		
		Object[] commonStrings = MinerUtil.generateCommonNodeList(nameStrings1, nameStrings2, new DefaultComparator());
		ArrayList<String> commonList = convertToStringList(commonStrings);
		
		if(commonList.size() == 0)
			return "*";
		
		generalizeCommonString(commonList, nameStrings1);
		generalizeCommonString(commonList, nameStrings2);
		
		String abstractedName = ""; 
		for(String s: commonList)
			abstractedName += s;
		return abstractedName;
	}
	
	
	
	private void generalizeCommonString(ArrayList<String> commonList,
			String[] nameStrings) {
		int commonIndex = 0;
		int specialIndex = 0;
		while(commonIndex < commonList.size() && commonList.get(commonIndex).equals("*"))commonIndex++;
		while(specialIndex < nameStrings.length && nameStrings[specialIndex].equals("*"))specialIndex++;
		
		if(commonIndex >= commonList.size() || specialIndex >= nameStrings.length)
			return;
		
		while(specialIndex < nameStrings.length){
			if(nameStrings[specialIndex].equals(commonList.get(commonIndex))){
				
				commonIndex++;
				specialIndex++;
				
				while(commonIndex < commonList.size() && commonList.get(commonIndex).equals("*"))commonIndex++;
				while(specialIndex < nameStrings.length && nameStrings[specialIndex].equals("*"))specialIndex++;
				
				if(commonIndex == commonList.size() && specialIndex < nameStrings.length){
					if(!commonList.get(commonIndex-1).equals("*"))
						commonList.add("*");
					return;
				}
			}
			else{
				if(commonIndex == 0 || !commonList.get(commonIndex-1).equals("*")){
					insertAstroidAndMoveOtherElementsBehind(commonList, commonIndex);
					commonIndex++;
				}
				
				specialIndex++;
				while(specialIndex < nameStrings.length && nameStrings[specialIndex].equals("*"))specialIndex++;
			}
			
		}
		
		//System.out.print("");
	}

	private void insertAstroidAndMoveOtherElementsBehind(
			ArrayList<String> commonList, int commonIndex) {
		commonList.add("*");
		String[] strList = commonList.toArray(new String[0]);
		for(int i=strList.length-2; i>=commonIndex; i--)
			strList[i+1] = strList[i];
		strList[commonIndex] = "*";
		
		commonList.clear();
		for(String s: strList)
			commonList.add(s);
	}

	private ArrayList<String> convertToStringList(Object[] objList){
		ArrayList<String> stringList = new ArrayList<String>();
		for(Object obj: objList){
			String str = obj.toString();
			if(str.length() == 1)
				str = "*";
			stringList.add(str);
		}
		return stringList;
	}
	
	protected ArrayList<? extends OntologicalElement> getCommonAbstractedElements(OntologicalElement[] es1, OntologicalElement[] es2, OntologicalElementAbstractor abstractor, OntologicalElementComparator comparator) throws Exception{
		
		ArrayList<OntologicalElement> commonAbstractedFields = new ArrayList<OntologicalElement>(); 
		
		double[][] distanceMatrix = SimilarPairDistanceMatrixComputer.initializeDistanceMatrix(es1, es2, comparator);
		
		while(null != SimilarPairDistanceMatrixComputer.findLeastValidEntryInMatrix(distanceMatrix)){
			Distance smallestDistance = SimilarPairDistanceMatrixComputer.findSmallestDistanceInMatrix(distanceMatrix);
			
			OntologicalElement abElement =  abstractor.abstractElement(es1[smallestDistance.i], es2[smallestDistance.j]);
			commonAbstractedFields.add(abElement);
			
			SimilarPairDistanceMatrixComputer.nullifyIrreleventEntriesInMatrix(distanceMatrix, smallestDistance);
		}
		
		return commonAbstractedFields;
	}
	
	protected void attachSupportingElements(ProgrammingElement abstractElement, ProgrammingElement e1,
			ProgrammingElement e2){
		if(e1.isMerged())
			abstractElement.getSupportingElements().addAll(e1.getSupportingElements());
		else
			if(!abstractElement.getSupportingElements().contains(e1))
				abstractElement.getSupportingElements().add(e1);
		
		if(e2.isMerged())
			abstractElement.getSupportingElements().addAll(e2.getSupportingElements());
		else
			if(!abstractElement.getSupportingElements().contains(e2))
				abstractElement.getSupportingElements().add(e2);
	}
}
