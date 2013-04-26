package com.example.scholarscraper;

import android.provider.CalendarContract.Reminders;
import java.util.HashMap;
import java.util.Map;
import android.provider.CalendarContract.Events;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Calendar;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

public class CalendarSetter
{
    private Context context;
    private Map<String, Long> eventIdMap;
    private final String INSTANCE_ACCOUNT_NAME;
    private final long CALENDAR_ID;



    private static final String ACCOUNT_TYPE = CalendarContract.ACCOUNT_TYPE_LOCAL;
    private static final String TIME_ZONE = "America/New_York";

    private CalendarSetter(Context context, String accountName, long calenderId) {
        this.context= context;
        INSTANCE_ACCOUNT_NAME = accountName;
        CALENDAR_ID = calenderId;
        eventIdMap = new HashMap<String, Long>();
    }

    public void addEvent(Task task) {
        Calendar cal = task.getDueDate();
        String name = task.getName();
        String description = task.getDescription();

        long start = cal.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, start);
        values.put(Events.DTEND, start);
        values.put(Events.TITLE, name);
        values.put(Events.EVENT_LOCATION, description);
        values.put(Events.CALENDAR_ID, CALENDAR_ID);
        values.put(Events.EVENT_TIMEZONE, TIME_ZONE);
        values.put(Events.DESCRIPTION,
              name + " is due soon!");
        // reasonable defaults exist:
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        values.put(Events.SELF_ATTENDEE_STATUS,
              Events.STATUS_CONFIRMED);
        values.put(Events.ALL_DAY, 0);
        values.put(Events.ORGANIZER, INSTANCE_ACCOUNT_NAME + "@vt.edu");
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 0);
        values.put(Events.GUESTS_CAN_MODIFY, 0);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_FREE);
        Uri uri =
              context.getContentResolver().
                    insert(Events.CONTENT_URI, values);
        long eventId = new Long(uri.getLastPathSegment());
        eventIdMap.put(name, eventId);
        setAlarm(eventId);

    }

    public void addEvent() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        cal.set(Calendar.DATE, 21);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.HOUR_OF_DAY, 19);
        cal.set(Calendar.MINUTE, 12);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, start);
        values.put(Events.DTEND, start);
        values.put(Events.TITLE, "Assignment");
        values.put(Events.EVENT_LOCATION, "Blacksburg, VA");
        values.put(Events.CALENDAR_ID, CALENDAR_ID);
        values.put(Events.EVENT_TIMEZONE, TIME_ZONE);
        values.put(Events.DESCRIPTION,
              "An assignment is due soon!");
        // reasonable defaults exist:
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        values.put(Events.SELF_ATTENDEE_STATUS,
              Events.STATUS_CONFIRMED);
        values.put(Events.ALL_DAY, 0);
        values.put(Events.ORGANIZER, INSTANCE_ACCOUNT_NAME + "@vt.edu");
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 0);
        values.put(Events.GUESTS_CAN_MODIFY, 0);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_FREE);
        Uri uri =
              context.getContentResolver().
                    insert(Events.CONTENT_URI, values);
        long eventId = new Long(uri.getLastPathSegment());
        eventIdMap.put("Assignment", eventId);
        setAlarm(eventId);

    }

    public void setAlarm(long eventId) {
        ContentValues values = new ContentValues();
        values.put(Reminders.EVENT_ID, eventId);
        values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        values.put(Reminders.MINUTES, 1);
        context.getContentResolver().insert(Reminders.CONTENT_URI, values);
    }


    //////////////////////////////////////////////////////////////////////////
    //    Builder Methods
    /////////////////////////////////////////////////////////////////////////
    public static CalendarSetter getInstance(Context context, String accountName) {
        if (getCalendarId(context, accountName) == -1) {
            newCalendar(context, accountName);
        }
        long calendarId = getCalendarId(context, accountName);
        return new CalendarSetter(context, accountName, calendarId);
    }

    /**
     * Creates a new, local calendar named after the given account name
     */
    private static void newCalendar(Context context, final String INSTANCE_ACCOUNT_NAME) {
        ContentValues values = new ContentValues();
        values.put(
              Calendars.ACCOUNT_NAME,
              INSTANCE_ACCOUNT_NAME);
        values.put(
              Calendars.ACCOUNT_TYPE,
              ACCOUNT_TYPE);
        values.put(
              Calendars.NAME,
              INSTANCE_ACCOUNT_NAME + " Scholar Calendar");
        values.put(
              Calendars.CALENDAR_DISPLAY_NAME,
              INSTANCE_ACCOUNT_NAME + " Scholar Calendar");
        values.put(
              Calendars.CALENDAR_COLOR,
              0xffff0000);
        values.put(
              Calendars.CALENDAR_ACCESS_LEVEL,
              Calendars.CAL_ACCESS_OWNER);
        values.put(
              Calendars.OWNER_ACCOUNT,
              INSTANCE_ACCOUNT_NAME + "@vt.edu");
        values.put(
              Calendars.CALENDAR_TIME_ZONE,
              "Europe/Berlin");
        Uri.Builder builder =
              CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
              Calendars.ACCOUNT_NAME,
              "com.scholarcalendar");
        builder.appendQueryParameter(
              Calendars.ACCOUNT_TYPE,
              CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
              CalendarContract.CALLER_IS_SYNCADAPTER,
              "true");
        Uri uri =
              context.getContentResolver().insert(builder.build(), values);
    }

    /**
     * Returns the calendar ID of the Scholar Scraper calendar
     * @return the calendar ID, or -1 if the ID does not exist.
     */
    private static long getCalendarId(Context context, final String INSTANCE_ACCOUNT_NAME) {
        String[] projection =
            new String[]{
                  Calendars._ID,
                  Calendars.NAME,
                  Calendars.ACCOUNT_NAME,
                  Calendars.ACCOUNT_TYPE};
      Cursor calendars =
            context.getContentResolver().
                  query(Calendars.CONTENT_URI,
                        projection,
                        Calendars.VISIBLE + " = 1",
                        null,
                        Calendars._ID + " ASC");
      if (calendars.moveToFirst()) {
         do {
            long id = calendars.getLong(0);
            String displayName = calendars.getString(1);
            if (displayName.equals(INSTANCE_ACCOUNT_NAME + " Scholar Calendar")) {
                return id;
            }
         } while (calendars.moveToNext());
      }
      return -1;
    }
}
