package eu.parlance.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class FolderAnonymizer {

	public FolderAnonymizer() {

	}

	
	private static void generateCsvFile (String csvName){
		try {
			FileWriter csvOutput = new FileWriter(csvName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void appendCsvFile (String csvName, String oldName, String newName){
		try {
			 PrintWriter csvOutput = new PrintWriter(new BufferedWriter(new FileWriter(csvName, true)));
			 csvOutput.print(oldName);
			 csvOutput.print(",");
			 csvOutput.print(newName);
			 csvOutput.print("\n");
			 csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void renameFolderFiles(String folder){
		File fol = new File(folder);
		System.out.println("logFolderPath: " + fol.toString());
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(fol.list()));
		
		int index=-1, voipPos=0, dashPos=0, hash=0;
		
		String oldName, voipSubstring, phoneNumber, newName="", csvName;
		
		//old file names: voip-phoneNumber-date-hour
		//new file names: voip-hash-date-hour
		
		for (int i=0; i<names.size(); i++){ 
			oldName = names.get(i);
		
			if (oldName.startsWith("voip-")) {
				index = names.get(i).indexOf("voip-"); //first dash //voip-
			}
			else if (oldName.startsWith("NLG_voip-")) {
				index = names.get(i).indexOf("NLG_voip-"); //first dash //NLG_voip-
			}
			else {
				index = -1; 
				}
			
			if (index==0) { //-1 => no "voip-" found; 0 => "voip-" found
				
				//csvName =  folder + "\\anonymizedFolders.csv"; //Windows
				csvName =  folder + File.separator + "anonymizedFolders.csv"; //Windows or UNIX
				File csvFile = new File(csvName);
				
				System.out.println();
				System.out.println("f.list("+ i + "): ");
				System.out.println("oldName: " + oldName);
				
				if (oldName.startsWith("voip-"))    voipPos=index+5;
				if (oldName.startsWith("NLG_voip")) voipPos=index+9;
				
				voipSubstring = names.get(i).substring(voipPos);
				System.out.println("substring: " + voipSubstring);
				
				dashPos = voipSubstring.indexOf("-"); //second dash
				phoneNumber = voipSubstring.substring(0, dashPos);
				System.out.println("phoneNumber: " + phoneNumber);
				
				hash = phoneNumber.hashCode();
				if (hash<0) hash*=-1; //unsigned hash
				System.out.println("hash: " + hash);
				
				if (oldName.startsWith("voip-")) newName = "voip-" + hash + voipSubstring.substring(dashPos);
				if (oldName.startsWith("NLG_voip-")) newName = "NLG_voip-" + hash + voipSubstring.substring(dashPos);
				System.out.println("newName: " + newName);
				
				if (!csvFile.exists()) {
					generateCsvFile(csvName);
					appendCsvFile(csvName, oldName, newName);
				}
				else{ //file.exists()
					appendCsvFile(csvName, oldName, newName);
				}
				
				//File oldFolder = new File(folder + "\\" + oldName); //Windows
				File oldFolder = new File(folder + File.separator + oldName); //Windows or UNIX
				//File newFolder = new File(folder + "\\" + newName); //Windows
				File newFolder = new File(folder + File.separator + newName); //Windows or UNIX
				
				 if ( oldFolder.isDirectory() ) {  
					 oldFolder.renameTo(newFolder);  
				 } else {  
					 oldFolder.mkdir();  
					 oldFolder.renameTo(newFolder);  
				 }
			}
		}
	}



	public static void main(String[] args) {
		try {
			
			if (args.length < 1) {
				System.out.println("Usage: script pathToLogFolder");
				System.exit(0);
			}
			String logFolderPath = args[0]; //C:\Users\agonzalez\Desktop\Parlance_LOGS\Oficial\SystemName
			
			renameFolderFiles(logFolderPath); //rename the voip-telephone-data-hour folders
			

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}

	}

}
