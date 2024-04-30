package com.programs.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.programs.main.interfaces.CertificateParser;

public class CertificateProcessor {
	private Boolean listModeEnabled = false;
	private String suppliedPassword = "";
	private File filesDirectory;
	private List<String> processFailedFiles = new ArrayList<>();
	
	public CertificateProcessor(String filesDirectory) {
		this.filesDirectory = new File(filesDirectory);
	}
	
	public CertificateProcessor(String filesDirectory, String password) {
		this.suppliedPassword = password;
		this.filesDirectory = new File(filesDirectory);
	}

	public void start() {
		int filesQty = 0;
		long initTime = System.currentTimeMillis();
		
		for(File file : filesDirectory.listFiles()) {
			String currentFileName = file.getName();
			File currentFile = file;
			
			if(!currentFileName.contains(".pfx")) continue;
			if(currentFileName.contains("  ")) {
				File renamedFile = 
						removeDoubleSpaces(currentFileName, filesDirectory, file);
				if(renamedFile.equals(currentFile)) continue;
				currentFile = renamedFile;
				currentFileName = currentFile.getName();
			}
			try {
				String result = "";
				if(listModeEnabled) {
					List<IndexValuePair> passwordList = PropertiesReader.getValues("password.");
					if(passwordList.isEmpty()) {
						System.out.println("No passwords were found in list!");
						return;
					}
					for(IndexValuePair pair : passwordList) {
						result = executeCommand(pair.getValue(), currentFile);
						if(!result.equals("SENHA INVÁLIDA")) {
							break;
						}
					}
					appendResultToFilename(result, currentFile);
				} else {
					result = executeCommand(suppliedPassword, currentFile);
					appendResultToFilename(result, currentFile);
				}
				filesQty++;
			} catch (FileNotFoundException e) {
				System.out.println("Properties file not found!");
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.printf("Arquivo %s processado.\n", currentFileName);
		}
		if(filesQty > 0) {
			Double timeElapsed = getElapsedSeconds(initTime, System.currentTimeMillis());
			String message = 
					String.format("%d arquivos processados em %.2f segundos.\n", filesQty, timeElapsed);
			System.out.println(message);
		}
		
		if(!processFailedFiles.isEmpty()) {
			System.out.println("Os seguintes certificados não puderam ser renomeados:");
			processFailedFiles.forEach(fileName -> System.out.println(fileName));
		}
	}
	
	private void appendResultToFilename(String result, File file) {
		if(!file.exists()) return;
		String filePath = file
												.getAbsolutePath()
												.replaceAll(".pfx", "")
												.replaceAll(" \\[[\\S\\s]*\\]", "");
		String newName = String.format("%s [%s].pfx", filePath, result);
		Boolean isRenamed = renameFileTo(newName, file);
		if(!isRenamed) {
			System.out.printf("The file %s.pfx could not be renamed.\n", filePath);
		}
	}
	
	private String sanitizeName(final String name) {
		String currentString = name;
		while(currentString.contains("  ")) {
			currentString = currentString.replaceAll("  ", " ");
		}
		return currentString;
	}
	
	private Boolean renameFileTo(String absolutePath, File file) {
		return file.renameTo(new File(absolutePath));
	}
	
	public File removeDoubleSpaces(String oldFileName, File fileDirectory,  File oldFile) {
		String sanitizedName = sanitizeName(oldFileName);
		String newName = String.format("%s\\%s", fileDirectory.toString(), sanitizedName);
		Boolean success = renameFileTo(newName, oldFile);
		if(!success) {
			processFailedFiles.add(oldFile.getAbsolutePath());
			return oldFile;
		}
		return new File(newName);
	}

	public String executeCommand(String password, File currentFile) throws Exception {
		String command = String.format("certutil -dump -p %s \"%s\"", password, currentFile);
		ProcessHandler.createProcess(command, filesDirectory);
		String data = LineReader.readData(ProcessHandler.getInputStream());
		CertificateParser parser = new CertificateDumpParser();
		return parser.parse(data);
	}
	
	private Double getElapsedSeconds(Long init, Long end) {
		final Double MILISECONDS = 1000.0;
		Double diff = (end.doubleValue() - init.doubleValue())/MILISECONDS;
		return diff;
	}
	
	public Boolean getIsListMode() {
		return this.listModeEnabled;
	}

	public CertificateProcessor activateListPasswordMode() {
		this.listModeEnabled = true;
		return this;
	}
	
	public CertificateProcessor deactivateListPasswordMode() {
		this.listModeEnabled = false;
		return this;
	}

	public File getFilesDirectory() {
		return this.filesDirectory;
	}

	public void setFilesDirectory(File filesDirectory) {
		this.filesDirectory =  filesDirectory;
	}
}
