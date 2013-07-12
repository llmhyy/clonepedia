package clonepedia.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import clonepedia.dialog.SkeletonGenerationDialog;

public class SkeletonGenerationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Iterator<Object> iterator = strucSelection.iterator();
			while(iterator.hasNext()) {
				Object element = iterator.next();
				System.out.println(element.toString());
				
				SkeletonGenerationDialog dialog = new SkeletonGenerationDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				
				dialog.open();
				
			}
		}
		return null;
	}

}
