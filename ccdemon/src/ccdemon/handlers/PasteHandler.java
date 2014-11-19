package ccdemon.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import mcidiff.main.MCIDiff;
import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ccdemon.model.ConfigurationPoint;
import ccdemon.model.ConfigurationPointSet;
import ccdemon.model.ReferrableCloneSet;
import ccdemon.model.SelectedCodeRange;
import ccdemon.proposal.RankedCompletionProposal;
import ccdemon.util.CCDemonUtil;
import ccdemon.util.SharedData;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSets;


public class PasteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		/**
		 * search related clone instances in project's clone set.
		 */
		SelectedCodeRange copiedRange = SharedData.copiedRange;
		
		ITextSelection textSelection = (ITextSelection) HandlerUtil.getActivePart(event).getSite().getSelectionProvider().getSelection();
		int startPositionInPastedFile = textSelection.getOffset();
		
		CloneSets sets = clonepedia.Activator.plainSets;
		ArrayList<ReferrableCloneSet> referrableCloneSets = CCDemonUtil.findCodeTemplateMaterials(sets, copiedRange);
		
		if(referrableCloneSets.size() != 0){
			ConfigurationPointSet cps = 
					identifyConfigurationPoints(event, startPositionInPastedFile, copiedRange, referrableCloneSets);
			
			cps.prepareForInstallation(referrableCloneSets);

			CCDemonUtil.setActiveEditor(event);
			CCDemonUtil.positions.clear();
			installConfigurationPointsOnCode(cps);
		}
		
		return null;
	}



	/**
	 * @param cps
	 */
	public static void installConfigurationPointsOnCode(ConfigurationPointSet cps) {
		AbstractTextEditor activeEditor = CCDemonUtil.getActiveEditor();
		ISourceViewer sourceViewer = (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);
		IDocument document= sourceViewer.getDocument();
		
		try{
			LinkedModeModel model = new LinkedModeModel();
			for(ConfigurationPoint cp: cps.getConfigurationPoints()){
				LinkedPositionGroup group = new LinkedPositionGroup();
				ICompletionProposal[] proposals = new ICompletionProposal[cp.getCandidates().size()]; 
				for(int i=0; i<proposals.length; i++){
					proposals[i] = new RankedCompletionProposal(cp.getCandidates().get(i).getText(), cp.getModifiedTokenSeq().getStartPosition(),
							cp.getModifiedTokenSeq().getPositionLength(), 0, 0);
				}
				
				LinkedPosition lp = new ProposalPosition(document, cp.getModifiedTokenSeq().getStartPosition(), 
						cp.getModifiedTokenSeq().getPositionLength(), proposals);
				for(ICompletionProposal icp : proposals){
					RankedCompletionProposal rcp = (RankedCompletionProposal) icp;
					rcp.setPosition(lp);
				}
				
				group.addPosition(lp);
				model.addGroup(group);
				CCDemonUtil.positions.add(lp);
			}
			model.forceInstall();
			CustomLinkedModeUI ui = new CustomLinkedModeUI(model, sourceViewer);
			CustomLinkedModeUIFocusListener listener = new CustomLinkedModeUIFocusListener();
			listener.setConfigurationPointSet(cps);
			ui.setPositionListener(listener);
			//ui.setExitPosition(sourceViewer, startPositionInPastedFile, copiedRange.getPositionLength(), Integer.MAX_VALUE);
			ui.enter();
		}
		catch(BadLocationException e){
			e.printStackTrace();
		}
	}
	
	

	private ConfigurationPointSet identifyConfigurationPoints(ExecutionEvent event, int startPositionInPastedFile, 
			SelectedCodeRange copiedRange, ArrayList<ReferrableCloneSet> referrableCloneSets ) throws ExecutionException {
		if(copiedRange != null){
			ReferrableCloneSet rcs = referrableCloneSets.get(0);
			mcidiff.model.CloneSet set = CCDemonUtil.adaptClonepediaModel(rcs.getCloneSet()); 
			MCIDiff diff = new MCIDiff();
			ArrayList<SeqMultiset> diffList = diff.diff(set);

			ArrayList<ConfigurationPoint> configurationPoints = 
					constructConfigurationPoints(rcs.getReferredCloneInstance(), diffList);
			/**
			 * filter out those configuration points which are not copied.
			 */
			filterUnrelevantConfigurationPoints(configurationPoints, copiedRange);
			/**
			 * At this time, we need to match the token sequence in copied clone instance to
			 * the pasted code fragments. Then the configuration point can be identified.
			 */
			appendConfigurationPointsWithPastedSeq(event, copiedRange, configurationPoints, startPositionInPastedFile);
			
			return new ConfigurationPointSet(configurationPoints);
		}
		
		return new ConfigurationPointSet();
	}

	/**
	 * At this time, we need to match the token sequence in copied clone instance to
	 * the pasted code fragments. Then the configuration point can be identified.
	 * @param event
	 * @param copiedRange
	 * @param configurationPoints
	 * @throws ExecutionException
	 */
	private void appendConfigurationPointsWithPastedSeq(ExecutionEvent event, SelectedCodeRange copiedRange,
			ArrayList<ConfigurationPoint> configurationPoints, int startPositionInPastedFile)
			throws ExecutionException {
		ArrayList<Token> pastedTokenList = parsePastedTokens(event, copiedRange.getPositionLength());
		int offsetFromCopiedCodeToPastedCode = startPositionInPastedFile - copiedRange.getStartPosition();
		
		for(ConfigurationPoint point: configurationPoints){
			TokenSeq copiedSeq = point.getCopiedTokenSeq();
			int startPosInPasted = copiedSeq.getStartPosition() + offsetFromCopiedCodeToPastedCode;
			int endPosInPasted = copiedSeq.getEndPosition() + offsetFromCopiedCodeToPastedCode;
			
			TokenSeq pastedSeq = constructTokenSeqInPastedCode(pastedTokenList, startPosInPasted, endPosInPasted);
			if(pastedSeq.isEpisolonTokenSeq()){
				Token epsolonToken = new Token(Token.episolonSymbol, null, null, startPosInPasted, endPosInPasted);
				pastedSeq.addToken(epsolonToken);
			}
			
			point.setModifiedTokenSeq(pastedSeq);
		}
	}
	
	private TokenSeq constructTokenSeqInPastedCode(ArrayList<Token> pastedTokenList, int startPosInPasted, int endPosInPasted){
		TokenSeq ts = new TokenSeq();
		for(Token t: pastedTokenList){
			if(t.getStartPosition() >= startPosInPasted && t.getEndPosition() <= endPosInPasted){
				ts.addToken(t);
			}
		}
		
		return ts;
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

	private void filterUnrelevantConfigurationPoints(
			ArrayList<ConfigurationPoint> configurationPoints, SelectedCodeRange range) {
		Iterator<ConfigurationPoint> iterator = configurationPoints.iterator();
		while(iterator.hasNext()){
			ConfigurationPoint cp = iterator.next();
			TokenSeq seq = cp.getCopiedTokenSeq();
			
			if(!(range.getStartPosition()<=seq.getStartPosition() && 
					range.getEndPosition()>=seq.getEndPosition())){
				iterator.remove();
			}
		}
		
	}

	private ArrayList<Token> parsePastedTokens(ExecutionEvent event, int length) throws ExecutionException {
		AbstractTextEditor activeEditor = (AbstractTextEditor) HandlerUtil.getActiveEditor(event);
		ISourceViewer sourceViewer = (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);	
		IDocument document = sourceViewer.getDocument();
		ITextSelection textSelection = (ITextSelection) HandlerUtil.getActivePart(event).
				getSite().getSelectionProvider().getSelection();
		int cursorOffset = textSelection.getOffset();
		
		try {
			int lineNumber = document.getLineOfOffset(cursorOffset) + 1;
			System.out.println("start line: " + lineNumber);
			
			CCDemonUtil.callBackDefaultEvent("paste", event);
			
			String pastedContent = document.get(cursorOffset, length);
			
			ArrayList<Token> pastedTokenList = parseTokenFromText(pastedContent, cursorOffset);
			
			return pastedTokenList;
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	private ArrayList<Token> parseTokenFromText(String pastedContent, int basePosition) {
		ArrayList<Token> tokenList = new ArrayList<>();
		
		IScanner scanner = ToolFactory.createScanner(false, false, false, false);
		scanner.setSource(pastedContent.toCharArray());
		
		Token previous = null;
		while(true){
			try {
				int t = scanner.getNextToken();
				if(t == ITerminalSymbols.TokenNameEOF){
					break;
				}
				String tokenName = new String(scanner.getCurrentTokenSource());
				
				int startPosition = basePosition + scanner.getCurrentTokenStartPosition();
				int endPosition = basePosition + scanner.getCurrentTokenEndPosition()+1;
				
				Token token = new Token(tokenName, null, null, startPosition, endPosition);
				tokenList.add(token);
				
				token.setPreviousToken(previous);
				if(previous != null){
					previous.setPostToken(token);
				}
				previous = token;
			} catch (InvalidInputException e) {
				e.printStackTrace();
			}
			
		}
		
		return tokenList;
	}

	
}
