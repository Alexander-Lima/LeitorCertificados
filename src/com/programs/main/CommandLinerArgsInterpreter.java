package com.programs.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CommandLinerArgsInterpreter {
	private static final String PASSWORD_KEY = "-p";
	private static final String DIRECTORY_KEY = "-d";
	private static final String LIST_KEY = "-list";
	private static String FILES_DIRECTORY;
	private static String PASSWORD;
	private static Map<Boolean, String> validations = new HashMap<>();
	
	public static void main(String[] args) {
		switch (args.length) {
		case 3: 
			FILES_DIRECTORY = args[2];
			if(!listPasswordValidation(args)) break;
			new CertificateProcessor(FILES_DIRECTORY)
														.activateListPasswordMode()
														.start();
			break;
			
		case 4:
			if(!uniquePasswordValidation(args)) break;
			PASSWORD = args[1];
			FILES_DIRECTORY = args[3];
			new CertificateProcessor(FILES_DIRECTORY, PASSWORD).start();
			break;
			
		default:
			System.out.println("Invalid arguments number!");
			standardErrorPrint();
			break;
		}
	}
	
	private static Boolean uniquePasswordValidation(String[] args) {
		validations.put(isValidKeysUniquePassword(args), "Invalid expression!");
		return isValid();
	}
	
	private static Boolean listPasswordValidation(String[] args) {
		validations.put(isValidKeysListPassword(args), "Invalid expression!");
		return isValid();
	}
	
	private static Boolean isValidKeysUniquePassword(String[] args) {
		return args[0].equals(PASSWORD_KEY) && args[2].equals(DIRECTORY_KEY);
	}
	
	private static Boolean isValidKeysListPassword(String[] args) {
		return args[0].equals(LIST_KEY) && args[1].equals(DIRECTORY_KEY);
	}
	
	private static Boolean isValid() {
		Boolean isValid = true;
		
		for(Entry<Boolean, String> test : validations.entrySet()) {
			Boolean testResult = test.getKey();
			String error = test.getValue();
			if(!testResult) {
				isValid = false;
				System.out.println(error);
				standardErrorPrint();
				break;
			}
		}
		validations.clear();
		return isValid;
	}
	
	private static void standardErrorPrint() {
		System.out.println("Valid expressions:");
		System.out.println("Unique password: -p [password] -d [directory]");
		System.out.println("Properties list passwords: -list -d [directory]");
	}
}
