package com.billybyte.commonstaticmethods;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class BusinessDateIterable implements Iterable<DateTime> {

	private String currentLocale = "US";
	private static DateTimeFormatter dtFormat = DateTimeFormat.forPattern("yyyyMMdd");

	/**
	 * Create a business date iterator
	 * @param startDate
	 * @param endDate
	 */
	
	private BusinessDateIterable(){
		holidaysMap.put("US", holidays);
		holidaysMap.put("UK", londonHolidays);
		holidaysMap.put("NYMEX", nymexHolidays);
	}
	
	public BusinessDateIterable( DateTime startDate, DateTime endDate)
	{
		
		this();
		
		this.startDate = new DateTime(startDate);
		while( !isTradingDay(this.startDate))
			this.startDate = this.startDate.plusDays(1);
		this.endDate = new DateTime(endDate);
		while(!isTradingDay(this.endDate))
			this.endDate = this.endDate.minusDays(1);
	}
	
	public BusinessDateIterable( String locale,DateTime startDate, DateTime endDate)
	{
		this();
//		this(startDate,endDate);
		this.currentLocale = locale;
		
		
		this.startDate = new DateTime(startDate);
		while( !isTradingDay(this.startDate))
			this.startDate = this.startDate.plusDays(1);
		this.endDate = new DateTime(endDate);
		while(!isTradingDay(this.endDate))
			this.endDate = this.endDate.minusDays(1);
	}

	
	//	/**
//	 * @param startDate
//	 * @param endDate
//	 * @param newHolidays
//	 * @param ignoreDefaultHolidays
//	 */
//	public BusinessDateIterable( String locale,DateTime startDate, DateTime endDate, String[] newHolidays, boolean ignoreDefaultHolidays )
//	{
//		this( startDate, endDate);
//		if( ignoreDefaultHolidays )
//			this.holidays = newHolidays;
//		else
//			addHolidays(locale, newHolidays);
//	}
	
	private class BusinessDateIterator implements Iterator<DateTime>
	{
		private  int compareYyyyMmDd(DateTime d0, DateTime d1){
			int yyyyMmDd0 = d0.getYear()*10000+d0.get(DateTimeFieldType.monthOfYear())*100+d0.get(DateTimeFieldType.dayOfMonth());
			int yyyyMmDd1 = d1.getYear()*10000+d1.get(DateTimeFieldType.monthOfYear())*100+d1.get(DateTimeFieldType.dayOfMonth());
			return yyyyMmDd0-yyyyMmDd1;
		}
		
		private DateTime currentDate = null;
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
//			if( currentDate == null )
//				return( startDate.compareTo(endDate) <= 0);
//			else
//				return( currentDate.compareTo(endDate) < 0);

			if( currentDate == null )
				return( compareYyyyMmDd(startDate,endDate) <= 0);
			else
				return( compareYyyyMmDd(currentDate,endDate) < 0);
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public DateTime next() {
			if( currentDate == null )
				currentDate = startDate;
			else
				currentDate = currentDate.plusDays(1);
			while(!isTradingDay(currentDate))
				currentDate = currentDate.plusDays(1);

//			if(currentDate.compareTo(endDate) <= 0 )
				if(compareYyyyMmDd(currentDate,endDate) <= 0 )
				return currentDate;
			else
				return null;

		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			// take no action
			
		}
		
		
	}
	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<DateTime> iterator() {
		// TODO Auto-generated method stub
		return new BusinessDateIterator();
	}


	private DateTime startDate;
	private DateTime endDate;
	
	private Map<String, String[]> holidaysMap = 
		new HashMap<String,String[]>();

	// EDIT THESE SETTINGS
	private String[] holidays = {
		"20061123",
		"20061225",
		"20070101",
		"20070102",
		"20070115",
		"20070219",
		"20070528",
		"20070704",
		"20070903",
		"20071122",
		"20071225",
		"20080101",
		"20080121",
		"20080218",
		"20080526",
		"20080704",
		"20080901",
		"20081013",
		"20081111",
		"20081127",
		"20081225",
		"20090119",
		"20090216",
		"20090410",
		"20090525",
		"20090703",
		"20090907",
		"20091126",
		"20091225",
		"20100101",
		"20100118",
		"20100215",
		"20100402",
		"20100531",
		"20100705",
		"20100906",
		"20101125",
		"20101225",
		"20110101",
		"20110117",
		"20110221",
		"20110422",
		"20110530",
		"20110704",
		"20110905",
		"20111010",
		"20111111",
		"20111124",
		"20111226",
		"20120102",
		"20120116",
		"20120220",
		"20120404",
		"20120528",
		"20120704",
		"20120903",
		"20121008",
		"20121112",
		"20121122",
		"20121225",
		"20130101",
		"20130121",
		"20130218",
		"20130329",
		"20130527",
		"20130704",
		"20130902",
//		"20131014",
//		"20131111",
		"20131128",
		"20131225",
		"20140101",
		"20140120",
		"20140217",
		"20140418",
		"20140526",
		"20140704",
		"20140901",
		"20141013",
		"20141111",
		"20141127",
		"20141225",
		"20150101",
		"20150119",
		"20150216",
		"20150403",
		"20150525",
		"20150704",
		"20150907",
		"20151012",
		"20151111",
		"20151126",
		"20151225",
		"20160101",
		"20160118",
		"20160215",
		"20160530",
		"20160704",
		"20160905",
		"20161010",
		"20161111",
		"20161124",
		"20161226",
		"20160325",
		"20170102",
		"20170116",
		"20170220",
		"20170414",
		"20170529",
		"20170704",
		"20170904",
		"20171009",
		"20171111",
		"20171123",
		"20171225",
		"20180101",
		"20180115",
		"20180219",
		"20180330",
		"20180528",
		"20180704",
		"20180903",
		"20181008",
		"20181112",
		"20181122",
		"20181225",
		"20190101",
		"20190121",
		"20190218",
		"20190419",
		"20190527",
		"20190704",
		"20190902",
		"20191014",
		"20191111",
		"20191128",
		"20191225",
		"20200101",
		"20200120",
		"20200217",
		"20200410",
		"20200525",
		"20200704",
		"20200907",
		"20201012",
		"20201111",
		"20201126",
		"20201225",
		"20210101",

		
	};
	

	private String[] londonHolidays={
		"20070406",
		"20070409",
		"20070507",
		"20070827",
		"20071226",
		"20080321",
		"20080324",
		"20080505",
		"20080825",
		"20081225",
		"20081226",
		"20090101",
		"20090410",
//		"20090413",
//		"20090504",
//		"20090525",
//		"20090831",
		"20091225",
//		"20091228",
		"20100101",
		"20110103",
		"20110422",
		"20110425",
		"20110429",
		"20110502",
		"20110530",
		"20110829",
		"20111226",
		"20111227",
		"20120102",
		"20120406",
		"20120409",
		"20120507",
		"20120604",
		"20120605",
		"20120827",
		"20121225",
		"20121226",
		"20130101",
		"20130329",
		"20130401",
		"20130506",
		"20130527",
		"20130826",
		"20131225",
		"20131226",
		"20140101",
		"20140418",
		"20140421",
		"20140505",
		"20140526",
		"20140825",
		"20141225",
		"20141226",
		"20150101",
		"20150403",
		"20150406",
		"20150504",
		"20150525",
		"20150831",
		"20151225",
		"20151228",
		"20160101",
		"20160325",
		"20160328",
		"20160502",
		"20160530",
		"20160829",
		"20161226",
		"20161227",
		"20170102",
		"20170414",
		"20170417",
		"20170501",
		"20170529",
		"20170828",
		"20171225",
		"20171226",
		"20180101",
		"20180330",
		"20180402",
		"20180507",
		"20180528",
		"20180827",
		"20181225",
		"20181226",
		"20190101",
		"20190419",
		"20190422",
		"20190506",
		"20190527",
		"20190826",
		"20191225",
		"20191226",
		"20200101",
		"20200410",
		"20200413",
		"20200504",
		"20200525",
		"20200831",
		"20201225",
		"20201228",
		
	};
	
	private String[] nymexHolidays={
			"20061123",
			"20061225",
			"20070101",
			"20070102",
			"20070115",
			"20070219",
			"20070528",
			"20070704",
			"20070903",
			"20071122",
			"20071225",
			"20080101",
			"20080121",
			"20080218",
			"20080526",
			"20080704",
			"20080901",
			"20081013",
			"20081111",
			"20081127",
			"20081225",
			"20090119",
			"20090216",
			"20090410",
			"20090525",
			"20090703",
			"20090907",
			"20091126",
			"20091225",
			"20100101",
			"20100118",
			"20100215",
			"20100402",
			"20100531",
			"20100705",
			"20100906",
			"20101125",
			"20101225",
			"20110101",
			"20110117",
			"20110221",
			"20110422",
			"20110530",
			"20110704",
			"20110905",
//			"20111010",
			"20111111",
			"20111124",
			"20111226",
			"20120102",
			"20120116",
			"20120220",
			"20120404",
			"20120528",
			"20120704",
			"20120903",
//			"20121008",
			"20121112",
			"20121122",
			"20121225",
			"20130101",
			"20130121",
			"20130218",
			"20130329",
			"20130527",
			"20130704",
			"20130902",
//			"20131014",
			"20131111",
			"20131128",
			"20131225",
			"20140101",
			"20140120",
			"20140217",
			"20140418",
			"20140526",
			"20140704",
			"20140901",
//			"20141013",
			"20141111",
			"20141127",
			"20141225",
			"20150101",
			"20150119",
			"20150216",
			"20150403",
			"20150525",
			"20150704",
			"20150907",
//			"20151012",
			"20151111",
			"20151126",
			"20151225",
			"20160101",
			"20160118",
			"20160215",
			"20160530",
			"20160704",
			"20160905",
//			"20161010",
			"20161111",
			"20161124",
			"20161226",
			"20160325",
			"20170102",
			"20170116",
			"20170220",
			"20170414",
			"20170529",
			"20170704",
			"20170904",
//			"20171009",
			"20171111",
			"20171123",
			"20171225",
			"20180101",
			"20180115",
			"20180219",
			"20180330",
			"20180528",
			"20180704",
			"20180903",
//			"20181008",
			"20181112",
			"20181122",
			"20181225",
			"20190101",
			"20190121",
			"20190218",
			"20190419",
			"20190527",
			"20190704",
			"20190902",
//			"20191014",
			"20191111",
			"20191128",
			"20191225",
			"20200101",
			"20200120",
			"20200217",
			"20200410",
			"20200525",
			"20200704",
			"20200907",
//			"20201012",
			"20201111",
			"20201126",
			"20201225",
			"20210101",
			
			
		};
		
	
	
	/**
	 * Trading days are weekdays which are not on the holiday list
	 * @param day
	 * @return true if the day is a trading day
	 * 
	 */
	public boolean isTradingDay( DateTime day ) {
		String[] holidayList = holidaysMap.get(currentLocale);
		return (Arrays.binarySearch(holidayList, dtFormat.print(day)) < 0 &&
				day.dayOfWeek().get() != DateTimeConstants.SATURDAY &&
				day.dayOfWeek().get() != DateTimeConstants.SUNDAY );
	}
	
	public boolean isTradingDay( String symbol,DateTime day , String Exchange) {
		
		String[] addtionalHoliday={"20071123", "20071224"};
		Boolean isInAddtionalHoliday=false;
				
		if(Exchange.equals("CME"))
		{
			if(Arrays.binarySearch(addtionalHoliday, dtFormat.print(day)) >= 0)
				isInAddtionalHoliday=true;
		}
	
		if(symbol.contains("BB"))
		    return (Arrays.binarySearch(holidays, dtFormat.print(day)) < 0 &&
					day.dayOfWeek().get() != DateTimeConstants.SATURDAY &&
					day.dayOfWeek().get() != DateTimeConstants.SUNDAY  &&
					!isInAddtionalHoliday &&
					Arrays.binarySearch(londonHolidays, dtFormat.print(day)) < 0);
		else
			return (Arrays.binarySearch(holidays, dtFormat.print(day)) < 0 &&
					day.dayOfWeek().get() != DateTimeConstants.SATURDAY &&
					day.dayOfWeek().get() != DateTimeConstants.SUNDAY  &&
					!isInAddtionalHoliday);
	}
	
	public boolean isLondonHoliday( DateTime day) {
		return (Arrays.binarySearch(londonHolidays, dtFormat.print(day)) > 0 || 
				day.dayOfWeek().get()== DateTimeConstants.SATURDAY ||
				day.dayOfWeek().get()== DateTimeConstants.SUNDAY );
	}
	

	/**
	 * @return the holidays, excluded from the list
	 */
	public String[] getHolidays() {
		return holidays;
	}
	
	/**
	 * Adds the given holidays to the list;
	 * @param newHolidays
	 */
	private void addHolidays( String local,String[] newHolidays ) {
		Collection<String> oldHols = Arrays.asList(this.holidays);
		Collection<String> newHols = Arrays.asList(newHolidays);
		SortedSet<String> sorter   = new TreeSet<String>(oldHols);
		sorter.addAll(newHols);
		this.holidays = sorter.toArray(new String[] {} );
	}
	
	/**
	 * @return the last business date in the list
	 */
	public final DateTime getEndDate() {
		return endDate;
	}

	/**
	 * @return the first business date in the list
	 */
	public final DateTime getStartDate() {
		return startDate;
	}

}
