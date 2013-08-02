package clonepedia.test.patternextraction;

import java.io.Serializable;
import java.util.HashSet;

public class A implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3866621802874279852L;

	private B b;

	public A() {
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		A other = (A) object;
		return getB().equals(other.getB());
	}

	public int hashCode() {
		//return getB().hashCode(); // FAILS HERE in deserialization.
		return super.hashCode();
	}

	public void setB(B b) {
		this.b = b;
	}

	public B getB() {
		return b;
	}
}
