package clonepedia.filepraser;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.Settings;

public class CloneDetectionFileParser extends FileParser {
	
	private CloneSets cloneSets = new CloneSets();

	/**
	 * if the debugState is specified as true, then we will only show the clone set with the given setIdInDebug. 
	 * @param debugState
	 * @param setIdInDebug
	 */
	public CloneDetectionFileParser(boolean debugState, String setIdInDebug){
		Document doc = getDocument(Settings.inputCloneFile);
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		
		Node root = doc.getElementsByTagName("cloneReport").item(0);
		try {
			NodeList cloneClassNodeList = (NodeList)xpath.evaluate("child::cloneClass", root, XPathConstants.NODESET);
			for(int i=0; i<cloneClassNodeList.getLength(); i++){
				
				Node cloneClassNode = cloneClassNodeList.item(i);
				String id = getAttributeValue(cloneClassNode, "id");
				CloneSet cloneSet = new CloneSet(id);
				
				NodeList cloneNodeList = (NodeList)xpath.evaluate("child::clone", cloneClassNode, XPathConstants.NODESET);
				for(int j=0; j<cloneNodeList.getLength(); j++){
					
					if(debugState)
						if(!id.equals(setIdInDebug))continue;
					
					
					Node cloneNode = cloneNodeList.item(j);
					String startLineString = getAttributeValue(cloneNode, "startLine");
					String lineCountString = getAttributeValue(cloneNode, "lineCount");
					int startLine = Integer.valueOf(startLineString) + 1;
					int endLine = startLine -1 + Integer.valueOf(lineCountString);
					
					String sourceFileId = getAttributeValue(cloneNode, "sourceFileId");
					
					String expression = "child::sourceFile[@id='" + sourceFileId + "']";
					
					Node sourceFileNode = (Node)xpath.evaluate(expression, root, XPathConstants.NODE);
					String filePath = getAttributeValue(sourceFileNode, "location");
					
					//String cloneInstanceId = "ci" + UUID.randomUUID();
					CloneInstance cloneInstance = new CloneInstance(cloneSet, filePath, startLine, endLine);
					
					cloneSet.add(cloneInstance);
				}
				
				
				cloneSets.add(cloneSet);
				//System.out.println();
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public CloneSets getCloneSets() {
		return cloneSets;
	}

}
