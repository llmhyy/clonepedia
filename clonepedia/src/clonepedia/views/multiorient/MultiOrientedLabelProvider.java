package clonepedia.views.multiorient;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.syntactic.ClonePatternGroup;
import clonepedia.model.viewer.PathPatternGroupWrapper;
import clonepedia.model.viewer.PatternGroupCategory;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.PatternGroupWrapper;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.programmingelement.ClassWrapper;
import clonepedia.model.viewer.programmingelement.FieldWrapper;
import clonepedia.model.viewer.programmingelement.InterfaceWrapper;
import clonepedia.model.viewer.programmingelement.MergeableSimpleElementWrapper;
import clonepedia.model.viewer.programmingelement.MethodWrapper;
import clonepedia.model.viewer.programmingelement.ProgrammingElementWrapper;
import clonepedia.model.viewer.programmingelement.VariableWrapper;
import clonepedia.summary.NaturalLanguateTemplate;

public class MultiOrientedLabelProvider implements ILabelProvider {

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

	@SuppressWarnings({ "deprecation", "restriction" })
	@Override
	public Image getImage(Object element) {
		if(element instanceof TopicWrapper)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
		else if(element instanceof ClonePatternGroupWrapper)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		else if(element instanceof PathPatternGroupWrapper)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		else if(element instanceof CloneSetWrapper)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		else if(element instanceof CloneInstance)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		else if(element instanceof PatternGroupCategory)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
		else if(element instanceof MethodWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ENV_VAR);
		else if(element instanceof VariableWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CUNIT);
		else if(element instanceof FieldWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_LOCAL_VARIABLE);
		else if(element instanceof InterfaceWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
		else if(element instanceof ClassWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
		else if(element instanceof MergeableSimpleElementWrapper)
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_IMPDECL);
		else if(element instanceof ProgrammingElementWrapper)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_COLLAPSEALL);
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof TopicWrapper)
			return ((TopicWrapper)element).getTopic().getTopicString();
		else if(element instanceof PatternGroupWrapper){
			try {
				//return ((PatternGroupWrapper)element).getEpitomise3();
				return ((PatternGroupWrapper)element).getEpitomise();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(element instanceof CloneSetWrapper){
			CloneSetWrapper set = (CloneSetWrapper)element;
			return set.getCloneSet().getId() + "<ins: " + set.getCloneSet().size()+ ", frag: " + set.computeAverageCodeFragmentLength() + ">";
		}
		else if(element instanceof CloneInstance){
			CloneInstance instance = (CloneInstance)element;
			return instance.toString();
		}
		else if(element instanceof PatternGroupCategory)
			return ((PatternGroupCategory)element).getName();
		else if(element instanceof ProgrammingElementWrapper)
			return ((ProgrammingElementWrapper) element).getName() + 
					NaturalLanguateTemplate.getSupportingElementDescription(((ProgrammingElementWrapper) element).getElement())
					/*+ "  (" + String.valueOf(((ProgrammingElementWrapper)element).getContainedClonePatternNumber()) + ")"*/;
		
		return null;
	}

}
