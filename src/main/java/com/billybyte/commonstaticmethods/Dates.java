package com.billybyte.commonstaticmethods;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;


public class Dates {
	private static final String[] MONTH_CODES_REGULAR = {"F","G","H","J","K","M","N","Q","U","V","X","Z"};
	private static 		DecimalFormat dfMonth = new DecimalFormat("00");
	private static 		DecimalFormat dfYear = new DecimalFormat("0000");
	
	public enum DateField{
		YEAR,
		MONTH,
		DAY;
	}
	
	public static int getLastDayOfMonth(int year, int month){
		Calendar c = Calendar.getInstance();
		c.set(year, month,1);
		return c.getActualMaximum(c.DAY_OF_MONTH);
		
	}

	public static DateTime getDateTimeFromCalendar(Calendar c){
		DateTime dateTime = new DateTime(c.getTimeInMillis());
		if(c.getTimeInMillis()!=dateTime.getMillis()){
			return null;
		}
		return dateTime;
	}
	
	public static List<DateTime> getAllBusinessDays(Calendar startDate, Calendar endDate){
		return getAllBusinessDays(getDateTimeFromCalendar(startDate),getDateTimeFromCalendar(endDate));
	}
	
	public static List<DateTime> getAllBusinessDays(String locale,Calendar startDate, Calendar endDate){
		return getAllBusinessDays(locale,getDateTimeFromCalendar(startDate),getDateTimeFromCalendar(endDate));
	}

	
	public static List<DateTime> getAllBusinessDays(DateTime startDate, DateTime endDate){
		BusinessDateIterable bdi = new BusinessDateIterable(startDate, endDate);
		List<DateTime> returnArray = new ArrayList<DateTime>();
		for(DateTime dt : bdi){
			returnArray.add(dt);
		}
		return returnArray;
	}
	
	public static List<DateTime> getAllBusinessDays(String locale,DateTime startDate, DateTime endDate){
		BusinessDateIterable bdi = new BusinessDateIterable(locale,startDate, endDate);
		List<DateTime> returnArray = new ArrayList<DateTime>();
		for(DateTime dt : bdi){
			returnArray.add(dt);
		}
		return returnArray;
	}

	public static boolean isBusinessDay(String locale,Calendar date){
		List<DateTime> bussDays = getAllBusinessDays(locale,new DateTime(date.getTimeInMillis()),new DateTime(date.getTimeInMillis()));
		if(bussDays!=null && bussDays.size()>0){
			return true;
			
		}else{
			return false;
		}
	}
	
	public static Calendar addBusinessDays(String locale,Calendar startDate, int daysToAdd){
		int businessDaysToFetch = Math.abs(daysToAdd*3);
		if(businessDaysToFetch<6)businessDaysToFetch=6;
		if(daysToAdd>0){
//			Calendar endDate = Dates.addToCalendar(startDate,daysToAdd*3,Calendar.DAY_OF_MONTH,false);
			Calendar endDate = Dates.addToCalendar(startDate,businessDaysToFetch,Calendar.DAY_OF_MONTH,false);
			Calendar begDate = Dates.addToCalendar(startDate,1,Calendar.DAY_OF_MONTH,false);
			List<DateTime> bussDays = getAllBusinessDays(locale,new DateTime(begDate.getTimeInMillis()),new DateTime(endDate.getTimeInMillis()));
			Calendar ret = Calendar.getInstance();
//			ret.setTimeInMillis(bussDays.get(daysToAdd).getMillis());
			ret.setTimeInMillis(bussDays.get(daysToAdd-1).getMillis());
			return ret;
		}
		if(daysToAdd<0){
			// if you have to go back in time, get a set of business dates, then return the day
			//   that is at set.get(set.size()-abs(daysToAdd) ,  
			//       which, in this case is set.size()+daysToAdd, 
			//         since daysToAdd is negative here
			
//			Calendar begDate = Dates.addToCalendar(startDate,daysToAdd*3,Calendar.DAY_OF_MONTH,false);
			Calendar begDate = Dates.addToCalendar(startDate,businessDaysToFetch*-1,Calendar.DAY_OF_MONTH,false);
			Calendar endDate = Dates.addToCalendar(startDate,-1,Calendar.DAY_OF_MONTH,false);
			List<DateTime> bussDays = getAllBusinessDays(locale,new DateTime(begDate.getTimeInMillis()),new DateTime(endDate.getTimeInMillis()));
			Calendar ret = Calendar.getInstance();
			int index = bussDays.size()+daysToAdd; // daysToAdd is negative, so your are subtracting here
			ret.setTimeInMillis(bussDays.get(index).getMillis());
			return ret;
		}
		// daysToAdd = 0, get the most recent "preceeding" business day.  
		//   for example, if startDate falls on a Sunday, then the most recent preceeding business day (assuming no holidays) is 
		//    the previous Friday
		Calendar begDate = Dates.addToCalendar(startDate,-10,Calendar.DAY_OF_MONTH,false);
		Calendar endDate = startDate;
		List<DateTime> bussDays = getAllBusinessDays(locale,new DateTime(begDate.getTimeInMillis()),new DateTime(endDate.getTimeInMillis()));
		Calendar ret = Calendar.getInstance();
		ret.setTimeInMillis(bussDays.get(bussDays.size()-1).getMillis());
		return ret;
	}


	public static boolean regexMatch(String regexExpression,String keyToSearch){
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = 	pattern.matcher(keyToSearch);
		return matcher.find();
	}
	
	public static void prt(String s){
		System.out.println(s);
	}
	
	public static void sleep(long millsToSleep){
		try {
			Thread.sleep(millsToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException(Dates.class.getName());
		}
	}

	/**
	 * 
	 * @param cInitial start calendar
	 * @param unitsToAdd integer of units to add
	 * @param unitType - like Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.YEAR, etc.
	 * @param allowWeekends
	 * @return
	 */
	public static Calendar addToCalendar(Calendar cInitial,int unitsToAdd,int unitType,boolean allowWeekends){
		Calendar cEarly = Calendar.getInstance();
		cEarly = (Calendar)cInitial.clone();
		
		cEarly.add(unitType,unitsToAdd);
//		Long timeInMills = cEarly.getTimeInMillis() + new Long(1000*24*60*60)*new Long(daysToAdd);
//		cEarly.setTimeInMillis(timeInMills);
		if(!allowWeekends){
			// make sure date is not a weekend
			if(cEarly.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				if(unitsToAdd>0){
					// set time one day earlier
					cEarly.add(Calendar.DAY_OF_MONTH,2);// go forward 2 days
				}else{
					// set time one day earlier
					cEarly.add(Calendar.DAY_OF_MONTH,-1); // go back 1 day
				}
			}else if(cEarly.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){ 
				if(unitsToAdd>0){
					cEarly.add(Calendar.DAY_OF_MONTH, 1);// go forward 1 day
				}else{
					cEarly.add(Calendar.DAY_OF_MONTH,-2);// go back 2 days
				}
			}
		}
		return cEarly;
		
	}
	public static Long addCalendarDaysToYYYYMMDDDate(Long initialDateYYYYMMDD,int daysToAdd,boolean allowWeekends){
		Calendar cEarly = Calendar.getInstance();
		int year = new Integer(initialDateYYYYMMDD.toString().substring(0,4));
		int month = new Integer(initialDateYYYYMMDD.toString().substring(4,6));
		int day = new Integer(initialDateYYYYMMDD.toString().substring(6,8));
		
		cEarly.set(year,month-1,day);
		cEarly.add(Calendar.DAY_OF_MONTH,daysToAdd);
//		Long timeInMills = cEarly.getTimeInMillis() + new Long(1000*24*60*60)*new Long(daysToAdd);
//		cEarly.setTimeInMillis(timeInMills);
		if(!allowWeekends){
			// make sure date is not a weekend
			if(cEarly.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				if(daysToAdd>0){
					// set time one day earlier
					cEarly.add(Calendar.DAY_OF_MONTH,2);// go forward 2 days
				}else{
					// set time one day earlier
					cEarly.add(Calendar.DAY_OF_MONTH,-1); // go back 1 day
				}
			}else if(cEarly.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){ 
				if(daysToAdd>0){
					cEarly.add(Calendar.DAY_OF_MONTH, 1);// go forward 1 day
				}else{
					cEarly.add(Calendar.DAY_OF_MONTH,-2);// go back 2 days
				}
			}
		}
		Long earliestDateYYYYMMDD = new Long(cEarly.get(Calendar.YEAR)*100*100 + (cEarly.get(Calendar.MONTH)+1)*100 + cEarly.get(Calendar.DAY_OF_MONTH));
	
		
		
		return earliestDateYYYYMMDD;
	}
	/**
	 * Subtract a from b
	 * @param a
	 * @param b
	 * @param units
	 * @return b - a
	 */
	public static long getDifference(Calendar a, Calendar b, TimeUnit units) {
		    return 
		       units.convert(b.getTimeInMillis()- a.getTimeInMillis(), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 
	 * @param begYYMMDD Long
	 * @param endYYMMDD Long
	 * @param unitsToReturn TimeUnit
	 * @return
	 */
	public static long getDifference(Long begYYMMDD, Long endYYMMDD, TimeUnit unitsToReturn){
		TimeUnit units=unitsToReturn;
		if(unitsToReturn==null)units=TimeUnit.DAYS;
		Calendar beg = getCalenderFromYYYYMMDDLong(begYYMMDD);
		Calendar end = getCalenderFromYYYYMMDDLong(endYYMMDD);
		return getDifference(beg, end, units);
	}
	
	public static long getDaysInMonth(int year, int month){
		Calendar c = Calendar.getInstance();
		Calendar d = Calendar.getInstance();
		c.set(year,month-1,1,0,0,1);
		d.set(year,month-0,1,1,0,1);
		long diff = TimeUnit.DAYS.convert(d.getTimeInMillis()- c.getTimeInMillis(), TimeUnit.MILLISECONDS);
		return diff;
	}

	
	public static Calendar createCalendar(int year, int month, int day, int hour, int min, int sec, int millisecond){
		Calendar c = Calendar.getInstance();
		c.set(year,month-1,day,hour,min,sec);
		c.set(Calendar.MILLISECOND, millisecond);
		return c;
	}

	public static Calendar createCalendar(int year, int month, int day){
		return createCalendar(year,month,day,23,59,59,999);
	}

	public static Calendar createCalendar(int year, int month, int day,int hour){
		return createCalendar(year,month,day,hour,59,59,999);
	}

	public static Calendar createCalendar(int year, int month, int day,int hour,int min){
		return createCalendar(year,month,day,hour,min,59,999);
	}

	public static Calendar createCalendar(int year, int month, int day,int hour,int min, int sec){
		return createCalendar(year,month,day,hour,min,sec,999);
	}

	/**
	 * 
	 * @param yyyyMmDdOptionalHhMmSsMmm this can actually be YYYYMMDD or 
	 * 		YYYYMMDDHH, YYYYMMDDHHMM, YYYYMMDDHHMMSS, YYYYMMDDHHMMSSMMM
	 * @return
	 */
	public static Calendar getCalenderFromYYYYMMDDLong(Long yyyyMmDdOptionalHhMmSsMmm){
		if(yyyyMmDdOptionalHhMmSsMmm<99999999L){
			return createCalendar(
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(0,4)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(4,6)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(6,8)));
		}
		if((yyyyMmDdOptionalHhMmSsMmm<9999999999L)){
			return createCalendar(
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(0,4)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(4,6)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(6,8)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(8,10)));
		}
		if((yyyyMmDdOptionalHhMmSsMmm<999999999999L)){
			return createCalendar(
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(0,4)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(4,6)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(6,8)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(8,10)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(10,12)));
		}
		if((yyyyMmDdOptionalHhMmSsMmm<99999999999999L)){
			return createCalendar(
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(0,4)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(4,6)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(6,8)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(8,10)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(10,12)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(12,14)));
		}
		if((yyyyMmDdOptionalHhMmSsMmm<99999999999999999L)){
			return createCalendar(
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(0,4)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(4,6)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(6,8)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(8,10)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(10,12)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(12,14)),
					new Integer(yyyyMmDdOptionalHhMmSsMmm.toString().substring(14,17)));
		}

		return null;
		
	}
	
	public static Calendar getCalenderFromYYYYMMDDBigDecimal(BigDecimal YYYYMMDD_HHMMSS){
		String dateString = YYYYMMDD_HHMMSS.toString().replace("\\.","");
		
		try {
			return getCalenderFromYYYYMMDDLong(new Long(dateString));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static Long getToday(){
		Calendar c = Calendar.getInstance();
		Long ret = 	new Long(c.get(Calendar.YEAR)*10000+(c.get(Calendar.MONTH)+1)*100+c.get(Calendar.DAY_OF_MONTH));

		return ret;
	}

	/**
	 * returns contract in form MYY when supplied in form YYYYMM (e.g. 201301 -> F13)
	 * @param YYYYMM
	 * @return
	 */
	public static String getMYYfromYYYYMM(String YYYYMM){
		Integer month = Integer.parseInt(YYYYMM.substring(4));
		String year = YYYYMM.substring(2, 4);
		return MONTH_CODES_REGULAR[month-1]+year;
	}
	
	
	
	public static Long getYyyyMmDd(int year,int month,int day){
		DecimalFormat dfYr = new DecimalFormat("0000");
		DecimalFormat dfMonDay = new DecimalFormat("00");
		return new Long(dfYr.format(year)+dfMonDay.format(month)+dfMonDay.format(day));
		
	}
	
	public static String getYyyyMmStringFromCalendar(Calendar c){
		return dfYear.format(c.get(Calendar.YEAR))+dfMonth.format(c.get(Calendar.MONTH)+1);
	}
	
	public static Long getYyyyMmDdFromCalendar(Calendar c){
		return getYyyyMmDd(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
	}
	
	public static Long getYyyyMmDdHhMmSsFromCalendar(Calendar c){
		return getYyyyMmDdHhMmSs(c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
	}
	
	public static Long getYyyyMmDdHhMmSs(int year,int month,int day,int hour, int min, int sec){
		DecimalFormat dfYr = new DecimalFormat("0000");
		DecimalFormat dfMonDay = new DecimalFormat("00");
		return new Long(dfYr.format(year)+dfMonDay.format(month)+dfMonDay.format(day)+dfMonDay.format(hour)+dfMonDay.format(min)+dfMonDay.format(sec));
		
	}

	/**
	 *
	 * @param dateString - many formats, like 09/21/2012 or 2012-09-21
	 * @param dateDelimiter - like "/" or "-"
	 * @param dateFieldOrder - example:
	 * 		new DateField[]{DateField.YEAR,DateField.MONTH,DateField.DAY}
	 * 		SEE the DateField in com.billybyte.commonstaticmethods.Dates.DateField
	 * @param timeDelimiter - like null or ":";
	 * @return
	 */
	public static Long getYyyyMmDdDate(
			String dateString,
			String dateDelimiter,
			DateField[] dateFieldOrder,
			String timeDelimiter){
		
		String[] dateTokens ;
		int year=0;
		int month=0;
		int day=0;

		String[] spaceDelimitedTokens = dateString.split(" ");
		if(spaceDelimitedTokens.length>= 2){
			dateTokens = spaceDelimitedTokens[0].split(dateDelimiter);
		}else{
			dateTokens = dateString.split(dateDelimiter);
		}
		
		// proccess date fields
		for(int i=0;i<dateFieldOrder.length;i++){
			DateField df = dateFieldOrder[i];
			switch(df){
			case YEAR:
				year = new Integer(dateTokens[i]);
				break;
			case MONTH:
				month = new Integer(dateTokens[i]);
				break;
			case DAY:
				day = new Integer(dateTokens[i]);
				break;
			}
		}
		if(year<=0){
			throw new IllegalStateException(Dates.class.getName()+" bad year in getYyyyMmDdFromStringMmDdYyyyDate");
		}
		if(month<=0){
			throw new IllegalStateException(Dates.class.getName()+" bad month in getYyyyMmDdFromStringMmDdYyyyDate");
		}
		if(day<=0){
			throw new IllegalStateException(Dates.class.getName()+" bad day in getYyyyMmDdFromStringMmDdYyyyDate");
		}
		
		// either process date/time or just date
		if(spaceDelimitedTokens.length>= 2){
			String[] timeTokens = spaceDelimitedTokens[1].split(timeDelimiter);
			int hour = new Integer(timeTokens[0]);
			int min = new Integer(timeTokens[1]);
			int sec = 1;
			return getYyyyMmDdHhMmSs(year, month, day, hour, min, sec);
		}else{
			return getYyyyMmDd(year, month, day);
		}
	}
	
	
	
	public static Long getYyyyMmDdFromStringMmDdYyyyDate(String dateString){
		String[] spaceDelimitedTokens = dateString.split(" ");
		if(spaceDelimitedTokens.length>= 2){
			String[] possibleDateDelimiters = {"/","-"};
			String[] dateTokens=null;;
			for(int i = 0;i<possibleDateDelimiters.length;i++){
				dateTokens = spaceDelimitedTokens[0].split(possibleDateDelimiters[i]);
				if(dateTokens.length==3){
					break;
				}
			}
//			String[] dateTokens = spaceDelimitedTokens[0].split("/");
			
//			String[] timeTokens = spaceDelimitedTokens[1].split(":");
			String[] possibleTimeDelimiters = {":"};
			String[] timeTokens=null;;
			for(int i = 0;i<possibleTimeDelimiters.length;i++){
				timeTokens = spaceDelimitedTokens[0].split(possibleTimeDelimiters[i]);
				if(timeTokens.length==3){
					break;
				}
			}
			
			int year  = new Integer(dateTokens[2]);
			int month = new Integer(dateTokens[0]);
			int day =  new Integer(dateTokens[1]);
			int hour = new Integer(timeTokens[0]);
			int min = new Integer(timeTokens[1]);
			int sec = 1;
			return getYyyyMmDdHhMmSs(year, month, day, hour, min, sec);
//			return new  Long(dateTokens[2]+dateTokens[0]+dateTokens[1]);
		}else{
			String[] dateTokens = dateString.split("/");
			int year  = new Integer(dateTokens[2]);
			int month = new Integer(dateTokens[0]);
			int day =  new Integer(dateTokens[1]);
			return getYyyyMmDd(year, month, day);
		}
//		String[] tokens = dateString.split("/");
//		return new  Long(tokens[2]+tokens[0]+tokens[1]);
	}
	
	public static String getMmDdYyFromYyyyMmDd_Form(Long dateInYyyyMmDd_Form){
		Calendar c = getCalendarFromYYYYMMDD(dateInYyyyMmDd_Form);
		String yy = new Integer(c.get(Calendar.YEAR)).toString().substring(2,4);
		String mm = dfMonth.format(c.get(Calendar.MONTH)+1);
		String dd = dfMonth.format(c.get(Calendar.DAY_OF_YEAR));
		return mm+dd+yy;
	}
	
	//******************** from TradingBase
	public static Calendar getCalendarFromYYYYMMDD(Long dateInYYYYMMDD_Form){
		Integer year = getYearFromYYYYMMDD(dateInYYYYMMDD_Form.toString());
		Integer month = getMonthFromYYYYMMDD(dateInYYYYMMDD_Form.toString());
		Integer day = getDayFromYYYYMMDD(dateInYYYYMMDD_Form.toString());
		Calendar c = Calendar.getInstance();
		c.set(year, month-1,day);
		return c;

	}

	public static Long getCalendarTimeInMillsFromYYYYMMDD(Long dateInYYYYMMDD_Form){
		return getCalendarFromYYYYMMDD(dateInYYYYMMDD_Form).getTimeInMillis();
	}

	
	public static Integer getYearFromYYYYMMDD(String s){
		if(s==null || s.length()<4)return null;
		return new Integer(s.substring(0, 4));
	}

	public static Integer getMonthFromYYYYMMDD(String s){
		if(s==null || s.length()<6)return null;
		return new Integer(s.substring(4, 6));
	}

	public static Integer getDayFromYYYYMMDD(String s){
		if(s==null || s.length()<8){
			return null;
		}
		return new Integer(s.substring(6, 8));
	}
	
	public static int getWeekOfColumbusDay(int yearOfColumbusDay){
		Calendar columDay = Calendar.getInstance();
		columDay.set(yearOfColumbusDay, 9,1);
		// find 2 Mon 
		for(int i = 0;i<2;i++){
			for(int j=0;j<7;j++){
				int day = columDay.get(Calendar.DAY_OF_WEEK);
				columDay = Dates.addToCalendar(columDay, 1, Calendar.DAY_OF_MONTH, true);
				if(Calendar.MONDAY==day){
					break;
				}
			}
		}
		int columWeekOfMonth  = columDay.get(Calendar.WEEK_OF_MONTH);
		return columWeekOfMonth;
	}

	public static int getWeekOfThanksgiving(int yearOfThanksgiving){
		Calendar thanksgiving = Calendar.getInstance();
		// set to nov 1st
		thanksgiving.set(yearOfThanksgiving, 10,1);
		// find 4 th thursday
		for(int i = 0;i<4;i++){
			for(int j=0;j<7;j++){
				int day = thanksgiving.get(Calendar.DAY_OF_WEEK);
				thanksgiving = Dates.addToCalendar(thanksgiving, 1, Calendar.DAY_OF_MONTH, true);
				if(Calendar.THURSDAY==day){
					break;
				}
			}
		}
		int thanksgivingWeekOfMonth  = thanksgiving.get(Calendar.WEEK_OF_MONTH);
		return thanksgivingWeekOfMonth;
	}
	
	public static boolean isLeapYear(int year){
	       boolean ret = (year % 4 == 0);

	        // divisible by 4 and not 100
	        ret = ret && (year % 100 != 0);

	        // divisible by 4 and not 100 unless divisible by 400
	        ret = ret || (year % 400 == 0);
	        return ret;

	}

	public static long getExcelDateNum(Calendar c){
		return new Double(HSSFDateUtil.getExcelDate(c.getTime())).longValue();
	}
	
	
	public static Calendar getSettlementDay(Calendar c,int hourOfNewDay,int minOfNewDay){
		int actualHour = c.get(Calendar.HOUR_OF_DAY);
		int actualMin = c.get(Calendar.MINUTE);
		int actMinInDay = actualHour*60+actualMin;
		int cutOffMinInDay = hourOfNewDay*60+minOfNewDay;
		// if the actual time is less than the time to start a new day, then use the
		//  previous day's files
		if(actMinInDay<cutOffMinInDay){
			return Dates.addBusinessDays("US", c, -1);
		}else{
			return Dates.addBusinessDays("US", c, 0);
		}
//		return c;
	}

}
