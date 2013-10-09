package com.scholarscraper.separators;

import java.text.SimpleDateFormat;
import com.scholarscraper.Task;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Map;

public class TodaySeparator
    extends DateSeparator
{
    private static final String DUE_TODAY = "Due Today";

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

    @Override
    protected String getSeparatorString()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(", MMMMM d");
        String date = formatter.format(calendar.getTime());
        return DUE_TODAY + date;
    }

}
