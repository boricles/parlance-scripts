package eu.parlance.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Curator {

	
	static Connection connection = null;
	
	public Curator() {
		
	}
	
	
	protected Connection getDatabaseConnection () {
		  String driverClass = "com.mysql.jdbc.Driver";
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
	
	protected ResultSet getDatabaseConent(String system, String folderLog) {
		try {
			
			//connection = getDatabaseConnection();
		 
		    //Create a Statement class to execute the SQL statement
		    Statement stmt = connection.createStatement();
		 
		    //Execute the SQL statement and get the results in a Resultset
		    String query = "SELECT distinct * FROM system1_evaluation_answers where system = \'" + system +"\' and logfile like \'%" + folderLog+ "%\'";
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
	
	protected String getPreviousSystemUtterance(String folder, String logLocation, String line) {
		
		
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
		
		return getCorrespondingSystemUtterance(sessionFile,fileName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//System.out.println(args.length);
			if (args.length < 3) {
				System.out.println("Usage: script pathToListFile pathToLogFolder System");
				System.exit(0);
			}
			String listFile = args [0];
			String folder = args [1];
			String system = args [2];
			Curator ex = new Curator();
			connection = ex.getDatabaseConnection();
			
			//read line by line
			BufferedReader br = new BufferedReader(new FileReader(listFile));
	        String line;
	        int index1, index2;
	        String folderLog;
	     	int size = 0;
	     	
	     	
    		FileWriter pw = new FileWriter("file-" + system + ".csv", true);
    		
    		
	        while((line = br.readLine()) != null) {
	        	index1 = line.lastIndexOf("voip");
	        	if (index1==-1)
	        		break;
	        	index2 = line.lastIndexOf("/");
	        	if (index2==-1)
	        		break;
	        	folderLog = line.substring(index1,index2);
	        	ResultSet rs = ex.getDatabaseConent(system,folderLog);
	        	String taskID = "", log="",systemUtterance="";
	        	
	        	while (rs.next()){
	        		taskID = rs.getString("taskID");
	        		log = rs.getString("logfile");
	        		
	        		systemUtterance = ex.getPreviousSystemUtterance(folder,log,line);
	        		//e.g. log = log/voip-7174192798-130813_052841
	        		//save in the csv
	        		//taskID, log, line
	        		systemUtterance.replace('\"', ' ');
	        		pw.append(taskID + "|" + log + "|" + line + "| "+ systemUtterance + "\n");
	        		
	        		size++; 
	        		
	        	}
	        }

	        pw.close();
	        
	        System.out.println("size " + size);
			
			/*
			ResultSet rs = ex.getDatabaseConent(system);
			
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
						t.setQ6(rs.getString("q6"));						
						t.setQ7(rs.getString("q7"));						
						t.setQ8(rs.getString("q8"));
						t.setTaskID(rs.getString("taskID"));
						
						tasks.add(t);
					}
					
				//}
				
			}*/
			

			
		}
		catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}


	}

}
