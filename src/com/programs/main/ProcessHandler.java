package com.programs.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ProcessHandler {
	private static Process process;
	private static InputStream inputStream;
	
	public static void createProcess(String command, File fileDirectory) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process currentProcess = runtime.exec(command, null, fileDirectory);
		process = currentProcess;
		inputStream = process.getInputStream();
	}
	
	public static InputStream getInputStream() throws Exception {
		if(Objects.isNull(inputStream)) throw new Exception("There's no process opened.");
		return inputStream;
	}

	public static Process getProcess() {
		return process;
	}
}
