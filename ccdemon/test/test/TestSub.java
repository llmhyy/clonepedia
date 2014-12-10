package test;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.text.IDocument;

import java.lang.Number;

public class TestSub {
	public static void main(String[] args){
		ConfigurationBuilder cb = new ConfigurationBuilder().setScanners(new SubTypesScanner());
		cb.addUrls(ClasspathHelper.forClass(IDocument.class));
	    Reflections reflections = new Reflections(cb);
	
		Set<Class<? extends IDocument>> subset = reflections.getSubTypesOf(IDocument.class);
		System.out.println(subset.toString());
	}
}