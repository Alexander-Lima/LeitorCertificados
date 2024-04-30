package com.programs.main;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.programs.main.interfaces.CertificateParser;

public class CertificateDumpParser implements CertificateParser {
	public String parse(String data) {
		String line =  extractExpiryDate(data);
		if(line.length() == 0) return "SENHA INVÁLIDA"; 
		String dateString =  extractDate(line);
		return isExpired(dateString) ? "VENCIDO" : dateString;
	}
	
	private String extractDate(String line) {
		return line
						.replaceAll("NotAfter: ", "")
						.replaceAll("/", "-")
						.replaceAll(" ", "")
						.subSequence(0, 10)
						.toString();
	};

	private String extractExpiryDate(String text) {
		Pattern pattern = Pattern.compile("NotAfter:[\\s\\S]*?\\n");
		Matcher matcher = pattern.matcher(text);
		Comparator<MatchResult> comparator = 
				(match1, match2) -> match2.group().compareTo(match1.group());
		
		if(matcher.find()) {
			String uniqueMatch = matcher.group();
			Optional<MatchResult> optionalResult = 
					matcher
						.results()
						.sorted(comparator)
						.findFirst();
			
			if(optionalResult.isPresent()) {
				return optionalResult
									.get()
									.group()
									.replaceAll("\n", "");
			} else {
				return uniqueMatch.replaceAll("\n", "");
			}
		}
		return "";
	}
	
	private Boolean isExpired(String dateString) {
		final String[] parts = dateString.split("-");
		final String day = parts[0];
		final String month = parts[1];
		final String year = parts[2];
		final String IsoFomattedDate = String.format("%s-%s-%s", year, month, day);
		LocalDate date = LocalDate.parse(IsoFomattedDate);
		return date.isBefore(LocalDate.now());
	}
	
}
