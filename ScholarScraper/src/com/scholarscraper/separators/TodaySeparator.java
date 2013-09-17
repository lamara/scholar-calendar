package com.scholarscraper.separators;

import com.scholarscraper.Task;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Map;

public class TodaySeparator
    extends DateSeparator
{
    public TodaySeparator() {
        super();
        //set time to the beginning of the current day.
        this.calendar.set(calendar.get(Calendar.YEAR),
                          calendar.get(Calendar.MONTH),
                          calendar.get(Calendar.DAY_OF_MONTH),
                          0,
                          0,
                          0);
    }

}
