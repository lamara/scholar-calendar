package com.scholarscraper.alarm;

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

public class AlarmSetter
{
    private static int ALARM_OFFSET = 43200000; //12 hours in milliseconds
                                                //TODO make this a user setting

    //used for passing extra intent data
    public static final String DUE_DATE_EXTRA = "dueDate";
    public static final String ASSIGNMENT_NAME_EXTRA = "assignmentName";
    public static final String COURSE_NAME_EXTRA = "courseName";

    //TODO add an overloaded method that takes a courselist so we don't have to
    //do IO.
    public static void setNextAlarm(Context context) {
        try
        {
            List<Course> courses = recoverCourses(context);
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
        catch (ClassNotFoundException e)
        {
            System.out.println("failed to set alarm");
        }
        catch (IOException e)
        {
            System.out.println("failed to set alarm");
        }
    }

    public static void cancelAlarm(long alarmId) {
        //TODO cancel it ok!
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
        String uniqueId = Long.toString(task.getUniqueId());
        Uri uri = Uri.parse("android.resource://com.scholarscraper/alarmId/?" + uniqueId);

        Intent intent = new Intent("com.scholarscraper.ALARM_ALERT", uri);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE 'at' h:mm a");
        String date = formatter.format(task.getDueDate().getTime());

        intent.putExtra(Task.class.getName() + "." + DUE_DATE_EXTRA, date);
        intent.putExtra(Task.class.getName() + "." + COURSE_NAME_EXTRA, task.getCourseName());
        intent.putExtra(Task.class.getName() + "." + ASSIGNMENT_NAME_EXTRA, task.getName());
        return intent;
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




    /**
     * tries to retrieve the courselist from internal storage, returns true if
     * successful, false if not
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static List<Course> recoverCourses(Context context) throws ClassNotFoundException, IOException
    {
        File file = new File(context.getFilesDir(), MainActivity.COURSE_FILE_NAME);
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
                return recoveredCourses;

            }
            finally
            {
                input.close();
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
