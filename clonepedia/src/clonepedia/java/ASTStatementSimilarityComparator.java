package clonepedia.java;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;

import clonepedia.java.visitor.CloneASTStatementNodeVisitor;
import clonepedia.util.ASTNodeSimilarityComparator;
import clonepedia.util.BoolToSimilarityComparatorAdapter;
import clonepedia.util.MinerUtil;
import clonepedia.util.SimilarityComparator;

public class ASTStatementSimilarityComparator implements SimilarityComparator {
	
	@Override
	public double computeCost(Object obj1, Object obj2) throws Exception {
		if((obj1 instanceof Statement) && (obj2 instanceof Statement)){
			Statement stat1 = (Statement)obj1;
			Statement stat2 = (Statement)obj2;
			
			ArrayList<ASTNode> list1 = new ArrayList<ASTNode>();
			ArrayList<ASTNode> list2 = new ArrayList<ASTNode>();
			
			CloneASTStatementNodeVisitor visitor1 = new CloneASTStatementNodeVisitor(0, 0, list1, null, stat1);
			stat1.accept(visitor1);
			CloneASTStatementNodeVisitor visitor2 = new CloneASTStatementNodeVisitor(0, 0, list2, null, stat2);
			stat2.accept(visitor2);
			
			//BoolToSimilarityComparatorAdapter comparator = new BoolToSimilarityComparatorAdapter(new ASTComparator());
			ASTNodeSimilarityComparator comparator = new ASTNodeSimilarityComparator();
			return MinerUtil.computeAdjustedLevenshteinDistance(list1, list2, comparator);
		}
		else{
			throw new Exception("ASTStatementSimilarityComparator can only accept statement");
		}
	}

}
