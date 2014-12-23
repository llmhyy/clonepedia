package test;

import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import types.Parent;

public class TestSub {
	public static void main(String[] args){
		ConfigurationBuilder cb = new ConfigurationBuilder().setScanners(new SubTypesScanner());
		cb.addUrls(ClasspathHelper.forClass(Parent.class));
	    Reflections reflections = new Reflections(cb);
	
		Set<Class<? extends Parent>> subset = reflections.getSubTypesOf(Parent.class);
		System.out.println(subset.toString());
	}
}