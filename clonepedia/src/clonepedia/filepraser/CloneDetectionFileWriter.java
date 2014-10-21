package clonepedia.filepraser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.util.Settings;

public class CloneDetectionFileWriter{
	public void writeToXML(CloneSets cloneSets){
		
		ArrayList<String> filePathList = getDistinctFilePath(cloneSets);
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("cloneReport");
			doc.appendChild(rootElement);
			
			for(int i=0; i<filePathList.size(); i++){
				Element fileElement = doc.createElement("sourceFile");
				
				Attr idAttr = doc.createAttribute("id");
				idAttr.setValue(String.valueOf(i));
				fileElement.setAttributeNode(idAttr);
				
				Attr locationAttr = doc.createAttribute("location");
				locationAttr.setValue(filePathList.get(i));
				fileElement.setAttributeNode(locationAttr);
				
				rootElement.appendChild(fileElement);
			}
			
			
			for(CloneSet set: cloneSets.getCloneList()){
				Element cloneSetElement = doc.createElement("cloneClass");
				Attr idAttr = doc.createAttribute("id");
				idAttr.setValue(set.getId());
				cloneSetElement.setAttributeNode(idAttr);
				
				for(CloneInstance instance: set){
					
					Element cloneInstanceElement = doc.createElement("clone");
					cloneInstanceElement.setAttribute("startLine", String.valueOf(instance.getStartLine()-1));
					cloneInstanceElement.setAttribute("lineCount", String.valueOf(instance.getEndLine()-instance.getStartLine()+1));
					cloneInstanceElement.setAttribute("sourceFileId", 
							String.valueOf(getIndexInList(filePathList, instance.getFileLocation())));
					cloneSetElement.appendChild(cloneInstanceElement);
				}
				
				rootElement.appendChild(cloneSetElement);
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			File cloneFile = new File(Settings.inputCloneFile);
			
			String dirPath = Settings.inputCloneFile.substring(0, Settings.inputCloneFile.lastIndexOf("\\"));
			File dir = new File(dirPath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			if(!cloneFile.exists()){
				cloneFile.createNewFile();
			}
			
			StreamResult result = new StreamResult(cloneFile);
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> getDistinctFilePath(CloneSets cloneSets){
		ArrayList<String> filePathList = new ArrayList<String>();
		
		for(CloneSet set: cloneSets.getCloneList()){
			for(CloneInstance instance: set){
				if(!filePathList.contains(instance.getFileLocation())){
					filePathList.add(instance.getFileLocation());
				}
			}
		}
		
		return filePathList;
	}
	
	private int getIndexInList(ArrayList<String> filePathList, String filePath){
		for(int i=0; i<filePathList.size(); i++){
			String p = filePathList.get(i);
			if(p.equals(filePath)){
				return i;
			}
		}
		
		return -1;
	}
}
