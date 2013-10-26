package com.scholarscraper.separators;

import com.scholarscraper.Listable;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class DateSeparator
    implements Listable
{
    protected static final String TIME_ZONE      = "America/New_York";
    protected static final String LONG_DASH      = " \u2014 ";

    protected static final int    SIX_DAYS_MILLI = 518400000;

    protected Calendar            calendar;


    public DateSeparator()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        this.calendar = calendar;
    }


    protected abstract String getSeparatorString();


    @Override
    public String toString()
    {
        return getSeparatorString();
    }


    public Calendar getDueDate()
    {
        return (Calendar)calendar.clone();
    }
}
