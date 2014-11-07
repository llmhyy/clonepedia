package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.util.ASTUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ccdemon.model.SelectedCodeRange;
import ccdemon.util.CCDemonUtil;
import ccdemon.util.SharedData;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;


public class PasteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CCDemonUtil.callBackDefaultEvent("paste", event);
		
		/**
		 * search related clone instances in project's clone set.
		 */
		SelectedCodeRange range = SharedData.range;
		if(range != null){
			CloneSets sets = clonepedia.Activator.getCloneSets();
			CompilationUnit cu = ASTUtil.generateCompilationUnit(range.getFileName());
			int startLine = cu.getLineNumber(range.getStartPosition());
			int endLine = cu.getLineNumber(range.getEndPosition());
			ArrayList<CloneSet> setsInNeed = sets.findCloneSets(range.getFileName(), 
					startLine, endLine);
			
			System.currentTimeMillis();
		}
		
		/**
		 * identify clone differences.
		 */
		
		System.out.println("paste");
		return null;
	}

}
