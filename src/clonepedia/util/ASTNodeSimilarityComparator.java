package clonepedia.util;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;

import clonepedia.java.ASTComparator;

public class ASTNodeSimilarityComparator implements
		SimilarityComparator {

	@Override
	public double computeCost(Object obj1, Object obj2) throws Exception {
		if(obj1 instanceof ASTNode && obj2 instanceof ASTNode){
			ASTNode node1 = (ASTNode)obj1;
			ASTNode node2 = (ASTNode)obj2;
			
			if(node1.getNodeType() == node2.getNodeType()){
				if(node1 instanceof SimpleName && node2 instanceof SimpleName){
					SimpleName name1 = (SimpleName)node1;
					SimpleName name2 = (SimpleName)node2;
					
					String[] strList1 = MinerUtil.splitCamelString(name1.getIdentifier());
					String[] strList2 = MinerUtil.splitCamelString(name2.getIdentifier());
					
					Object[] commonList = MinerUtil.generateCommonNodeList(strList1, strList2, new DefaultComparator());
					
					double longerLength = strList1.length > strList2.length ? strList1.length : strList2.length;
					return 1 - commonList.length/longerLength;
				}
				else{
					return new ASTComparator().isMatch(node1, node2)? 0 : 1;
				}
			}
			else return 1;
		}
		else{
			throw new Exception("either object to be compared are not ASTNode");
		}
	}

}
