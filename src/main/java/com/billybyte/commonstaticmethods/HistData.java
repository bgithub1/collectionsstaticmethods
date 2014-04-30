package com.billybyte.commonstaticmethods;

import java.util.Calendar;


public class HistData {
//	Date, Open, High, Low, Close, Volume, Adj Close]
	private final long dateInMills;
	private final double open;
	private final double high;
	private final double low;
	private final double close;
	private final long volume;
	private final double adjClose;
	private final long openInterest;

	private static final long getMills(int year,int month,int day, 
			int hour, int minute, int sec, int millisecond){
		Calendar c = Calendar.getInstance();
		c.set(year,month-1,day,hour,minute,sec);
		c.set(Calendar.MILLISECOND, millisecond);
		return c.getTimeInMillis();
	}
	
	public HistData(int year,int month,int day, int hour, int minute, int sec, int millisecond,
			double open, double high, double low,
			double close, long volume, double adjClose, long openInterest) {
		this(
				getMills(year,month,day,hour,minute,sec,millisecond),
				open, high, low, close, volume, adjClose, openInterest);
	}
	
	
	public HistData(long dateInMills, double open, double high, double low,
			double close, long volume, double adjClose, long openInterest) {
		super();
		this.dateInMills = dateInMills;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.adjClose = adjClose;
		this.openInterest = openInterest;
	}
	public long getDateInMills() {
		return dateInMills;
	}
	public double getOpen() {
		return open;
	}
	public double getHigh() {
		return high;
	}
	public double getLow() {
		return low;
	}
	public double getClose() {
		return close;
	}
	public long getVolume() {
		return volume;
	}
	public double getAdjClose() {
		return adjClose;
	}
	public long getOpenInterest() {
		return openInterest;
	}
	
	@Override
	public String toString() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateInMills);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		int mill = c.get(Calendar.MILLISECOND);
		return year+":"+month+":"+day+":"+min+":"+sec+":"+mill + ", " + open + ", " + high + ", " + low + ", "
				+ close + ", " + volume + ", " + adjClose + ", " + openInterest;
	}

	
}
