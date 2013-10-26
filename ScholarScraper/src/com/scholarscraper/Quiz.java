package com.scholarscraper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * // -------------------------------------------------------------------------
 * /** Implementation of a Task, used when retrieving tasks from a scholar quiz
 * page.
 * 
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version May 5, 2013
 */
public class Quiz
    extends Task
{
    private final static String NULL_CONSTANT = "n/a";


    public Quiz(String name, String courseName, String dueDate)
        throws ParseException
    {
        super(name, courseName, dueDate);
    }


    /**
     * Parses a string of the format "2013-Jan-27 04:48 PM" into a calendar
     * object
     */
    @Override
    public Calendar parseDate(String date)
        throws ParseException
    {
        if (date.equals(NULL_CONSTANT))
        {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.clear(); // clearing is important because any difference in seconds or
// milliseconds
        // will mess up comparing two calendar's equality even if they are set
        // to the same date/hour/minute

        String[] data = date.split("\\s+");
        String[] yearMonthDay = data[0].split("-");
        String[] hourMinute = data[1].split(":");
        String amPm = data[2];

        int year = Integer.parseInt(yearMonthDay[0]);
        int month = getCalendarMonth(yearMonthDay[1]);
        int day = Integer.parseInt(yearMonthDay[2]);
        int hour = Integer.parseInt(hourMinute[0]);
        int minute = Integer.parseInt(hourMinute[1]);
        /* Calendar hours go from 0-11, but scholar can display hours up to 12 */
        if (hour == 12)
        {
            hour = 0;
        }
        if (amPm.equals("AM"))
        {
            c.set(Calendar.AM_PM, Calendar.AM);
        }
        else if (amPm.equals("PM"))
        {
            c.set(Calendar.AM_PM, Calendar.PM);
        }
        else
        {
            throw new ParseException("Could not parse AM/PM", 0);
        }
        c.setTimeZone(TimeZone.getTimeZone(TIME_ZONE)); // TIME_ZONE should be
// set to "America/New_York"
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        return c;
    }
}
