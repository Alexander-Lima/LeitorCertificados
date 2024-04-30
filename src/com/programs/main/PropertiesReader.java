package com.programs.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertiesReader {
	public static List<IndexValuePair> getValues(String property) throws FileNotFoundException, IOException {
		Set<String> uniquePasswords = new HashSet<>();
		List<IndexValuePair> passwordList = new ArrayList<>();
		Properties properties = new Properties();
		
		String propsLocation = System.getenv("PROPERTIES");
		
		FileInputStream in = new FileInputStream(String.format("%s\\%s", propsLocation, "props.properties"));
		properties.load(in);
		
		for(Entry<Object, Object> key : properties.entrySet()) {
			String keyString = key.getKey().toString();
			String[] splitArray = keyString.split("\\.");
			String keyIndex = splitArray.length == 2 ? splitArray[1] : null;
			String passwordString = key.getValue().toString();
			
			if(!Objects.nonNull(keyIndex)) continue;
			if(!keyString.contains(property) || uniquePasswords.contains(passwordString)) continue;
			uniquePasswords.add(passwordString);
			passwordList.add(new IndexValuePair(keyIndex, passwordString));	
		}

		passwordList.sort(null);
		return passwordList
				.stream()
				.filter(pair -> !pair.getValue().equalsIgnoreCase("skip"))
				.collect(Collectors.toList());
	}
}
