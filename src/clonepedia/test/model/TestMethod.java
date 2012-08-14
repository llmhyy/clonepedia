package clonepedia.test.model;

import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.Method;
import junit.framework.TestCase;

public class TestMethod extends TestCase{
	public void testIsDefinedInClass(){
		Class claz = new Class("cldfadf", null, "Test");
		Method method = new Method(claz, "fff", null, null);
		assertTrue(method.isDeclaredInClass());
	}
}
