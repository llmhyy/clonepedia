package ccdemon.model;

import java.util.ArrayList;
import java.util.Set;

import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ccdemon.model.rule.NameInstance;
import ccdemon.model.rule.NamingRule;
import ccdemon.model.rule.RuleItem;


public class ConfigurationPointSet {
	private ArrayList<ConfigurationPoint> configurationPoints = new ArrayList<>();
	private CloneSet referrableCloneSet;
	private CompilationUnit pastedCompilationUnit;
	private int startPositionInPastedFile;
	
	private NamingRule rule;

	public class ContextContent{
		private TypeDeclaration typeDeclaration;
		private MethodDeclaration methodDeclaration;
		
		/**
		 * @param typeDeclaration
		 * @param methodDeclaration
		 */
		public ContextContent(TypeDeclaration typeDeclaration,
				MethodDeclaration methodDeclaration) {
			super();
			this.typeDeclaration = typeDeclaration;
			this.methodDeclaration = methodDeclaration;
		}
		/**
		 * @return the typeDeclaration
		 */
		public TypeDeclaration getTypeDeclaration() {
			return typeDeclaration;
		}
		/**
		 * @param typeDeclaration the typeDeclaration to set
		 */
		public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
			this.typeDeclaration = typeDeclaration;
		}
		/**
		 * @return the methodDeclaration
		 */
		public MethodDeclaration getMethodDeclaration() {
			return methodDeclaration;
		}
		/**
		 * @param methodDeclaration the methodDeclaration to set
		 */
		public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
			this.methodDeclaration = methodDeclaration;
		}
	}
	
	public ConfigurationPointSet(){}
	
	/**
	 * @param configurationPoints
	 */
	public ConfigurationPointSet(ArrayList<ConfigurationPoint> configurationPoints, 
			CloneSet referrableCloneSet, CompilationUnit pastedCompilationUnit,
			int startPositionInPastedFile) {
		super();
		this.configurationPoints = configurationPoints;
		this.referrableCloneSet = referrableCloneSet;
		this.pastedCompilationUnit = pastedCompilationUnit;
		this.startPositionInPastedFile = startPositionInPastedFile;
	}
	
	public void prepareForInstallation(ArrayList<ReferrableCloneSet> referrableCloneSets){
		
		expandEnvironmentBasedCandidates(this.configurationPoints);
		generateNamingRules(this.configurationPoints, this.referrableCloneSet);
		
		OccurrenceTable occurrences = constructCandidateOccurrences(referrableCloneSets);
		adjustCandidateRanking(this.configurationPoints, occurrences);
	}
	
	private void generateNamingRules(
			ArrayList<ConfigurationPoint> configurationPoints, CloneSet referrableCloneSet) {
		
		NamingRule rule = new NamingRule();
		
		RuleItem typeNameItem = new RuleItem(null);
		rule.addItem(typeNameItem);
		
		RuleItem methodNameItem = new RuleItem(null);
		rule.addItem(methodNameItem);
		
		RuleItem[] items = new RuleItem[configurationPoints.size()];
		for(int i=0; i<items.length; i++){
			items[i] = new RuleItem(configurationPoints.get(i));
			rule.addItem(items[i]);
		}
		
		for(CloneInstance instance: referrableCloneSet.getInstances()){
			ContextContent content = parseContextPoint(instance);	
			
			String typeName = content.getTypeDeclaration().getName().getIdentifier();
			String methodName = content.getMethodDeclaration().getName().getIdentifier();
			
			typeNameItem.addNameInstance(new NameInstance(typeName, true));
			methodNameItem.addNameInstance(new NameInstance(methodName, true));
			
			for(int i=0; i<configurationPoints.size(); i++){
				ConfigurationPoint point = configurationPoints.get(i);
				SeqMultiset set = point.getSeqMultiset();
				TokenSeq seq = set.findTokenSeqByCloneInstance(instance.getFileName(), 
						instance.getStartLine(), instance.getEndLine());
				
				items[i].addNameInstance(new NameInstance(seq.getText(), seq.isSingleToken()));
			}
		}
		
		rule.parseNamingPattern();

		setRule(rule);
	}
 
	private ContextContent parseContextPoint(CloneInstance instance) {
		ASTNode node = null;
		for(Token t: instance.getTokenList()){
			if(!t.isEpisolon()){
				node = t.getNode();
				break;
			}
		}
		TypeDeclaration typeDeclaration = null;
		MethodDeclaration methodDeclaration = null;
		
		while(node != null){
			if(node instanceof MethodDeclaration && methodDeclaration == null){
				methodDeclaration = (MethodDeclaration)node;
			}
			else if(node instanceof TypeDeclaration && typeDeclaration == null){
				typeDeclaration = (TypeDeclaration)node;
			}
			
			if(methodDeclaration != null && typeDeclaration != null){
				break;
			}
			
			node = node.getParent();
		}
		
		ContextContent items = new ContextContent(typeDeclaration, methodDeclaration);
		return items;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandEnvironmentBasedCandidates(
			ArrayList<ConfigurationPoint> configurationPoints) {
		for(ConfigurationPoint point: configurationPoints){
			if(point.isType()){
				//find its sibling types
				ArrayList<Class> types = point.getSuperClasses();
				ArrayList<Class> siblings = new ArrayList<Class>();
				for(Class c : types){
					ConfigurationBuilder cb = new ConfigurationBuilder().setScanners(new SubTypesScanner());
					cb.addUrls(ClasspathHelper.forClass(c));
				    Reflections reflections = new Reflections(cb);				
					Set<Class<?>> subset = reflections.getSubTypesOf(c);
					if(subset.size() != 0){
						for(Class sub : subset){
							if(!siblings.contains(sub)){
								siblings.add(sub);
								point.getCandidates().add(new Candidate(sub.getSimpleName(), 0 ,Candidate.ENVIRONMENT));
							}
						}
					}
				}
			}
			else if(point.isVariableOrField()){
				//find compatible variable in the context
				ArrayList<String> types = point.getVariableOrFieldTypes();
				String pastedVariableName = point.getCopiedTokenSeq().getText();
				
				NodeFinder finder = new NodeFinder(pastedCompilationUnit, startPositionInPastedFile, 1);
				ASTNode node = finder.getCoveringNode();
				while(!(node instanceof MethodDeclaration || node instanceof TypeDeclaration)){
					node = node.getParent();
				}
				//if copied code is put in a method, we need to search for the variable in the method as well
				if(node instanceof MethodDeclaration){
					VariableVisitor visitor = new VariableVisitor(types, pastedVariableName);
					node.accept(visitor);
					for(String variable : visitor.getCompatibleVariables()){
						point.getCandidates().add(new Candidate(variable, 0 ,Candidate.ENVIRONMENT));
					}
				}
				//otherwise, we just search those fields
				FieldVisitor visitor = new FieldVisitor(types, pastedVariableName);
				pastedCompilationUnit.accept(visitor);
				for(String variable : visitor.getCompatibleVariables()){
					point.getCandidates().add(new Candidate(variable, 0 ,Candidate.ENVIRONMENT));
				}
			}
			else if(point.isMethod()){
				//do nothing for now
			}
		}
	}

	private OccurrenceTable constructCandidateOccurrences(ArrayList<ReferrableCloneSet> referrableCloneSets) {
		ReferrableCloneSet rcs = referrableCloneSets.get(0);
		String[][] occurrenceTable = new String[rcs.getCloneSet().size()][configurationPoints.size()];
		clonepedia.model.ontology.CloneInstance[] instanceArray = rcs.getCloneSet().toArray(new clonepedia.model.ontology.CloneInstance[0]);
		
		for(int i=0; i<instanceArray.length; i++){
			clonepedia.model.ontology.CloneInstance instance = instanceArray[i];
			
			for(int j=0; j<configurationPoints.size(); j++){
				ConfigurationPoint cp = configurationPoints.get(j);
				TokenSeq tokenSeq = cp.getSeqMultiset().findTokenSeqByCloneInstance(instance.getFileLocation(), 
						instance.getStartLine(), instance.getEndLine());
				if(tokenSeq != null){
					occurrenceTable[i][j] = tokenSeq.getText();
				}
			}
		}
		
		return new OccurrenceTable(occurrenceTable);
	}

	private void adjustCandidateRanking(ArrayList<ConfigurationPoint> configurationPoints,
			OccurrenceTable occurrences) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the rule
	 */
	public NamingRule getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(NamingRule rule) {
		this.rule = rule;
	}

	/**
	 * @return the configurationPoints
	 */
	public ArrayList<ConfigurationPoint> getConfigurationPoints() {
		return configurationPoints;
	}

	/**
	 * @param configurationPoints the configurationPoints to set
	 */
	public void setConfigurationPoints(
			ArrayList<ConfigurationPoint> configurationPoints) {
		this.configurationPoints = configurationPoints;
	}
	
	public class VariableVisitor extends ASTVisitor{
		ArrayList<String> types;
		ArrayList<String> compatibleVariables = new ArrayList<String>();
		String pastedVariableName;
		
		public VariableVisitor(ArrayList<String> types, String pastedVariableName){
			this.types = types;
			this.pastedVariableName = pastedVariableName;
		}
		
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			if(binding != null && binding instanceof IVariableBinding){
				if(types.contains(((IVariableBinding) binding).getType().getQualifiedName())){
					//don't add the just pasted variable in, otherwise will cause duplication
					if(!name.toString().equals(pastedVariableName) && !this.compatibleVariables.contains(name.toString())){
						this.compatibleVariables.add(name.toString());
					}
				}
			}
			return false;
		}
		
		public ArrayList<String> getCompatibleVariables(){
			return this.compatibleVariables;
		}
	}
	
	public class FieldVisitor extends ASTVisitor{
		ArrayList<String> types;
		ArrayList<String> compatibleVariables = new ArrayList<String>();
		String pastedVariableName;
		
		public FieldVisitor(ArrayList<String> types, String pastedVariableName){
			this.types = types;
			this.pastedVariableName = pastedVariableName;
		}
		
		public boolean visit(FieldDeclaration field){
			if(types.contains(field.getType().resolveBinding().getQualifiedName())){
				//don't add the just pasted variable in, otherwise will cause duplication
				String fieldName = ((VariableDeclarationFragment)field.fragments().get(0)).getName().toString();
				if(!fieldName.equals(pastedVariableName) && !this.compatibleVariables.contains(fieldName)){
					this.compatibleVariables.add(fieldName);
				}
			}
			return false;
		}
		
		public ArrayList<String> getCompatibleVariables(){
			return this.compatibleVariables;
		}
	}
}
