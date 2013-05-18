package com.example.scholarscraper;

import com.example.scholarscraper.Task;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.widget.TextView;
import android.graphics.Color;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

// -------------------------------------------------------------------------
/**
 * A subclass of BaseAdapter that creates a Calendar from a gridView on screen.
 * ImageAdapter determines the month and correctly displays it. If a date has an
 * assignment due the colour of the text changes to indicate this. The current
 * date is also higlighted.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
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

    private static int ORANGE = Color.rgb(232, 117, 17);
    private static int MAROON = Color.rgb(128, 0, 0);


    // ----------------------------------------------------------
    /**
     * Create a new ImageAdapter object.
     *
     * @param c
     *            The Context
     * @param month
     *            The current month.
     * @param year
     *            The current year.
     * @param course
     *            A list of the user's courses.
     */
    public ImageAdapter(Context c, int month, int year, List<Course> course)
    {
        context = c;
        calendar = Calendar.getInstance();
        cal = new GregorianCalendar(year, month, 1);
        this.year = year;
        this.month = month;
        this.course = course;

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

        if (course != null)
        {
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

        daysToShow.add("Sun");
        daysToShow.add("Mon");
        daysToShow.add("Tue");
        daysToShow.add("Wed");
        daysToShow.add("Thur");
        daysToShow.add("Fri");
        daysToShow.add("Sat");

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
        while (total < 42)
        {
            daysToShow.add(String.valueOf(d));
            d++;
            total++;
        }
    }


    /**
     * Returns the amount of items displayed in the gridView.
     *
     * @return The number of days shown.
     */
    public int getCount()
    {
        return daysToShow.size();
    }


    /**
     * Returns the Object at the given position in the gridView.
     *
     * @param position
     *            The position to look at.
     * @return The item at specified position.
     */
    public Object getItem(int position)
    {
        return daysToShow.get(position);
    }


    // ----------------------------------------------------------
    /**
     * Returns the item at the specified position in the form of a string.
     *
     * @param position
     *            The position to check.
     * @return The String form of the item at the position.
     */
    public String getDayString(int position)
    {
        return daysToShow.get(position);
    }


    /**
     * Returns the item's id.
     *
     * @param position
     * @return 0
     */
    public long getItemId(int position)
    {
        return 0;
    }


    /**
     * Creates the view for the calendar.
     *
     * @param position
     *            The position to generate.
     * @param convertView
     *            The view.
     * @param parent
     *            The parent view.
     * @return The view to be displayed.
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView view = new TextView(context);

        view.setPadding(0, 0, 0, 0);

        if (position > 6)
        {
            if (isToday(Integer.parseInt(daysToShow.get(position)), month, year))
            {
                view.setTextColor(Color.RED);
            }

            view.setText(daysToShow.get(position));

            if (getMonthInt(
                position,
                Integer.parseInt(daysToShow.get(position))) == prevMonth)
            {
                view.setTextColor(Color.GRAY);
            }
            else if (getMonthInt(
                position,
                Integer.parseInt(daysToShow.get(position))) == month)
            {
                view.setBackgroundColor(Color.WHITE);
            }
            else
            {
                view.setTextColor(Color.GRAY);
            }
            if (assignments.size() > 0
                && hasAssignment(
                    position,
                    Integer.parseInt(daysToShow.get(position))))
            {
                view.setTextColor(MAROON);
            }
        }
        else
        {
            view.setText(daysToShow.get(position));
        }
        return view;
    }


    // ----------------------------------------------------------
    /**
     * Translates Calendar's days.
     *
     * @param d
     *            The Day to translate.
     * @return A numerical value for the day. Between 0-6. Where Sunday is 0.
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
     * Returns the amount of days in the month while accounting for leap years.
     *
     * @param m
     *            The month to return. 0 - 11 where 0 is January.
     * @return The number of days in the month.
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
     * Determines if the inputed date is the current date.
     *
     * @param day
     *            The day.
     * @param m
     *            The month.
     * @param y
     *            The year.
     * @return true if the days match.
     */
    public boolean isToday(int day, int m, int y)
    {
        if (day == calendar.get(Calendar.DAY_OF_MONTH)
            && m == calendar.get(Calendar.MONTH)
            && y == calendar.get(Calendar.YEAR))
        {
            return true;
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Gets the user's assignments and adds them to a list.
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
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Determines if the specified date has an assignment due.
     *
     * @param p
     *            Position to check.
     * @param d
     *            Day to check.
     * @return true if date has an assignment.
     */
    public boolean hasAssignment(int p, int d)
    {
        int m;

        if (p < 6 && d > 24)
        {
            m = prevMonth;

        }
        else if (p > 22 && d < 7)
        {
            m = nextMonth;
        }
        else
        {
            m = month;
        }

        for (Task t : assignments)
        {
            if (d == t.getDueDate().get(Calendar.DATE)
                && m == t.getDueDate().get(Calendar.MONTH)
                && year == t.getDueDate().get(Calendar.YEAR))
            {
                return true;
            }
        }

        return false;
    }


    // ----------------------------------------------------------
    /**
     * Returns the month as a String.
     *
     * @param position
     *            The position to check.
     * @param d
     *            The day to check.
     * @return temp The numeric month associated with the day.
     */
    public String getMonth(int position, int d)
    {
        String temp;
        if (position < 13 && d > 24)
        {
            temp = "" + prevMonth;
        }
        else if (position > 29 && d < 7)
        {
            temp = "" + nextMonth;
        }
        else
        {
            temp = "" + month;
        }
        return temp;
    }


    // ----------------------------------------------------------
    /**
     * Returns the month as an integer.
     *
     * @param position
     *            The position to check.
     * @param d
     *            The day to check.
     * @return temp The month in Integer form.
     */
    public int getMonthInt(int position, int d)
    {
        int temp;
        if (position < 13 && d > 24)
        {
            temp = prevMonth;
        }
        else if (position > 29 && d < 7)
        {
            temp = nextMonth;
        }
        else
        {
            temp = month;
        }
        return temp;
    }

}
