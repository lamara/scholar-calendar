package com.example.scholarscraper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Calendar;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectInput;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import android.content.Context;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.IntentService;

// -------------------------------------------------------------------------
/**
 * creates a alarm that will go at a speicified time using pending intent so
 * that a notification will be created at the specific time
 *
 * @author Paul Yea
 * @version May 5, 2013
 */
public class AlarmService
    extends IntentService
{
    public static final String CREATE   = "CREATE";
    public static final String POPULATE = "POPULATE";
    public static final String CANCEL   = "CANCEL";
    private List<Course>       courses;
    private IntentFilter       matcher;


    // ----------------------------------------------------------
    /**
     * Create a new AlarmService object.
     */
    public AlarmService()
    {
        super("AlarmService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(POPULATE);
        matcher.addAction(CANCEL);
    }


    protected void onHandleIntent(Intent intent)
    {
        DbHelper db = new DbHelper(this);
        String str1 = intent.getAction();
        AlarmManager localAlarmManager =
            (AlarmManager)getSystemService("alarm");
        Calendar c = Calendar.getInstance();
        if (this.matcher.matchAction(str1))
        {
            // Three possible actions, the "CREATE" action will add a single
// alarm
            // based on the passed in intent data. The "POPULATE" alarm will
            // create an alarm for every task in the course list. "POPULATE"
            // is intended to be called on system reboot, as a system reboot
            // deletes all previously queued alarms.
            // "CANCEL" will cancel all alarm in the system.

            if ("CREATE".equals(str1))
            {
                long id = intent.getLongExtra("alarm_id", -1L);
                db.addAlarmId(new AlarmId((int)id));
                long dueDate = intent.getLongExtra("dueDate", -1L);
                String name = intent.getStringExtra("name");
                localAlarmManager =
                    (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent localIntent = new Intent(this, AlarmReceiver.class);
                localIntent.putExtra("id", id);
                localIntent.putExtra("name", name);
                PendingIntent localPendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        (int)id,
                        localIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                localAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    dueDate,
                    localPendingIntent);
                System.out.println("Alarm set");
            }
            if ("CANCEL".equals(str1))
            {
                List<AlarmId> ids = db.getAllIds();
                for (int x = 0; x < ids.size(); x++)
                {
                    Intent localIntent = new Intent(this, AlarmReceiver.class);
                    PendingIntent localPendingIntent =
                        PendingIntent.getBroadcast(
                            this,
                            ids.get(x).getAlarmId(),
                            localIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    localAlarmManager.cancel(localPendingIntent);
                    db.deleteAlarmId(ids.get(x));
                }
            }

            if ("POPULATE".equals(str1))
            {
                SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(this);
                for (int x = 0; x < this.courses.size(); x++)
                {
                    if (!retrieveCourses(this))
                    {
                        System.out
                            .println("Courses could not be retrieved while populating "
                                + "alarm list");
                        return;
                    }
                    List<Task> temp = courses.get(x).getAssignments();
                    for (int i = 0; i < temp.size(); i++)
                    {
                        Intent localIntent =
                            new Intent(this, AlarmReceiver.class);
                        long id = System.currentTimeMillis();
                        String name = temp.get(i).getName();
                        localIntent.putExtra("id", id);
                        localIntent.putExtra("name", name);
                        PendingIntent localPendingIntent =
                            PendingIntent.getBroadcast(
                                this,
                                (int)id,
                                localIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        c = temp.get(i).getDueDate();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        String delayPreference =
                            sharedPref.getString("pref_notificationDelay", "0");
                        int delay = Integer.parseInt(delayPreference);
                        c.set(Calendar.HOUR_OF_DAY, hour - delay);
                        localAlarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            c.getTimeInMillis(),
                            localPendingIntent);
                    }
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * retrieve list of courses
     *
     * @param context
     * @return all courses in the app
     */
    public boolean retrieveCourses(Context context)
    {
        File file = new File(context.getFilesDir(), "courses");
        try
        {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInput input = new ObjectInputStream(buffer);
            try
            {
                List<Course> recoveredCourses =
                    (List<Course>)input.readObject();
                System.out.println("courses retrieved");

                for (Course course : recoveredCourses)
                {
                    System.out.println(course);
                }

                this.courses = recoveredCourses;

                return true;

            }
            finally
            {
                input.close();
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
