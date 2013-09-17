package com.scholarscraper.separators;

import java.util.Calendar;
import java.util.Map;

public class DistantSeparator
    extends DateSeparator
{
    public DistantSeparator() {
        super();
        //set time to the beginning of two weeks from now
        int twoWeekOffset = 15 - calendar.get(Calendar.DAY_OF_WEEK);
        this.calendar.set(calendar.get(Calendar.YEAR),
                          calendar.get(Calendar.MONTH),
                          calendar.get(Calendar.DAY_OF_MONTH) + twoWeekOffset,
                          0,
                          0,
                          0);
    }

}