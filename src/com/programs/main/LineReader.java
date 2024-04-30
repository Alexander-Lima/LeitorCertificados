package com.programs.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LineReader {
	public static String readData(InputStream inputStream) {
		try {
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader buffReader = new BufferedReader(reader);
			String line = "";
			StringBuilder result = new StringBuilder();
			
			while((line = buffReader.readLine()) != null) {
				result .append(line + "\n");
			}
			return result.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
