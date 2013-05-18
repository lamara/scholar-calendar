package com.example.scholarscraper;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
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
 * Displays the assignments that are due on the specified date. Includes the due
 * date, a description, and the name of the assignment.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 29, 2013
 */
public class AssignmentPopUp
    extends Activity
{
    private List<Course>   courses     = new ArrayList<Course>();
    private List<Task>     assignments = new ArrayList<Task>();
    private RelativeLayout layout;
    private Button         back;
    private TextView       assignmentText;
    private String         text        = "No assignments to display.";
    private int            date;
    private int            month;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up);
        Intent i = getIntent();
        date = i.getIntExtra("com.example.scholarscrapper.Date", 0);
        month = i.getIntExtra("com.example.scholarscrapper.Month", 0);
        courses =
            (List<Course>)i
                .getSerializableExtra("com.example.scholarscrapper.courses");
        layout = (RelativeLayout)this.findViewById(R.id.layout);
        back = (Button)this.findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v)
            {
                finish();
            }
        });

        List<Task> tasks;
        if (courses == null) {
            courses = new ArrayList<Course>();
        }
        for (Course c : courses)
        {
            tasks = c.getAssignments();
            for (int x = 0; x < tasks.size(); x++)
            {
                assignments.add(tasks.get(x));
            }
        }
        this.addAssignments();
    }


    // ----------------------------------------------------------
    /**
     * Adds all the assignments from the courses into the list.
     */
    public void addAssignments()
    {
        String temp = " ";
        for (Task t : assignments)
        {
            if (t.getDueDate().get(Calendar.DATE) == date
                && t.getDueDate().get(Calendar.MONTH) == month)
            {
                int m = t.getDueDate().get(Calendar.MONTH) + 1;
                temp =
                    temp + " Assignment: " + t.getName() + "\n" + " Due Date: "
                        + m + "/" + t.getDueDate().get(Calendar.DATE) + "\n"
                        + " Description: " + t.getDescription() + "\n" + "\n";
            }
        }

        if (assignmentText != null)
        {
            layout.removeView(assignmentText);
        }
        if (!temp.equals(" "))
        {
            text = temp;
            assignmentText = new TextView(this);
            assignmentText.setText(text);
            layout.addView(assignmentText);
        }
        else
        {
            assignmentText = new TextView(this);
            assignmentText.setText(text);
            layout.addView(assignmentText);
        }
    }


    // ----------------------------------------------------------
    /**
     * Used for testing purposes. Returns the list of assignments.
     *
     * @return The list of assignments.
     */
    public List<Task> getAssignments()
    {
        return assignments;
    }


    // ----------------------------------------------------------
    /**
     * Used for testing purposes. Sets the current list of assignments.
     *
     * @param list
     *            A list of Tasks to use.
     */
    public void setAssignments(List<Task> list)
    {
        assignments = list;
    }
}
