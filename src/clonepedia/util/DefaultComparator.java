package clonepedia.util;

public class DefaultComparator implements BoolComparator {

	@Override
	public boolean isMatch(Object obj1, Object obj2) {
		return obj1.equals(obj2);
	}

}
