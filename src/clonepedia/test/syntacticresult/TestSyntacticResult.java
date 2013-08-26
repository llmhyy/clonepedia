package clonepedia.test.syntacticresult;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;

import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.util.MinerUtil;

public class TestSyntacticResult {

	private int a = 0;
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*String resultFile = "clusters_15.0";
		MinerUtil mu = new MinerUtil();
		ArrayList<SyntacticCluster> list = (ArrayList<SyntacticCluster>)mu.deserialize(resultFile, false);
		System.out.print("");*/
		
		TestSyntacticResult tsr = new TestSyntacticResult();
		tsr.addPoperpertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("a changed");
				
			}
		});
		
		tsr.setA(1);
		
	}
	
	public int getA() {
		return a;
	}
	
	public void setA(int a) {
		int oldA = this.a;
		this.a = a;
		support.firePropertyChange("a",	oldA, a);
	}
	
	public void addPoperpertyChangeListener(PropertyChangeListener listener){
		support.addPropertyChangeListener(listener);
	}
	
	public void removePoperpertyChangeListener(PropertyChangeListener listener){
		support.removePropertyChangeListener(listener);
	}

}
