package ccdemon.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ccdemon.util.CCDemonUtil;


public class PasteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CCDemonUtil.callBackDefaultEvent("paste", event);
		
		/**
		 * search related clone instances in project's clone set.
		 */
		
		System.out.println("paste");
		return null;
	}

}
