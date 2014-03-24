package clonepedia.test.patternextraction;

import java.io.Serializable;
import java.util.Set;

public class B implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 646778394447695300L;

	private String name;
	private Set aSet;
	
	public B() {
	}

	public B(String name) {
		this.name = name;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		B other = (B) object;
		return getName().equals(other.getName());
	}

	public int hashCode() {
		return getName().hashCode();
	}

	public String getName() {
		return name;
	}

	public void setASet(Set aSet) {
		this.aSet = aSet;
	}

	public Set getASet() {
		return aSet;
	}
}
