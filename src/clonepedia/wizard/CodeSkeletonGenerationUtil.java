package clonepedia.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;

import clonepedia.util.Settings;

public class CodeSkeletonGenerationUtil {
	public static IPackageFragmentRoot getPackageFragmentRoot(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject project = root.getProject(Settings.projectName);
		IJavaProject jProject = JavaCore.create(project);
		
		if (jProject.exists()) {
			IPackageFragmentRoot initRoot = null;
			IPackageFragmentRoot[] roots;
			try {
				roots = jProject.getPackageFragmentRoots();
				for (int i= 0; i < roots.length; i++) {
					if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
						initRoot = roots[i];
						
	
						return initRoot;
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
