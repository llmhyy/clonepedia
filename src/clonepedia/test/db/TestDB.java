package clonepedia.test.db;

import java.util.Properties;

import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.model.ontology.Project;
import junit.framework.TestCase;

public class TestDB extends TestCase {
	private OntologicalDBDataFetcher fetcher = new OntologicalDBDataFetcher();
	
	/*public void testDBConnection(){
		Connection conn = DBManager.getConnection();
		assertNotNull(conn);
	}*/
	
	public void testProjectStore() throws Exception{
		Project project = new Project("jhotDraw", "java", "");
		fetcher.storeProject(project);
		
		Properties properties = new Properties();
		properties.put("projectName", "jhotDraw7.0.6");
		
		//Project prj = fetcher.getProject(properties);
		//assertTrue(prj.equals(project));
	}
	
	/*public void testDeleteProject(){
		Properties properties = new Properties();
		properties.put("projectName", "jhotDraw7.0.6");
		
		op.delete(Project.class, properties);
	}
	
	public void testCheckIfanObjectExist(){
		Properties properties = new Properties();
		properties.put("projectName", "jhotDraw7.0.6");
		properties.put("programmingLanguage", "java");
		
		boolean flag = op.checkIfanObjectExist(Project.class, properties);
		assertTrue(flag);
	}*/
	
}
