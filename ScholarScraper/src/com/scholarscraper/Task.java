package com.scholarscraper;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;

// -------------------------------------------------------------------------
/**
 * Defines how scholar assignments/quizes should behave. Tasks
 * should have a way to parse a string date into a java calendar date, and fields
 * for due date, name, course name and description. Tasks are also meant to be
 * immutable.
 *
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version Apr 20, 2013
 */

public abstract class Task implements Serializable, Comparable<Task>, Listable
{
    //The name of the Task
    private String                name;
    //A description of the Task
    private String                description;
    //The name of the course associated with the task
    private String                courseName;
    //Time the Task is due
    private Calendar              dueDate;

    protected static final String TIME_ZONE = "America/New_York";


    // ----------------------------------------------------------
    /**
     * Creates a new calendar object, given a name, description, and a string
     * representation of a date
     * @param name The name of the task
     * @param description A description of the class (should be left short, usually just it's coursename
     * @param dueDate A string representation of the due date of a task
     * @throws ParseException
     */
    public Task(String name, String courseName, String dueDate)
        throws ParseException
    {
        this.name = name;
        this.courseName = courseName;
        this.dueDate = parseDate(dueDate);
    }

    // ----------------------------------------------------------
    /**
     * Create a new Task object, given a name, description, and a calendar object
     * @param name The name of the task
     * @param description A description of the class (should be left short, usually just it's coursename
     * @param courseName the name of the course associated with this task
     * @param dueDate The date the task is due.
     * @throws ParseException
     */
    public Task(String name, String description, String courseName, String dueDate) throws ParseException
    {
        this.name = name;
        this.description = description;
        this.courseName = courseName;
        this.dueDate = parseDate(dueDate);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Task object, given a name, description, and a calendar object
     * @param name The name of the task
     * @param description A description of the class (should be left short, usually just it's coursename
     * @param dueDate The date the task is due.
     */
    public Task(String name, String description, Calendar dueDate)
    {
        this.name = name;
        this.description = description;
        Calendar c = null;
        if (dueDate != null) {
            c = (Calendar) dueDate.clone(); //copy to keep class immutable
        }
        this.dueDate = c;
    }



    /**
     * Returns the name of the Task
     *
     * @return name the name of the task
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the date and time of when task is due
     *
     * @return dateDue the date that the task is due, as a calendar object
     */
    public Calendar getDueDate()
    {
        /* calendar objects are mutable so we have to be careful returning them */
        if (dueDate == null) {
            return null;
        }
        return (Calendar) dueDate.clone();
    }


    // ----------------------------------------------------------
    /**
     * Returns the description (class name) of the task
     *
     * @return description the description of the task
     */
    public String getDescription()
    {
        return description;
    }

    public String getCourseName() {
        return courseName;
    }
/*
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    */


    /**
     * Compares two task objects for equality
     * @return true if the given tasks are equal
     */
    @Override
    public boolean equals(Object task)
    {
        if (task instanceof Task)
        {
            Task cmpr = (Task) task;
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
     * Compares two task objects based on the point in time that they are due
     */
    public int compareTo(Task cmprTask) {
        return dueDate.compareTo(cmprTask.getDueDate());
    }


    /**
     * Returns a Calendar.MONTH constant from a given 3 letter month
     * abbreviation (i.e. "Jan", or "Aug").
     * @param month The month to be parsed
     * @return the Calendar.MONTH representation of the month passed
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
            throw new ParseException("Month was not parsed", 0);
        }
    }

    public String toString() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<String, String>();
    }


    /**
     * Parses a given string representation of a date (which vary based on the
     * scholar page being accessed) into a Calendar object.
     * @param date
     * @return A calendar object based on the string representation of the object
     *
     * @throws ParseException
     */
    protected abstract Calendar parseDate(String date)
        throws ParseException;
}
