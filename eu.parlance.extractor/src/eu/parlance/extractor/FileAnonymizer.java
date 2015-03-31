package eu.parlance.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileAnonymizer {

	
	public FileAnonymizer(){
		
	}
	
	public static void renameFiles (String folderName) {
		
		File folder = new File(folderName);
		System.out.println("folderName: " + folder.toString());
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(folder.list()));
		
		String sonName, fileName;
		
		for (int i=0; i<names.size(); i++){
			sonName = names.get(i);
			System.out.println("sonName: " + sonName );
			
			//File sonFolder = new File(folderName + "\\" + sonName); //Windows
			File sonFolder = new File(folderName + File.separator + sonName); //Windows or UNIX
			
			if (sonFolder.isDirectory()) {
				System.out.println("IT IS A DIRECTORY!");
				System.out.println("================ CHILDREN: =================================");
				ArrayList<String> files = new ArrayList<String>(Arrays.asList(sonFolder.list()));
				
				for (int j=0; j<files.size(); j++){
					
					fileName = files.get(j);
					System.out.println("fileName: " + fileName );
					
					if (fileName.contentEquals("session.xml")) {
						System.out.print("----------------->SESSION");
						System.out.println();
						
						DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder;
						
						try {
							docBuilder = docBuilderFactory.newDocumentBuilder();
							Document doc = docBuilder.parse(new File(folderName + File.separator + sonName + File.separator + fileName));
							
							NodeList sessionList = doc.getElementsByTagName("session");
							
							Node node = sessionList.item(0);
							String session = node.getFirstChild().getTextContent();
						
							System.out.println("session tag: " + session);
							System.out.println("Father directory " + sonName);
							
							//MODIFYING the session tag:
							//node.getFirstChild().setTextContent(sonName);
							node.getFirstChild().setNodeValue(sonName);
							
							//System.out.println("new session tag: " + node.getFirstChild().getTextContent());
							System.out.println("new session tag: " + node.getFirstChild().getNodeValue());
						
							//It is necessary to save parsed and changed DOM document in the xml file:
							Transformer transformer = TransformerFactory.newInstance().newTransformer();
							StreamResult output = new StreamResult(new File(folderName + File.separator + sonName + File.separator + fileName));
							Source input = new DOMSource(doc);

							transformer.transform(input, output);
							
							System.out.println("Done! tag val modified successfuly");
							
						} catch (java.lang.Exception e) {
							e.printStackTrace();
						}
						
					}
					
				}
			}
			}
		
	}
	
	public static void main(String[] args) {
		
		try{
			if (args.length<1){
				System.out.println("Usage: script folderPath");
				System.exit(0);
			}
			
			String folderPath = args[0];
			renameFiles (folderPath);
			
		} catch(java.lang.Exception ex) {
			ex.printStackTrace();
			
		}
	
	}
	
}
