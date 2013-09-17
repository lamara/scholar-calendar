package com.scholarscraper.separators;

import java.util.HashMap;
import java.util.TimeZone;
import java.util.Calendar;
import com.scholarscraper.Listable;
import java.util.Map;

public abstract class DateSeparator implements Listable
{
    protected static final String TIME_ZONE = "America/New_York";

    protected Calendar calendar;

    public DateSeparator() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        this.calendar = calendar;
    }

    public Map<String, String> getAttributes()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("separator", "yes");
        return map;
    }

    public Calendar getDueDate() {
        return (Calendar) calendar.clone();
    }
}
