package ccdemon.handlers;

import java.util.ArrayList;

import mcidiff.main.MCIDiff;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;
import mcidiff.util.ASTUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.commands.ExpressionContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ccdemon.model.ReferrableCloneSet;
import ccdemon.model.ConfigurationPoint;
import ccdemon.model.SelectedCodeRange;
import ccdemon.util.CCDemonUtil;
import ccdemon.util.SharedData;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSets;


public class PasteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

//		AbstractTextEditor activeEditor = (AbstractTextEditor) HandlerUtil.getActiveEditor(event);
//		ISourceViewer sourceViewer = (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);	
//		IDocument document= sourceViewer.getDocument();
		ITextSelection textSelection = (ITextSelection) HandlerUtil.getActivePart(event).getSite().getSelectionProvider().getSelection();
		int cursorOffset = textSelection.getOffset();
//		try {
//			int lineNumber = document.getLineOfOffset(cursorOffset) + 1;
//			System.out.println("start line" + lineNumber);
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("start position" + cursorOffset);
		
		CCDemonUtil.callBackDefaultEvent("paste", event);
		
		/*AbstractTextEditor activeEditor = (AbstractTextEditor) HandlerUtil.getActiveEditor(event);
		ISourceViewer sourceViewer = (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);		
		IEditorInput input = activeEditor.getEditorInput(); 
		IFile file = ((FileEditorInput) input).getFile(); 		
		Point point = sourceViewer.getSelectedRange();
		
		CompilationUnit cu = ASTUtil.generateCompilationUnit(file.getRawLocation().toOSString());
		int startLine = cu.getLineNumber(point.x);*/
		
		/**
		 * search related clone instances in project's clone set.
		 */
		SelectedCodeRange range = SharedData.range;
		
		
		if(range != null){
			CloneSets sets = clonepedia.Activator.plainSets;
			
			ArrayList<ReferrableCloneSet> referrableCloneSets = CCDemonUtil.findCodeTemplateMaterials(sets, range);
			
			if(referrableCloneSets.size() != 0){
				ReferrableCloneSet rcs = referrableCloneSets.get(0);
				mcidiff.model.CloneSet set = CCDemonUtil.adaptClonepediaModel(rcs.getCloneSet()); 
				MCIDiff diff = new MCIDiff();
				ArrayList<SeqMultiset> diffList = diff.diff(set);

				ArrayList<ConfigurationPoint> configurationPoints = 
						constructConfigurationPoints(rcs.getReferredCloneInstance(), diffList);
				
				/**
				 * At this time, we need to match the token sequence in copied clone instance to
				 * the pasted code fragments. Then the configuration point can be identified.
				 */
				
				System.out.println(diffList);
			}
			
		}
		
		System.out.println("paste");
		
		return null;
	}

	private ArrayList<ConfigurationPoint> constructConfigurationPoints(
			CloneInstance referredCloneInstance, ArrayList<SeqMultiset> diffList) {
		ArrayList<ConfigurationPoint> cpList = new ArrayList<>();
		for(SeqMultiset multiset: diffList){
			for(TokenSeq tokenSeq: multiset.getSequences()){
				mcidiff.model.CloneInstance ins = tokenSeq.getCloneInstance();
				if(referredCloneInstance.getFileLocation().equals(ins.getFileName()) &&
						referredCloneInstance.getStartLine() == ins.getStartLine() &&
						referredCloneInstance.getEndLine() == ins.getEndLine()){
					ConfigurationPoint point = new ConfigurationPoint(tokenSeq, multiset);
					cpList.add(point);
					continue;
				}
			}
		}
		
		return cpList;
	}

	
}
