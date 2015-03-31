package eu.parlance.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FeedbackGenerator {

	static Connection connection = null;
	
	public FeedbackGenerator() {
		
	}
	
	protected Connection getDatabaseConnection () {
		  String driverClass = "com.mysql.jdbc.Driver";
		  //172.18.3.193 -> Almu //localhost
		  //172.18.3.150 -> Boris
		  String connectionURL = "jdbc:mysql://localhost:3306/parlance_feedback?useUnicode=true&characterEncoding=UTF-8";
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
	
	
	
	protected ResultSet getDatabaseConent() {
		try {
			
			//connection = getDatabaseConnection();
		 
		    //Create a Statement class to execute the SQL statement
		    Statement stmt = connection.createStatement();
		 
		    //Execute the SQL statement and get the results in a Resultset
		    ResultSet rs = stmt.executeQuery("SELECT * FROM system1_evaluation_answers");
		 
		    // Iterate through the ResultSet, displaying two values
		    // for each row using the getString method
		 
		    return rs;
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}	

	public static boolean folderExists(String logFolderPath, String logFile) {
		boolean exists = false;
		
		int indexLogWord = logFile.indexOf("log/");
		if (indexLogWord != -1) {
			final String aux = logFile.substring(indexLogWord+4);
			File dir = new File(logFolderPath);
			File[] matchingFiles = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			    	return name.equals(aux);
			    }
			});
			if (matchingFiles.length == 1 && matchingFiles[0].getName().equals(aux))
				exists = true;
		} else {
			final String aux = logFile;
			File dir = new File(logFolderPath);
			File[] matchingFiles = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			    	return name.equals(aux);
			    }
			});
			if (matchingFiles.length == 1 && matchingFiles[0].getName().equals(aux))
				exists = true;
			
		}
		return exists;
	}
		
	
	public String getTaskGoal(int taskID, String taskFile){
		
		String goal = null;
		// 1. Read tasks from xml file
		
				try {
					BufferedReader br = new BufferedReader(new FileReader(taskFile));
					
			        StringBuilder sb = new StringBuilder();
			        String line = br.readLine();

			        while (line != null) {
			            sb.append(line);
			            sb.append("\n");
			            line = br.readLine();
			        }
			        String task_xml = sb.toString();
			    
			        if (task_xml!=null){
			        	
			    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
									
						InputSource inStream = new InputSource();
						inStream.setCharacterStream(new StringReader(task_xml));
										
						org.w3c.dom.Document doc = db.parse(inStream);
						NodeList nodeList = doc.getElementsByTagName("task");
						for (int s = 0; s < nodeList.getLength(); s++) {
							Node fstNode = nodeList.item(s);
							if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							    Element fstElmnt = (Element) fstNode;
							    String id = fstElmnt.getAttribute("id");
							    
							    if (Integer.parseInt(id) == taskID){
							    
							    if (fstElmnt.getElementsByTagName("goal")!=null){
					    			goal = fstElmnt.getElementsByTagName("goal").item(0).getTextContent();
					    			//goal = goal.replaceAll(",", " ");
					    			goal = goal.replaceAll("\n", " ");
					    			goal = goal.replaceAll("\t", " ");
					    			goal = goal.replaceAll("\"", " ");
					    			//goal = goal.replaceAll(";", " ");
					    			goal = goal.trim();
							    }
					    			
					    		}
				    			
							}					
						}
			        }
			        
			        br.close();
			        
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
			    
		
		
		return goal;
	}
	
	
	public static void main(String []args) {
		try {
			//System.out.println(args.length);
			if (args.length < 3) {
				System.out.println("Usage: script pathToXMLTaskFile pathToLogFolder System");
				System.exit(0);
			}
			String taskFilePath = args [0];
			String logFolderPath = args [1];
			String system = args[2];
			FeedbackGenerator ex = new FeedbackGenerator();
			connection = ex.getDatabaseConnection();
			ResultSet rs = ex.getDatabaseConent();
			
			List<Task> tasks = new ArrayList<Task>();
						
			while (rs.next()) {
				//String q1 = rs.getString("q1");
				//if (!q1.contains("No restaurant found")) {
					String logFile = rs.getString("logfile");
					if (folderExists(logFolderPath,logFile)) {
						System.out.println("processing data " + logFile);
						Task t = new Task();
						int taskId = rs.getInt("taskID");
						String goal = ex.getTaskGoal(taskId,taskFilePath);
						t.setId(rs.getString("id"));
						t.setToken(rs.getString("token"));
						t.setLogfile(rs.getString("logfile"));
						t.setGoal(goal);
						
						String q1 = rs.getString("q1");
						if (q1.contains("No restaurant found"))
							t.setQ1("No");
						else
							t.setQ1("Yes");
				
						//t.setQ1(rs.getString("q1"));
						t.setQ2(rs.getString("q2"));
						t.setQ3(rs.getString("q3"));
						t.setQ4(rs.getString("q4"));
						t.setQ5(rs.getString("q5"));
						//t.setQ6(rs.getString("q6"));						
						//t.setQ7(rs.getString("q7"));						
						//t.setQ8(rs.getString("q8"));
						t.setTaskID(rs.getString("taskID"));
						
						//setAdditionalParameters(t,logFolderPath,logFile);
						
						tasks.add(t);
					}
					
				//}
				
			}
			
			for (Task task : tasks) {
				String fileContent = new String();
				fileContent = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
				fileContent += "<feedback>\n";
				
				//2014 (System15)
				fileContent += "<question id=\"1\" name=\"Did you manage to find a restaurant?\">" + task.getQ1() + "</question>\n";
				fileContent += "<question id=\"2\" name=\"I found all the information I was looking for.\">" + task.getQ2() + "</question>\n";
				fileContent += "<question id=\"3\" name=\"The system appeared to understand what I was saying.\">" + task.getQ3() + "</question>\n";
				fileContent += "<question id=\"4\" name=\"The information on restaurants was well-presented.\">" + task.getQ4() + "</question>\n";
				fileContent += "<question id=\"5\" name=\"The system's utterances were not repetitive.\">" + task.getQ5() + "</question>\n";
			
		
				//2013 (System1)
				/*
				fileContent += "<question id=\"1\" name=\"Did you manage to find a restaurant?\">" + task.getQ1() + "</question>\n";
				fileContent += "<question id=\"2\" name=\"The system appeared to understand what I was saying.\">" + task.getQ2() + "</question>\n";
				fileContent += "<question id=\"3\" name=\" I found all the information I was looking for.\">" + task.getQ3() + "</question>\n";
				fileContent += "<question id=\"4\" name=\" The system\'s utterances were well phrased.\">" + task.getQ4() + "</question>\n";
				fileContent += "<question id=\"5\" name=\" The system\'s utterances were natural, i.e. could have been produced by a human.\">" + task.getQ5() + "</question>\n";
				//fileContent += "<question id=\"6\" name=\" The system\'s utterances were repetitive.\">" + task.getQ6() + "</question>\n";
				//fileContent += "<question id=\"7\" name=\" The pace of the interaction was appropriate.\">" + task.getQ7() + "</question>\n";
				//fileContent += "<question id=\"8\" name=\" In this conversation, it was easy to find a restaurant.\">" + task.getQ8() + "</question>\n";
				*/
				
				fileContent += "<token>" + task.getToken() + "</token>\n";
				fileContent += "<dialogueId>" + task.getLogfile() + "</dialogueId>\n";
				fileContent += "<goal>" +task.getGoal() + "</goal>\n";
				fileContent += "<task>" +task.getTaskID() + "</task>\n";				
				fileContent += "</feedback>";

				//ex.updateSystemField (task.getId(),system);
				//ex.updateAdditionalParameters(task.getId(),task.getUserTurns(),task.getSystemTurns(),task.getLength(),task.getAvgWordsPerSystemTurn());
				
				
				File file = new File("feedback/"+task.getLogfile());
				if (!file.exists())
					if (!file.mkdir()) {
						System.out.println("Failed to create the folder " + "feedback/"+task.getLogfile() );
						System.exit(0);
					}
				PrintWriter write = new PrintWriter("feedback/"+task.getLogfile()+"/feedback.xml");
				write.print(fileContent);
				write.close();
				
			}
			
			
			
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	
}
