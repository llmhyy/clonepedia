package ccdemon.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ccdemon.util.CCDemonUtil;

public class CopyHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CCDemonUtil.callBackDefaultEvent("copy", event);
		
		/**
		 * record the location of copied code
		 */
		
		
		System.out.println("copy");
		return null;
	}

}
