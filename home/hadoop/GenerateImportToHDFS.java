import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

public class GenerateImportToHDFS {
	public static void main(String[] args) throws IOException {
	
		File inputFile = new File("openaddresses_csv_files.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		File outputFile = new File("importToHDFS.sh");
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		GenerateImportToHDFS.writeLine(bw, "#!/bin/bash");
		GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -mkdir -p /user/hadoop/openaddresses");
		GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -chmod g+w /user/hadoop/openaddresses");
		GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -mkdir -p /user/hadoop/openaddresses/raw");
		GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -chmod g+w /user/hadoop/openaddresses/raw");
		
		String sourcefile;
		String lastFolder = "";
		Pattern pattern = Pattern.compile("openaddr-collected-europe\\/([a-z0-9]+)\\/([a-z0-9_\\-\\/]+).csv");
		
		while ((sourcefile = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(sourcefile);
			
			if(!matcher.find()) {
				continue;
			}
			
			String folderName = matcher.group(1);
			String fileName = matcher.group(2).replace('/','-');
			
			// skip the summary folder
			if(folderName.equals("summary")) {
				continue;
			}
			
			// create the hadoop folder if necessary
			String hadoopFolder = "/user/hadoop/openaddresses/raw/" + folderName;
			if(!folderName.equals(lastFolder)) {
				// create hadoop directory
				GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -mkdir -p " + hadoopFolder);
				GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -chmod g+w " + hadoopFolder);
				lastFolder = folderName;
			}
			
			GenerateImportToHDFS.writeLine(bw, "sudo -u hadoop /home/hadoop/hadoop/bin/hadoop fs -put " + sourcefile + " " + hadoopFolder + "/" + fileName + ".csv");
		}
		
		br.close();
		bw.close();
	}
	
	private static void writeLine(BufferedWriter bw, String line) throws IOException {
		bw.write(line);
		bw.newLine();
	}
}

