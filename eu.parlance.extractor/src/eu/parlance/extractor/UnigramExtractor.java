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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UnigramExtractor {

	public UnigramExtractor() {

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
	
	
	public void updateParameters(String id, String helloSysDact,
			String confirmSysDact, String requestSysDact, String informSysDact,
			String nullSysDact, String givetokenSysDact, String goodbyeSysDact,
			String helloUserSemihyp, String confirmUserSemihyp,
			String informUserSemihyp, String givetokenUserSemihyp, String requestUserSemihyp, 
			String nullUserSemihyp, String byeUserSemihyp, String afirmUserSemihyp) {

		Statement statement = null;

		String updateTableSQL = "UPDATE system1_evaluation_answers"
				+ " SET helloSysDact = " + helloSysDact
				+ " , requestSysDact = " + requestSysDact
				+ ", informSysDact = " + informSysDact 
				+ ", nullSysDact = "+ nullSysDact
				+ ", confirmSysDact = " + confirmSysDact
				+ ", givetokenSysDact = " + givetokenSysDact
				+ ", goodbyeSysDact = " + goodbyeSysDact
				+ ", confirmUserSemihyp = " + confirmUserSemihyp
				+ ", informUserSemihyp = " + informUserSemihyp
				+ ", givetokenUserSemihyp = " + givetokenUserSemihyp
				+ ", requestUserSemihyp = " + requestUserSemihyp
				+ ", nullUserSemihyp = " + nullUserSemihyp
				+ ", byeUserSemihyp = " + byeUserSemihyp
				+ ", afirmUserSemihyp = " + afirmUserSemihyp
				+ " WHERE id = " + id;
		System.out.println("id ============================================ "
				+ id);
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
			if (matchingFiles.length == 1
					&& matchingFiles[0].getName().equals(aux))
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

	public static void setAdditionalParameters(Task t, String logFolderPath,
			String logFile) {
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

			NodeList systemTurnsList = doc.getElementsByTagName("systurn");

			int systemTurns = systemTurnsList.getLength();

			NodeList dactsList = doc.getElementsByTagName("dact");

			int dacts = dactsList.getLength();

			int helloCount = 0;
			int confirmCount = 0;
			int requestCount = 0;
			int informCount = 0;
			int nullCount = 0;
			int givetokenCount = 0;
			int goodbyeCount = 0;
			
			for (int i = 0; i < dactsList.getLength(); i++) {
				Node n = dactsList.item(i);
				String text = n.getFirstChild().getTextContent();

				System.out.println("text " + text);


				if (text.startsWith("hello(")) {
					helloCount++;
					t.setHelloSysDact(Integer.toString(helloCount));
					System.out.println("++++++++++++++++++++++++++++hello() found." + helloCount );
				}
				
				if (text.startsWith("confirm(")) {
					confirmCount++;
					t.setConfirmSysDact(Integer.toString(confirmCount));
					System.out.println("+++++++++++++++++++++++confirm() found." + confirmCount );
				}

				if (text.startsWith("request(")) {
					requestCount++;
					t.setRequestSysDact(Integer.toString(requestCount));
					System.out.println("++++++++++++++++++++++++++++request() found." + requestCount);
				}

				if (text.startsWith("inform(")) {
					informCount++;
					t.setInformSysDact(Integer.toString(informCount));
					System.out.println("+++++++++++++++++++++++++++++++++inform() found." + informCount);
				}

				if (text.startsWith("null(")) {
					nullCount++;
					t.setNullSysDact(Integer.toString(nullCount));
					System.out.println("++++++++++++++++++++++++++++++++null() found." + nullCount);
				}

				if (text.startsWith("givetoken(")) {
					givetokenCount++;
					t.setGivetokenSysDact(Integer.toString(givetokenCount));
					System.out.println("+++++++++++++++++++++++++++++++givetoken() found." + givetokenCount);
				}

				if (text.startsWith("goodbye(")) {
					goodbyeCount++;
					t.setGoodbyeSysDact(Integer.toString(goodbyeCount));
					System.out.println("+++++++++++++++++++++++++++++++++goodbye() found."+ goodbyeCount);
				}
			}
			
			
			int informUserCount = 0;
			int requestUserCount = 0;
			int confirmUserCount = 0;
			int givetokenUserCount = 0;
			int nullUserCount = 0;
			int byeUserCount = 0;
			int afirmUserCount = 0;
			
			NodeList semiList = doc.getElementsByTagName("semi");
			
			int semis = semiList.getLength();
			

			for (int j = 0; j < semis; j++) {
				
				Node nn = semiList.item(j);
					
				NodeList chidList = nn.getChildNodes();
					
				//We need just the first semihyp, no all of them 
				Node n0 = chidList.item(1);
				String textU = n0.getTextContent();
					
				System.out.println("textU " + textU);
	
				if (textU.startsWith("inform(")) {
					informUserCount++;
					t.setInformUserSemihyp(Integer.toString(informUserCount));
					System.out.println("++++++++++++++++++++++++++++++++inform() found." + informUserCount);
				}
				
				if (textU.startsWith("request(")) {
					requestUserCount++;
					t.setRequestUserSemihyp(Integer.toString(requestUserCount));
					System.out.println("++++++++++++++++++++++++++++++++request() found." + requestUserCount);
				}
	
				if (textU.startsWith("confirm(")) {
					System.out.println("confirm() found. +1");
					confirmUserCount++;
					t.setConfirmUserSemihyp(Integer.toString(confirmUserCount));
					System.out.println("++++++++++++++++++++++++++++++++confirm() found." + confirmUserCount);
				}
					
				if (textU.startsWith("givetoken(")) {
					System.out.println("givetoken() found. +1");
					givetokenUserCount++;
					t.setGivetokenUserSemihyp(Integer.toString(givetokenUserCount));
					System.out.println("++++++++++++++++++++++++++++++++givetoken() found." + givetokenUserCount);
				}
					
				if (textU.startsWith("null(")) {
					nullUserCount++;
					t.setNullUserSemihyp(Integer.toString(nullUserCount));
					System.out.println("++++++++++++++++++++++++++++++++null() found." + nullUserCount);
				}
					
				if (textU.startsWith("bye(")) {
					byeUserCount++;
					t.setByeUserSemihyp(Integer.toString(byeUserCount));
					System.out.println("++++++++++++++++++++++++++++++++bye() found." + byeUserCount);
				}
					
				if (textU.startsWith("affirm(")) {
					afirmUserCount++;
					t.setAfirmUserSemihyp(Integer.toString(afirmUserCount));
					System.out.println("++++++++++++++++++++++++++++++++affirm() found." + afirmUserCount);
				}
				
				
			}

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			t.setHelloSysDact("0");
			t.setRequestSysDact("0");
			t.setInformSysDact("0");
			t.setNullSysDact("0");
			t.setGivetokenSysDact("0");
		}

	}

	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.out.println("Usage: script pathToLogFolder");
				System.exit(0);
			}
			String logFolderPath = args[0]; //C:\Users\agonzalez\Desktop\Parlance_LOGS\Oficial

			UnigramExtractor ex = new UnigramExtractor();
			connection = ex.getDatabaseConnection();
			ResultSet rs = ex.getDatabaseConent();

			List<Task> tasks = new ArrayList<Task>();

			while (rs.next()) {

				String logFile = rs.getString("logfile");
				
				if (folderExists(logFolderPath, logFile)) {
					System.out.println("logFile"+logFile);
					System.out.println(folderExists(logFolderPath, logFile));
					System.out.println("processing data " + logFile);
					Task t = new Task();
					int taskId = rs.getInt("taskID");
					System.out.println("id "+taskId);
					t.setId(rs.getString("id"));
					t.setToken(rs.getString("token"));
					t.setLogfile(rs.getString("logfile"));

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

				ex.updateParameters(task.getId(), task.getHelloSysDact(), task.getConfirmSysDact(), task.getRequestSysDact(),
						task.getInformSysDact(), task.getNullSysDact(), task.getGivetokenSysDact(), task.getGoodbyeSysDact(),
						task.getHelloUserSemihyp(), task.getConfirmUserSemihyp(), task.getInformUserSemihyp(),
						task.getGivetokenSysDact(), task.getRequestUserSemihyp(), task.getNullUserSemihyp(),
						task.getByeUserSemihyp(), task.getAfirmUserSemihyp());
			}

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}

	}

}
