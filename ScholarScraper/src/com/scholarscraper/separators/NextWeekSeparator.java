package com.scholarscraper.separators;

import java.util.Calendar;
import java.util.Map;

public class NextWeekSeparator extends DateSeparator
{
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
}
