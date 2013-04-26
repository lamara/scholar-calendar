package com.example.scholarscraper;

import java.text.ParseException;
import java.util.Calendar;

// -------------------------------------------------------------------------
/**
 * Defines how scholar assignments/quizes should behave. Every assignment or
 * quiz needs a way to parse a string date into a java calendar date, and fields
 * for due date, name, and description.
 *
 * @author Alex Lamar
 * @version Apr 20, 2013
 */

public abstract class Task
{
    private String                name;
    private String                description;
    private Calendar              dueDate;

    protected static final String TIME_ZONE = "America/New_York";


    public Task(String name, String description, String dueDate)
        throws ParseException
    {
        this.name = name;
        this.description = description;
        this.dueDate = parseDate(dueDate);
    }


    public Task(String name, String description, Calendar dueDate)
    {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
    }


    /**
     * Returns the name of the assignment
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }


    /**
     * returns the date and time of when assignment is due
     *
     * @return dateDue
     */
    public Calendar getDueDate()
    {
        return dueDate;
    }


    // ----------------------------------------------------------
    /**
     * Returns the description (class name) of the assignment
     *
     * @return description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Compares two task objects for equality
     */
    @Override
    public boolean equals(Object task)
    {
        if (task instanceof Task)
        {
            Task cmpr = (Task)task;
            if (this.getDueDate().equals(cmpr.getDueDate())
                && this.getName().equals(cmpr.getName())
                && this.getDescription().equals(cmpr.getDescription()))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns a Calendar.MONTH constant from a given 3 letter month
     * abbreviation (i.e. "Jan", or "Aug").
     *
     * @throws ParseException
     */
    protected int getCalendarMonth(String month)
        throws ParseException
    {
        if (month.equals("Jan"))
        {
            return Calendar.JANUARY;
        }
        else if (month.equals("Feb"))
        {
            return Calendar.FEBRUARY;
        }
        else if (month.equals("Mar"))
        {
            return Calendar.MARCH;
        }
        else if (month.equals("Apr"))
        {
            return Calendar.APRIL;
        }
        else if (month.equals("May"))
        {
            return Calendar.MAY;
        }
        else if (month.equals("Jun"))
        {
            return Calendar.JUNE;
        }
        else if (month.equals("Jul"))
        {
            return Calendar.JULY;
        }
        else if (month.equals("Aug"))
        {
            return Calendar.AUGUST;
        }
        else if (month.equals("Sep"))
        {
            return Calendar.SEPTEMBER;
        }
        else if (month.equals("Oct"))
        {
            return Calendar.OCTOBER;
        }
        else if (month.equals("Nov"))
        {
            return Calendar.NOVEMBER;
        }
        else if (month.equals("Dec"))
        {
            return Calendar.DECEMBER;
        }
        else
        {
            throw new ParseException("Month was not be parsed", 0);
        }
    }


    /**
     * Parses a given string representation of a date (which vary based on the
     * scholar page being accessed) into a Calendar object.
     *
     * @throws ParseException
     */
    protected abstract Calendar parseDate(String date)
        throws ParseException;
}
