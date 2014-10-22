package test;

import mcidiff.main.MCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;

public class TestMain {
	
	public static void main(String[] ars){
		
		String path1 = "test/test1.java";
		String path2 = "test/test2.java";
		String path3 = "test/test3.java";
		
		CloneInstance instance1 = new CloneInstance(path1, 1, 24);
		CloneInstance instance2 = new CloneInstance(path2, 1, 24);
		CloneInstance instance3 = new CloneInstance(path3, 18, 23);
		
		CloneSet set = new CloneSet();
		set.addInstance(instance1);
		set.addInstance(instance2);
		set.addInstance(instance3);
		
		new MCIDiff().diff(set);
	}
}
