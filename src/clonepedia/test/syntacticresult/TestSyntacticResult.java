package clonepedia.test.syntacticresult;

import java.io.File;
import java.util.ArrayList;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.util.MinerUtil;

public class TestSyntacticResult {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String resultFile = "clusters_15.0";
		MinerUtil mu = new MinerUtil();
		ArrayList<SyntacticCluster> list = (ArrayList<SyntacticCluster>)mu.deserialize(resultFile, false);
		System.out.print("");
	}

}
