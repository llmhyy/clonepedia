package clonepedia.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;

import clonepedia.java.ASTComparator;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.CompilationUnitPool;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.util.DefaultComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class CloneSetWrapper extends HashSet<CloneInstanceWrapper>{
	
	private static final long serialVersionUID = 4698936071832101817L;

	private CloneSet cloneSet;
	private CompilationUnitPool pool;
	
	private ASTNode[] commonASTNodeList;
	public int startContextIndex = 0;
	public int endContextIndex = 0;
	
	private ArrayList<HashSet<ASTNode>> patternNodeSets = new ArrayList<HashSet<ASTNode>>();
	private ArrayList<DiffCounterRelationGroupEmulator> relationGroups 
		= new ArrayList<DiffCounterRelationGroupEmulator>();
	
	/**
	 * This constructor is able to extract the context information, i.e. program structural information
	 * of a clone set.
	 * 
	 * @param cloneSet
	 * @param pool
	 */
	public CloneSetWrapper(CloneSet cloneSet, CompilationUnitPool pool){
		this.pool = pool;
		this.cloneSet = cloneSet;
		for(CloneInstance ci: cloneSet){
			add(new CloneInstanceWrapper(ci, this.pool));
		}
	}
	
	public boolean isStartContextIndexReachTheEnd() {
		return this.startContextIndex == this.getCommonASTNodeList().length - 1;
	}
	
	/**
	 * This method is part of a diff algorithm to detect counter-relational differences of a clone set. 
	 * Please investigate the specification in 
	 * {@link CloneInformationExtractor#extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper)}<p>
	 * 
	 * This method is used in the beginning of diff algorithm. It will be the case where the fragments
	 * of clone instances are different at the starting parts. Therefore, this method is used to locate 
	 * where are clone instances' AST nodes corresponded to the AST node the common starting index 
	 * cursor pointing to.    
	 * 
	 */
	public void initiailizeEndIndexes(){
		
		ASTNode node = this.getCommonASTNodeList()[this.startContextIndex];
		
		for(CloneInstanceWrapper instance: this){
			ASTNode instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			while(!new ASTComparator().isMatch(node, instanceNode)){
				instance.endContextIndex++;
				instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			}
		}
	}
	
	/**
	 * This method is part of a diff algorithm to detect counter-relational differences of a clone set. 
	 * Please investigate the specification in 
	 * {@link CloneInformationExtractor#extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper)}<p>
	 * 
	 * Given a common starting cursor in runtime(let's call the AST node just following the pointed 
	 * common node as n0), this method is used to judge whether the nodes just following the pointed 
	 * to by different instance starting cursors match with n0.
	 * 
	 * @return
	 */
	public boolean isAllTheASTNodeFollowingStartIndexMatch() {

		for (CloneInstanceWrapper instance : this) {
			if (!new ASTComparator().isMatch(
					this.getCommonASTNodeList()[this.startContextIndex + 1],
					instance.getAstNodeList()[instance.startContextIndex + 1]))
				return false;
		}
		return true;
	}
	
	public void incrementAllTheStartIndex() {
		for (CloneInstanceWrapper instance : this) {
			instance.startContextIndex++;
		}
		this.startContextIndex++;
	}
	
	/**
	 * This method is part of a diff algorithm to detect counter-relational differences of a clone set. 
	 * Please investigate the specification in 
	 * {@link CloneInformationExtractor#extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper)}<p>
	 * 
	 * Before this method is called, all the instance starting index cursors should have been corresponded
	 * to the common starting index. After this method is called, the common ending index cursor will 
	 * just follow the common starting index while all the instance ending index cursor will point to the
	 * nodes corresponding to the node pointed by the common ending index.
	 * 
	 */
	public void setAllTheEndIndexAccordingtoStartIndex() {

		this.endContextIndex = this.startContextIndex + 1;
		ASTNode node = this.getCommonASTNodeList()[this.endContextIndex];
		for (CloneInstanceWrapper instance : this) {
			instance.endContextIndex = instance.startContextIndex + 1;
			ASTNode instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			while (!new ASTComparator().isMatch(node, instanceNode)) {
				instance.endContextIndex++;
				instanceNode = instance.getAstNodeList()[instance.endContextIndex];
			}
		}
	}
	
	/**
	 * This method is part of a diff algorithm to detect counter-relational differences of a clone set. 
	 * Please investigate the specification in 
	 * {@link CloneInformationExtractor#extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper)}<p>
	 * 
	 * After finishing a diff comparison in a range, the new common/instance starting index cursor is set 
	 * as the same location as the common/instance ending index.
	 */
	public void setAllTheStartIndexToEndIndex() {
		this.startContextIndex = this.endContextIndex;
		for (CloneInstanceWrapper instance : this) {
			instance.startContextIndex = instance.endContextIndex;
		}
	}
	
	/**
	 * This method is part of a diff algorithm to detect counter-relational differences of a clone set. 
	 * Please investigate the specification in 
	 * {@link CloneInformationExtractor#extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper)}<p>
	 * 
	 * After the common index cursors reach the end, the remaining parts of the code fragments of clone
	 * instances should still be compared. This method is used to set the ending index cursors of all the
	 * clone instances to the last AST node so that the last step of comparison could be conducted.
	 */
	public void finalizeEndIndexes(){
		for(CloneInstanceWrapper instance: this){
			instance.endContextIndex = instance.getAstNodeList().length - 1;
		}
	}
	
	/**
	 * Given a clone instance <parameter>instance</parameter>, this method return the clone instances of
	 * the clone set except <parameter>instance</parameter>.
	 * @param instance
	 * @param s
	 * @return
	 */
	public ArrayList<CloneInstanceWrapper> getOtherInstancesInSet(
			CloneInstanceWrapper instance) {
		ArrayList<CloneInstanceWrapper> otherInstanceList = new ArrayList<CloneInstanceWrapper>();
		for (CloneInstanceWrapper i : this) {
			if (!i.equals(instance)) {
				otherInstanceList.add(i);
			}
		}
		return otherInstanceList;
	}
	
	/**
	 * This method is to find the diff ast nodes with counter relation in a specific region(between 
	 * starting node and ending node). The handling approach will be a little bit different for the
	 * region precede the common region and other regions. (Please refer to {@link #initiailizeEndIndexes()}}
	 * method in this class). Therefore, there will be a parameter <parameter>isForThePrefixCondition</parameter>
	 * to specify the cases.
	 * 
	 * @param isForThePrefixCondition
	 */
	public void generatePatternSharingASTNodes(boolean isForThePrefixCondition) {

		for (CloneInstanceWrapper instance : this){
			if(!isForThePrefixCondition){
				instance.comparePointer = instance.startContextIndex + 1;
			}
			else{
				instance.comparePointer = instance.startContextIndex;
			}
		}	

		for (CloneInstanceWrapper instance : this) {
			while (instance.comparePointer < instance.endContextIndex) {

				if (!instance.isMarked(instance.comparePointer)) {
					HashSet<ASTNode> patternNodeSet = new HashSet<ASTNode>();
					//String groupId = "cg" + UUID.randomUUID();
					DiffCounterRelationGroupEmulator relationGroup = new DiffCounterRelationGroupEmulator();
					
					
					ArrayList<CloneInstanceWrapper> otherInstances = this.getOtherInstancesInSet(instance);
					ASTNode currentNode = instance.getAstNodeList()[instance.comparePointer];
					/*if(currentNode instanceof PrimitiveType)
						System.out.print("");*/
					
					patternNodeSet.add(currentNode);
					relationGroup.addRelation(new DiffInstanceElementRelationEmulator(instance, currentNode));
					
					//instance.markIndex(instance.comparePointer);
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
							this.size())) {
						this.getPatternNodeSets().add(patternNodeSet);
						relationGroup.setId("cr" + UUID.randomUUID());
						this.addRelationGroup(relationGroup);
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
			if(!instance.isMarked(i)){
				ASTNode node = (ASTNode) instance.getAstNodeList()[i];
				ITypeBinding binding = MinerUtilforJava.getBinding(node);
				
				if(binding != null){
					
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

		/**
		 * To judge whether the nodes are many enough
		 */
		int thresholdSize = 0;
		if (totalSize == 2)
			thresholdSize = 2;
		else
			thresholdSize = (int) (totalSize * Settings.diffComparisonThreshold);

		if (patternNodeSet.size() < thresholdSize)
			return false;

		/**
		 * To judge whether the nodes are exactly the same
		 */
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
	
	public String toString(){
		return cloneSet.getId();
	}
	
	public String getId(){
		return this.cloneSet.getId();
	}
	
	public CloneSet getCloneSet() {
		return cloneSet;
	}

	public void setCloneSet(CloneSet cloneSet) {
		this.cloneSet = cloneSet;
	}

	public ASTNode[] getCommonASTNodeList() {
		return commonASTNodeList;
	}

	public void setCommonASTNodeList(ASTNode[] commonASTNodeList) {
		this.commonASTNodeList = commonASTNodeList;
	}

	public ArrayList<DiffCounterRelationGroupEmulator> getRelationGroups() {
		return relationGroups;
	}

	public void setRelationGroups(
			ArrayList<DiffCounterRelationGroupEmulator> relationGroups) {
		this.relationGroups = relationGroups;
	}
	
	public void addRelationGroup(DiffCounterRelationGroupEmulator relationGroup){
		this.relationGroups.add(relationGroup);
	}

	public ArrayList<HashSet<ASTNode>> getPatternNodeSets() {
		return patternNodeSets;
	}

	public void setPatternNodeSets(ArrayList<HashSet<ASTNode>> patternNodeSets) {
		this.patternNodeSets = patternNodeSets;
	}
	
	public void addPatternNodeSet(HashSet<ASTNode> patternNodeSet){
		this.patternNodeSets.add(patternNodeSet);
	}
}
