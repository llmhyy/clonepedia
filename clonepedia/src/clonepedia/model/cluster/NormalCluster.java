/**
 * 
 */
package clonepedia.model.cluster;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class NormalCluster extends ArrayList<IClusterable>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3277774859957841224L;

	public void merge(NormalCluster normalCluster) {
		this.addAll(normalCluster);
		
	}
	
}
