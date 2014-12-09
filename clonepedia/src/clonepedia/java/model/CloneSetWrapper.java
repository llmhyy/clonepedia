package clonepedia.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Statement;

import clonepedia.java.ASTComparator;
import clonepedia.java.ASTStatementBoolComparator;
import clonepedia.java.ASTStatementSimilarityComparator;
import clonepedia.java.CloneInformationExtractor;
import clonepedia.java.CompilationUnitPool;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.java.visitor.CloneASTNodeVisitor;
import clonepedia.java.visitor.CloneASTStatementNodeVisitor;
import clonepedia.java.visitor.CloneASTStatementVisitor;
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
	
	private Statement[] commonStatementList;
	public int startStatementContextIndex = 0;
	public int endStatementContextIndex = 0;
	
	private ArrayList<HashSet<ASTNode>> patternNodeSets = new ArrayList<HashSet<ASTNode>>();
	private ArrayList<DiffCounterRelationGroupEmulator> relationGroups = new ArrayList<DiffCounterRelationGroupEmulator>();
	
	private ArrayList<HashSet<Statement>> counterStatementSets = new ArrayList<HashSet<Statement>>();
	private ArrayList<DiffCounterRelationGroupEmulator> relationStatementGroups = new ArrayList<DiffCounterRelationGroupEmulator>();
	
	
	/**
	 * This constructor is able to extract the context information, i.e. program structural information
	 * of a clone set.
	 * 
	 * @param cloneSet
	 * @param pool
	 */
	public CloneSetWrapper(CloneSet cloneSet, CompilationUnitPool pool){
		this(cloneSet, pool, true, null);
	}
	
	/**
	 * Each AST node in {@nodeList} represents exact AST code fragment standing for a clone instance
	 * @param cloneSet
	 * @param pool
	 * @param isLimitToMethod
	 * @param nodeList
	 */
	public CloneSetWrapper(CloneSet cloneSet, CompilationUnitPool pool, boolean isLimitToMethod, ArrayList<ASTNode> nodeList){
		this.pool = pool;
		this.cloneSet = cloneSet;
		int i=0;
		for(CloneInstance ci: cloneSet){
			ASTNode node = (isLimitToMethod)? null : nodeList.get(i++);
			add(new CloneInstanceWrapper(ci, this.pool, isLimitToMethod, node));
		}
	}
	
	public void generateCommonListforCloneSetWrapper() {

		ArrayList<ASTNode>[] lists = this.generateASTListforCloneRegion();

		Object[] commonList = MinerUtil.generateCommonNodeListFromMultiSequence(lists, new ASTComparator());
		
		/*ASTNode[] commonList = MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
				lists[0].toArray(new ASTNode[0]),
				lists[1].toArray(new ASTNode[0]), new ASTComparator()));

		if (lists.length > 2) {
			for (int k = 2; k < lists.length; k++) {
				commonList = MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
						commonList, lists[k].toArray(new ASTNode[0]),
						new ASTComparator()));
			}
		}*/

		this.setCommonASTNodeList(MinerUtilforJava.convertASTNode(commonList));
		//if (commonList.length != 0) getDiffPart(setWrapper);
		 
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<ASTNode>[] generateASTListforCloneRegion() {
		int size = this.getCloneSet().size();
		ArrayList<ASTNode>[] lists = new ArrayList[size];

		int i = 0;
		for (CloneInstanceWrapper instanceWrapper : this) {

			ArrayList<ASTNode> astNodeList = new ArrayList<ASTNode>();
			
			ASTNode cu = instanceWrapper.getMinimumContainingASTNode().getRoot();
			/*if (!(cu instanceof CompilationUnit))
				cu = cu.getParent();*/

			CloneASTNodeVisitor visitor = new CloneASTNodeVisitor(
					instanceWrapper.getStartLine(),
					instanceWrapper.getEndLine(), astNodeList,
					(CompilationUnit) cu);

			instanceWrapper.getMinimumContainingASTNode().accept(visitor);

			//filterComplicatedASTNodeforList(astNodeList);

			instanceWrapper.setAstNodeList(astNodeList.toArray(new ASTNode[0]));

			lists[i++] = astNodeList;
		}

		return lists;
		// System.out.println(astNodeList);
	}
	
	public void generateDiffPart() {

		/**
		 * deal with the instances with extra length in the beginning
		 */
		this.initiailizeEndIndexes();
		this.generatePatternSharingASTNodes(true);
		this.setAllTheStartIndexToEndIndex();
		
		while (!this.isStartContextIndexReachTheEnd()) {

			while (!this.isStartContextIndexReachTheEnd()
					&& this.isAllTheASTNodeFollowingStartIndexMatch()) {
				this.markMatchedNodesInStartIndex();
				this.incrementAllTheStartIndex();
			}
			this.markMatchedNodesInStartIndex();
			
			if (!this.isStartContextIndexReachTheEnd()) {
				this.setAllTheEndIndexAccordingtoStartIndex();

				/**
				 * do comparison
				 */
				this.generatePatternSharingASTNodes(false);

				this.setAllTheStartIndexToEndIndex();
			}
		}
		this.markMatchedNodesInStartIndex();
		
		/**
		 * deal with the instances with extra length in the end
		 */
		this.finalizeEndIndexes();
		this.generatePatternSharingASTNodes(false);
		
		this.collectUncounterRetionalDifferentASTNodes();
		//System.out.println();
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
			instance.endContextIndex = instance.getAstNodeList().length;
		}
	}
	
	public void markMatchedNodesInStartIndex(){
		for(CloneInstanceWrapper instance: this){
			instance.markIndex(instance.startContextIndex);
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

					if (patternNodeSet.size() > 1) {
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

		//ITypeBinding referBinding = MinerUtilforJava.getBinding(referenceNode);
		
		
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
				//ITypeBinding binding = MinerUtilforJava.getBinding(node);
				if(MinerUtilforJava.isConcernedType(referenceNode) && MinerUtilforJava.isConcernedType(node)){
					if(MinerUtilforJava.isASTNodesofTheSameType(referenceNode, node) /*&& 
							MinerUtilforJava.isTheASTNodesBelongToSimilarStatement(node, referenceNode)*/)
						similarNodeIndexCandidates.add(i);
				}
				/*if(binding != null && referBinding != null){
					
					if (binding.getKind() == referBinding.getKind()) {
						if(MinerUtilforJava.isTheBindingsofTheSameType(referBinding, binding)
								&& MinerUtilforJava.isASTNodesofTheSameType(referenceNode, node))
							similarNodeIndexCandidates.add(i);
					}
				}
				*//**
				 * The fact that binding information is null means the referenceNode is
				 * just a landmark-used keyword. 
				 *//*
				else if(referBinding == null && binding == null){
					if(referenceNode.getNodeType() == node.getNodeType()){
						similarNodeIndexCandidates.add(i);
					}
				}*/
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

			String[] referString = MinerUtil.splitCamelString(MinerUtilforJava
					.getConcernedASTNodeName(referenceNode));
			
			int lcs = 0;
			int returnedIndex = candidates.get(0);

			for (Integer index : candidates) {
				ASTNode node = (ASTNode) instance.getAstNodeList()[index];
				
				String[] candidateString = MinerUtil.splitCamelString(MinerUtilforJava
						.getConcernedASTNodeName(node));

				Object[] commonString = MinerUtil.generateCommonNodeList(referString, 
						candidateString, new DefaultComparator());

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
		return true;
		/*boolean isExactSame = true;
		if(this.size() != patternNodeSet.size()){
			return true;
		}
		ASTNode[] nodeList = patternNodeSet.toArray(new ASTNode[0]);
		ASTNode node = nodeList[0];
		for (int i = 1; i < nodeList.length; i++) {
			boolean flag = new ASTComparator().isMatch(node, nodeList[i]);
			isExactSame = isExactSame && flag;

			if (!isExactSame)
				return true;
		}

		return false;*/
	}
	
	/**
	 * The concerned nodes which is neither exact same nodes and counter relational nodes are Unmatched AST nodes.
	 */
	private void collectUncounterRetionalDifferentASTNodes(){
		for(CloneInstanceWrapper instance: this){
			ASTNode[] list = instance.getAstNodeList();
			for(int i=0; i<list.length; i++){
				ASTNode node = list[i];
				if(MinerUtilforJava.isConcernedType(node) && !instance.isMarked(i)){
					instance.getUncounterRelationalDifferenceNodes().add(node);
				}
			}
		}
	}
	
	public String toString(){
		return cloneSet.getId();
	}
	
	public void generateCommonStatementListforCloneSetWrapper() {
		ArrayList<Statement>[] lists = this.generateStatementListforCloneRegion();

		Object[] commonList = MinerUtil.generateCommonNodeListFromMultiSequence(lists, new ASTStatementBoolComparator());
		
		/*Statement[] commonList = (Statement[]) MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
				lists[0].toArray(new Statement[0]),
				lists[1].toArray(new Statement[0]), new ASTStatementComparator()));

		if (lists.length > 2) {
			for (int k = 2; k < lists.length; k++) {
				commonList = (Statement[]) MinerUtilforJava.convertASTNode(MinerUtil.generateCommonNodeList(
						commonList, lists[k].toArray(new Statement[0]),
						new ASTStatementComparator()));
			}
		}*/

		this.setCommonStatementList(MinerUtilforJava.convertStatement(commonList));
	}
	
	private ArrayList<Statement>[] generateStatementListforCloneRegion() {
		int size = this.getCloneSet().size();
		@SuppressWarnings("unchecked")
		ArrayList<Statement>[] lists = new ArrayList[size];

		int i = 0;
		for (CloneInstanceWrapper instanceWrapper : this) {

			ArrayList<Statement> statementList = new ArrayList<Statement>();

			ASTNode cu = instanceWrapper.getMinimumContainingASTNode().getRoot();
			/*if (!(cu instanceof CompilationUnit))
				cu = cu.getParent();*/

			CloneASTStatementVisitor visitor = new CloneASTStatementVisitor(
					instanceWrapper.getStartLine(),
					instanceWrapper.getEndLine(), statementList,
					(CompilationUnit) cu);

			instanceWrapper.getMinimumContainingASTNode().accept(visitor);

			//filterComplicatedASTNodeforList(astNodeList);

			instanceWrapper.setStatementList(statementList.toArray(new Statement[0]));

			lists[i++] = statementList;
		}

		return lists;
	}

	public void generateDiffPartWithinSyntacticBoundary(boolean isStatementFuzzyMatch) throws Exception {
		/**
		 * deal with the instances with extra length in the beginning
		 */
		this.initiailizeEndStatementIndexes();
		this.generatePatternSharingASTNodesInStatements(isStatementFuzzyMatch, true);
		this.setAllTheStartStatementIndexToEndStatementIndex();
		
		while (!this.isStartStatementContextIndexReachTheEnd()) {

			while (!this.isStartStatementContextIndexReachTheEnd()
					&& this.isAllTheASTNodeFollowingStartStatementIndexMatch()) {
				this.markMatchedNodesInStartStatementIndex();
				this.incrementAllTheStartStatementIndex();
			}
			this.markMatchedNodesInStartStatementIndex();
			
			if (!this.isStartStatementContextIndexReachTheEnd()) {
				this.setAllTheEndStatementIndexAccordingtoStartStatementIndex();

				/**
				 * do comparison
				 */
				this.generatePatternSharingASTNodesInStatements(isStatementFuzzyMatch, false);

				this.setAllTheStartStatementIndexToEndStatementIndex();
			}
		}
		this.markMatchedNodesInStartStatementIndex();
		
		/**
		 * deal with the instances with extra length in the end
		 */
		this.finalizeEndStatementIndexes();
		this.generatePatternSharingASTNodesInStatements(isStatementFuzzyMatch, false);
		
		if(isStatementFuzzyMatch){
			collectUncounterRetionalDifferentASTStatements();
			generateDiffPartInCounterRelationalStatements();
		}
	}
	
	private void generateDiffPartInCounterRelationalStatements() {
		for(DiffCounterRelationGroupEmulator relationStatementGroup: relationStatementGroups){
			ArrayList<CloneInstanceWrapper> instanceList = new ArrayList<CloneInstanceWrapper>();
			for(DiffInstanceElementRelationEmulator relation: relationStatementGroup.getElements()){
				CloneInstanceWrapper instance = relation.getInstanceWrapper();
				Statement stat = (Statement) relation.getNode();
				
				ArrayList<ASTNode> list = new ArrayList<ASTNode>();
				CloneASTStatementNodeVisitor visitor = new CloneASTStatementNodeVisitor(0, 0, list, null, stat);
				stat.accept(visitor);
				
				instance.setAstNodeList(list.toArray(new ASTNode[0]));
				instanceList.add(instance);
			}
			
			this.setEmptyForASTNodeListOfOtherInstances(instanceList);
			this.computeCommonSubsequence();
			if(this.commonASTNodeList.length != 0){
				this.generateDiffPart();
			}
			else{
				for(CloneInstanceWrapper instance: this){
					instance.startContextIndex = 0;
					instance.endContextIndex = instance.getAstNodeList().length;
				}
				this.generatePatternSharingASTNodes(true);
			}
			this.resetSequences();
			
		}
		
	}
	
	
	private void setEmptyForASTNodeListOfOtherInstances(ArrayList<CloneInstanceWrapper> referList){
		Iterator<CloneInstanceWrapper> iter = this.iterator();
		while(iter.hasNext()){
			CloneInstanceWrapper instance = iter.next();
			if(!referList.contains(instance)){
				ArrayList<ASTNode> emptyList = new ArrayList<ASTNode>();
				instance.setAstNodeList(emptyList.toArray(new ASTNode[0]));
			}
		}
	}
	
	/*private void addRemovedCloneInstancesBack(ArrayList<CloneInstanceWrapper> resultList){
		if(resultList.size() > 0){
			for(CloneInstanceWrapper instance: resultList){
				this.add(instance);
			}
		}
	}*/

	private void collectUncounterRetionalDifferentASTStatements() {
		for(CloneInstanceWrapper instance: this){
			Statement[] list = instance.getStatementList();
			for(int i=0; i<list.length; i++){
				Statement stat = list[i];
				if(!instance.isStatementMarked(i)){
					instance.getUncounterRelationalDifferenceNodes().add(stat);	
				}
			}
		}
	}

	private void generatePatternSharingASTNodesInStatements(boolean isStatementFuzzyMatch, boolean isForThePrefixCondition) throws Exception{
		if(isStatementFuzzyMatch){
			this.generatePatternSharingASTNodesInStatementsWithFuzzyMatch(isForThePrefixCondition);
		}
		else{
			this.generatePatternSharingASTNodesInStatementsWithNoFuzzyMatch(isForThePrefixCondition);
		}
	}
	
	public boolean isStartStatementContextIndexReachTheEnd() {
		return this.startStatementContextIndex == this.commonStatementList.length - 1;
	}
	
	/**
	 * Find the corresponding ending statement cursor for each clone instance.
	 */
	public void initiailizeEndStatementIndexes(){
		
		Statement stat = this.commonStatementList[this.startStatementContextIndex];
		
		for(CloneInstanceWrapper instance: this){
			Statement instanceStat = instance.getStatementList()[instance.endStatementContextIndex];
			while(!new ASTStatementBoolComparator().isMatch(stat, instanceStat)){
				instance.endStatementContextIndex++;
				instanceStat = instance.getStatementList()[instance.endStatementContextIndex];
			}
		}
	}
	
	public boolean isAllTheASTNodeFollowingStartStatementIndexMatch() {

		for (CloneInstanceWrapper instance : this) {
			if (!new ASTStatementBoolComparator().isMatch(
					this.commonStatementList[this.startStatementContextIndex + 1],
					instance.getStatementList()[instance.startStatementContextIndex + 1]))
				return false;
		}
		return true;
	}
	
	public void incrementAllTheStartStatementIndex() {
		for (CloneInstanceWrapper instance : this) {
			instance.startStatementContextIndex++;
		}
		this.startStatementContextIndex++;
	}
	
	public void setAllTheEndStatementIndexAccordingtoStartStatementIndex() {

		this.endStatementContextIndex = this.startStatementContextIndex + 1;
		Statement stat = this.commonStatementList[this.endStatementContextIndex];
		for (CloneInstanceWrapper instance : this) {
			instance.endStatementContextIndex = instance.startStatementContextIndex + 1;
			Statement instanceStat = instance.getStatementList()[instance.endStatementContextIndex];
			while (!new ASTStatementBoolComparator().isMatch(stat, instanceStat)) {
				instance.endStatementContextIndex++;
				instanceStat = instance.getStatementList()[instance.endStatementContextIndex];
			}
		}
	}
	
	public void setAllTheStartStatementIndexToEndStatementIndex() {
		this.startStatementContextIndex = this.endStatementContextIndex;
		for (CloneInstanceWrapper instance : this) {
			instance.startStatementContextIndex = instance.endStatementContextIndex;
		}
	}
	
	public void finalizeEndStatementIndexes(){
		for(CloneInstanceWrapper instance: this){
			instance.endStatementContextIndex = instance.getStatementList().length;
		}
	}
	
	public void markMatchedNodesInStartStatementIndex(){
		for(CloneInstanceWrapper instance: this){
			instance.markStatementIndex(instance.startStatementContextIndex);
		}
	}
	
	private void generatePatternSharingASTNodesInStatementsWithFuzzyMatch(boolean isForThePrefixCondition) throws Exception{
		for (CloneInstanceWrapper instance : this){
			if(!isForThePrefixCondition){
				instance.compareStatementPointer = instance.startStatementContextIndex + 1;
			}
			else{
				instance.compareStatementPointer = instance.startStatementContextIndex;
			}
		}	

		for (CloneInstanceWrapper instance : this) {
			while (instance.compareStatementPointer < instance.endStatementContextIndex) {

				if (!instance.isStatementMarked(instance.compareStatementPointer)) {
					HashSet<Statement> counterStatementSet = new HashSet<Statement>();
					
					DiffCounterRelationGroupEmulator relationStatementGroup = new DiffCounterRelationGroupEmulator();
					
					
					Statement currentStatement = instance.getStatementList()[instance.compareStatementPointer];
					ArrayList<CloneInstanceWrapper> otherInstances = this.getOtherInstancesInSet(instance);
					/*if(currentNode instanceof PrimitiveType)
						System.out.print("");*/
					
					counterStatementSet.add(currentStatement);
					relationStatementGroup.addRelation(new DiffInstanceElementRelationEmulator(instance, currentStatement));
					
					//instance.markIndex(instance.comparePointer);
					for (CloneInstanceWrapper i : otherInstances) {
						int index = -1;
						index = findTheSimilarStatementIndexInInstance(
								currentStatement, i, isForThePrefixCondition);

						//System.out.print("");
						if (-1 != index) {
							Statement similarStatement = i.getStatementList()[index];
							
							counterStatementSet.add(similarStatement);
							relationStatementGroup.addRelation(new DiffInstanceElementRelationEmulator(i, similarStatement));
							
							i.markStatementIndex(index);
							instance.markStatementIndex(instance.compareStatementPointer);
						}
					}
					
					if(counterStatementSet.size() > 1){
						this.counterStatementSets.add(counterStatementSet);
						this.relationStatementGroups.add(relationStatementGroup);
					}
				}
				instance.compareStatementPointer++;
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
	 * @throws Exception 
	 */
	private int findTheSimilarStatementIndexInInstance(Statement referenceStatement,
			CloneInstanceWrapper instance, boolean isForThePrefixCondition) throws Exception {

		int similarStatementIndex = -1;
		double smallestDistance = 0;
		
		int i;
		if(isForThePrefixCondition)
			i = instance.startStatementContextIndex;
		else 
			i = instance.startStatementContextIndex + 1;
		
		for ( ; i < instance.endStatementContextIndex; i++) {
			if(!instance.isStatementMarked(i)){
				Statement stat = (Statement) instance.getStatementList()[i];
				
				double distance = new ASTStatementSimilarityComparator().computeCost(stat, referenceStatement);
				
				if((stat.getNodeType() == referenceStatement.getNodeType()) && 
						(distance <= Settings.thresholdForStatementDifference)){
					if((smallestDistance > distance) || (similarStatementIndex == -1)){
						similarStatementIndex = i;
						smallestDistance = distance;
					}
				}
			}
		}

		return similarStatementIndex;
	}
	
	private void generatePatternSharingASTNodesInStatementsWithNoFuzzyMatch(boolean isForThePrefixCondition) {
		this.transferStatementsToASTSequences(isForThePrefixCondition);
		this.computeCommonSubsequence();
		if (this.getCommonASTNodeList().length != 0) 
			this.generateDiffPart();
		else{
			for(CloneInstanceWrapper instance: this){
				instance.startContextIndex = 0;
				instance.endContextIndex = instance.getAstNodeList().length;
			}
			this.generatePatternSharingASTNodes(true);
			this.collectUncounterRetionalDifferentASTNodes();
		}
		this.resetSequences();
	}

	private void transferStatementsToASTSequences(boolean isForThePrefixCondition) {
		for(CloneInstanceWrapper instance: this){
			ArrayList<ASTNode> instanceList = new ArrayList<ASTNode>();
			CloneASTStatementNodeVisitor visitor = new CloneASTStatementNodeVisitor(instance.getStartLine(), instance.getEndLine(), 
					instanceList, (CompilationUnit)instance.getMinimumContainingASTNode().getRoot(), null);
			
			int startIndex;
			if(isForThePrefixCondition){
				startIndex = instance.startStatementContextIndex;
			}
			else{
				startIndex = instance.startStatementContextIndex + 1;
			}
			
			for(int i=startIndex; i<instance.endStatementContextIndex; i++){
				Statement stat = instance.getStatementList()[i];
				visitor.setRoot(stat);
				stat.accept(visitor);
			}
			
			instance.setAstNodeList(instanceList.toArray(new ASTNode[0]));
		}
	}

	/**
	 * The method is used to compute the longest common sequence of the AST node lists in
	 * clone instance.
	 * 
	 * Prerequisite: the astNodeList in all the clone instances contained in this clone set
	 * should be specified.
	 */
	private void computeCommonSubsequence() {
		@SuppressWarnings("unchecked")
		ArrayList<ASTNode>[] sequenceList = new ArrayList[this.size()];
		int i=0;
		for(CloneInstanceWrapper instance: this){
			ArrayList<ASTNode> sequence = new ArrayList<ASTNode>();
			for(ASTNode node: instance.getAstNodeList()){
				sequence.add(node);
			}
			sequenceList[i++] = sequence;
		}
		
		Object[] commonList = MinerUtil.generateCommonNodeListFromMultiSequence(sequenceList, new ASTComparator());
 		this.setCommonASTNodeList(MinerUtilforJava.convertASTNode(commonList));
	}

	private void resetSequences() {
		this.startContextIndex = 0;
		this.endContextIndex = 0;
		
		for(CloneInstanceWrapper instance: this){
			instance.startContextIndex = 0;
			instance.endContextIndex = 0;
			instance.comparePointer = 0;
			instance.clearMarkedIndexes();
		}
		
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

	public Statement[] getCommonStatementList() {
		return commonStatementList;
	}

	public void setCommonStatementList(Statement[] commonStatementList) {
		this.commonStatementList = commonStatementList;
	}
	
	public CloneInstanceWrapper findLongestCloneInstance(){
		CloneInstanceWrapper instanceWrapper = null;
		int length = 0;
		for(CloneInstanceWrapper insWrapper: this){
			if(instanceWrapper == null){
				instanceWrapper = insWrapper;
				length = instanceWrapper.getEndLine() - instanceWrapper.getStartLine();
			}
			else{
				int len = instanceWrapper.getEndLine() - instanceWrapper.getStartLine();
				if(len > length){
					instanceWrapper = insWrapper;
					length = len;
				}
			}
		}
		
		return instanceWrapper;
	}
}
