package com.scholarscraper.separators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeekSeparator
    extends DateSeparator
{

    private static final String DUE_THIS_WEEK = "Due this week";


    public WeekSeparator()
    {
        super();
        // set time to the beginning of the next day.
        this.calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH) + 1,
            0,
            0,
            0);
    }


    @Override
    protected String getSeparatorString()
    {
        Calendar current = Calendar.getInstance();
        int beginningOffset =
            current.get(Calendar.DATE) - current.get(Calendar.DAY_OF_WEEK) + 2;
        System.out.println("==============================================");
        System.out.println("current date: " + current.get(Calendar.DATE));
        System.out.println("day of week: " + current.get(Calendar.DAY_OF_WEEK));
        Calendar beginOfWeek = current;
        beginOfWeek.set(Calendar.DATE, beginningOffset);
        SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d");
        String beginWeekString = formatter.format(beginOfWeek.getTime());
        // the date class is awful god help our souls
        String endWeekString =
            formatter.format(new Date(beginOfWeek.getTime().getTime()
                + SIX_DAYS_MILLI));
        return DUE_THIS_WEEK + ", " + beginWeekString + LONG_DASH
            + endWeekString;
    }
}
