package com.billybyte.commonstaticmethods;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;



public class HistDataSources {
	
	private static final DateTimeFormatter dayFormat = DateTimeFormat.forPattern("d").withZone(DateTimeZone.UTC);
	private static final DateTimeFormatter monthFormat = DateTimeFormat.forPattern("M").withZone(DateTimeZone.UTC);
	private static final DateTimeFormatter yearFormat = DateTimeFormat.forPattern("yyyy").withZone(DateTimeZone.UTC);
	private static final DateTimeFormatter yahooFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.forID("America/New_York"));
	public static final DateTimeFormatter intFormat = DateTimeFormat.forPattern("yyyyMMdd").withZone(DateTimeZone.forID("America/New_York"));
	
	public static final int YAHOO_DATE= 0;
	public static final int YAHOO_OPEN = 1;
	public static final int YAHOO_HIGH = 2;
	public static final int YAHOO_LOW = 3;
	public static final int YAHOO_CLOSE = 4;
	public static final int YAHOO_VOL = 5;
	public static final int YAHOO_ADJ_CLOSE = 6;
	public static final int YAHOO_OPEN_INTEREST = 7; // not there for stocks

	
	public static String createYahooQuoteURL(String symbol){
		return "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl1d1t1c1ohgv&e=.csv";
	}
	public static String createYahooHistoryURL(String symbol,long begDateInMillisecondTime, long endDateInMillisecondTime){
		DateTime startDate = new DateTime(begDateInMillisecondTime);
		DateTime todaysDate = new DateTime(endDateInMillisecondTime);
		Integer startMonth = new Integer(monthFormat.print(startDate))-1;
		Integer endMonth = new Integer(monthFormat.print(todaysDate))-1;
		return "http://ichart.finance.yahoo.com/table.csv" +
		"?s=" +
		symbol + "&a="  +
		startMonth.toString()+
		"&b=" + 
		dayFormat.print(startDate) + 
		"&c="  + 
		yearFormat.print(startDate) +
		"&d=" +
		endMonth.toString()+
		"&e=" +
		dayFormat.print(todaysDate) + "&f=" +
		yearFormat.print(todaysDate) +
		"&g=d&ignore=.csv";

	}
	
	public static List<HistData> getYahooDailyHistData(
			String symbol,long begDateInMillisecondTime, 
			long endDateInMillisecondTime) {
		
		List<String[]> csv = getYahooCsvData(symbol, begDateInMillisecondTime, endDateInMillisecondTime);
		List<HistData> ret = new ArrayList<HistData>();
		
		for(int i = csv.size()-1;i>0;i--){
			String[] line = csv.get(i);
			
			Calendar c =  yahooDateToCalendar(line[YAHOO_DATE]);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			long dateInMills = c.getTimeInMillis();
			double open =line.length>YAHOO_OPEN ? new Double(line[YAHOO_OPEN]) : 0.0; 
			double high =line.length>YAHOO_HIGH ? new Double(line[YAHOO_HIGH]) : 0.0; 
			double low = line.length>YAHOO_LOW ? new Double(line[YAHOO_LOW]) : 0.0;
			double close = line.length>YAHOO_ADJ_CLOSE ?   new Double(line[YAHOO_CLOSE]) : 0.0;
			long volume = line.length>YAHOO_VOL ? new Long(line[YAHOO_VOL]) : 0;
			double adjClose = line.length>YAHOO_ADJ_CLOSE ? new Double(line[YAHOO_ADJ_CLOSE]) : 0.0;
			
			long openInterest = line.length>YAHOO_OPEN_INTEREST ? new Long(line[YAHOO_OPEN_INTEREST]) : 0;
			HistData hd = 
					new HistData(
							dateInMills, open, high, 
							low, close, volume, 
							adjClose, openInterest);
			ret.add(hd);
			
		}
		return ret;
	}
	
	private static final Calendar yahooDateToCalendar(String yahooDate){
		int year;
		int month;
		int day ;
		// figure out format
		if(yahooDate.contains("/")){
			// it's mm/dd/yy or mm/dd/yyyy
			String[] parts = yahooDate.split("/");
			if(parts.length!=3){
				throw Utils.IllArg(HistDataSources.class," bad input date: "+yahooDate);
			}
			day = new Integer(parts[1]);
			month = new Integer(parts[0]);
			year = new Integer(parts[2]);
			
		}else{
			year = new Integer(yahooDate.substring(0, 4));
			month = new Integer(yahooDate.substring(5,7));
			day = new Integer(yahooDate.substring(8,10));
		}
		Calendar c = Calendar.getInstance(); 
		c.set(year, month-1, day);
		return c;
	}
	public static List<String[]> getYahooCsvData(String symbol,long begDateInMillisecondTime, long endDateInMillisecondTime) {
		
		
		String name= createYahooHistoryURL(symbol, begDateInMillisecondTime, endDateInMillisecondTime);
		URL yahooUrl=null;
		try {
			yahooUrl = new URL(name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Can't find "+symbol);
			return null;
		}

		InputStream iS=null;
		try {
			iS = yahooUrl.openStream();
		} catch (IOException e) {
			System.out.println("Can't find "+symbol);
			e.printStackTrace();
			return null;
		}
		ArrayList<String[]> retList = new ArrayList<String[]>();
		DataInputStream stream = new DataInputStream( new BufferedInputStream(iS));
		String line=null;
		try {
			while ((line = stream.readLine()) != null) {
				retList.add(line.split(","));
			}
		} catch (Exception e) {
			System.out.println("Can't find "+symbol);
			e.printStackTrace();
			return null;
		}
		
		return retList;
	
	}

}
