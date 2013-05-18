package com.example.scholarscraper;

import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.app.ActionBar;
import android.widget.BaseAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.GridView;
import java.util.GregorianCalendar;
import java.util.Calendar;
import android.content.Context;
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.io.Serializable;
import android.content.Intent;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

// -------------------------------------------------------------------------
/**
 *
 *  The main screen of the app.
 *  Holds a calendar that displays assignments on their due dates, and also supports
 *  an action bar that can displays different popup menus.
 *
 *  @author Alex Lamar
 *  @author Paul Yea
 *  @author Brianna Beitzel
 *  @version May 5, 2013
 */
public class MainActivity
    extends Activity
{
    List<Course> courses;
    private GregorianCalendar   calendar;
    private Calendar            c;
    private GridView            gridView;
    private int                 p;
    private int                 displayedMonth;
    private String[]            months           = { "January", "Febuary",
        "March", "April", "May", "June", "July", "August", "September",
        "October", "November", "December"};
    private Button              next;
    private Button              previous;
    private Button              updateButton;
    private TextView            month;

    private static final String COURSE_FILE_NAME = "courses";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        if (!recoverCourses()) {
            System.out.println("courses not retrieved");
            courses = null;
            launchUpdateDialog();
        }

        if (courses != null) {
            Intent service = new Intent(this, UpdateService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, service, 0);

            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar cal = Calendar.getInstance();

            alarm.cancel(pendingIntent); //removes already existing alarms (if they
                                         //exist) to prevent duplicates from happening
            /* triggers update process every 3 hours */
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                               AlarmManager.INTERVAL_HOUR * 3, pendingIntent);
        }

        c = new GregorianCalendar();
        c = Calendar.getInstance();

        month = (TextView)this.findViewById(R.id.month);
        month.setText(months[c.get(Calendar.MONTH)]);
        displayedMonth = c.get(Calendar.MONTH);

        gridView = (GridView)this.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this, c.get(Calendar.MONTH), c
                            .get(Calendar.YEAR), courses));

        gridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(
                AdapterView<?> parent,
                View v,
                int position,
                long id)
            {
                Intent i = new Intent(MainActivity.this, AssignmentPopUp.class);
                i.putExtra(
                    "com.example.scholarscrapper.Date",
                    Integer.parseInt((String)gridView.getAdapter().getItem(
                        position)));
                i.putExtra(
                    "com.example.scholarscrapper.Month",
                    ((ImageAdapter)gridView.getAdapter()).getMonthInt(
                        position,
                        Integer.parseInt(((ImageAdapter)gridView.getAdapter())
                            .getDayString(position))));
                i.putExtra(
                    "com.example.scholarscrapper.courses",
                    (Serializable)courses);
                MainActivity.this.startActivity(i);
            }
        });

        next = (Button)this.findViewById(R.id.next);
        next.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v)
            {
                int m = displayedMonth + 1;
                if (m <= 11)
                {
                    gridView.setAdapter(new ImageAdapter(
                        gridView.getContext(),
                        m,
                        c.get(Calendar.YEAR),
                        courses));
                    month.setText(months[m]);
                    displayedMonth++;
                }
                else
                {
                    gridView.setAdapter(new ImageAdapter(
                        gridView.getContext(),
                        0,
                        c.get(Calendar.YEAR) + 1,
                        courses));
                    month.setText(months[0]);
                    displayedMonth = 0;
                }
                ((BaseAdapter)gridView.getAdapter()).notifyDataSetChanged();
            }
        });

        previous = (Button)this.findViewById(R.id.previous);
        previous.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v)
            {

                int m = displayedMonth - 1;
                if (m >= 0) // TODO Logic might not be exactly right. Check it.
                {
                    gridView.setAdapter(new ImageAdapter(
                        gridView.getContext(),
                        m,
                        c.get(Calendar.YEAR),
                        courses));
                    month.setText(months[m]);
                    displayedMonth--;
                }
                else
                {
                    gridView.setAdapter(new ImageAdapter(
                        gridView.getContext(),
                        11,
                        c.get(Calendar.YEAR) - 1,
                        courses));
                    month.setText(months[11]);
                    displayedMonth = 11;
                }
                ((BaseAdapter)gridView.getAdapter()).notifyDataSetChanged();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        ActionBar actionBar = getActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          case R.id.action_update: {
              launchUpdateDialog();
              break;
          }
          case R.id.action_change_user: {
              launchChangeDialog();
              break;
          }
          case R.id.action_settings: {
              launchSettingsActivity();
              break;
          }
      }
      return true;
    }

    /**
     * Launches the change user dialog
     */
    private void launchChangeDialog() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new ChangeFragment();
        fragment.show(transaction, "change");
    }

    /**
     * Launches the settings popup dialog
     */
    private void launchSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    /**
     * Launches the update popup dialog
     */
    private void launchUpdateDialog() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new UpdateFragment();
        fragment.show(transaction, "update");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Callback by the update fragment, passes a list of courses with fully
     * updated assignment info.
     */
    public void onUpdateFinished(List<Course> courses) {
        this.courses = courses;
    }
    /**
     * tries to retrieve the courselist from internal storage, returns true
     * if successful, false if not
     */
    private boolean recoverCourses() {
        File file = new File(getFilesDir(), COURSE_FILE_NAME);
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
