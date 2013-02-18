package clonepedia.syntactic.util;

import java.io.Serializable;
import java.util.ArrayList;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.MergeableSimpleConcreteElement;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.ontology.VariableUseType;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.abstractor.PathAbstractor;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;

public class SyntacticUtil implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834940993843368824L;
	
	/**
	 * The result of the method should be in a range from 0 to 1.
	 * if maxNumber = minNumber, the result should be sum/maxNumber
	 * or sum/minNumber;
	 * if maxNumber >> minNumber, the result should be near 1;
	 * in normal case, the result should be greater than sum/minNumber;
	 * we can think the elements in two groups are cakes, if the distance of
	 * two cakes are 0.8, then their differential parts will be 1.6. That's
	 * why smallestDistanceSum should be multiplied with 2.
	 * 
	 * @author linyun (email: llmhyy@gmail.com)
	 * @param maxNumber
	 * @param minNumber
	 * @param smallestDistanceSum
	 * @return
	 */
	public static double calculateAverageDistanceOfTwoGroups(double maxNumber, double minNumber, double smallestDistanceSum){
		return (maxNumber - minNumber + 2*smallestDistanceSum)/(maxNumber + minNumber);
	}

	/*public static PathSequence transferPathToSequence(Path path) throws Exception {
		PathSequence seq = new PathSequence();
		OntologicalElement[] elements = path.toArray(new OntologicalElement[0]);
		OntologicalElement pre = elements[0];
		seq.add(pre);
		
		for(int i=1; i<elements.length; i++){
			OntologicalElement element = elements[i];
			OntologicalRelationType relationType = identifyOntologicalRelation(pre, element);
			seq.add(relationType);
			seq.add(element);
			
			pre = element;
		}
		return seq;
	}*/
	
	public static OntologicalRelationType identifyOntologicalRelation(OntologicalElement subject, OntologicalElement object) throws Exception{
		//clone set
		if(subject instanceof CloneSet 
				&& object instanceof CloneInstance)
			return OntologicalRelationType.contain;
		else if(subject instanceof CloneSet 
				&& object instanceof Method)
			return OntologicalRelationType.call;
		else if(subject instanceof CloneSet 
				&& object instanceof Field)
			return OntologicalRelationType.access;
		else if(subject instanceof CloneSet 
				&& object instanceof Variable){
			if(((Variable)object).getUseType().equals(VariableUseType.DEFINE))
				return OntologicalRelationType.define;
			else
				return OntologicalRelationType.refer;
		}
		else if(subject instanceof CloneSet 
				&& object instanceof Constant)
			return OntologicalRelationType.useConstant;
		else if(subject instanceof CloneSet 
				&& object instanceof Class)
			return OntologicalRelationType.useClass;
		else if(subject instanceof CloneSet 
				&& object instanceof Interface)
			return OntologicalRelationType.useInterface;
		//clone instance
		else if(subject instanceof CloneInstance
				&& object instanceof Method){
			CloneInstance instance = (CloneInstance)subject;
			Method method = (Method)object;
			if(instance.getResidingMethod().getMethodId().equals(method.getMethodId()))
				return OntologicalRelationType.resideIn;
			else
				return OntologicalRelationType.call;
		}
		else if(subject instanceof CloneInstance
				&& object instanceof Field){
			return OntologicalRelationType.access;
		}
		else if(subject instanceof CloneInstance
				&& object instanceof Variable){
			if(((Variable)object).getUseType().equals(VariableUseType.DEFINE))
				return OntologicalRelationType.define;
			else
				return OntologicalRelationType.refer;
		}
		else if(subject instanceof CloneInstance
				&& object instanceof Variable){
			if(((Variable)object).getUseType().equals(VariableUseType.DEFINE))
				return OntologicalRelationType.define;
			else
				return OntologicalRelationType.refer;
		}
		else if(subject instanceof CloneInstance
				&& object instanceof Constant)
			return OntologicalRelationType.useConstant;
		else if(subject instanceof CloneInstance
				&& object instanceof Class)
			return OntologicalRelationType.useClass;
		else if(subject instanceof CloneInstance
				&& object instanceof Interface)
			return OntologicalRelationType.useInterface;
		else if(subject instanceof CloneInstance
				&& object instanceof PrimiType){
			return OntologicalRelationType.use;
		}
		//class
		else if(subject instanceof Class 
				&& object instanceof Class){
			Class subjectClass = (Class)subject;
			Class objectClass = (Class)object;
			if(null != subjectClass.getSuperClass()
					&& subjectClass.getSuperClass().getId().equals(objectClass.getId()))
				return OntologicalRelationType.extendClass;
			else if(null != subjectClass.getOuterClass()
					&& subjectClass.getOuterClass().getId().equals(objectClass.getId()))
				return OntologicalRelationType.isInnerClassOf;
			else
				throw new Exception("unrecognized subject " + subject.toString() + " and object " + object.toString() + " pair");
		}
		else if(subject instanceof Class
				&& object instanceof Interface)
			return OntologicalRelationType.implement;
		//interface
		else if(subject instanceof Interface
				&& object instanceof Interface)
			return OntologicalRelationType.extendInterface;
		//method
		else if(subject instanceof Method
				&& object instanceof ComplexType){
			Method method = (Method)subject;			
			ComplexType type = (ComplexType)object;
			if(method.getOwner().getId().equals(type.getId()))
				return OntologicalRelationType.declaredIn;
			else{
				VarType returnType = method.getReturnType();
				if(returnType instanceof ComplexType){
					if(((ComplexType)returnType).getId().equals(type.getId()))
						return OntologicalRelationType.hasType;
				}
				/*else
					throw new Exception("unsuitable return type "+ returnType.toString() + " in ontological model");*/
								
			}
			
			for(Variable parameter: method.getParameters()){
				VarType paramType = parameter.getVariableType();
				if(paramType instanceof ComplexType
						&& ((ComplexType)paramType).getId().equals(type.getId()))
					return OntologicalRelationType.hasParameterType;
			}
			
			throw new Exception("unrecognized subject " + subject.toString() + " and object " + object.toString() + " pair");
		}
		//field
		else if(subject instanceof Field
				&& object instanceof ComplexType){
			Field field = (Field)subject;
			ComplexType type = (ComplexType)object;
			
			if(field.getOwnerType().getId().equals(type.getId()))
				return OntologicalRelationType.declaredIn;
			else{
				VarType fType = field.getFieldType();
				if(fType instanceof ComplexType){
					if(((ComplexType)fType).getId().equals(type.getId()))
						return OntologicalRelationType.hasType;
					else
						throw new Exception("unrecognized subject " + subject.toString() + " and object " + object.toString() + " pair");
				}
				else
					throw new Exception("unsuitable field type "+ fType.toString() + " in ontological model");
			}
		}
		//variable
		else if(subject instanceof Variable
				&& object instanceof ComplexType){
			return OntologicalRelationType.hasType;			
		}
		
		else
			throw new Exception("unrecognized subject " + subject.toString() + " and object " + object.toString() + " pair");
	}
	
	public static void collectNodesAndEdges(Path path, ArrayList<OntologicalElement> nodes, ArrayList<OntologicalRelationType> edges){
		OntologicalElement[] list = path.toArray(new OntologicalElement[0]);
		OntologicalElement pre = list[0];
		if(!nodes.contains(pre))
			nodes.add(pre);
		
		for(int i=1; i<list.length; i++){
			try{
				OntologicalRelationType edge = identifyOntologicalRelation(pre, list[i]);
				
				if(!nodes.contains(list[i]))
					nodes.add(list[i]);
				if(!edges.contains(edge))
					edges.add(edge);
				
				pre = list[i];
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * compute how many ontological nodes do vectors exactly share.
	 * @param offset
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static int getCommonNodeNumber(int offset, double[] vector1, double[] vector2){
		int count = 0;
		for(int i=0; i<offset; i++){
			if(vector1[i] != 0 && vector2[i] != 0
					&& vector1[i] == vector2[i] && vector1[i]>=0.5)
				count++;
		}
		return count;
	}
	
	/**
	 * The size of the two vectors must be same.
	 * @param vector1
	 * @param vector2
	 * @throws Exception 
	 */
	public static double computeCosineValue(double[] vector1, double[] vector2) throws Exception{
		if(vector1.length != vector2.length)
			throw new Exception("Two vectors are not of the same size.");
		
		int vectorSize = vector1.length;
		
		double sum = 0d;
		for(int i=0; i<vectorSize; i++){
			sum += vector1[i]*vector2[i];
		}
		
		return sum/(computeSquareRoot(vector1)*computeSquareRoot(vector2));
	}
	
	/**
	 * The size of the two vectors must be same.
	 * @param vector1
	 * @param vector2
	 * @throws Exception 
	 */
	public static double computeEuclideanDistance(double[] vector1, double[] vector2) throws Exception{
		if(vector1.length != vector2.length)
			throw new Exception("Two vectors are not of the same size.");
		
		int vectorSize = vector1.length;
		
		double sum = 0d;
		for(int i=0; i<vectorSize; i++){
			sum += Math.pow(vector1[i]-vector2[i], 2) ;
		}
		
		return Math.sqrt(sum);
	}
	
	private static double computeSquareRoot(double[] vector){
		double sum = 0;
		for(int i=0; i<vector.length; i++){
			sum += vector[i]*vector[i];
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Location pattern has a looser requirement that at most one ontological element should
	 * be more than "*"; however, usage pattern has a tighter requirement that all the ontological
	 * element must be more than "*". Otherwise, it is still too general for the usage pattern.
	 * @param isForLocationPattern
	 * @return
	 */
	public static boolean isOverGeneral(String clonePatternStyle, PathSequence pathSeq){
		
		boolean isOverGen = false;
		if(clonePatternStyle.equals(PathSequence.LOCATION) || clonePatternStyle.equals(PathSequence.DIFF_USAGE))
			isOverGen = true;
		else if(clonePatternStyle.equals(PathSequence.COMMON_USAGE))
			isOverGen = false;
		
		for(int i=2; i< pathSeq.size(); i++){
			Object obj = pathSeq.get(i);
			if(obj instanceof OntologicalElement){
				String name = ((OntologicalElement)obj).getSimpleElementName();
				if(clonePatternStyle.equals(PathSequence.LOCATION) || clonePatternStyle.equals(PathSequence.DIFF_USAGE)){
					if(!name.equals("*"))
						return false;
				}
				else if(clonePatternStyle.equals(PathSequence.COMMON_USAGE))
					if(name.equals("*"))
						return true;
			}
		}
		
		return isOverGen;
	}
	
	/**
	 * Temporary heuristic method to make sure a better result. For example, in this case, a method having a void return
	 * type will not be consistent with a field.
	 * @return
	 */
	public static boolean isConsistentInUsage(PathSequence pathSeq1, PathSequence pathSeq2){
		
		OntologicalElement e1 = (OntologicalElement)pathSeq1.get(2);
		OntologicalElement e2 = (OntologicalElement)pathSeq2.get(2);
		
		//System.out.println(getVarConcreteTypeOfOntologicalElement(e1) + " " + getVarConcreteTypeOfOntologicalElement(e2));
		
		VarType vType1 = getVarConcreteTypeOfOntologicalElement(e1);
		VarType vType2 = getVarConcreteTypeOfOntologicalElement(e2);
		
		if(vType1 == null && vType1 == null)
			return true;
		else if(vType1 == null || vType2 == null)
			return false;
		else{
			return isCompatibleType(vType1, vType2);
		}
	}
	
	public static boolean isCompatibleType(VarType vType1, VarType vType2){
		if(vType1.getConcreteType() == vType2.getConcreteType()){
			if((vType1 instanceof Class) || (vType1 instanceof Interface)){
				ComplexType ct1 = (ComplexType)vType1;
				ComplexType ct2 = (ComplexType)vType2;
				
				String[] nameStrings1 = MinerUtil.splitCamelString(ct1.getSimpleName());
				String[] nameStrings2 = MinerUtil.splitCamelString(ct2.getSimpleName());
				
				MinerUtil.changeEachHeadOfStringIntoUpperCase(nameStrings1);
				MinerUtil.changeEachHeadOfStringIntoUpperCase(nameStrings2);
				
				Object[] commonStrings = MinerUtil.generateCommonNodeList(nameStrings1, nameStrings2, new DefaultComparator());
				
				if(commonStrings.length > 0 || ct1.isCompatibleWith(ct2))
					return true;
				else
					return false;
			}
			else
				return true;
		}
		return false;
	}
	
	public static VarType getVarConcreteTypeOfOntologicalElement(OntologicalElement element){
		
		if(element instanceof Method){
			return ((Method)element).getReturnType();
			//return (null == vType)? VarType.UnknownType : vType.getConcreteType();
		}
		else if(element instanceof Field){
			return ((Field)element).getFieldType();
			//return (null == vType)? VarType.UnknownType : vType.getConcreteType();
		}
		else if(element instanceof Variable){
			return ((Variable)element).getVariableType();
			//return (null == vType)? VarType.UnknownType : vType.getConcreteType();
		}
		else if(element instanceof MergeableSimpleConcreteElement){
			return ((MergeableSimpleConcreteElement)element).getVarType();
			//return (null == vType)? VarType.UnknownType : vType.getConcreteType();
		}
		else if(element instanceof Interface){
			return (VarType)element;
		}
		else if(element instanceof Class){
			return (VarType)element;
		}
		
		return null;
	}
}
