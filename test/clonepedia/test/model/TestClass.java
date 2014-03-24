package clonepedia.test.model;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Project;
import junit.framework.TestCase;

public class TestClass extends TestCase {
	public void testGetSimpleClassName(){
		Project project = new Project("jhotDraw", "java", "");
		Class cl = new Class("aa", project, "org.eclipse.Test");
		assertEquals(cl.getSimpleName(),"Test");
	}
}
