package clonepedia.java.model;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;

import clonepedia.java.CompilationUnitPool;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;

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
	
	public CloneSetWrapper(CloneSet cloneSet, CompilationUnitPool pool){
		this.pool = pool;
		this.cloneSet = cloneSet;
		for(CloneInstance ci: cloneSet){
			add(new CloneInstanceWrapper(ci, this.pool));
		}
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
