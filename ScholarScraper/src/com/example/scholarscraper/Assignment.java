package com.example.scholarscraper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
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
     * @param name
     * @param description
     * @param dueDate
     */
    public Assignment(String name, String description, Calendar dueDate)
    {
        super(name, description, dueDate);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Assignment object.
     * @param name
     * @param description
     * @param dueDate
     * @throws ParseException
     */
    public Assignment(String name, String description, String dueDate)
        throws ParseException
    {
        super(name, description, dueDate);
    }


    /* Apr 17, 2013 12:30 am */

    @Override
    public Calendar parseDate(String date)
        throws ParseException
    {
        Calendar c = Calendar.getInstance();

        String[] data = date.split("\\s+"); // splits around whitespace
        String[] hourMinute = data[3].split(":");

        int month =  getCalendarMonth(data[0]);
        int day =    Integer.parseInt(data[1].replace(",", "")); //
        int year =   Integer.parseInt(data[2]);
        int hour =   Integer.parseInt(hourMinute[0]);
        int minute = Integer.parseInt(hourMinute[1]);
        /* Calendar hours go from 0-11, but scholar can display hours up to 12 */
        if (hour == 12) {
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
        c.setTimeZone(TimeZone.getTimeZone(TIME_ZONE)); // TIME_ZONE should be set to "America/New_York"
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        System.out.println(c.getTime());
        return c;
    }
}
