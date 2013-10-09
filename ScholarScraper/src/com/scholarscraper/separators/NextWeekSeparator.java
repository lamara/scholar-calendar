package com.scholarscraper.separators;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class NextWeekSeparator extends DateSeparator
{
    private static final String DUE_NEXT_WEEK = "Due next week";

    public NextWeekSeparator() {
        super();
        //set time to the beginning of the next week (if the day is saturday then
        //the WeekSeparator and NextWeekSeparator will end up on the same day of
        //the month, to keep things sequential this calendar has +1 second
        int weekOffset = 8 - calendar.get(Calendar.DAY_OF_WEEK);
        this.calendar.set(calendar.get(Calendar.YEAR),
                          calendar.get(Calendar.MONTH),
                          calendar.get(Calendar.DAY_OF_MONTH) + weekOffset,
                          0,
                          0,
                          1);
    }

    @Override
    protected String getSeparatorString()
    {
        Calendar current = Calendar.getInstance();
        int beginningOffset = current.get(Calendar.DATE) - current.get(Calendar.DAY_OF_WEEK + 2);
        Calendar beginOfWeek = current;
        beginOfWeek.set(Calendar.DATE, beginningOffset + 7);
        SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d");
        String beginWeekString = formatter.format(beginOfWeek.getTime());
        //the date class is awful god help our souls
        String endWeekString = formatter.format(new Date(beginOfWeek.getTime().getTime() + SIX_DAYS_MILLI));
        return DUE_NEXT_WEEK + ", " + beginWeekString + LONG_DASH + endWeekString;
    }
}
