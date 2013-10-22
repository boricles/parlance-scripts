package eu.parlance.extractor;

import java.io.File;
import java.io.FilenameFilter;
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

public class AudioExtractor {

	public AudioExtractor() {

	}

	static Connection connection = null;

	protected Connection getDatabaseConnection() {
		String driverClass = "com.mysql.jdbc.Driver";
		String connectionURL = "jdbc:mysql://localhost:3306/parlance_feedback?useUnicode=true&characterEncoding=UTF-8";
		String connectionUser = "root";
		String connectionUserPassword = "root";

		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = null;
		try {
			con = DriverManager.getConnection(connectionURL, connectionUser,
					connectionUserPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;

	}

	protected ResultSet getDatabaseConent() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM system1_evaluation_answers");
			return rs;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public void updateParameters(String id, String system, String logfile, String audio, String taskID) {
		
		Statement statement = null;
		
		int indexLogWord = logfile.indexOf("log/");
		String aux = logfile.substring(indexLogWord+4);
		
		String audioStr = "http://parlancelogs.isoco.net/evaluation/Evaluation1/" 
				+ system + "/" + aux + "/" + audio.trim();
		
		String taskHyperlink = "http://glocal.isoco.net/parlance/task_page_" + taskID + ".html";

		String updateTableSQL = "UPDATE system1_evaluation_answers"
				+ " SET audio = \'" + audioStr + "\' "
				+ " , taskHyperlink = \'" + taskHyperlink + "\' "
				+ " WHERE id = " + id;
		
		System.out.println("id ============================================ "+ id);
		System.out.println("CONSULTA: "+ updateTableSQL);

		try {
			statement = connection.createStatement();

			statement.execute(updateTableSQL);

			if (statement != null) {
				statement.close();
			}

			System.out
					.println("updateParameters(): Record is updated to DBUSER table!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

		}
	}

	public static boolean folderExists(String logFolderPath, String logFile) {
		
		boolean exists = false;

		int indexLogWord = logFile.indexOf("log/");
		if (indexLogWord != -1) {
			final String aux = logFile.substring(indexLogWord + 4);
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
			if (matchingFiles.length == 1
					&& matchingFiles[0].getName().equals(aux))
				exists = true;

		}
		return exists;
	}

	public static void setAdditionalParameters(Task t, String logFolderPath, String logFile) {
		try {
			
			int indexLogWord = logFile.indexOf("log/");
			String sessionFile = "";
			if (indexLogWord != -1) {
				String aux = logFile.substring(indexLogWord + 4);
				sessionFile = logFolderPath + "/" + aux + "/session.xml";
			} else
				sessionFile = logFolderPath + "/" + logFile + "/session.xml";

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(sessionFile));

			NodeList audioList = doc.getElementsByTagName("stereoaudio");
			
			Node node = audioList.item(0);
			String audio = node.getFirstChild().getTextContent();
		
			//System.out.println("audio " + audio);
			
			t.setAudio(audio);

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			
			if (args.length < 1) {
				System.out.println("Usage: script pathToLogFolder");
				System.exit(0);
			}
			String logFolderPath = args[0]; //C:\Users\agonzalez\Desktop\Parlance_LOGS\Oficial

			AudioExtractor ex = new AudioExtractor();
			connection = ex.getDatabaseConnection();
			ResultSet rs = ex.getDatabaseConent();

			List<Task> tasks = new ArrayList<Task>();

			while (rs.next()) {

				String logFile = rs.getString("logfile");
				
				System.out.println("logFile: "+logFile);
				//System.out.println(folderExists(logFolderPath, logFile));
				//System.out.println("processing data: " + logFile);
				
				if (folderExists(logFolderPath, logFile)) {
					
					Task t = new Task();
					int taskId = rs.getInt("taskID");
					String system = rs.getString("system");
					String taskID = rs.getString("taskID");
					
					//System.out.println("id "+taskId);
					//System.out.println("logfile" + logFile);
					//System.out.println("system" + system);
					
					t.setId(rs.getString("id"));
					t.setToken(rs.getString("token"));
					t.setLogfile(rs.getString("logfile"));
					t.setSystem(rs.getString("system"));
					
					t.setQ2(rs.getString("q2"));
					t.setQ3(rs.getString("q3"));
					t.setQ4(rs.getString("q4"));
					t.setQ5(rs.getString("q5"));
					t.setQ6(rs.getString("q6"));
					t.setQ7(rs.getString("q7"));
					t.setQ8(rs.getString("q8"));
					
					t.setTaskID(rs.getString("taskID"));

					setAdditionalParameters(t, logFolderPath, logFile);

					tasks.add(t);
				}

			}

			for (Task task : tasks) {

				String audioT;
				if (task.getAudio() != null) {
					audioT = task.getAudio();
				}
				else{
					audioT = "";
				}
				ex.updateParameters(task.getId(), task.getSystem(), task.getLogfile(), audioT, task.getTaskID());
			}

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}

	}

}
