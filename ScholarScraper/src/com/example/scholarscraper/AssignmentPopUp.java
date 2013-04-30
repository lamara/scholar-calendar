package com.example.scholarscraper;

import java.util.GregorianCalendar;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import android.content.Intent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author bribytz (brianna beitzel)
 * @version Apr 29, 2013
 */
public class AssignmentPopUp
    extends Activity
{
    private List<Course>        courses          = null;
    private List<Task>          assignments      = new ArrayList<Task>();
    private TextView            assignmentText;
    private String              text             = "No assignments to display.";
    private static final String COURSE_FILE_NAME = "courses";


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up);
        Intent i = getIntent();
        int date = i.getIntExtra("com.example.scholarscraper.Date", 0);
        int month = i.getIntExtra("com.example.scholarscraper.Month", 0);
        courses = (List<Course>) i.getSerializableExtra("com.example.scholarscraper.courses");
        assignmentText = (TextView)this.findViewById(R.id.assignmentText);

        List<Task> tasks;
        for (Course c : courses)
        {
            tasks = c.getAssignments();
            for (int x = 0; x < tasks.size(); x++)
            {
                assignments.add(tasks.get(x));
            }
        }

        String temp = " ";
        //TODO Hey. Take this out before turning it in
        for (Task t : assignments)
        {
            if (t.getDueDate().get(Calendar.DATE) == date
                && t.getDueDate().get(Calendar.MONTH) == month)
            {
                int m = t.getDueDate().get(Calendar.MONTH) + 1;
                temp =
                    temp + " Assignment: " + t.getName() + " Due Date: "
                        + m + "/" + t.getDueDate().get(Calendar.DATE) + " Description:" + t.getDescription();
                // TODO Yo. Maybe just add t to toDisplay and then get the names
                // and stuff.
            }
        }

        if (!temp.equals(" "))
        {
            text = temp;
            assignmentText.setText(text);
        }
        else
        {
            assignmentText.setText(text);
        }
    }


    /**
     * tries to retrieve the courselist from internal storage, returns true if
     * successful, false if not
     */
    private boolean recoverCourses()
    {
        File file = new File(getFilesDir(), COURSE_FILE_NAME);
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
