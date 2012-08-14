package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.HashSet;


public interface IContainer {
	
	public ArrayList<IContent> getContent();
	public HashSet<CloneSetWrapper> getAllContainedCloneSet();
	public HashSet<CloneSetWrapper> getIntersectionWith(IContainer container);
	public double value();
	public int size();
	public int diversity();
}
