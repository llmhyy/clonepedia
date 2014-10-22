package test;

import mcidiff.main.MCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;

public class TestMain {
	
	public static void main(String[] ars){
		
		String path1 = "F://workspace//workspace_for_clonepedia_git//mcidiff//test//test1.java";
		String path2 = "F://workspace//workspace_for_clonepedia_git//mcidiff//test//test2.java";
		String path3 = "F://workspace//workspace_for_clonepedia_git//mcidiff//test//test3.java";
		
		CloneInstance instance1 = new CloneInstance(path1, 20, 22);
		CloneInstance instance2 = new CloneInstance(path2, 20, 22);
		CloneInstance instance3 = new CloneInstance(path3, 20, 22);
		
		CloneSet set = new CloneSet();
		set.addInstance(instance1);
		set.addInstance(instance2);
		set.addInstance(instance3);
		
		new MCIDiff().diff(set);
	}
}
