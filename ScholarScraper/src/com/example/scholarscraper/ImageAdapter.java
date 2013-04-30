package com.example.scholarscraper;

import com.example.scholarscraper.Task;
import java.util.HashMap;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.widget.TextView;
import android.graphics.PorterDuff;
import android.graphics.Color;
import android.widget.GridView;
import android.widget.ImageView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author bribytz (brianna beitzel)
 * @version Apr 17, 2013
 */
public class ImageAdapter
    extends BaseAdapter
{
    private Context           context;
    private int               month;
    private int               year;
    private int               prevMonth;
    private int               nextMonth;
    private GregorianCalendar cal;
    private Calendar          calendar;
    private List<String>      daysToShow   = new ArrayList<String>();
    private List<Course>      course;
    private List<Task>        assignments  = new ArrayList<Task>();
    private int[]             daysInMonths = { 31, 28, 31, 30, 31, 30, 31, 31,
        30, 31, 30, 31                    };
    private String[]          dayNames     = { "Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday" };


    // ----------------------------------------------------------
    /**
     * Create a new ImageAdapter object.
     *
     * @param c
     * @param month
     * @param year
     * @param courses
     */
    public ImageAdapter(Context c, int month, int year, List<Course> courses)
    {
        context = c;
        calendar = Calendar.getInstance();
        cal = new GregorianCalendar(year, month, 1);
        this.year = year;
        this.month = month;
        this.course = courses;
        if (month == 0)
        {
            prevMonth = 11;
        }
        else
        {
            prevMonth = this.month - 1;
        }
        if (month == 11)
        {
            nextMonth = 0;
        }
        else
        {
            nextMonth = this.month + 1;
        }
        if (courses != null) {
            addAssignment();
        }

        int numDays = daysInMonth(month);
        int firstDay = getDay(cal.get(Calendar.DAY_OF_WEEK));
        int prevDay;

        if (this.month == 0)
        {
            prevDay = daysInMonth(11) - firstDay + 1;
        }
        else
        {
            prevDay = daysInMonth(month - 1) - firstDay + 1;
        }

        // Sets the days from the previous month.
        for (int i = 0; i < firstDay; i++)
        {
            daysToShow.add(String.valueOf(prevDay + i));
        }

        // Sets the days for the current month
        for (int i = 1; i <= numDays; i++)
        {
            daysToShow.add(String.valueOf(i));
        }

        // Sets the days for the next month
        int total = daysToShow.size();
        int d = 1;
        while (total % 7 != 0) // total < 35
        {
            daysToShow.add(String.valueOf(d));
            d++;
            total++;
        }
    }


    public int getCount()
    {
        return daysToShow.size();
    }


    public Object getItem(int position)
    {
        return daysToShow.get(position);
    }


    public long getItemId(int position)
    {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView view = new TextView(context);

        if (isToday(Integer.parseInt(daysToShow.get(position)), month))
        {
            view.setTextColor(Color.RED);
        }
        if (assignments.size() > 0 && hasAssignment(position, Integer.parseInt(daysToShow.get(position))))
        {
            view.setTextColor(Color.MAGENTA);
        }
        view.setText(daysToShow.get(position));

        if (position < 1)
        {
            view.setBackgroundColor(Color.rgb(250, 240, 245));
        }
        else if (position < 31)
        {
            view.setBackgroundColor(Color.WHITE);
        }
        else
        // TODO Fix this logic. It isn't EXACTLY right. :/
        {
            view.setBackgroundColor(Color.rgb(250, 240, 245));
        }

        return view;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param d
     * @return
     */
    public int getDay(int d)
    {
        if (d == Calendar.SUNDAY)
        {
            return 0;
        }
        else if (d == Calendar.MONDAY)
        {
            return 1;
        }
        else if (d == Calendar.TUESDAY)
        {
            return 2;
        }
        else if (d == Calendar.WEDNESDAY)
        {
            return 3;
        }
        else if (d == Calendar.THURSDAY)
        {
            return 4;
        }
        else if (d == Calendar.FRIDAY)
        {
            return 5;
        }
        else
        {
            return 6;
        }
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param m
     * @return
     */
    public int daysInMonth(int m)
    {
        if (cal.isLeapYear(year) && m == 1)
        {
            return 29;
        }
        return daysInMonths[m];
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param day
     * @param m
     * @return
     */
    public boolean isToday(int day, int m)
    {
        if (day == calendar.get(Calendar.DAY_OF_MONTH)
            && m == calendar.get(Calendar.MONTH))
        {
            return true;
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    public void addAssignment()
    {
        List<Task> tasks;
        for (Course c : course)
        {
            tasks = c.getAssignments();
            for (int i = 0; i < tasks.size(); i++)
            {
                assignments.add(tasks.get(i));
                // assignments.add(c.getAssignments().get(i));
            }

        }
        Task t =
            new Assignment("Test", "More Test", new GregorianCalendar(
                2013,
                3,
                15));
        assignments.add(t);
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @return Description
     */
    public boolean hasAssignment(int p, int d)
    {
        // TODO BTWS fix the year assignment which may be slightly wrong.
        GregorianCalendar dueDate;
        /*
         * if (p < 6 && d > 24) { dueDate = new GregorianCalendar(year,
         * prevMonth, d); } else if (p > 22 && d < 7) { dueDate = new
         * GregorianCalendar(year, prevMonth, d); } else {
         */
        dueDate = new GregorianCalendar(year, month, d);
        // }

        for (Task t : assignments)
        {
            if (dueDate.get(Calendar.DATE) == t.getDueDate().get(Calendar.DATE)
                && dueDate.get(Calendar.MONTH) == t.getDueDate().get(
                    Calendar.MONTH))
            {
                System.out.println("DueDate: " + dueDate.get(Calendar.DATE)
                    + " Month: " + dueDate.get(Calendar.MONTH) + " T dueDate: "
                    + t.getDueDate().get(Calendar.DATE) + " Month: "
                    + t.getDueDate().get(Calendar.MONTH));
                return true;
            }
        }

        return false;
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @return
     */
    public String getM()
    {
        String temp = "" + month;
        return temp;
    }
}
