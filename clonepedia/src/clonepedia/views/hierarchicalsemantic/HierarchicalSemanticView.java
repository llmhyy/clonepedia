package clonepedia.views.hierarchicalsemantic;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import clonepedia.model.cluster.ICluster;
import clonepedia.model.cluster.SemanticCluster;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.semantic.SemanticClusteringer;
import clonepedia.syntactic.SyntacticClusteringer;
import clonepedia.util.MinerProperties;
import clonepedia.util.MinerUtil;
import clonepedia.views.SummaryView;
@Deprecated
public class HierarchicalSemanticView extends SummaryView {

	private ArrayList<SemanticCluster> semanticClusterList;
	//private TreeViewer viewer;

	private class TreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ArrayList) {
				ArrayList<SemanticCluster> list = (ArrayList<SemanticCluster>) inputElement;
				return list.toArray(new SemanticCluster[0]);
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ICluster) {
				ICluster cluster = (ICluster) parentElement;
				ArrayList<ICluster> units = cluster.getUnits();
				if (units.size() != 0) {
					return units.toArray(new ICluster[0]);
				} else {
					return cluster.getCloneSets().toArray(new CloneSet[0]);
				}
			} else if (parentElement instanceof CloneSet) {
				CloneSet set = (CloneSet) parentElement;
				return set.toArray(new CloneInstance[0]);
			}

			return null;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof CloneSet)
				return ((CloneSet) element).getResidingCluster();
			else if (element instanceof ICluster)
				return ((ICluster) element).getParentCluster();
			else if (element instanceof CloneInstance)
				return ((CloneInstance) element).getCloneSet();
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if ((element instanceof ICluster) || (element instanceof CloneSet)) {
				return true;
			}
			return false;
		}

	}

	private class TreeLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@SuppressWarnings("deprecation")
		@Override
		public Image getImage(Object element) {
			
			if (element instanceof SyntacticCluster)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
			else if (element instanceof SemanticCluster)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			else if (element instanceof CloneSet)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			else if (element instanceof CloneInstance)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);

			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof SyntacticCluster)
				return "syntactic cluster";
			else if (element instanceof SemanticCluster)
				return ((SemanticCluster) element).getLabel();
			else if (element instanceof CloneSet)
				return ((CloneSet) element).getId();
			else if (element instanceof CloneInstance)
				return ((CloneInstance) element).getId();

			return null;
		}

	}

	public HierarchicalSemanticView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		viewer = new CheckboxTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		//viewer.addDoubleClickListener(doubleClickListener);
		
		if(this.semanticClusterList == null)
			initialize();
		
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setInput(this.semanticClusterList);
		
		
		getSite().setSelectionProvider(viewer);
		
		//hookContextMenu();
	}

	

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		Action refresh = new Action() {
			public void run() {

				initialize();
				viewer.refresh();
			}
		};
		refresh.setText("Refresh");
		menuMgr.add(refresh);
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		try {
			//System.out.println("Clustering started");
			semanticClusterList = (ArrayList<SemanticCluster>) MinerUtil
					.deserialize("semantic_clusters", false);
			
			//ArrayList<SyntacticCluster> allList = allCluster.doClustering();

			/*semanticClusterList = new SemanticClusteringer()
					.doClustering(clusters);
			for (SemanticCluster semC : semanticClusterList) {
				for (ICluster clus : semC.getUnits()) {
					SyntacticCluster synC = (SyntacticCluster) clus;
					synC.doInnerClustering();
					// System.out.print("");
				}
				// System.out.print("");
			}
			System.out.print("Clustering finished");*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void openNewTab(Object selectionObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createTreeDetails(Tree tree, Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createTextDescription(FormText text, Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createTopicDescription(FormText text, Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createClonePatternDescription(FormText text,
			Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createInvolvedCloneSetsDescription(FormText text,
			Object targetObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addSearchButtonListener(Button searchButton) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addSearchTextListener(Text searchText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createSections(ScrolledForm form, Object targetObject) {
		// TODO Auto-generated method stub
		
	}

}
