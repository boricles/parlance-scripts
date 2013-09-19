package eu.parlance.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class CuratorPostProcessing {

	
	static Connection connection = null;
	
	public CuratorPostProcessing() {
		
	}
	
	
	protected Connection getDatabaseConnection () {
		  String driverClass = "com.mysql.jdbc.Driver";
		  //String connectionURL = "jdbc:mysql://172.18.3.150:3306/parlance_feedback?useUnicode=true&characterEncoding=UTF-8";
		  String connectionURL = "jdbc:mysql://172.18.3.150:3306/parlance_feedback?useUnicode=true&characterEncoding=UTF-8";
		  String connectionUser = "root";
		  String connectionUserPassword = "root";
		  
		  try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    // If you are using any other database then load the right driver here.
		 
		    //Create the connection using the static getConnection method
		    Connection con = null;
			try {
				con = DriverManager.getConnection (connectionURL,connectionUser,connectionUserPassword);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return con;
		
	}
	
	protected ResultSet getDatabaseConent(String system) {
		try {
			
			//connection = getDatabaseConnection();
		 
		    //Create a Statement class to execute the SQL statement
		    Statement stmt = connection.createStatement();
		 
		    //Execute the SQL statement and get the results in a Resultset
		    String query = "SELECT * FROM " + system + "_transcriptions order by audio, length(transcribed_text) desc";
		    
		    //System.out.println(query);
		    ResultSet rs = stmt.executeQuery(query);
		 
		    // Iterate through the ResultSet, displaying two values
		    // for each row using the getString method
		 
		    return rs;
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	protected String getCorrespondingSystemUtterance(String sessionFile, String fileName) {
		String utterance = "";
		try
		{
			String turnNum = null;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File(sessionFile));

			//get the turnNum from userturn when name == fname
			NodeList userTurnsList = doc.getElementsByTagName("userturn");
			int userTurns = userTurnsList.getLength();

			for (int i=0;i<userTurns && turnNum == null;i++) {
				Element current = (Element)userTurnsList.item(i);
				NodeList userNodes = current.getChildNodes();
				for (int j=0; j<userNodes.getLength(); j++) {
					Node cur = userNodes.item(j);
					//System.out.println(cur.getNodeName());
					if (cur.getNodeName().equals("rec")) {
						Element e = (Element) cur;
						String name = e.getAttribute("fname");
						if (name.equals(fileName)) {
							turnNum = current.getAttributeNode("turnnum").getNodeValue();
							break;
						}
						else
							break;
					}
				}			
			}
			
			//get the system utterance for turnNum
			
			
			NodeList systemTurnsList = doc.getElementsByTagName("systurn");
			int systemTurns = systemTurnsList.getLength();
			
			for (int i=0; i<systemTurns && utterance.isEmpty();i++) {
				Node cur = systemTurnsList.item(i);
				Element e = (Element) cur;
				String sysTurn = e.getAttribute("turnnum");
				if (sysTurn.equals(turnNum)) {
					
					NodeList sysChilds = cur.getChildNodes();
					for (int j=0;j<sysChilds.getLength();j++) {
						Node cursor = sysChilds.item(j);
						if (cursor.getNodeName().equals("prompt")) {
							utterance = cursor.getTextContent();
							break;
						}
					}
				}
			}
			
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return utterance;

	}
	
	protected String getPreviousSystemUtterance(String folder, String logLocation, String line, /*new*/ String wavFile) {
		
		
		int indexLogWord = logLocation.indexOf("log/");
		String sessionFile = "";
		if (indexLogWord != -1) {
			String aux = logLocation.substring(indexLogWord+4);
			sessionFile = folder + "/" +aux + "/session.xml";
		}
		else
			sessionFile = folder + "/" +logLocation + "/session.xml";
		
		int index1 = line.lastIndexOf("/");
		String fileName = line.substring(index1+1);
		
		
		
		///////////////////////////////////////////////////////////////////// new Curator -> CuratorPostProcesing
		
		//Parsing XML, Adding a New Node, Serializing
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringComments(true);
		
		String trasncription = "...HERE_THE_TRANSCRIPTION_TEXT"; //We should receive it
		
		/*TODO: Borrar! */System.out.println("antes del try");
		/*TODO: Borrar! */ System.out.println("sessionFile: " + sessionFile);
		/*TODO: Borrar! */ System.out.println("wavFile: " + wavFile);
		
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document doc = builder.parse(sessionFile);
			
			NodeList userTurnsList = doc.getElementsByTagName("userturn");
			int userTurns = userTurnsList.getLength();
			
			for (int i=0; i<userTurns; i++){
				
				Element current = (Element)userTurnsList.item(i);
				NodeList userNodes = current.getChildNodes();
				for (int j=0; j<userNodes.getLength(); j++) {
				
					Node cur  = userNodes.item(j);
					/*TODO: Borrar! */ System.out.println("nodeName: " + cur.getNodeName());
					
					if (cur.getNodeName().equals("rec")) {
						Element e1 = (Element) cur;
						String name = e1.getAttribute("fname");
						wavFile = wavFile + "-x.wav";
						if ( name.equals(wavFile) ) {
				
							Text t = doc.createTextNode(trasncription);
							Element e2 = doc.createElement("transcription"); //This is the New Node
							e2.appendChild(t);
					
							userNodes.item(0).getParentNode().insertBefore(e2, userNodes.item(0)); // It inserts the New Node before </useturn>
						}
					}
				}
			}
			
			/*
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

			String xmlOutput = result.getWriter().toString();
			System.out.println("XML con nuevo texto insertado: ");
			System.out.println(xmlOutput);
			*/
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		//} catch (TransformerException e) {
		//	e.printStackTrace();
		}
		
		/////////////////////////////////////////////////////////////////////
		
		return getCorrespondingSystemUtterance(sessionFile,fileName);
	}

	
	protected void writeTranscription(String sessionFile, String wavFile, String userTranscription) {
		
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringComments(true);

		
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document doc = builder.parse(sessionFile);
			
			NodeList userTurnsList = doc.getElementsByTagName("userturn");
			int userTurns = userTurnsList.getLength();
			boolean findIt = false;
			for (int i=0; i<userTurns && !findIt; i++){
				
				Element current = (Element)userTurnsList.item(i);
				NodeList userNodes = current.getChildNodes();
				for (int j=0; j<userNodes.getLength() && !findIt; j++) {
				
					Node cur  = userNodes.item(j);
					///*TODO: Borrar! */ System.out.println("nodeName: " + cur.getNodeName());
					
					if (cur.getNodeName().equals("rec")) {
						Element e1 = (Element) cur;
						String name = e1.getAttribute("fname");
						int ind = name.indexOf(".wav");
						name = name.substring(0,ind);
						name = name + "-x.wav";
						//System.out.println("====");
						//System.out.println(wavFile);
						//System.out.println(name);
						//System.exit(0);
						if ( name.equals(wavFile) ) {
				
							userTranscription = userTranscription.replace("goodbye", "GOOD-BYE");
							userTranscription = userTranscription.replace("post code", "POSTCODE");
							userTranscription = userTranscription.replace("steak house", "STEAKHOUSE");
							
							
							Text t = doc.createTextNode(userTranscription);
							Element e2 = doc.createElement("transcription"); //This is the New Node
							e2.appendChild(t);
					
							userNodes.item(0).getParentNode().insertBefore(e2, userNodes.item(0)); // It inserts the New Node before </useturn>
							findIt = true;
							break;
						}
					}
				}
			}
			
			
			if (findIt) {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				//StreamResult result = new StreamResult(new StringWriter());
				StreamResult result = new StreamResult(new File(sessionFile));
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

				/*String xmlOutput = result.getWriter().toString();
				System.out.println("XML con nuevo texto insertado: ");
				System.out.println(xmlOutput);*/
			}


		} catch (SAXException e) {
			e.printStackTrace();
			//System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(0);			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			//System.exit(0);			
		} catch (TransformerException e) {
			//e.printStackTrace();
		}
		
		
		
	}
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//System.out.println(args.length);
			if (args.length < 2) {
				System.out.println("Usage: script pathToLogFolder System");
				System.exit(0);
			}
			String folder = args [0];
			String system = args [1];
			CuratorPostProcessing ex = new CuratorPostProcessing();
			connection = ex.getDatabaseConnection();
			
			//read line by line
	        String folderLog, wavFile, userTranscription;
	        
	        String previousWav = "", originalWav = "";

        	ResultSet rs = ex.getDatabaseConent(system);
	        while(rs.next()) {
	        	
	        	//get folderLog
	        	//get wavFile
	        	
	        	folderLog = rs.getString("log");
	    		int indexLogWord = folderLog.indexOf("log/");
	    		String sessionFile = "";
	    		if (indexLogWord != -1) {
	    			String aux = folderLog.substring(indexLogWord+4);
	    			sessionFile = folder + "/" +aux + "/session.xml";
	    		}
	    		else
	    			sessionFile = folder + "/" +folderLog + "/session.xml";
	    		
	        	originalWav = rs.getString("audio");
	        	
	        	if (originalWav.equals(previousWav)) {
	        		//System.out.println(originalWav + "\t" + rs.getString("transcribed_text").length());
	        		
	        		
	        	}
	        	else {
	        		wavFile = originalWav;
		        	int indexSlash = wavFile.lastIndexOf("/");
		        	wavFile = wavFile.substring(indexSlash+1);
		        	userTranscription = rs.getString("transcribed_text");
		        	ex.writeTranscription(sessionFile, wavFile,userTranscription);
	        	}
       	
	        	previousWav = originalWav;
	        }
	     	

			
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}


	}

}
