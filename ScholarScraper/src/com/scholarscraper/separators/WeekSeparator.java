package com.scholarscraper.separators;

import java.util.Calendar;
import java.util.Map;

public class WeekSeparator
    extends DateSeparator
{
    public WeekSeparator() {
        super();
        //set time to the beginning of the next day.
        this.calendar.set(calendar.get(Calendar.YEAR),
                          calendar.get(Calendar.MONTH),
                          calendar.get(Calendar.DAY_OF_MONTH) + 1,
                          0,
                          0,
                          0);
    }
}
