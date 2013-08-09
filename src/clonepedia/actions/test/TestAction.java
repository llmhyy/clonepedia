package clonepedia.actions.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import clonepedia.featuretemplate.TemplateMethodBuilder;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.template.TemplateMethodGroup;
import clonepedia.syntactic.util.comparator.LevenshteinPathComparator;
import clonepedia.util.MinerUtil;
import clonepedia.util.Settings;

public class TestAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		CloneSets sets = (CloneSets)MinerUtil.deserialize(Settings.ontologyFile, true);
		sets.setPathComparator(new LevenshteinPathComparator());
		
		TemplateMethodBuilder builder = new TemplateMethodBuilder(sets);
		builder.build();
		HashSet<TemplateMethodGroup> templateMethodGroupList = builder.getMethodGroupList();
		
		ArrayList<TemplateMethodGroup> list = new ArrayList<TemplateMethodGroup>();
		for(TemplateMethodGroup tmg: templateMethodGroupList){
			list.add(tmg);
		}
		
		//sets.setTemplateMethodGroup(templateMethodGroupList);
		System.out.println();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
