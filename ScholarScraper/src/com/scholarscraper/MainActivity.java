package com.scholarscraper;

import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.app.AlertDialog;
import com.scholarscraper.separators.GenericSeparator;
import android.widget.TextView;
import com.scholarscraper.update.UpdateService;
import com.scholarscraper.update.ScholarScraper;
import com.scholarscraper.update.LightScholarScraper;
import java.util.AbstractMap.SimpleEntry;
import com.scholarscraper.update.DataManager;
import com.scholarscraper.alarm.AlarmSetter;
import java.util.Calendar;
import android.app.AlarmManager;
import android.content.Context;
import android.app.PendingIntent;
import com.scholarscraper.listview.PullToRefreshListView.OnRefreshListener;
import com.scholarscraper.listview.PullToRefreshListView;
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
import android.widget.Toast;
import com.scholarscraper.separators.DateSeparator;
import com.scholarscraper.separators.DistantSeparator;
import com.scholarscraper.separators.NextWeekSeparator;
import com.scholarscraper.separators.TodaySeparator;
import com.scholarscraper.separators.WeekSeparator;
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
//TODO add a "first app launch" dialog w/ app info, privacy statement, etc.
public class MainActivity
    extends Activity
{
    private ScholarScraper updateInstance;

    private List<Course>          courses;
    private PullToRefreshListView listView;
    private TextView              emptyListText;
    private ActionBar             actionBar;

    //UPDATED is a savedInstance key values
    private static final String UPDATED           = "updated";
    private boolean             hasUpdated        = false;
    private boolean             isLoggedIn        = false;
    private boolean             coursesRecovered  = false;

    //used as a shared preference to see if the user has agreed to app conditions
    private static final String AGREED_CONDITIONS_KEY = "com.scholarscraper.agreedConditions";

    private String              username;
    private String              password;



    public static final String  USER_FILE_NAME    = "userData";
    public static final String  COURSE_FILE_NAME  = "courses";

    private static final int    PAST_DUE_CONSTANT = 24 * 3600 * 1000; //1 day in milliseconds

    public static final int UPDATE_INTERVAL = 6; //in hours

    //used to show the state of the list view
    private static final String EMPTY_LIST_TEXT = "No assignments due";
    private static final String LOG_IN_LIST_TEXT = "Please log in!";
    //used for action menu items
    private static final String LOG_IN = "Log in";
    private static final String LOG_OUT = "Log out";

    private MenuItem settingsMenu;
    private MenuItem changeUserMenu;

    //TODO change logging app wide to stop using system.out, also remove any
    //logging that was previously used for debugging purposes

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /* --------- Initial setup ----------------*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        actionBar = getActionBar();
        listView = (PullToRefreshListView)findViewById(R.id.listView);
        initListView(listView);

        if (savedInstanceState != null)
        {
            //TODO currently screen orientation change is disabled because maintaining
            //the listview "refreshing" state between changes is nontrivial.
            //hasUpdated makes sure app doesn't constantly update after each orientation change,
            //keeping it in the logic even though orientation change isn't used at this point
            hasUpdated = savedInstanceState.getBoolean(UPDATED);
        }
        initScheduledUpdate();

        /* ---------- Handle startup logic -----------*/
        if (recoverUsernamePassword())
        {
            // TODO populate "logged in as" textview
            isLoggedIn = true;
            //emptyListText.setText("No assignments found!");
            coursesRecovered = recoverCourses();
            if (coursesRecovered)
            {
                populateListView();
                AlarmSetter.setNextAlarm(this, courses);
                if (!hasUpdated) {
                    lightUpdate(courses);
                    hasUpdated = true;
                }
            }
            if (!hasUpdated)
            {
                update();
                Toast.makeText(this, "Updating...", Toast.LENGTH_LONG).show();
                hasUpdated = true;
            }
        }
        else
        {
            isLoggedIn = false;
            showEmptyListView(LOG_IN_LIST_TEXT);
            launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        //onCreateOptionsMenu gets called after onCreate(), so we have an idea
        //of the app state at this point.
        changeUserMenu = menu.getItem(0); //update these indices if actionbar ever changes
        settingsMenu = menu.getItem(1);

        if (isLoggedIn) {
            changeUserMenu.setTitle(LOG_OUT);
        }
        else {
            changeUserMenu.setTitle(LOG_IN);
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
                if (isLoggedIn) {
                    launchChangeDialog();
                }
                else {
                    launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
                }
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
        //check to see if the user has agreed to the apps conditions
        final SharedPreferences preferences = this.getSharedPreferences("com.scholarscraper", Context.MODE_PRIVATE);
        boolean hasAgreed = preferences.getBoolean(AGREED_CONDITIONS_KEY, false);
        if (!hasAgreed) {
            launchFirstTimeInfoDialog();
            return;
        }

        //user has agreed to conditions, so we can let them log in
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
        FragmentTransaction transaction =
            getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new SettingsFragment();
        fragment.show(transaction, "settings");
    }


    private static String APP_INFO = "The Scholar Calendar App takes your Scholar " +
    		"login information and stores it on the device. Never store sensitive " +
    		"information on a rooted device. Bugs can pop up if Scholar " +
    		"ever updates its web page structure, so do not rely on this app alone. " +
    		"If a major bug is found, I'll do my best to send out a push notification " +
    		"letting you know when it should be fixed.";
    /**
     * Launches the app info dialog, will be launched the first time the user starts
     * the app. They have to agree and click yes in order to log in and use the app.
     */
    private void launchFirstTimeInfoDialog() {
     final SharedPreferences preferences = this.getSharedPreferences("com.scholarscraper", Context.MODE_PRIVATE);
     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

     alertDialogBuilder.setTitle("Application Info");


     alertDialogBuilder
         .setMessage(APP_INFO)
         .setCancelable(false)
         .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,int id) {
                  preferences.edit().putBoolean(AGREED_CONDITIONS_KEY, true).commit();
                  launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
              }
         })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,int id) {
                 dialog.cancel();
              }
     });
     AlertDialog alertDialog = alertDialogBuilder.create();
     alertDialog.show();
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
    public void onUpdateFinished(List<Course> courses, int result)
    {
        listView.onRefreshComplete();
        if (result == ScholarScraper.IO_ERROR) {
            //its possible for the update to finish but for course saving to still error out
            //this is a bad situation to be in because now the user state is potentially corrupted
            //so we are going to reset the state of the app. (this really shouldn't happen except
            //under bad IO circumstances beyond our control).
            logOut();

        }
        isLoggedIn = true;
        this.courses = courses;
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        populateListView();
        AlarmSetter.setNextAlarm(this, courses);
    }


    /**
     * Callback by async task retriever, signifies that something went wrong on
     * retrieval (mostly going to be wrong login exceptions).
     */
    public void onUpdateCancelled(int result, Exception e)
    {
        System.out.println("Update was cancelled");
        //TODO handle exception that comes in (with a null check as it can come in null)
        listView.onRefreshComplete();
        if (result == ScholarScraper.WRONG_LOGIN)
        {
            logOut();
            launchLoginDialog("Invalid username or password.");
        }
        if (result == ScholarScraper.ERROR)
        {
            Toast.makeText(
                this,
                "Update failed, try to refresh again",
                Toast.LENGTH_SHORT).show();
        }
    }

    public void setUsernamePassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<Course> getCourseList() {
        return courses;
    }

    /**
     * Starts a new update process
     */
    public void update() {
        cancelCurrentUpdate();
        listView.setRefreshing();
        updateInstance = new ScholarScraper();
        updateInstance.execute(username, password, this);
    }

    /**
     * Starts a new light update process
     */
    public void lightUpdate(List<Course> courses) {
        cancelCurrentUpdate();
        listView.setRefreshing();
        updateInstance = new LightScholarScraper();
        updateInstance.execute(username, password, courses, this);
    }

    /**
     * Cancel the current update if one is currently running
     */
    public void cancelCurrentUpdate() {
        if (updateInstance != null)
        {
            updateInstance.cancel(true);
            updateInstance = null;
            listView.onRefreshComplete();
        }
    }

    /**
     * Sets up a scheduled update that will run periodically, the first alarm will run
     * after the amount of time in the UPDATE_INTERVAL field has passed.
     */
    public void initScheduledUpdate() {
        Intent service = new Intent(this, UpdateService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, service, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();

        alarm.cancel(pendingIntent); //remove already existing alarms (if they exist)
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                           cal.getTimeInMillis() + AlarmManager.INTERVAL_HOUR * UPDATE_INTERVAL,
                           AlarmManager.INTERVAL_HOUR * UPDATE_INTERVAL,
                           pendingIntent);
    }

    public void initListView(final PullToRefreshListView listView) {
        listView.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                if (isLoggedIn && courses != null) {
                    lightUpdate(courses);
                }
                else if (isLoggedIn) {
                    update();
                }
                else {
                    listView.onRefreshComplete();
                }
            }
        });

        resetListView(); //has the side effect of "instantiating" the listview
                         //by adding an empty adapter to it, pull-to-refresh
                         //won't work on a listview that has not been set at all;
    }

    /**
     * Populate the listview with text signifying that it is empty
     */
    public void showEmptyListView(String text) {
        GenericSeparator separator = new GenericSeparator(text);
        Listable[] list = new Listable[1];
        list[0] = separator;

        AssignmentAdapter adapter = new AssignmentAdapter(this, list);
        listView.setAdapter(adapter);
    }

    /**
     * Populates the listview with an empty adapter
     */
    public void resetListView() {
        AssignmentAdapter adapter = new AssignmentAdapter(this, new Listable[0]);
        listView.setAdapter(adapter);
    }

    private void populateListView()
    {
        Listable[] tasks = flattenCourseList(courses);
        AssignmentAdapter adapter = new AssignmentAdapter(this, tasks);
        if (adapter.isEmpty()) {
            showEmptyListView(EMPTY_LIST_TEXT);
            return;
        }
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
        List<Course> recoveredCourses = DataManager.recoverCourses(this);
        if (recoveredCourses != null) {
            this.courses = recoveredCourses;
            return true;
        }
        else {
            System.out.println("courses not retrieved");
            return false;
        }
    }


    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    public boolean saveUsernamePassword(String username, String password)
    {
        setUsernamePassword(username, password);
        return DataManager.saveUsernamePassword(username, password, this);
    }


    /**
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword()
    {
        SimpleEntry<String, String> usernamePassword = DataManager.recoverUsernamePassword(this);
        if (usernamePassword != null) {
            setUsernamePassword(usernamePassword.getKey(), usernamePassword.getValue());
            return true;
        }
        else {
            setUsernamePassword(null, null);
            return false;
        }
    }

    public void logIn(String username, String password) {
        isLoggedIn = true;
        saveUsernamePassword(username, password);
        resetListView();
        changeUserMenu.setTitle(LOG_OUT);
        update();
    }

    public void logOut() {
        isLoggedIn = false;
        setUsernamePassword(null, null);
        showEmptyListView(LOG_IN_LIST_TEXT);
        changeUserMenu.setTitle(LOG_IN);
        destroyData();
    }

    /**
     * Destroys internal data state, effectively resetting the state of the app
     */
    public void destroyData()
    {
        DataManager.destroyData(this);
    }

}
