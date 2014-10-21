package clonepedia.test.syntacticresult;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;

//import clonepedia.featuretemplate.TemplateBuilder;
import clonepedia.model.cluster.SyntacticCluster;
import clonepedia.model.template.CandidateTemplate;
import clonepedia.model.template.CandidateTemplateList;
import clonepedia.model.template.SubCandidateTemplate;
import clonepedia.model.template.Template;
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
		
		/*TestSyntacticResult tsr = new TestSyntacticResult();
		tsr.addPoperpertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("a changed");
				
			}
		});
		
		tsr.setA(1);*/
		
		CandidateTemplateList featureGroups = (CandidateTemplateList)MinerUtil.deserialize("E:\\eclipse_for_model\\configurations\\JHotDraw7.0.6\\featureGroups", true);
		
		CandidateTemplateList significantGroups = new CandidateTemplateList();
		for(CandidateTemplate feature: featureGroups){
			int count = 0;
			for(SubCandidateTemplate tfg: feature){
				count += tfg.getTemplateMethodGroupList().size();
			}
			if(count > 3){
				
				if(feature.toString().contains("Main")){
					System.out.println();
				}
				
//				TemplateBuilder templateBuilder = new TemplateBuilder(feature);
//				Template template = templateBuilder.buildTemplate();
//				
//				feature.setTemplate(template);
//				significantGroups.add(feature);
			}
		}
		
		try {
			//MinerUtil.serialize(tmgList, "tmgList", false);
			MinerUtil.serialize(significantGroups, "E:\\eclipse_for_model\\configurations\\JHotDraw7.0.6\\totalTFGs", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
