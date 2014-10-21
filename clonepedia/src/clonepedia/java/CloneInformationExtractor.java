package clonepedia.java;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeLiteral;


import clonepedia.filepraser.CloneDetectionFileParser;
import clonepedia.filepraser.FileParser;
import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.java.ASTComparator;
import clonepedia.java.model.*;
import clonepedia.java.util.MinerUtilforJava;
import clonepedia.java.visitor.CloneASTNodeVisitor;
import clonepedia.java.visitor.CloneASTStatementVisitor;
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
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class CloneInformationExtractor {

	private OntologicalDataFetcher fetcher;
	private Project project;
	
	private CloneSets cloneSets;
	
	//private CloneDetectionFileParser cloneFileParser;
	private ArrayList<CloneSetWrapper> setWrapperList = new ArrayList<CloneSetWrapper>();

	public CloneInformationExtractor(CloneSets sets, Project project, OntologicalDataFetcher fetcher) {
		this.cloneSets = sets;
		this.project = project;
		
		this.fetcher = fetcher;
	}
	
	public CloneInformationExtractor(){}
	
	/**
	 * Extract the structural and diff information of clone sets
	 */
	public OntologicalDataFetcher extract(IProgressMonitor monitor) {
		
		CompilationUnitPool pool = new CompilationUnitPool();
		
		//CloneSets cloneSets = cloneFileParser.getCloneSets();
		for (CloneSet cloneSet : cloneSets.getCloneList()) {
			try{
				/**
				 * The following code is for debugging
				 */
				/*if(cloneSet.getId().equals("280097")){
					System.out.print("");
				}
				else
					continue;*/
				
				cloneSet.setProject(project);
				/**
				 * extract structural information
				 */
				CloneSetWrapper setWrapper = new CloneSetWrapper(cloneSet, pool);
				/**
				 * extract diff information
				 */
				if(Settings.diffComparisonMode.equals("ASTNode_Based")){
					setWrapper = extractCounterRelationalDifferencesOfCloneSet(setWrapper);					
				}
				else if(Settings.diffComparisonMode.equals("Statement_Based")){
					setWrapper = extractCounterRelationalDifferencesWithinSyntacticBoundary(setWrapper);
				}
				setWrapperList.add(setWrapper);
				
				if(monitor != null){
					monitor.worked(1);
					if(monitor.isCanceled()) return this.fetcher;
				}
			}
			catch(Exception e){
				System.out.println(cloneSet.getId());
				e.printStackTrace();
			}
		}
		
		fetcher.storeCloneInformation(this.setWrapperList, this.project);
		
		return this.fetcher;
	}

	/**
	 * A clone set will maintain a common node list represent all the common AST nodes shared
	 * by its instances in order, which is used as a reference list during the algorithm. 
	 * A clone instance will maintain its own instance node list in terms of ordered AST nodes 
	 * representing the cloned code fragment. In diff algorithm, two cursors will be maintained 
	 * for each node list to point to the AST node in comparison. The cursor in common node list 
	 * is called common cursor, while the cursor in instance node list is called instance cursor. 
	 * In order to specify a range in a list, we need two cursors: starting index cursor and ending 
	 * index cursor. They will change in the execution of the diff algorithm.<p>
	 * 
	 * We need to find the differences with counter-relation. Therefore, we need to identify the 
	 * range where the counter-relational differences happen. The reference is the common node list
	 * in clone set and all the instance starting/ending index cursors are identified according to
	 * the common starting/ending index cursor. <p>
	 * 
	 * This method is used to find all the counter-relational differences in a clone set.
	 * @param setWrapper
	 */
	public CloneSetWrapper extractCounterRelationalDifferencesOfCloneSet(CloneSetWrapper setWrapper){
		
		setWrapper.generateCommonListforCloneSetWrapper();
		if (setWrapper.getCommonASTNodeList().length != 0) 
			setWrapper.generateDiffPart();
		
		return setWrapper;
	}

	/*private void filterComplicatedASTNodeforList(ArrayList<ASTNode> list) {
		Iterator<ASTNode> iter = list.iterator();
		while (iter.hasNext()) {
			ASTNode node = (ASTNode) iter.next();
			
			if (!MinerUtilforJava.isConcernedType(node) && !MinerUtilforJava.isBenchMarkType(node))
				iter.remove();
		}
	}*/
	
	public CloneSetWrapper extractCounterRelationalDifferencesWithinSyntacticBoundary(CloneSetWrapper setWrapper) throws Exception{
		setWrapper.generateCommonStatementListforCloneSetWrapper();
		if (setWrapper.getCommonStatementList().length != 0) {
			setWrapper.generateDiffPartWithinSyntacticBoundary(true);			
		}
		else{
			extractCounterRelationalDifferencesOfCloneSet(setWrapper);
		}
		
		return setWrapper;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
