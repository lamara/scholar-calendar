package com.example.scholarscraper.receivers;

import com.example.scholarscraper.AlarmService;
import com.example.scholarscraper.UpdateService;
import com.example.scholarscraper.Course;
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.io.Serializable;
import java.util.Calendar;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.List;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
/**
 * // -------------------------------------------------------------------------
/**
 *  This class gets run every time the device launches. It is used to set a repeating
 *  update service that will launch a scholar update process every three hours.
 *
 *  @author Alex Lamar
 *  @author Paul Yea
 *  @author Brianna Bietzel
 *  @version May 5, 2013
 */
public class UpdateAtBootReceiver extends BroadcastReceiver {

    List<Course> courses;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("UpdateAtBootReciever started");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent service = new Intent(context, UpdateService.class);

            PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar cal = Calendar.getInstance();

            alarm.cancel(pendingIntent); //removes already existing alarms (if they
            //exist) to prevent duplicates from happening
            /* triggers update process every 3 hours */
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR * 3, pendingIntent);
        }

    }

    public boolean retrieveCourses(Context context) {
        File file = new File(context.getFilesDir(), "courses");
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInput input = new ObjectInputStream (buffer);
            try {
                List<Course> recoveredCourses = (List<Course>)input.readObject();
                System.out.println("courses retrieved");

                for(Course course : recoveredCourses) {
                    System.out.println(course);
                }

                this.courses = recoveredCourses;

                return true;

            }
            finally {
                input.close();
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
