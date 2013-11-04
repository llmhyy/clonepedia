package clonepedia.debug;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.OntologicalElement;
import clonepedia.model.syntactic.PathSequence;

public class Debugger {
	static NumberFormat format = new DecimalFormat("#0.000");
	

	public static void printProgress(double progress) {
		
		System.out.println("Finished by " + progress * 100 + "%");

	}

	public static void printLevenshteinMatrix(double[][] matrix,
			PathSequence seq1, PathSequence seq2) {
		for (int i = 0; i < seq1.size() + 2; i++) {
			for (int j = 0; j < seq2.size() + 2; j++) {
				double value = -1;
				String str = "";
				if (i == 0 && j == 0)
					str = String.valueOf(value);
				else if (i == 0 && j != 0) {
					if (j == 1)
						str = "empty";
					else {
						try {
							Object obj = seq2.get(j - 2);
							if (obj instanceof OntologicalElement)
								str = ((OntologicalElement) obj)
										.getOntologicalType().toString();
							else
								str = obj.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} else if (i != 0 && j == 0) {
					if (i == 1)
						str = "empty";
					else {
						try {
							Object obj = seq1.get(i - 2);
							if (obj instanceof OntologicalElement)
								str = ((OntologicalElement) obj)
										.getOntologicalType().toString();
							else
								str = obj.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				} else
					str = format.format(matrix[i - 1][j - 1]);

				System.out.print(str);
				printComplementarySpace(str, 20);
			}
			System.out.println();
		}
	}

	public static void printPairDistanceMatrix(double[][] matrix,
			OntologicalElement[] elements1, OntologicalElement[] elements2) {

		for (int i = 0; i < elements1.length + 1; i++) {
			for (int j = 0; j < elements2.length + 1; j++) {
				double value = -1;
				String str;
				if (i == 0 && j == 0)
					str = String.valueOf(value);
				else if (i == 0 && j != 0) {
					OntologicalElement e = elements2[j - 1];
					str = e.getSimpleElementName();
				} else if (i != 0 && j == 0) {
					OntologicalElement e = elements1[i - 1];
					str = e.getSimpleElementName();
				} else
					str = format.format(matrix[i - 1][j - 1]);

				System.out.print(str);
				printComplementarySpace(str, 35);
			}
			System.out.println();
		}

	}

	public static void printClusteringMatrix(double[][] matrix,
			SyntacticCluster[] clusterer) {

		for (int i = 0; i < matrix.length + 1; i++) {
			for (int j = 0; j < matrix.length + 1; j++) {
				double value = -1;
				String str;
				if (i == 0 && j == 0)
					str = String.valueOf(value);
				else if (i == 0 && j != 0)
					str = String.valueOf(clusterer[j - 1].get(0).getId());
				else if (i != 0 && j == 0)
					str = String.valueOf(clusterer[i - 1].get(0).getId());
				else
					str = format.format(matrix[i - 1][j - 1]);

				System.out.print(str);
				printComplementarySpace(str, 10);
			}
			System.out.println();
		}

	}

	private static void printComplementarySpace(String str, int spaceNumber) {
		int loopNumber = spaceNumber - str.length();
		for (int i = 0; i < loopNumber; i++)
			System.out.print(' ');
	}
}
