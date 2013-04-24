package clonepedia;



import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.ImageUI;
import clonepedia.util.MinerUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Clonepedia"; //$NON-NLS-1$
	
	private static CloneSets sets;

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public static CloneSets getCloneSets(){
		if(sets == null){
			sets = (CloneSets) MinerUtil.deserialize("sets");
		}
		
		return sets;
	}
	
	public static void setCloneSets(CloneSets setList){
		sets = setList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		sets = (CloneSets) MinerUtil.deserialize("sets");
		
		/*int sum = 0;
		for(CloneSet set: sets.getCloneList()){
			if(set.size() <= 3){				
				for(CloneInstance instance: set){
					sum += instance.getEndLine() - instance.getStartLine() + 1;
				}
			}
		}*/
		
		ImageRegistry imgReg = getImageRegistry();
		imgReg.put(ImageUI.TOPIC, getImageDescriptor(ImageUI.TOPIC));
		imgReg.put(ImageUI.CLONE_PATTERN, getImageDescriptor(ImageUI.CLONE_PATTERN));
		imgReg.put(ImageUI.SHOW_DETAILS, getImageDescriptor(ImageUI.SHOW_DETAILS));
		imgReg.put(ImageUI.PATTERN_HIERARCHY, getImageDescriptor(ImageUI.PATTERN_HIERARCHY));
		imgReg.put(ImageUI.SORT_BY_ALPHABETIC, getImageDescriptor(ImageUI.SORT_BY_ALPHABETIC));
		imgReg.put(ImageUI.SORT_BY_CONTAINED_PATTERN_NUMBER, getImageDescriptor(ImageUI.SORT_BY_CONTAINED_PATTERN_NUMBER));
		imgReg.put(ImageUI.SORT_BY_DEFAULT, getImageDescriptor(ImageUI.SORT_BY_DEFAULT));
		imgReg.put(ImageUI.SORT_BY_SIZE, getImageDescriptor(ImageUI.SORT_BY_SIZE));
		imgReg.put(ImageUI.SORT_BY_DIVERSITY, getImageDescriptor(ImageUI.SORT_BY_DIVERSITY));
		imgReg.put(ImageUI.FILTER, getImageDescriptor(ImageUI.FILTER));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	@SuppressWarnings("deprecation")
	public static ImageDescriptor getImageDescriptor(String path) {
		try {
			URL installURL = getDefault().getDescriptor().getInstallURL();
			//URL installURL = getPlugin().getBundle().getEntry("/");
			URL url = new URL(installURL, path);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
		//return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
