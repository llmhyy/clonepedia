package clonepedia.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeLiteral;


import clonepedia.clonefilepraser.CloneFileParser;
import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.java.ASTComparator;
import clonepedia.java.model.*;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.java.visitor.CloneASTNodeVisitor;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.RegionalOwner;
import clonepedia.model.ontology.Variable;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;

public class CloneInformationExtractor {

	private OntologicalDataFetcher fetcher = new OntologicalDataFetcher();
	private Project project;
	private CloneFileParser cloneFileParser;
	private ArrayList<CloneSetWrapper> setWrapperList = new ArrayList<CloneSetWrapper>();
	
	private double threshold = 0.7d;

	public CloneInformationExtractor(CloneFileParser cloneFileParser, Project project) {
		this.cloneFileParser = cloneFileParser;
		this.project = project;
	}
	
	private void storeInformation(){
		
		for(CloneSetWrapper setWrapper: setWrapperList){
			CloneSet set = setWrapper.getCloneSet();
			try{
				set.setProject(project);
				
				for(CloneInstanceWrapper instanceWrapper: setWrapper){
					CloneInstance instance = instanceWrapper.getCloneInstance();
					Method residingMethod = MinerUtilforJava.getMethodfromASTNode(instanceWrapper.getMethodDeclaration(), project);
					instance.setResidingMethod(residingMethod);
					fetcher.storeCloneInstanceWithDependency(instance);
				}
				
				for(ASTNode node: setWrapper.getCommonASTNodeList()){
					if(MinerUtilforJava.isConcernedType(node)){
						ProgrammingElement element = transferASTNodesToProgrammingElement(node, set);
						if(null != element) 
							fetcher.storeCommonRelation(set, element);
						System.out.print("");
					}
				}
				
				for(DiffCounterRelationGroupEmulator group: setWrapper.getRelationGroups()){
					String groupId = group.getId();
					for(DiffInstanceElementRelationEmulator relation: group.getRelations()){
						ASTNode node = relation.getNode();
						if(MinerUtilforJava.isConcernedType(node) || node.getNodeType() == ASTNode.PRIMITIVE_TYPE){
							ProgrammingElement element = transferASTNodesToProgrammingElement(node, set);
							CloneInstanceWrapper instanceWrapper = relation.getInstanceWrapper();
							CloneInstance instance = instanceWrapper.getCloneInstance();
							Method residingMethod = MinerUtilforJava.getMethodfromASTNode(instanceWrapper.getMethodDeclaration(), project);
							instance.setResidingMethod(residingMethod);
							if(null != element)
								fetcher.storeDiffRelation(groupId, instance, element);
							//System.out.print("");
						}
					}
				}
			}
			catch(Exception e){
				System.out.println(setWrapper.getId());
				e.printStackTrace();
			}
			
		}
	}
	
	

	private ProgrammingElement transferASTNodesToProgrammingElement(ASTNode node, RegionalOwner owner) throws Exception{
		
		if(node.getNodeType() == ASTNode.PRIMITIVE_TYPE){
			PrimitiveType primitiveType = (PrimitiveType)node;
			PrimiType primiType = new PrimiType(primitiveType.toString());
			return primiType;
		}
		
		if(MinerUtilforJava.isConcernedType(node)){
			
			if(node.getNodeType() == ASTNode.STRING_LITERAL ||
					node.getNodeType() == ASTNode.NUMBER_LITERAL ||
					node.getNodeType() == ASTNode.CHARACTER_LITERAL ||
					node.getNodeType() == ASTNode.BOOLEAN_LITERAL){
				
				String typeName = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
				typeName = typeName.substring(0, typeName.length()-7);
				Constant constant = new Constant(MinerUtilforJava.getConcernedASTNodeName(node), new PrimiType(typeName), false);
				return constant;
			}
			else if(node.getNodeType() == ASTNode.TYPE_LITERAL){
				TypeLiteral type = (TypeLiteral)node;
				if(type.getType().resolveBinding().isClass() || type.getType().resolveBinding().isInterface()){
					return MinerUtilforJava.transferTypeToComplexType(type.getType(), project, (CompilationUnit)node.getRoot());	
				}
				else return null;
			}
			else {
				SimpleName name = (SimpleName)node;
				if(name.resolveBinding().getKind() == IBinding.TYPE){
					ITypeBinding typeBinding = (ITypeBinding)name.resolveBinding();
					if(typeBinding.isClass() || typeBinding.isInterface()){
						return MinerUtilforJava.transferTypeToComplexType(typeBinding, project, (CompilationUnit)node.getRoot());	
					}
					else return null;
				}
				else if(name.resolveBinding().getKind() == IBinding.METHOD){
					IMethodBinding methodBinding = (IMethodBinding) name.resolveBinding();
					Method m = MinerUtilforJava.getMethodfromBinding(methodBinding, project, (CompilationUnit)node.getRoot());
					Method method = fetcher.getTheExistingMethodorCreateOne(m);
					return method;
				}
				else if(name.resolveBinding().getKind() == IBinding.VARIABLE){
					IVariableBinding variableBinding = (IVariableBinding) name.resolveBinding();
					if(variableBinding.isField()){
						Field f = MinerUtilforJava.getFieldfromBinding(variableBinding, project);
						if(f.getOwnerType() != null){
							Field field = fetcher.getTheExistingFieldorCreateOne(f);
							return field;
						}
						else{
							Variable v = MinerUtilforJava.getVariablefromBinding(name, variableBinding, project);
							v.setOwner(owner);
							
							return v;
						}
					}
					else {
						Variable v = MinerUtilforJava.getVariablefromBinding(name, variableBinding, project);
						v.setOwner(owner);
						
						return v;
					}
				}
				return null;
			}
		}
		else return null;
	}
	

	public void extract() {
		
		CompilationUnitPool pool = new CompilationUnitPool();
		
		CloneSets cloneSets = cloneFileParser.getCloneSets(false, "");
		for (CloneSet cloneSet : cloneSets.getCloneList()) {
			try{
				/**
				 * The following code is for debugging
				 */
				/*if(cloneSet.getId().equals("240")){
					System.out.print("");
				}
				else
					continue;*/
				
				
				cloneSet.setProject(project);
				CloneSetWrapper setWrapper = new CloneSetWrapper(cloneSet, pool);

				generateCommonListforCloneSetWrapper(setWrapper);
				if (setWrapper.getCommonASTNodeList().length != 0) 
					getDiffPart(setWrapper);

				setWrapperList.add(setWrapper);

				System.out.print("");
			}
			catch(Exception e){
				System.out.println(cloneSet.getId());
				e.printStackTrace();
			}
		}
		
		storeInformation();
	}

	private void generateCommonListforCloneSetWrapper(
			CloneSetWrapper setWrapper) {

		ArrayList<ASTNode>[] lists = generateASTListforCloneRegion(setWrapper);

		ASTNode[] commonList = MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
				lists[0].toArray(new ASTNode[0]),
				lists[1].toArray(new ASTNode[0]), new ASTComparator()));

		if (lists.length > 2) {
			for (int k = 2; k < lists.length; k++) {
				commonList = MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
						commonList, lists[k].toArray(new ASTNode[0]),
						new ASTComparator()));
			}
		}

		setWrapper.setCommonASTNodeList(commonList);

		
		//if (commonList.length != 0) getDiffPart(setWrapper);
		 
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ASTNode>[] generateASTListforCloneRegion(
			CloneSetWrapper setWrapper) {
		int size = setWrapper.getCloneSet().size();
		ArrayList<ASTNode>[] lists = new ArrayList[size];

		int i = 0;
		for (CloneInstanceWrapper instanceWrapper : setWrapper) {

			ArrayList<ASTNode> astNodeList = new ArrayList<ASTNode>();

			ASTNode cu = instanceWrapper.getMethodDeclaration().getRoot();
			/*if (!(cu instanceof CompilationUnit))
				cu = cu.getParent();*/

			CloneASTNodeVisitor visitor = new CloneASTNodeVisitor(
					instanceWrapper.getStartLine(),
					instanceWrapper.getEndLine(), astNodeList,
					(CompilationUnit) cu);

			instanceWrapper.getMethodDeclaration().accept(visitor);

			filterComplicatedASTNodeforList(astNodeList);

			instanceWrapper.setAstNodeList(astNodeList.toArray(new ASTNode[0]));

			lists[i++] = astNodeList;
		}

		return lists;
		// System.out.println(astNodeList);
	}

	private void filterComplicatedASTNodeforList(ArrayList<ASTNode> list) {
		Iterator<ASTNode> iter = list.iterator();
		while (iter.hasNext()) {
			ASTNode node = (ASTNode) iter.next();
			
			if (!MinerUtilforJava.isConcernedType(node) && !MinerUtilforJava.isBenchMarkType(node))
				iter.remove();
		}
	}

	private void getDiffPart(CloneSetWrapper setWrapper) {

		//deal with the instances with extra length in the beginning
		initiailizeEndIndexes(setWrapper);
		generatePatternSharingASTNodes(setWrapper, true);
		setAllTheStartIndexToEndIndex(setWrapper);
		
		while (!startContextReachTheEnd(setWrapper)) {

			while (!startContextReachTheEnd(setWrapper)
					&& allTheASTNodeFollowingStartIndexMatch(setWrapper)) {
				incrementAllTheStartIndex(setWrapper);
			}

			if (!startContextReachTheEnd(setWrapper)) {
				setAllTheEndIndexAccordingtoStartIndex(setWrapper);

				// do comparison
				generatePatternSharingASTNodes(setWrapper, false);

				setAllTheStartIndexToEndIndex(setWrapper);
			}

		}

		// deal with the instances with extra length in the end
		finalizeEndIndexes(setWrapper);
		generatePatternSharingASTNodes(setWrapper, false);

		//System.out.println();
	}

	private void generatePatternSharingASTNodes(CloneSetWrapper s, boolean isForThePrefixCondition) {

		for (CloneInstanceWrapper instance : s)
			if(!isForThePrefixCondition)
				instance.comparePointer = instance.startContextIndex + 1;
			else
				instance.comparePointer = instance.startContextIndex;

		for (CloneInstanceWrapper instance : s) {
			while (instance.comparePointer < instance.endContextIndex) {

				if (!instance.isMarked(instance.comparePointer)) {
					HashSet<ASTNode> patternNodeSet = new HashSet<ASTNode>();
					//String groupId = "cg" + UUID.randomUUID();
					DiffCounterRelationGroupEmulator relationGroup = new DiffCounterRelationGroupEmulator();
					
					
					ArrayList<CloneInstanceWrapper> otherInstances = getOtherInstancesInSet(
							instance, s);
					ASTNode currentNode = instance.getAstNodeList()[instance.comparePointer];
					if(currentNode instanceof PrimitiveType)
						System.out.print("");
					
					patternNodeSet.add(currentNode);
					relationGroup.addRelation(new DiffInstanceElementRelationEmulator(instance, currentNode));
					
					instance.markIndex(instance.comparePointer);
					for (CloneInstanceWrapper i : otherInstances) {
						int index = findTheSimilarNodeIndexInInstance(
								currentNode, i, isForThePrefixCondition);

						//System.out.print("");
						if (-1 != index) {
							ASTNode similarNode = i.getAstNodeList()[index];
							
							patternNodeSet.add(similarNode);
							relationGroup.addRelation(new DiffInstanceElementRelationEmulator(i, similarNode));
							
							i.markIndex(index);
							instance.markIndex(instance.comparePointer);
						}
					}

					if (theNodesAreNotExactSameAndManyEnough(patternNodeSet,
							s.size())) {
						s.getPatternNodeSets().add(patternNodeSet);
						relationGroup.setId("cr" + UUID.randomUUID());
						s.addRelationGroup(relationGroup);
					}
				}
				instance.comparePointer++;
			}
		}
	}

	/**
	 * Return -1 if the similar node is not found or the inputed referenceNode
	 * is a keyword.
	 * 
	 * @param referenceNode
	 * @param instance
	 * @return
	 */
	private int findTheSimilarNodeIndexInInstance(ASTNode referenceNode,
			CloneInstanceWrapper instance, boolean isForThePrefixCondition) {

		ITypeBinding referBinding = MinerUtilforJava.getBinding(referenceNode);
		
		
		/*if(referBinding == null)
			return -1;*/

		ArrayList<Integer> similarNodeIndexCandidates = new ArrayList<Integer>();

		int i;
		if(isForThePrefixCondition)
			i = instance.startContextIndex;
		else 
			i = instance.startContextIndex + 1;
		
		for ( ; i < instance.endContextIndex; i++) {
			ASTNode node = (ASTNode) instance.getAstNodeList()[i];
			if(!instance.isMarked(i)){
				ITypeBinding binding = MinerUtilforJava.getBinding(node);
				
				if(binding != null){
					/*binding.getJavaElement();
					binding.getName();
					binding.*/
					if (binding.getKind() == referBinding.getKind()) {
						if(MinerUtilforJava.isTheBindingsofTheSameType(referBinding, binding)
								&& MinerUtilforJava.isASTNodesofTheSameType(referenceNode, node))
							similarNodeIndexCandidates.add(i);
					}
				}
				/**
				 * The fact that binding information is null means the referenceNode is
				 * just a landmark-used keyword. 
				 */
				else if(referBinding == null && binding == null){
					if(referenceNode.getNodeType() == node.getNodeType()){
						similarNodeIndexCandidates.add(i);
					}
				}
			}
		}

		int similarNodeIndex = getTheIndexWithLCSfromCandidates(
				similarNodeIndexCandidates, instance, referenceNode);

		return similarNodeIndex;
	}

	private int getTheIndexWithLCSfromCandidates(ArrayList<Integer> candidates,
			CloneInstanceWrapper instance, ASTNode referenceNode) {
		if (candidates.size() == 0)
			return -1;
		if (candidates.size() == 1) {
			return candidates.get(0);
		} else {
			
			Character[] referString = MinerUtil.convertChar(MinerUtilforJava
					.getConcernedASTNodeName(referenceNode).toCharArray());

			int lcs = 0;
			int returnedIndex = candidates.get(0);

			for (Integer index : candidates) {
				ASTNode node = (ASTNode) instance.getAstNodeList()[index];
				
				Character[] candidateString = MinerUtil.convertChar(MinerUtilforJava
						.getConcernedASTNodeName(node).toCharArray());

				Character[] commonString = MinerUtil.convertCharacter(MinerUtil
						.generateCommonNodeList(referString, candidateString,
								new DefaultComparator()));

				if (commonString.length > lcs) {
					lcs = commonString.length;
					returnedIndex = index;
				}
			}

			return returnedIndex;
		}
	}

	private boolean theNodesAreNotExactSameAndManyEnough(
			HashSet<ASTNode> patternNodeSet, int totalSize) {

		int thresholdSize = 0;
		if (totalSize == 2)
			thresholdSize = 2;
		else
			thresholdSize = (int) (totalSize * threshold);

		if (patternNodeSet.size() < thresholdSize)
			return false;

		boolean isExactSame = true;
		ASTNode[] nodeList = patternNodeSet.toArray(new ASTNode[0]);
		ASTNode node = nodeList[0];
		for (int i = 1; i < nodeList.length; i++) {
			boolean flag = new ASTComparator().isMatch(node, nodeList[i]);
			isExactSame = isExactSame && flag;

			if (!isExactSame)
				return true;
		}

		return false;
	}

	private ArrayList<CloneInstanceWrapper> getOtherInstancesInSet(
			CloneInstanceWrapper instance, CloneSetWrapper s) {
		ArrayList<CloneInstanceWrapper> otherInstanceList = new ArrayList<CloneInstanceWrapper>();
		for (CloneInstanceWrapper i : s) {
			if (!i.equals(instance)) {
				otherInstanceList.add(i);
			}
		}
		return otherInstanceList;
	}

	private boolean startContextReachTheEnd(CloneSetWrapper setWrapper) {
		return setWrapper.startContextIndex == setWrapper.getCommonASTNodeList().length - 1;
	}

	private boolean allTheASTNodeFollowingStartIndexMatch(CloneSetWrapper setWrapper) {

		for (CloneInstanceWrapper instance : setWrapper) {
			if (!new ASTComparator().isMatch(
					setWrapper.getCommonASTNodeList()[setWrapper.startContextIndex + 1],
					instance.getAstNodeList()[instance.startContextIndex + 1]))
				return false;
		}
		return true;
	}

	private void incrementAllTheStartIndex(CloneSetWrapper setWrapper) {
		for (CloneInstanceWrapper instance : setWrapper) {
			instance.startContextIndex++;
		}
		setWrapper.startContextIndex++;
	}

	private void setAllTheEndIndexAccordingtoStartIndex(CloneSetWrapper setWrapper) {

		setWrapper.endContextIndex = setWrapper.startContextIndex + 1;
		ASTNode node = setWrapper.getCommonASTNodeList()[setWrapper.endContextIndex];
		for (CloneInstanceWrapper instance : setWrapper) {
			instance.endContextIndex = instance.startContextIndex + 1;
			ASTNode instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			while (!new ASTComparator().isMatch(node, instanceNode)) {
				instance.endContextIndex++;
				instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			}
		}
	}
	
	private void initiailizeEndIndexes(CloneSetWrapper setWrapper){
		
		ASTNode node = setWrapper.getCommonASTNodeList()[setWrapper.startContextIndex];
		
		for(CloneInstanceWrapper instance: setWrapper){
			ASTNode instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			while(!new ASTComparator().isMatch(node, instanceNode)){
				instance.endContextIndex++;
				instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			}
		}
	}
	
	private void finalizeEndIndexes(CloneSetWrapper setWrapper){
		for(CloneInstanceWrapper instance: setWrapper){
			instance.endContextIndex = instance.getAstNodeList().length - 1;
		}
	}

	private void setAllTheStartIndexToEndIndex(CloneSetWrapper setWrapper) {
		setWrapper.startContextIndex = setWrapper.endContextIndex;
		for (CloneInstanceWrapper instance : setWrapper) {
			instance.startContextIndex = instance.endContextIndex;
		}
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
