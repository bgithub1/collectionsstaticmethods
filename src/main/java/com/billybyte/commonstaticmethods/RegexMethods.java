package com.billybyte.commonstaticmethods;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMethods {
	public static List<String> getRegexMatches(String regexExpression,String stringToSearch){
//		List<String> ret = new ArrayList<String>();
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatches(matcher);

	}
	


	public static List<String> getRegexMatches(Pattern pattern,String stringToSearch){
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatches(matcher);
	}
	
	public static List<String> getRegexMatches(Matcher matcher){
		List<String> ret = new ArrayList<String>();
		HashSet<String> foundTokens = new HashSet<String>();
		while (matcher.find()) {
			String token = matcher.group();
			if(foundTokens.contains(token)){
				continue;
			}
			ret.add(token);
			foundTokens.add(token);
		}
		return ret;
	}

	public static List<Integer> getRegexMatchesIndices(Pattern pattern,String stringToSearch){
		Matcher matcher = pattern.matcher(stringToSearch);
		List<Integer> ret = new ArrayList<Integer>();
		while (matcher.find()) {
			Integer token = matcher.start();
			ret.add(token);
		}
		return ret;
	}

	
	public static List<Pattern> createPatternListFromStringList(List<String> stringList){
		List<Pattern> ret = new ArrayList<Pattern>();
		for(String s:stringList){
			Pattern p = Pattern.compile(s);
			ret.add(p);
		}
		return ret;
	}
	
	public static List<Pattern> createPatternListFromStringArray(String[] stringArray){
		return createPatternListFromStringList(Arrays.asList(stringArray));
	}
	
	public static int getRegexBestMatch(List<Pattern> patternList,String stringToSearch){
		String maxLenString = "";
		int indexOfBestMatch=-1;
		for(int i = 0;i<patternList.size();i++){
			Pattern pattern = patternList.get(i);
			List<String> matches  = getRegexMatches(pattern, stringToSearch);
			if(matches==null || matches.size()<1)continue;
			String match = matches.get(0);
			int len = match.length();
			if(len>maxLenString.length()){
				maxLenString = match;
				indexOfBestMatch = i;
			}
		}
		return indexOfBestMatch;
	}

	
	public static List<String> getRegexMatchesAllowRepeats(String regexExpression,String stringToSearch){
//		List<String> ret = new ArrayList<String>();
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatchesAllowRepeats(matcher);

	}
	


	public static List<String> getRegexMatchesAllowRepeats(Pattern pattern,String stringToSearch){
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatchesAllowRepeats(matcher);
	}

	public static List<String> getRegexMatchesAllowRepeats(Matcher matcher){
		List<String> ret = new ArrayList<String>();
		HashSet<String> foundTokens = new HashSet<String>();
		while (matcher.find()) {
			String token = matcher.group();
			ret.add(token);
			foundTokens.add(token);
		}
		return ret;
		
	}

	
	/**
	 * ^([\+\-\$]{0,1})([0-9]{1,100})\.([0-9]{1,})$
	 */
	
	// the double escape below because, in java strings, the backslash itself must be escaped.  
	//   therefore, regex demands that you escape the plus, minus and dollar signs inside of brackets, and java
	//     demands that you escape the escape character.  If you copy and paste this expression:  ^([\+\-\$]{0,1})([0-9]{1,100})\.([0-9]{1,})$
	//     into the RegexTestHarness, it works without the double escape, because only java JVM needs the double escape.
	private static String regexNumberPrefix = "([+\\-\\$]{0,1})";
	private static String regexNumberStringWithCommaWithoutDecimal=regexNumberPrefix+"([0-9]{1,3})((,{1,1})([0-9]{3,3})){1,}";
	private static String regexNumberStringWithCommaWithDecimal=regexNumberStringWithCommaWithoutDecimal+"(.{1,1})([0-9]{1,})";
	private static String regexNumberStringWithoutCommaWithoutDecimal=regexNumberPrefix+"([0-9]{1,})";
	private static String regexNumberStringWithoutCommaWithDecimal=regexNumberPrefix+"([0-9]{0,})(.{1,1})([0-9]{1,})";
	private static Pattern pattern_regexNumberStringWithCommaWithoutDecimal;
	private static Pattern pattern_regexNumberStringWithCommaWithDecimal;
	private static Pattern pattern_regexNumberStringWithoutCommaWithoutDecimal;
	private static Pattern pattern_regexNumberStringWithoutCommaWithDecimal;
	/**
	 * test to see if string is a number, without simply using BigDecimal exceptions
	 * 
	 * @param numberString
	 * @return boolean true or false
	 */
	public static boolean isNumber(String numberString){
		if(getValidBigDecimal(numberString)!=null){
			return true;
		}else{
			return false;
		}
	}

	private static Object getValidBigDecimal_Lock = new  Object();
	public static BigDecimal getValidBigDecimal(String numberString){
		if(numberString==null)return null;
		Matcher matcher;
		List<String> retList;
		String trimmedString = numberString.trim();
		if(trimmedString.contains(",") ){
			if(trimmedString.contains(".")){
				synchronized (getValidBigDecimal_Lock) {
					if (pattern_regexNumberStringWithCommaWithDecimal == null) {
						pattern_regexNumberStringWithCommaWithDecimal = Pattern
								.compile("^"
										+ regexNumberStringWithCommaWithDecimal
										+ "$");
					}
				}
//				matcher = pattern_regexNumberStringWithCommaWithDecimal.matcher(trimmedString);
				retList = getRegexMatches(pattern_regexNumberStringWithCommaWithDecimal,trimmedString);
				if(retList!=null && retList.size()>0 )return getBigDecimalFromNumberString(numberString);
			}else{
				synchronized (getValidBigDecimal_Lock) {
					if (pattern_regexNumberStringWithCommaWithoutDecimal == null) {
						pattern_regexNumberStringWithCommaWithoutDecimal = Pattern
								.compile("^"
										+ regexNumberStringWithCommaWithoutDecimal
										+ "$");
					}
				}
				matcher = pattern_regexNumberStringWithCommaWithoutDecimal.matcher(trimmedString);
				retList = getRegexMatches(matcher);
//				if(retList!=null && retList.size()>0 )return new BigDecimal(retList.get(0));
				if(retList!=null && retList.size()>0 )return getBigDecimalFromNumberString(numberString);
			}
		}else{
			if(trimmedString.contains(".")){
				synchronized (getValidBigDecimal_Lock) {
					if (pattern_regexNumberStringWithoutCommaWithDecimal == null) {
						pattern_regexNumberStringWithoutCommaWithDecimal = Pattern
								.compile("^"
										+ regexNumberStringWithoutCommaWithDecimal
										+ "$");
					}
				}
				matcher = pattern_regexNumberStringWithoutCommaWithDecimal.matcher(trimmedString);
				retList = getRegexMatches(matcher);
//				if(retList!=null && retList.size()>0 )return new BigDecimal(retList.get(0));
				if(retList!=null && retList.size()>0 )return getBigDecimalFromNumberString(numberString);
			}else{
				synchronized (getValidBigDecimal_Lock) {
					if (pattern_regexNumberStringWithoutCommaWithoutDecimal == null) {
						pattern_regexNumberStringWithoutCommaWithoutDecimal = Pattern
								.compile("^"
										+ regexNumberStringWithoutCommaWithoutDecimal
										+ "$");
					}
				}
				matcher = pattern_regexNumberStringWithoutCommaWithoutDecimal.matcher(trimmedString);
				retList = getRegexMatches(matcher);
//				if(retList!=null && retList.size()>0 )return new BigDecimal(retList.get(0));
				if(retList!=null && retList.size()>0 )return getBigDecimalFromNumberString(numberString);
			}
		}
		return null;

	}
	
	private static Pattern pattern_regexNumberStringWithoutCommaWithoutDecimal_inclusive;
	public static BigDecimal getBigDecimalFromNumberString(String numberString){
		synchronized (getValidBigDecimal_Lock) {
			if (pattern_regexNumberStringWithoutCommaWithoutDecimal_inclusive == null) {
				pattern_regexNumberStringWithoutCommaWithoutDecimal_inclusive = Pattern
						.compile("([0-9]{1,})");
			}
		}
		List<String> results = getRegexMatches(pattern_regexNumberStringWithoutCommaWithoutDecimal_inclusive,numberString.trim());
		if(results==null || results.size()<1){
			return null;
		}
		//TODO must debug this 2012 06 23
		String concatenatedString ="";
		for(String result:results){
			concatenatedString = concatenatedString+result;
		}
		return new BigDecimal(concatenatedString.trim());
	}
	
	
	
}
