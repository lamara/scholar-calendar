package com.scholarscraper;

import com.scholarscraper.model.Course;
import com.scholarscraper.model.Task;
import com.scholarscraper.listview.Listable;
import com.scholarscraper.listview.AssignmentAdapter;
import com.scholarscraper.listview.TaskListComparator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.scholarscraper.separators.DateSeparator;
import com.scholarscraper.separators.DistantSeparator;
import com.scholarscraper.separators.NextWeekSeparator;
import com.scholarscraper.separators.TodaySeparator;
import com.scholarscraper.separators.WeekSeparator;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * The main screen of the app. Holds a ListView that displays assignments on
 * their due dates, and also supports an action bar that can displays different
 * popup menus.
 *
 * @author Alex Lamar
 * @version May 5, 2013
 */
public class MainActivity
    extends Activity
{
    private List<Course>        courses;
    private ListView            listView;
    private ActionBar           actionBar;

    private boolean isLoggedIn = false;

    private static final String UPDATED           = "updated";
    private boolean             hasUpdated        = false;

    private String              username;
    private String              password;

    public static final String  USER_FILE_NAME    = "userData";
    public static final String  COURSE_FILE_NAME  = "courses";
    private static final int    PAST_DUE_CONSTANT = 86400000;
    public static final int     LOGGED_IN_STATE_INDEX = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        listView = (ListView)findViewById(R.id.listView);
        actionBar = getActionBar();

        if (savedInstanceState != null)
        {
            hasUpdated = savedInstanceState.getBoolean(UPDATED);
        }

        if (recoverUsernamePassword())
        {
            // TODO populate "logged in as" textview
            isLoggedIn = true;
            if (recoverCourses())
            {
                populateListView();
            }
            if (!hasUpdated)
            {
                new ScholarScraper().execute(username, password, this);
                Toast.makeText(this, "Updating...", Toast.LENGTH_LONG).show();
                hasUpdated = true;
            }
        }
        else
        {
            isLoggedIn = false;
            launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        actionBar = getActionBar();
        if (isLoggedIn) {
            //actionBar.getTabAt(LOGGED_IN_STATE_INDEX).setText("Log Out");
        }
        else {
            //actionBar.getTabAt(LOGGED_IN_STATE_INDEX).setText("Log In");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_change_user:
            {
                launchChangeDialog();
                break;
            }
            case R.id.action_settings:
            {
                launchSettingsActivity();
                break;
            }
        }
        return true;
    }


    /**
     * mainly used for keeping track of whether or not the app has already done
     * an update so we don't update every time the activity restarts its
     * lifecycle
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(UPDATED, hasUpdated);
    }


    /**
     * Launches the update popup dialog
     */
    public void launchLoginDialog(String prompt)
    {
        FragmentTransaction transaction =
            getFragmentManager().beginTransaction();
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
    private void launchChangeDialog()
    {
        FragmentTransaction transaction =
            getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new ChangeFragment();
        fragment.show(transaction, "change");
    }


    /**
     * Launches the settings popup dialog
     */
    private void launchSettingsActivity()
    {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    /**
     * Callback by the async task retriever, passes a list of courses with fully
     * updated assignment info.
     */
    public void onUpdateFinished(List<Course> courses)
    {
        this.courses = courses;
        Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
        populateListView();
    }


    /**
     * Callback by async task retriever, signifies that something went wrong on
     * retrieval (mostly going to be wrong login exceptions).
     */
    public void onUpdateCancelled(int result, Exception e)
    {
        //TODO handle exception (with a null check as it can come in null)
        if (result == ScholarScraper.WRONG_LOGIN)
        {
            username = null;
            password = null;
            saveUsernamePassword();
            launchLoginDialog("Invalid username or password.");
        }
        if (result == ScholarScraper.ERROR)
        {
            Toast.makeText(
                this,
                "Update failed, try to refresh again",
                Toast.LENGTH_LONG).show();
        }
    }

    public void depopulateListView() {
        //TODO pass an empty adapter into the listview, should be called whenever
        //a user logs out of the app.
    }


    private void populateListView()
    {
        AssignmentAdapter adapter =
            new AssignmentAdapter(this, flattenCourseList(courses));
        listView.setAdapter(adapter);
    }


    /**
     * Flattens the list of courses into an array of sorted items (tasks and
     * separators) to feed into the ListView
     */
    private Listable[] flattenCourseList(List<Course> courses)
    {
        List<Listable> flattenedList = new ArrayList<Listable>();
        flattenedList.add(new TodaySeparator());
        flattenedList.add(new WeekSeparator());
        flattenedList.add(new NextWeekSeparator());
        flattenedList.add(new DistantSeparator());

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
                else if (task.getDueDate().getTimeInMillis() >= currentTime
                    - PAST_DUE_CONSTANT)
                {
                    flattenedList.add(task);
                }
            }
        }
        // sort is stable so separators are guaranteed to precede tasks if
        // the two happen to share the same date.
        Collections.sort(flattenedList, new TaskListComparator());

        // we don't want two separators to appear together with nothing in
// between them
        removeSequentialSeparators(flattenedList);
        return flattenedList.toArray(new Listable[0]);
    }


    private void removeSequentialSeparators(List<Listable> elements)
    {
        boolean previousElementWasSeparator = false;
        for (int i = 0; i < elements.size(); i++)
        {
            Listable element = elements.get(i);
            if (element instanceof DateSeparator)
            {
                if (previousElementWasSeparator)
                {
                    elements.remove(i - 1);
                    i--;
                }
                previousElementWasSeparator = true;
            }
            else
            {
                previousElementWasSeparator = false;
            }
        }
        if (previousElementWasSeparator)
        {
            // last element was a separator so we delete it as nothing follows
            elements.remove(elements.size() - 1);
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


    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    private boolean saveUsernamePassword()
    {
        if (username == null || password == null)
        {
            System.out.println("username/password were not saved (null)");
            return false;
        }
        File file = new File(this.getFilesDir(), USER_FILE_NAME);
        try
        {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream stream = new ObjectOutputStream(fout);
            try
            {

                stream.writeObject(username);
                stream.writeObject(password);
                System.out.println("username/password were saved");
                return true;
            }
            finally
            {
                stream.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword()
    {
        File file = new File(this.getFilesDir(), USER_FILE_NAME);
        try
        {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInputStream input = new ObjectInputStream(buffer);
            try
            {
                username = (String)input.readObject();
                password = (String)input.readObject();
                if (username != null && password != null)
                {
                    System.out.println("Username/password retrieved");
                    return true;
                }
                System.out.println("Username/password not retrieved");
                return false;
            }
            finally
            {
                input.close();
            }
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
    }

}
