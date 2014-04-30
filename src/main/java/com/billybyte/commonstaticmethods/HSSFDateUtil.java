package com.billybyte.commonstaticmethods;

/**
 * 
 * LibFormula : a free Java formula library
 * 
 *
 * Project Info:  http://reporting.pentaho.org/libformula/
 *
 * (C) Copyright 2006-2007, by Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 *
 * ------------
 * $Id: HSSFDateUtil.java 3522 2007-10-16 10:56:57Z tmorgner $
 * ------------
 * (C) Copyright 2006-2007, by Pentaho Corporation.
 */

/*
 * DateUtil.java
 *
 * Created on January 19, 2002, 9:30 AM
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Contains methods for dealing with Excel dates. <br/> Modified by Cedric
 * Pronzato
 * 
 * @author Michael Harhen
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Dan Sherman (dsherman at isisph.com)
 * @author Hack Kampbjorn (hak at 2mba.dk)
 */

public class HSSFDateUtil {
  private HSSFDateUtil() {
  }

  private static final int BAD_DATE = -1; // used to specify that date is
                                          // invalid

  private static final long DAY_MILLISECONDS = 24 * 60 * 60 * 1000;

  private static final double CAL_1900_ABSOLUTE = (double) absoluteDay(new GregorianCalendar(1900,
      Calendar.JANUARY, 1)) - 2.0;

  /**
   * Given a Date, converts it into a double representing its internal Excel
   * representation, which is the number of days since 1/1/1900. Fractional days
   * represent hours, minutes, and seconds.
   * 
   * @return Excel representation of Date (-1 if error - test for error by
   *         checking for less than 0.1)
   * @param date
   *          the Date
   */

  public static double getExcelDate(final Date date) {
    Calendar calStart = new GregorianCalendar();

    calStart.setTime(date); // If date includes hours, minutes, and seconds, set
                            // them to 0
    // if (calStart.get(Calendar.YEAR) < 1900)
    // {
    // return BAD_DATE;
    // }
    // else
    // {
    // Because of daylight time saving we cannot use
    // date.getTime() - calStart.getTimeInMillis()
    // as the difference in milliseconds between 00:00 and 04:00
    // can be 3, 4 or 5 hours but Excel expects it to always
    // be 4 hours.
    // E.g. 2004-03-28 04:00 CEST - 2004-03-28 00:00 CET is 3 hours
    // and 2004-10-31 04:00 CET - 2004-10-31 00:00 CEST is 5 hours
    final double fraction = (((calStart.get(Calendar.HOUR_OF_DAY) * 60 + calStart
        .get(Calendar.MINUTE)) * 60 + calStart.get(Calendar.SECOND)) * 1000 + calStart
        .get(Calendar.MILLISECOND))
        / (double) DAY_MILLISECONDS;
    calStart = dayStart(calStart);

    return fraction + (double) absoluteDay(calStart) - CAL_1900_ABSOLUTE;
  }

  // }

  /**
   * Given a excel date, converts it into a Date. Assumes 1900 date windowing.
   * 
   * @param date
   *          the Excel Date
   * 
   * @return Java representation of a date (null if error)
   * @see #getJavaDate(double,boolean)
   */

  public static Date getJavaDate(final double date) {

    return getJavaDate(date, true);
  }

  /**
   * Given an Excel date with either 1900 or 1904 date windowing, converts it to
   * a java.util.Date.
   * 
   * NOTE: If the default <code>TimeZone</code> in Java uses Daylight Saving
   * Time then the conversion back to an Excel date may not give the same value,
   * that is the comparison <CODE>excelDate ==
   * getExcelDate(getJavaDate(excelDate,false))</CODE> is not always true. For
   * example if default timezone is <code>Europe/Copenhagen</code>, on
   * 2004-03-28 the minute after 01:59 CET is 03:00 CEST, if the excel date
   * represents a time between 02:00 and 03:00 then it is converted to past
   * 03:00 summer time
   * 
   * @param date
   *          The Excel date.
   * @param use1904windowing
   *          true if date uses 1904 windowing, or false if using 1900 date
   *          windowing.
   * @return Java representation of the date, or null if date is not a valid
   *         Excel date
   * @see java.util.TimeZone
   */
  public static Date getJavaDate(final double date, final boolean use1904windowing) {
    if (isValidExcelDate(date)) {
      int startYear = 1900;
      int dayAdjust = -1; // Excel thinks 2/29/1900 is a valid date, which it
                          // isn't
      final int wholeDays = (int) Math.floor(date);
      if (use1904windowing) {
        startYear = 1904;
        dayAdjust = 1; // 1904 date windowing uses 1/2/1904 as the first day
      } else if (wholeDays < 61) {
        // Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900
        // exists
        // If Excel date == 2/29/1900, will become 3/1/1900 in Java
        // representation
        dayAdjust = 0;
      }
      final GregorianCalendar calendar = new GregorianCalendar(startYear, 0, wholeDays + dayAdjust);
      final int millisecondsInDay = (int) ((date - Math.floor(date)) * (double) DAY_MILLISECONDS + 0.5);
      calendar.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
      return calendar.getTime();
    } else {
      return null;
    }
  }

  /**
   * given a format ID this will check whether the format represents an internal
   * date format or not.
   */
  public static boolean isInternalDateFormat(final int format) {
    boolean retval;

    switch (format) {
    // Internal Date Formats as described on page 427 in
    // Microsoft Excel Dev's Kit...
    case 0x0e:
    case 0x0f:
    case 0x10:
    case 0x11:
    case 0x12:
    case 0x13:
    case 0x14:
    case 0x15:
    case 0x16:
    case 0x2d:
    case 0x2e:
    case 0x2f:
      retval = true;
      break;

    default:
      retval = false;
      break;
    }
    return retval;
  }

  /**
   * Given a double, checks if it is a valid Excel date.
   * 
   * @return true if valid
   * @param value
   *          the double value
   */

  public static boolean isValidExcelDate(final double value) {
    return (value > -Double.MIN_VALUE);
  }

  /**
   * Given a Calendar, return the number of days since 1600/12/31.
   * 
   * @return days number of days since 1600/12/31
   * @param cal
   *          the Calendar
   * @exception IllegalArgumentException
   *              if date is invalid
   */

  private static int absoluteDay(final Calendar cal) {
    return cal.get(Calendar.DAY_OF_YEAR) + daysInPriorYears(cal.get(Calendar.YEAR));
  }

  /**
   * Return the number of days in prior years since 1601
   * 
   * @return days number of days in years prior to yr.
   * @param yr
   *          a year (1600 < yr < 4000)
   * @exception IllegalArgumentException
   *              if year is outside of range.
   */

  private static int daysInPriorYears(final int yr) {
    if (yr < 1601) {
      throw new IllegalArgumentException("'year' must be 1601 or greater");
    }
    final int y = yr - 1601;

    return 365 * y // days in prior years
        + y / 4 // plus julian leap days in prior years
        - y / 100 // minus prior century years
        + y / 400;
  }

  // set HH:MM:SS fields of cal to 00:00:00:000
  private static Calendar dayStart(final Calendar cal) {
    cal.get(Calendar.HOUR_OF_DAY); // force recalculation of internal fields
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.get(Calendar.HOUR_OF_DAY); // force recalculation of internal fields
    return cal;
  }

  // ---------------------------------------------------------------------------------------------------------
}

   
    
    