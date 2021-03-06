package clonepedia.views.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;

import clonepedia.java.model.CloneInstanceWrapper;
import clonepedia.java.model.CloneSetWrapper;
import clonepedia.java.model.Diff;
import clonepedia.java.model.DiffElement;
import clonepedia.util.MinerProperties;
import clonepedia.util.Settings;
import mcidiff.main.SeqMCIDiff;
import mcidiff.main.TokenMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.Multiset;
import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

public class DiffUtil {
	public static clonepedia.java.model.CloneSetWrapper constructDiff(clonepedia.java.model.CloneSetWrapper cloneSet) {
		CloneSet set = convertToMCIDiffModel(cloneSet);

		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			String projectName = Settings.projectName;
			IProject project = root.getProject(projectName);
			if (project.isNatureEnabled(MinerProperties.javaNatureName)) {
				IJavaProject javaProject = JavaCore.create(project);

				ArrayList<? extends Multiset> multisets = new ArrayList<>();
				if (Settings.diffComparisonMode.equals("ASTNode_Based")) {
					multisets = new TokenMCIDiff().diff(set, javaProject);
				} else if (Settings.diffComparisonMode.equals("Statement_Based")) {
					multisets = new SeqMCIDiff().diff(set, javaProject);
				}

				for (Multiset multiset : multisets) {
					Diff diff = new Diff();
					for (mcidiff.model.DiffElement diffElement : multiset.getDiffElements()) {
						CloneInstance instance = diffElement.getCloneInstance();
						CloneInstanceWrapper insWrapper = findInstanceWrapper(cloneSet, instance);
						
						TokenSeq seq = new TokenSeq();
						if(diffElement instanceof TokenSeq){
							seq = (TokenSeq)diffElement;
						}
						else if(diffElement instanceof Token){
							seq.addToken((Token)diffElement);
						}
						
						DiffElement element = new DiffElement(insWrapper, seq);

						List<ASTNode> nodeList = new ArrayList<>();
						if (!seq.isEpisolonTokenSeq()) {
							Token token = seq.getTokens().get(0);
							nodeList.add(token.getNode());
						}
						element.setNodeList(nodeList);

						diff.addElement(element);
					}

					cloneSet.addDiff(diff);
				}

				return cloneSet;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static CloneInstanceWrapper findInstanceWrapper(CloneSetWrapper cloneSet, CloneInstance instance) {
		for (CloneInstanceWrapper ins : cloneSet) {
			if (ins.getFilePath().equals(instance.getFileName()) && ins.getStartLine() == instance.getStartLine()
					&& ins.getEndLine() == instance.getEndLine()) {
				return ins;
			}
		}
		return null;
	}

	private static CloneSet convertToMCIDiffModel(CloneSetWrapper cloneSet) {

		CloneSet set = new CloneSet(cloneSet.getId());
		for (clonepedia.model.ontology.CloneInstance ins : cloneSet.getCloneSet()) {
			CloneInstance instance = new CloneInstance(ins.getFileLocation(), ins.getStartLine(), ins.getEndLine());
			set.addInstance(instance);
		}

		return set;
	}
}
