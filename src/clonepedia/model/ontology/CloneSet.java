package clonepedia.model.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import clonepedia.db.DBOperator;
import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.ISemanticClusterable;
import clonepedia.model.semantic.Topic;
import clonepedia.model.semantic.Word;
import clonepedia.model.semantic.WordBag;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.syntactic.Path;
import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.model.syntactic.PathSequence;
import clonepedia.syntactic.util.abstractor.PathAbstractor;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class CloneSet extends HashSet<CloneInstance> implements RegionalOwner, ISemanticClusterable {
	private CloneSets sets;
	//private PathComparator pathComparator;
	private String cloneSetId;
	private Project project;
	//private double thresholdToFormPattern = 0.4d;
	//private double thresholdDistance = 2.5d;
	//private double distanceDeductRate = 0.9d;

	private static final long serialVersionUID = -5687012109505287778L;

	private ArrayList<Method> callingMethods = new ArrayList<Method>();
	private ArrayList<Field> accessingFields = new ArrayList<Field>();
	private ArrayList<Variable> definingVariables = new ArrayList<Variable>();
	private ArrayList<Variable> referingVariables = new ArrayList<Variable>();
	private ArrayList<Constant> usingConstants = new ArrayList<Constant>();
	private ArrayList<Class> usingClasses = new ArrayList<Class>();
	private ArrayList<Interface> usingInterfaces = new ArrayList<Interface>();

	private ICluster residingCluster;
	
	// private ArrayList<ProgrammingElement> programmingElements = new
	// ArrayList<ProgrammingElement>();
	private ArrayList<CounterRelationGroup> counterRelationGroups = new ArrayList<CounterRelationGroup>();
	private ArrayList<PathPatternGroup> locationPatterns = new ArrayList<PathPatternGroup>();
	private ArrayList<PathPatternGroup> diffUsagePatterns = new ArrayList<PathPatternGroup>();
	private ArrayList<PathPatternGroup> commonUsagePatterns = new ArrayList<PathPatternGroup>();
	
	private WordBag commonWords = new WordBag();
	private WordBag diffWords = new WordBag();
	
	private ArrayList<ClonePatternGroup> patternLabels = new ArrayList<ClonePatternGroup>();
	private ArrayList<Topic> topicLabels = new ArrayList<Topic>();
	
	public void addPatternLabel(ClonePatternGroup patternLabel){
		if(!this.patternLabels.contains(patternLabel))
			this.patternLabels.add(patternLabel);
	}
	
	public void addTopicLabel(Topic topicLabel){
		if(!this.topicLabels.contains(topicLabel))
			this.topicLabels.add(topicLabel);
	}
	
	public ArrayList<ClonePatternGroup> getPatternLabels() {
		return patternLabels;
	}

	public ArrayList<Topic> getTopicLabels() {
		return topicLabels;
	}

	private class InstanceDerivedPaths extends ArrayList<Path>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1425565285586143173L;
		
	}
	
	/*public PathComparator getPathComparator() {
		return this.sets.getPathComparator();
	}*/

	public boolean equals(Object obj){
		if(obj instanceof CloneSet)
			return getId().equals(((CloneSet)obj).getId());
		return false;
	}
	
	public int hashCode(){
		return getId().hashCode();
	}
	
	public String toString(){
		return this.cloneSetId;
	}
	
	private void splitNameAndAddThemIntoWordBag(String name, WordBag wordBag){
		String[] words = MinerUtil.splitCamelString(name);
		for(String word: words){
			if(words.length > 2)
				wordBag.add(new Word(word));
		}
	}
	
	private void addWordsIntoWordBag(ArrayList<PathPatternGroup> ppgs, WordBag wordBag){
		for(PathPatternGroup ppg: ppgs)
			for(Path path: ppg)
				for(OntologicalElement element: path){
					String name = element.getSimpleElementName();
					splitNameAndAddThemIntoWordBag(name, wordBag);
				}
	}
	
	public WordBag getRelatedWords(){
		WordBag wordBag = new WordBag();
		
		addWordsIntoWordBag(this.locationPatterns, wordBag);
		addWordsIntoWordBag(this.diffUsagePatterns, wordBag);
		//addWordsIntoWordBag(this.commonUsagePatterns, wordBag);
		
		for(Method method: callingMethods){
			splitNameAndAddThemIntoWordBag(method.getSimpleElementName(), wordBag);
		}
		for(Field field: accessingFields){
			splitNameAndAddThemIntoWordBag(field.getSimpleElementName(), wordBag);
		}
		for(Variable variable: definingVariables){
			splitNameAndAddThemIntoWordBag(variable.getSimpleElementName(), wordBag);
		}
		for(Variable variable: referingVariables){
			splitNameAndAddThemIntoWordBag(variable.getSimpleElementName(), wordBag);
		}
		for(Constant constant: usingConstants){
			splitNameAndAddThemIntoWordBag(constant.getSimpleElementName(), wordBag);
		}
		for(Class clas: usingClasses){
			splitNameAndAddThemIntoWordBag(clas.getSimpleElementName(), wordBag);
		}
		for(Interface interf: usingInterfaces){
			splitNameAndAddThemIntoWordBag(interf.getSimpleElementName(), wordBag);
		}
		/*for(CounterRelationGroup group: getCounterRelationGroups())
			for(InstanceElementRelation relation: group.getRelationList()){
				String name = relation.getElement().getSimpleElementName();
				String[] words = MinerUtil.splitCamelString(name);
				for(String word: words){
					if(words.length > 2)
						wordBag.add(new Word(word));
				}
					
			}*/
		
		/*DBOperator op = new DBOperator();
		wordBag.addAll(op.getWordsOfCloneSet(getId()));*/
			
		return wordBag;	
	}
	
	public ArrayList<PathPatternGroup> getAllClusterablePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.locationPatterns)
			ppgs.add(ppg);
		for(PathPatternGroup ppg: this.diffUsagePatterns)
			ppgs.add(ppg);
		for(PathPatternGroup ppg: this.commonUsagePatterns)
			ppgs.add(ppg);
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableLocationPatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.locationPatterns){
			if(ppg.isClusterable())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.commonUsagePatterns)
			if(ppg.isClusterable())
				ppgs.add(ppg);
		for(PathPatternGroup ppg: this.diffUsagePatterns)
			if(ppg.isClusterable())
				ppgs.add(ppg);
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableCommonUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.commonUsagePatterns){
			if(ppg.isClusterable())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableDiffUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.diffUsagePatterns){
			if(ppg.isClusterable())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableMethodFieldVariableCommonUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.commonUsagePatterns){
			if(ppg.isClusterable() && ppg.isMethodFieldVariableUseagePattern())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableComplexTypeCommonUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.commonUsagePatterns){
			if(ppg.isClusterable() && ppg.isComplexTypeUseagePattern())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableMethodFieldVariableDiffUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.diffUsagePatterns){
			if(ppg.isClusterable() && ppg.isMethodFieldVariableUseagePattern())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<PathPatternGroup> getClusterableComplexTypeDiffUsagePatterns() throws Exception{
		ArrayList<PathPatternGroup> ppgs = new ArrayList<PathPatternGroup>();
		for(PathPatternGroup ppg: this.diffUsagePatterns){
			if(ppg.isClusterable() && ppg.isComplexTypeUseagePattern())
				ppgs.add(ppg);
		}
		return ppgs;
	}
	
	public ArrayList<Path> getAllContainedPaths(){
		ArrayList<Path> pathList = new ArrayList<Path>();
		for(CounterRelationGroup relations: counterRelationGroups){
			for(InstanceElementRelation relation: relations.getRelationList()){
				CloneInstance instance = relation.getCloneInstance();
				ProgrammingElement pe = relation.getElement();
				
				Path path = new Path();
				path.add(instance);
				path.add(pe);
				
				InstanceDerivedPaths paths = new InstanceDerivedPaths(); 
				paths = generatePaths(path, paths);
				
				pathList.addAll(paths);
			}
		}
		
		for(CloneInstance instance: this){
			Path path = new Path(instance);
			InstanceDerivedPaths paths = new InstanceDerivedPaths(); 
			paths = generatePaths(path, paths);
			
			pathList.addAll(paths);
		}
		
		return pathList;
	}

	public void buildPatterns() throws Exception{
		buildPatternforResidingInRelation();
		buildPatternforElementsInCounterRelations();
		//buildPatternForCommonPart();
	}
	
	private void buildPatternForCommonPart() throws Exception {
		
		for(Method method: callingMethods){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(method, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Field field: accessingFields){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(field, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Variable variable: definingVariables){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(variable, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Variable variable: referingVariables){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(variable, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Constant constant: usingConstants){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(constant, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Class clas: usingClasses){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(clas, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		for(Interface interf: usingInterfaces){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			generateCommonPartPathInInstancePathGroup(interf, instancePathsGroup);
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.commonUsagePatterns);
		}
		
		//long start = System.currentTimeMillis();
		
		
	}
	
	private void generateCommonPartPathInInstancePathGroup(ProgrammingElement element, 
			ArrayList<InstanceDerivedPaths> instancePathsGroup){
		for(CloneInstance instance: this){
			Path path = new Path(instance);
			path.add(element);
			
			InstanceDerivedPaths paths = new InstanceDerivedPaths(); 
			paths = generatePaths(path, paths);
			instancePathsGroup.add(paths);
		}
	}

	private void buildPatternforElementsInCounterRelations() throws Exception{
		
		for(CounterRelationGroup relations: counterRelationGroups){
			ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
			for(InstanceElementRelation relation: relations.getRelationList()){
				CloneInstance instance = relation.getCloneInstance();
				ProgrammingElement pe = relation.getElement();
				
				Path path = new Path();
				path.add(instance);
				path.add(pe);
				
				InstanceDerivedPaths paths = new InstanceDerivedPaths(); 
				paths = generatePaths(path, paths);
				instancePathsGroup.add(paths);
				
			}
			//long start = System.currentTimeMillis();
			buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.diffUsagePatterns);
			//long end = System.currentTimeMillis();
			//long time = end - start;
			//System.out.println("Time used in buildPatternsFromGroupsOfInstanceDerivedPaths:" + time);
			
		}
	}
	
	private void buildPatternforResidingInRelation() throws Exception{
		ArrayList<InstanceDerivedPaths> instancePathsGroup = new ArrayList<InstanceDerivedPaths>(); 
		for(CloneInstance instance: this){
			Path path = new Path(instance);
			InstanceDerivedPaths paths = new InstanceDerivedPaths(); 
			paths = generatePaths(path, paths);
			instancePathsGroup.add(paths);
		}
		
		//long start = System.currentTimeMillis();
		buildPatternsFromGroupsOfInstanceDerivedPaths(instancePathsGroup, this.locationPatterns);
		//long end = System.currentTimeMillis();
		//long time = end - start;
		//System.out.println("Time used in buildPatternsFromGroupsOfInstanceDerivedPaths:" + time);
		
	}
	
	/**
	 * Given an instance and its derived relations in ontological model, a tree or even graph rooted on such a instance
	 * is possible to be generated. For each separate path of the generated tree or graph, we put it into a specific group
	 * such a group is called a derived path group of the instance. 
	 * 
	 * Given derived path groups of all the instances of clone set, the method is able to compute a set of patterns by
	 * calculating the Levenshtein distance of the paths and clustering them.
	 * 
	 * Therefore, the input <code>instancePathsGroup</code> must be n tree/group rooted on clone instances of a clone set where
	 * n is the number of clone instances of the clone set.
	 * @param instancePathsGroup
	 * @throws Exception
	 */
	private void buildPatternsFromGroupsOfInstanceDerivedPaths(ArrayList<InstanceDerivedPaths> instancePathsGroup, ArrayList<PathPatternGroup> patterns) throws Exception{
		for(InstanceDerivedPaths instancePaths: instancePathsGroup){
			ArrayList<InstanceDerivedPaths> otherPathsGroup = getOtherInstacePathsGroup(instancePaths, instancePathsGroup);
			Iterator<Path> pathIter = instancePaths.iterator();
			while(pathIter.hasNext()){
				Path path = pathIter.next();
				PathPatternGroup ppg = new PathPatternGroup(this);
				
				ppg.add(path);
				ppg.updateAbstractPathSequenceByJustOneComparison();
				
				for(InstanceDerivedPaths otherPaths: otherPathsGroup){
					
					if(otherPaths.size() != 0){
						//Path mostSimilarPath = getTheMostSimilarPathFromGroup(ppg, otherPaths);
						Path mostSimilarPath = getTheShortestCompatibleDistancePathFromGroup(ppg, otherPaths);
						if(null != mostSimilarPath){
							ppg.add(mostSimilarPath);
							ppg.updateAbstractPathSequenceByJustOneComparison();
							otherPaths.remove(mostSimilarPath);
						}
					}
				}
				
				if(ppg.size() >= 2){
					
					pathIter.remove();
					
					//long start = System.currentTimeMillis();
					ppg.updateAbstractPathSequence();
					//long end = System.currentTimeMillis();
					//long time = end - start;
					//System.out.println("Time used in updateAbstractPathSequence:" + time);
					if(!isPatternsContainingThePattern(patterns, ppg))
						patterns.add(ppg);
				}
			}
		}
	}
	
	private boolean isPatternsContainingThePattern(ArrayList<PathPatternGroup> patterns, PathPatternGroup ppg){
		for(PathPatternGroup group: patterns){
			if(group.getAbstractPathSequence().toString().equals(ppg.getAbstractPathSequence().toString()))
				return true;
		}
		return false;
	}
	
	private Path getTheShortestCompatibleDistancePathFromGroup(PathPatternGroup ppg, InstanceDerivedPaths paths) throws Exception{
		
		Path mostSimilarPath = null;
		double shortestDistance = 0;
		try{
			shortestDistance = computeLongestDistanceFromAPathToPatternPathGroup(ppg, paths.get(0));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		for(Path p: paths){
			double distance = computeLongestDistanceFromAPathToPatternPathGroup(ppg, p);
			if(distance <= shortestDistance
					&& distance <= Settings.thresholdDistanceToFormPattern){
				PathSequence abSeq = ppg.getAbstractPathSequence();
				PathSequence pathSeq = p.transferToSequence();
				
				/**
				 * Make sure the returned path is compatible with all paths in path pattern group.
				 */
				if(null != new PathAbstractor().extractAbstractPath(pathSeq, abSeq)){
					mostSimilarPath = p;
					shortestDistance = distance;
				}
				
			}
		}
		
		return mostSimilarPath;
		
	}
	
	
	private double computeLongestDistanceFromAPathToPatternPathGroup(PathPatternGroup ppg, Path p) throws Exception {
		
		
		//PathComparator pathComparator = new VectorPathComparator();
		
		double longestDistance = 0d;
		for(Path path: ppg){
			//long start = System.currentTimeMillis();
			double s = this.sets.getPathComparator().computePathDistance(path, p, this.cloneSetId, this.sets.getAveragePathSequenceLength());
			//long end = System.currentTimeMillis();
			//long time = end - start;
			//System.out.println("Time used in computePathDistance:" + time);
			
			if(s > longestDistance){
				longestDistance = s;
			}
		}
		return longestDistance;
	}

	/*public int getAveragePathSequenceLength() {
		return this.sets.getAveragePathSequenceLength();
	}*/
	
	/*private Path getTheMostSimilarPathFromGroup(PatternPathGroup ppg, InstanceDerivedPaths paths) throws Exception{
		Path mostSimilarPath = null;
		double highestSimilarity = 0d;
		
		for(Path p: paths){
			double similarity = computeLeastSimilarity(ppg, p);
			if(similarity > highestSimilarity
					&& similarity >= thresholdToFormPattern){
				mostSimilarPath = p;
				highestSimilarity = similarity;
			}
		}
		
		return mostSimilarPath;
		
	}
	
	
	private double computeLeastSimilarity(PatternPathGroup ppg, Path p) throws Exception {
		double leastSimilarity = 1d;
		for(Path path: ppg){
			double s = computePathSimilarity(path, p);
			if(s < leastSimilarity){
				leastSimilarity = s;
			}
		}
		return leastSimilarity;
	}

	private double computePathSimilarity(Path p1, Path p2) throws Exception{
		ArrayList<OntologicalElement> nodes = new ArrayList<OntologicalElement>();
		ArrayList<OntologicalRelationType> edges = new ArrayList<OntologicalRelationType>();
		
		SyntacticUtil su = new SyntacticUtil();
		su.collectNodesAndEdges(p1, nodes, edges);
		su.collectNodesAndEdges(p2, nodes, edges);
		
		*//**
		 * the node types, edge types compose all the dimension of the vector.
		 *//*
		int vectorSize = nodes.size() + edges.size();
		
		*//**
		 * The order is as the following: node types in path1 and path2, 
		 * edge types in path1 and path2.
		 *//*
		double vector1[] = new double[vectorSize];
		double vector2[] = new double[vectorSize];
		
		initializeVector(vector1, p1, nodes, edges);
		initializeVector(vector2, p2, nodes, edges);
		
		double distance = su.computeEuclideanDistance(vector1, vector2);
		*//**
		 * compute how many ontological nodes do vectors exactly share.
		 *//*
		int numberOfSharedNodes = su.getCommonNodeNumber(nodes.size(), vector1, vector2);
		
		distance *= Math.pow(distanceDeductRate, numberOfSharedNodes);
		
		return 1/(distance);
		//return new MinerUtil().computeCosineValue(vector1, vector2);
	}*/
	
	/**
	 * compute how many ontological nodes do vectors exactly share.
	 * @param offset
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	/*private int commonNodeNumber(int offset, double[] vector1, double[] vector2){
		int count = 0;
		for(int i=0; i<offset; i++){
			if(vector1[i] != 0 && vector2[i] != 0
					&& vector1[i] == vector2[i])
				count++;
		}
		return count;
	}*/
	
	
	
	
	
	private ArrayList<InstanceDerivedPaths> getOtherInstacePathsGroup(
			InstanceDerivedPaths instancePaths,
			ArrayList<InstanceDerivedPaths> instancePathsGroup) {
		ArrayList<InstanceDerivedPaths> otherPathsGroup = new ArrayList<InstanceDerivedPaths>();
		for(InstanceDerivedPaths paths: instancePathsGroup){
			if(paths != instancePaths)
				otherPathsGroup.add(paths);
		}
		return otherPathsGroup;
	}

	private InstanceDerivedPaths generatePaths(Path formerPath, InstanceDerivedPaths paths) {
		OntologicalElement element = formerPath.getLastElement();
		
		if(element.getSuccessors().size() == 0){
			if(!formerPath.isDisposable())
				paths.add(formerPath);
		}
		else{
			for(OntologicalElement e: element.getSuccessors()){
				if(e instanceof ComplexType && ((ComplexType) e).isGeneralType()){
					if(!paths.contains(formerPath) && !formerPath.isDisposable())
						if(formerPath.size() > 2)
							paths.add(formerPath);
				}
				else{
					Path path = formerPath.copyPath();
					path.add(e);
					generatePaths(path, paths);					
				}
			}
		}
		
		return paths;
	}

	public CounterRelationGroup containsCounterRelationGroup(String counterRelationId){
		for(CounterRelationGroup g: counterRelationGroups){
			if(g.getCounterRelationsId().equals(counterRelationId))
				return g;
		}
		return null;
	}
	
	public void addCounterRelationGroup(CounterRelationGroup group){
		if(null == containsCounterRelationGroup(group.getCounterRelationsId())){
			this.counterRelationGroups.add(group);
		}
	}

	public CloneSet(String id) {
		this.cloneSetId = id;
	}

	public ArrayList<Method> getCallingMethods() {
		return callingMethods;
	}

	public void setCallingMethods(ArrayList<Method> callingMethods) {
		this.callingMethods = callingMethods;
	}

	public ArrayList<Field> getAccessingFields() {
		return accessingFields;
	}

	public void setAccessingFields(ArrayList<Field> accessingFields) {
		this.accessingFields = accessingFields;
	}

	public ArrayList<Variable> getDefiningVariables() {
		return definingVariables;
	}

	public void setDefiningVariables(ArrayList<Variable> definingVariables) {
		this.definingVariables = definingVariables;
	}

	public ArrayList<Variable> getReferingVariables() {
		return referingVariables;
	}

	public void setReferingVariables(ArrayList<Variable> referingVariables) {
		this.referingVariables = referingVariables;
	}

	public ArrayList<Constant> getUsingConstants() {
		return usingConstants;
	}

	public void setUsingConstants(ArrayList<Constant> usingConstants) {
		this.usingConstants = usingConstants;
	}

	public ArrayList<Class> getUsingClasses() {
		return usingClasses;
	}

	public void setUsingClasses(ArrayList<Class> usingClasses) {
		this.usingClasses = usingClasses;
	}

	public ArrayList<Interface> getUsingInterfaces() {
		return usingInterfaces;
	}

	public void setUsingInterfaces(ArrayList<Interface> usingInterfaces) {
		this.usingInterfaces = usingInterfaces;
	}

	public CloneSet(String cloneSetId, Project project) {
		super();
		this.cloneSetId = cloneSetId;
		this.project = project;
	}

	public String getId() {
		return cloneSetId;
	}

	public void setId(String cloneSetId) {
		this.cloneSetId = cloneSetId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	/*
	 * public ArrayList<ProgrammingElement> getProgrammingElements() { return
	 * programmingElements; }
	 * 
	 * public void setProgrammingElements( ArrayList<ProgrammingElement>
	 * programmingElements) { this.programmingElements = programmingElements; }
	 * 
	 * public ArrayList<DiffCounterRelationGroup> getCounterRelationGroups() {
	 * return counterRelationGroups; }
	 * 
	 * public void setCounterRelationGroups( ArrayList<DiffCounterRelationGroup>
	 * counterRelationGroups) { this.counterRelationGroups =
	 * counterRelationGroups; }
	 */

	@Override
	public OntologicalElementType getOntologicalType() {
		return OntologicalElementType.CloneSet;
	}

	@Override
	public ArrayList<OntologicalElement> getSuccessors() {
		ArrayList<OntologicalElement> successors = new ArrayList<OntologicalElement>();
		successors.addAll(this);
		successors.addAll(callingMethods);
		successors.addAll(accessingFields);
		successors.addAll(definingVariables);
		successors.addAll(referingVariables);
		successors.addAll(usingConstants);
		successors.addAll(usingClasses);
		successors.addAll(usingInterfaces);

		return successors;
	}

	public ArrayList<CounterRelationGroup> getCounterRelationGroups() {
		return counterRelationGroups;
	}

	public void setCounterRelationGroups(
			ArrayList<CounterRelationGroup> counterRelationGroups) {
		this.counterRelationGroups = counterRelationGroups;
	}

	@Override
	public boolean equals(OntologicalElement element) {
		if(element instanceof CloneSet){
			CloneSet referSet = (CloneSet)element;
			return referSet.getId().equals(this.getId());
		}
		return false;
	}

	@Override
	public String getSimpleElementName() {
		return toString();
	}

	public CloneSets getCloneSets() {
		return sets;
	}

	public void setCloneSets(CloneSets sets) {
		this.sets = sets;
	}

	public WordBag getDiffWords() {
		return diffWords;
	}

	public void setDiffWords(WordBag diffWords) {
		this.diffWords = diffWords;
	}

	public WordBag getCommonWords() {
		return commonWords;
	}

	public void setCommonWords(WordBag commonWords) {
		this.commonWords = commonWords;
	}

	public ICluster getResidingCluster() {
		return residingCluster;
	}

	public void setResidingCluster(ICluster residingCluster) {
		this.residingCluster = residingCluster;
	}

	public ArrayList<PathPatternGroup> getLocationPatterns() {
		return locationPatterns;
	}

	public void setLocationPatterns(ArrayList<PathPatternGroup> locationPatterns) {
		this.locationPatterns = locationPatterns;
	}

	public ArrayList<PathPatternGroup> getDiffUsagePatterns() {
		return diffUsagePatterns;
	}

	public void setDiffUsagePatterns(ArrayList<PathPatternGroup> diffUsagePatterns) {
		this.diffUsagePatterns = diffUsagePatterns;
	}

	public ArrayList<PathPatternGroup> getCommonUsagePatterns() {
		return commonUsagePatterns;
	}

	public void setCommonUsagePatterns(
			ArrayList<PathPatternGroup> commonUsagePatterns) {
		this.commonUsagePatterns = commonUsagePatterns;
	}

	public ArrayList<PathPatternGroup> getPatterns(){
		ArrayList<PathPatternGroup> list = new ArrayList<PathPatternGroup>();
		list.addAll(this.locationPatterns);
		list.addAll(this.commonUsagePatterns);
		list.addAll(this.diffUsagePatterns);
		
		return list;
	}
}
