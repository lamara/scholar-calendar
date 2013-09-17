package com.scholarscraper;

import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.Map;
import com.scholarscraper.separators.DistantSeparator;
import com.scholarscraper.separators.NextWeekSeparator;
import com.scholarscraper.separators.WeekSeparator;
import com.scholarscraper.separators.TodaySeparator;
import com.scholarscraper.R;
import com.scholarscraper.separators.DateSeparator;
import java.util.Collections;
import java.util.ArrayList;
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
 *  Holds a ListView that displays assignments on their due dates, and also supports
 *  an action bar that can displays different popup menus.
 *
 *  @author Alex Lamar
 *  @version May 5, 2013
 */
public class MainActivity
    extends Activity
{
    private List<Course> courses;
    private ListView listView;


    private String[] months = { "January", "Febuary",
        "March", "April", "May", "June", "July", "August", "September",
        "October", "November", "December"};

    private String username;
    private String password;


    private static final String USER_FILE_NAME = "userData";
    private static final String COURSE_FILE_NAME = "courses";
    private static final int NUM_SEPERATORS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        listView = (ListView) findViewById(R.id.listView);

        if (recoverUsernamePassword()) {
            //TODO populate "logged in as" textview
            if (recoverCourses()) {
                populateListView();
            }
            new ScholarScraper().execute(username, password, this);
            //TODO launch an "updating.." blurb
        }
        else {
            launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
        }
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
    * Launches the update popup dialog
    */
        private void launchLoginDialog(String prompt) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            Bundle bundle = new Bundle();
            bundle.putString("prompt", prompt);

            DialogFragment fragment = new UpdateFragment();
            fragment.setArguments(bundle);
            fragment.show(transaction, "update");
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Callback by the async task retriever, passes a list of courses with fully
     * updated assignment info.
     */
    public void onUpdateFinished(List<Course> courses) {
        this.courses = courses;
        populateListView();
    }

    /**
     * Callback by async task retriever, signifies that something went wrong on
     * retrieval (mostly going to be wrong login exceptions).
     */
    public void onUpdateCancelled(int result) {
        if (result == ScholarScraper.WRONG_LOGIN) {
            username = null;
            password = null;
            saveUsernamePassword();
            launchLoginDialog("Invalid username or password.");
        }
        if (result == ScholarScraper.ERROR) {
            // TODO notify that error happened, ask to hit refresh mechanism
        }
    }

    private void populateListView() {
        ArrayAdapter<Listable> adapter = new ArrayAdapter<Listable>(this, R.layout.listview_element);
        adapter.addAll(flattenCourseList(courses));
        listView.setAdapter(adapter);
    }

    /**
     * Flattens the list of courses into an array of sorted items (tasks and
     * separators) to feed into the ListView
     */
    private Listable[] flattenCourseList(List<Course> courses) {
        List<Listable> flattenedList = new ArrayList<Listable>();
        flattenedList.add(new TodaySeparator());
        flattenedList.add(new WeekSeparator());
        flattenedList.add(new NextWeekSeparator());
        flattenedList.add(new DistantSeparator());

        for (Course course: courses) {
            flattenedList.addAll(course.getAssignments());
        }

        //sort is stable so separators are guaranteed to precede tasks if
        //the two happen to share the same date.
        Collections.sort(flattenedList, new TaskListComparator());

        //we don't want two separators to appear together with nothing in between them
        removeSequentialSeparators(flattenedList);
        return flattenedList.toArray(new Listable[0]);
    }

    private void removeSequentialSeparators(List<Listable> elements) {
        boolean previousElementWasSeparator = false;
        for (int i = 0; i < elements.size(); i++) {
            Listable element = elements.get(i);
            Map<String, String> attributes = element.getAttributes();
            if (attributes.get("separator") != null) {
                if (previousElementWasSeparator) {
                    elements.remove(i - 1);
                    i--;
                }
                previousElementWasSeparator = true;
            }
            else {
                previousElementWasSeparator = false;
            }
        }
        if (previousElementWasSeparator) {
            //last element was a separator so we delete it as nothing follows
            elements.remove(elements.size() - 1);
        }
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

    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    private boolean saveUsernamePassword() {
        if (username == null || password == null) {
            System.out.println("username/password were not saved (null)");
            return false;
        }
        File file = new File(this.getFilesDir(), USER_FILE_NAME);
        try {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream stream= new ObjectOutputStream(fout);
            try {

                stream.writeObject(username);
                stream.writeObject(password);
                System.out.println("username/password were saved");
                return true;
            }
            finally {
                stream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword() {
        File file = new File(this.getFilesDir(), USER_FILE_NAME);
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInputStream input = new ObjectInputStream (buffer);
            try {
                username = (String) input.readObject();
                password = (String) input.readObject();
                if (username != null && password != null) {
                    System.out.println("Username/password retrieved");
                    return true;
                }
                System.out.println("Username/password not retrieved");
                return false;
            }
            finally {
                input.close();
            }
        }
        catch (ClassNotFoundException e) {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
    }

}
