package com.scholarscraper.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

// -------------------------------------------------------------------------
/**
 * An implementation of a Task, used when retrieving tasks from an assignment
 * page on Scholar.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 6, 2013
 */
public class Assignment
    extends Task
{
    // ----------------------------------------------------------
    /**
     * Create a new Assignment object.
     *
     * @param name
     * @param courseName
     * @param dueDate
     * @throws ParseException
     */
    public Assignment(String name, String courseName, String dueDate)
        throws ParseException
    {
        super(name, courseName, dueDate);
    }


    /**
     * Parses date of format "Apr 17, 2013 12:30 am"
     */
    @Override
    public Calendar parseDate(String date)
        throws ParseException
    {
        Calendar c = Calendar.getInstance();
        c.clear(); // clearing is important because any difference in seconds or
// milliseconds
                   // will mess up comparing two calendar's equality even if
// they are set
                   // to the same date/hour/minute

        String[] data = date.split("\\s+"); // splits around whitespace
        String[] hourMinute = data[3].split(":");

        int month = getCalendarMonth(data[0]);
        int day = Integer.parseInt(data[1].replace(",", ""));
        int year = Integer.parseInt(data[2]);
        int hour = Integer.parseInt(hourMinute[0]);
        int minute = Integer.parseInt(hourMinute[1]);
        /* Calendar hours go from 0-11, but scholar can display hours up to 12 */
        if (hour == 12)
        {
            hour = 0;
        }

        String amPm = data[4];
        if (amPm.equals("am"))
        {
            c.set(Calendar.AM_PM, Calendar.AM);
        }
        else if (amPm.equals("pm"))
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
