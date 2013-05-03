package com.example.scholarscraper;

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

public class AlarmService
    extends IntentService
{
    public static final String CREATE   = "CREATE";
    public static final String POPULATE = "POPULATE";
    private List<Course>       courses;
    private IntentFilter       matcher;


    public AlarmService()
    {
        super("AlarmService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(POPULATE);
    }


    protected void onHandleIntent(Intent intent)
    {
        String str1 = intent.getAction();
        AlarmManager localAlarmManager =
            (AlarmManager)getSystemService("alarm");
        Calendar c = Calendar.getInstance();
        if (this.matcher.matchAction(str1))
        {
            //Two possible actions, the "CREATE" action will add a single alarm
            //based on the passed in intent data. The "POPULATE" alarm will
            //create an alarm for every task in the course list. "POPULATE"
            //is intended to be called on system reboot, as a system reboot
            //deletes all previously queued alarms.
            if ("CREATE".equals(str1))
            {
                int id = (int)intent.getLongExtra("alarm_id", -1L);
                long dueDate = intent.getLongExtra("dueDate", -1L);
                localAlarmManager =
                    (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent localIntent = new Intent(this, AlarmReceiver.class);
                PendingIntent localPendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        id,
                        localIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                localAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    dueDate,
                    localPendingIntent);
                System.out.println("Alarm set");
            }
            if ("POPULATE".equals(str1))
            {
                for (int x = 0; x < this.courses.size(); x++)
                {
                    if (!retrieveCourses(this)) {
                        System.out.println("Courses could not be retrieved while populating " +
                        		          "alarm list");
                        return;
                    }
                    List<Task> temp = courses.get(x).getAssignments();
                    for (int i = 0; i < temp.size(); i++)
                    {
                        Intent localIntent =
                            new Intent(this, AlarmReceiver.class);
                        PendingIntent localPendingIntent =
                            PendingIntent.getBroadcast(
                                this,
                                0,
                                localIntent,
                                134217728);
                        c = temp.get(i).getDueDate();
                        localAlarmManager.set(
                            0,
                            c.getTimeInMillis(),
                            localPendingIntent);
                    }
                }
            }
        }
    }


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
