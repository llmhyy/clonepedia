package clonepedia.clonefilepraser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import clonepedia.model.ontology.CloneSets;

public abstract class CloneFileParser {
	public abstract CloneSets getCloneSets(boolean debugState, String setIdInDebug);
	
	protected Document getDocument(String filePath){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		Document doc = null;
		
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(filePath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	protected String getAttributeValue(Node node, String attributeName){
		return node.getAttributes().getNamedItem(attributeName).getNodeValue();
	}
}
