package clonepedia.java.visitor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.CloneSetWrapper;
import clonepedia.java.model.DiffCounterRelationGroupEmulator;
import clonepedia.java.model.DiffInstanceElementRelationEmulator;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.wizard.CodeSkeletonGenerationUtil;

public class FindCloneDifferenceVisitor extends ASTVisitor {
	
	
	private static int COUNTER_MODE = 0;
	private static int UNCOUNTER_MODE = 1;	
	
	private CloneSetWrapper setWrapper;
	private CloneInstanceWrapper insWrapper;
	
	//private ArrayList<ASTNode> counterDiffList = new ArrayList<ASTNode>();
	//private ArrayList<ASTNode> uncounterDiffList = new ArrayList<ASTNode>();
	
	private HashMap<Statement, ArrayList<ASTNode>> counterDiffMap 
		= new HashMap<Statement, ArrayList<ASTNode>>();
	private HashMap<Statement, ArrayList<ASTNode>> uncounterDiffMap 
		= new HashMap<Statement, ArrayList<ASTNode>>();
	
	public FindCloneDifferenceVisitor(CloneSetWrapper setWrapper, CloneInstanceWrapper insWrapper){
		this.setWrapper = setWrapper;
		this.insWrapper = insWrapper;
	}
	
	public void preVisit(ASTNode targetNode){
		for(DiffCounterRelationGroupEmulator diff: setWrapper.getRelationGroups()){
			
			//if(diff.getElements().size() != setWrapper.size())continue;
			
			for(DiffInstanceElementRelationEmulator diffElement: diff.getElements()){
				ASTNode node = diffElement.getNode();
				
				if(node.equals(targetNode)){
					
					recordDiffInformation(COUNTER_MODE, targetNode);
					
					//counterDiffList.add(targetNode);
				}
			}
		}
		
		for(ASTNode node: insWrapper.getUncounterRelationalDifferenceNodes()){
			if(node.equals(targetNode)){
				
				recordDiffInformation(UNCOUNTER_MODE, targetNode);
				//uncounterDiffList.add(targetNode);
			}
		}
		
		if(targetNode instanceof Statement){
			Statement stat = (Statement)targetNode;
			
			CompilationUnit unit = MinerUtilforJava.getCompilationUnit(insWrapper.getMethodDeclaration());
			if(!CodeSkeletonGenerationUtil.isCloneRelatedStatement(stat, insWrapper, unit)){
				stat.setLeadingComment("//not clone");
			}
		}
	}
	
	
	
	private void recordDiffInformation(int mode, ASTNode targetNode){
		Statement stat = findMostInnerStatement(targetNode);
		
		ArrayList<ASTNode> nodeList = counterDiffMap.get(stat);
		if(nodeList == null){
			nodeList = new ArrayList<ASTNode>();
		}
		nodeList.add(targetNode);
		
		if(mode == COUNTER_MODE){
			counterDiffMap.put(stat, nodeList);
		}
		else if(mode == UNCOUNTER_MODE){
			uncounterDiffMap.put(stat, nodeList);
		}
	}
	
	private Statement findMostInnerStatement(ASTNode node){
		Statement stat = null;
		if(node instanceof Statement){
			stat = (Statement)node;
		}
		else{
			ASTNode parent = node.getParent();
			while(!(parent instanceof Statement)){
				parent = parent.getParent();
			}
			stat = (Statement)parent;
		}
		
		return stat;
	}

	public HashMap<Statement, ArrayList<ASTNode>> getCounterDiffMap() {
		return counterDiffMap;
	}

	public HashMap<Statement, ArrayList<ASTNode>> getUncounterDiffMap() {
		return uncounterDiffMap;
	}
}
