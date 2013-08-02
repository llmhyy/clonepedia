package clonepedia.test.patternextraction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class Test {
	public Test() {
	}

	public static void main(String argv[]) {
		A a = new A();
		B b = new B("FOO");
		a.setB(b);
		HashSet aSet = new HashSet();
		aSet.add(a);
		b.setASet(aSet);

		try {
			FileOutputStream fileOut = new FileOutputStream("D:\\linyun\\test.srl");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(a);
			fileOut.close();

			FileInputStream fileIn = new FileInputStream("D:\\linyun\\test.srl");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			A rehydratedA = (A) in.readObject();
			// Won't get this far, will fail above...
			System.out.println(rehydratedA);
		} catch (Exception anyException) {
			anyException.printStackTrace();
		}
	}
}
