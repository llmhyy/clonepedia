package clonepedia.actions.steps;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * For the regulation which must be followed for each step to generate pattern.
 * @author linyun
 *
 */
public interface Step {
	
	public void run(IProgressMonitor monitor);
	
	public int getTotolEffort();
}
