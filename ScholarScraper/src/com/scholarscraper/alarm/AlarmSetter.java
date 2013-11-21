package com.scholarscraper.alarm;

import com.scholarscraper.update.DataManager;
import java.util.Date;
import com.scholarscraper.model.Assignment;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import java.util.Calendar;
import com.scholarscraper.listview.Listable;
import com.scholarscraper.listview.TaskListComparator;
import com.scholarscraper.model.Task;
import com.scholarscraper.separators.DistantSeparator;
import com.scholarscraper.separators.NextWeekSeparator;
import com.scholarscraper.separators.TodaySeparator;
import com.scholarscraper.separators.WeekSeparator;
import java.util.ArrayList;
import java.util.Collections;
import com.scholarscraper.MainActivity;
import android.content.Context;
import com.scholarscraper.model.Course;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;

/**
// -------------------------------------------------------------------------
/**
 *  This class handles alarm management for the app. It operates under a
 *  one-alarm-at-a-time principle, so it will only set alarms for a user's
 *  nearest upcoming assignment (however, if multiple assignments share the
 *  same due date then it will still set alarms for all of them).
 *
 *
 *  @author Alex
 *  @version Nov 19, 2013
 */
public class AlarmSetter
{
    private static int ALARM_OFFSET = 12 * 3600 * 1000; //12 hours in milliseconds
                                                        //TODO make this a user setting

    //identifier strings for passing extra intent data
    public static final String DUE_DATE_EXTRA = "dueDate";
    public static final String ASSIGNMENT_NAME_EXTRA = "assignmentName";
    public static final String COURSE_NAME_EXTRA = "courseName";

    //TODO add an overloaded method that takes a courselist so we don't have to
    //do IO.
    public static void setNextAlarm(Context context) {
        List<Course> courses = DataManager.recoverCourses(context);
        if (courses == null) {
            System.out.println("no alarms set, failed to retrieve courses");
            return;
        }
        List<Task> sortedTasks = flattenCourseList(courses);
        if (sortedTasks.size() == 0) {
            System.out.println("No alarms to set");
            return;
        }
        Task nextTask = sortedTasks.get(0);
        if (nextTask.getDueDate() == null) {
            System.out.println(nextTask.getName() + " has an indefinite due date, no alarms set");
            return;
        }
        enableAlarm(sortedTasks, context);
    }

    /**
     * Cancels any alarms that match the given alarmId. Note that this will
     * not prevent future alarms from being set, and will only cancel the
     * current alarm in the queue as long as it matches the given alarmId.
     */
    public static void cancelAlarm(Context context, long alarmId) {
        Intent intent = buildCancellingIntent(alarmId);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        //TODO do testing on this to verify intent filtering works
        AlarmManager alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
        alarmManager.cancel(pIntent);
    }

    /**
     * Making this recursive so we can easily set sequential alarms that have
     * the same due date, each call sets an alarm for the current task in the
     * task list, and we stop once nextTask's due date differs from currentTask's
     */
    private static void enableAlarm(List<Task> sortedTasks, Context context) {
        if (sortedTasks.size() == 0) {
            System.out.println("Tasklist is empty, no alarms set");
            return;
        }
        enableAlarm(sortedTasks, 0, context); //always start with first task in the list
    }

    private static void enableAlarm(List<Task> sortedTasks, int index, Context context) {
        Task currentTask = sortedTasks.get(index);
        if (currentTask.getDueDate() == null) {
            System.out.println(currentTask.getName() + " has an indefinite due date, no alarms set");
            return;
        }
        if (index + 1 < sortedTasks.size() &&
            sortedTasks.get(index + 1).getDueDate().equals(currentTask.getDueDate()))
        {
            enableAlarm(sortedTasks, index + 1, context);
        }
        setAlarm(currentTask, context);
    }

    private static void setAlarm(Task task, Context context) {
        Intent intent = buildIntent(task);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //TODO with the way pending intents interact with the alarm manager, if duplicate
        //alarms get set for the same task then the previous one should get cancelled
        //(regardless of the time the alarm is set for, useful if the user changes
        //what time the alarms should go off). Verify that this actually works as intended.
        AlarmManager alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDueDate().getTimeInMillis() -
            ALARM_OFFSET, pIntent);

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        String date = formatter.format(new Date(task.getDueDate().getTimeInMillis() -
            ALARM_OFFSET));
        System.out.println("Alarm set for " + task + " at time " + date);
    }

    private static Intent buildIntent(Task task) {
        Uri uri = Uri.parse("android.resource://com.scholarscraper/alarmId/?" + task.getUniqueId());

        Intent intent = new Intent("com.scholarscraper.ALARM_ALERT", uri);


        SimpleDateFormat formatter = new SimpleDateFormat("EEEE 'at' h:mm a");
        String date = formatter.format(task.getDueDate().getTime());

        intent.putExtra(Task.class.getName() + "." + DUE_DATE_EXTRA, date);
        intent.putExtra(Task.class.getName() + "." + COURSE_NAME_EXTRA, task.getCourseName());
        intent.putExtra(Task.class.getName() + "." + ASSIGNMENT_NAME_EXTRA, task.getName());
        return intent;
    }

    /**
     * When passed through the alarm manager, these intents will cancel out any
     * existing alarms that match the given uniqueId.
     */
    private static Intent buildCancellingIntent(long uniqueId) {
        Uri uri = Uri.parse("android.resource://com.scholarscraper/alarmId/?" + uniqueId);

        return new Intent("com.scholarscraper.ALARM_ALERT", uri);
    }


    /**
     * Flattens the list of courses into an array of sorted tasks
     */
    private static List<Task> flattenCourseList(List<Course> courses)
    {
        List<Task> flattenedList = new ArrayList<Task>();

        // add all tasks that aren't older than some constant
        long currentTime = System.currentTimeMillis();
        for (Course course : courses)
        {
            for (Task task : course.getAssignments())
            {
                if (task.getDueDate() == null)
                {
                    flattenedList.add(task);
                }
                else if (task.getDueDate().getTimeInMillis() >= currentTime + ALARM_OFFSET)
                {
                    flattenedList.add(task);
                }
            }
        }
        Collections.sort(flattenedList, new TaskListComparator());
        return flattenedList;
    }
}
