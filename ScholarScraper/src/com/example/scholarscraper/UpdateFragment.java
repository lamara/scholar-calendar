package com.example.scholarscraper;

import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import android.widget.ProgressBar;
import android.app.ProgressDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import java.util.List;

/**
 * The update fragment executes the course loading process
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 15, 2013
 */
public class UpdateFragment
    extends DialogFragment
{

    WebView webView;
    Context context;
    ScholarScraper scraperInstance;
    EditText usernameEdit;
    EditText passwordEdit;
    TextView updateNotification;
    TextView updateComplete;
    TextView prompt;
    Button launch;
    ProgressBar progressBar;
    /*
     * index is used for keeping track of which courses are to be processed
     * during the update process
     */
    private int              index;
    private List<Course>     courses;
    private UpdateListener listener;
    private String username;
    private String password;

    final private static String USER_FILE_NAME = "userData";
    final private static String COURSE_FILE_NAME = "courses";



    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        final View myFragmentView =
            inflater.inflate(R.layout.fragment, container, false);

        webView =              (WebView) myFragmentView.findViewById(R.id.webView1);
        usernameEdit =        (EditText) myFragmentView.findViewById(R.id.username);
        passwordEdit =        (EditText) myFragmentView.findViewById(R.id.password);
        updateNotification =  (TextView) myFragmentView.findViewById(R.id.updateNotification);
        updateComplete =      (TextView) myFragmentView.findViewById(R.id.updateComplete);
        prompt =              (TextView) myFragmentView.findViewById(R.id.prompt);
        launch =                (Button) myFragmentView.findViewById(R.id.launch);
        progressBar =      (ProgressBar) myFragmentView.findViewById(R.id.progressBar);


        context = getActivity();

        listener = new UpdateFragmentListener();

        updateNotification.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        updateComplete.setVisibility(View.GONE);

        if (recoverUsernamePassword()) {
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }

        index = 0;

        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                launch.setEnabled(false);
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                try
                {
                    scraperInstance =
                        new ScholarScraper(
                            username,
                            password,
                            getActivity(),
                            webView,
                            listener);
                    switchLayout(true);
                    setCancelable(false);
                }
                catch (WrongLoginException e)
                {
                    System.out.println("Wrong login credentials");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        return myFragmentView;
    }

    /**
     * Switches the layout of the update fragment between the progress bar and the
     * login form.
     * @param result If true, switches to progress bar, if false, to the login form
     */
    private void switchLayout(boolean result) {
        if (result) {
            usernameEdit.setVisibility(View.GONE);
            passwordEdit.setVisibility(View.GONE);
            launch.setVisibility(View.GONE);
            prompt.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);
            updateNotification.setVisibility(View.VISIBLE);
        }
        else {
            usernameEdit.setVisibility(View.VISIBLE);
            passwordEdit.setVisibility(View.VISIBLE);
            launch.setVisibility(View.VISIBLE);
            prompt.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            updateNotification.setVisibility(View.GONE);
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
        File file = new File(context.getFilesDir(), USER_FILE_NAME);
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
        System.out.println(context.getFilesDir() + " update fragment");
        File file = new File(context.getFilesDir(), USER_FILE_NAME);
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

    /**
     * returns true if courses are successfully saved to internal storage,
     * false if not
     * @throws IOException
     */
    private boolean saveCourses() {
        File file = new File(context.getFilesDir(), COURSE_FILE_NAME);
        try {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream objectStream= new ObjectOutputStream(fout);
            try {
                if (courses != null) {
                    objectStream.writeObject(courses);
                    System.out.println("courses were saved");
                    return true;
                }
                else {
                    System.out.println("courses were not saved");
                    return false;
                }
            }
            finally {
                objectStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Used as a listener to process the onPagedFinished callbacks in the
     * ScholarScraper class. This is necessary to keep the main thread
     * from hanging for too long, and is useful for organizing the update
     * process as a whole.
     */
    public class UpdateFragmentListener implements UpdateListener
    {

        /**
         * handles execution after the main page is loaded.
         *
         * @param result
         *            from loading the main page from the scraper instance. If
         *            false, then main page failed to load (most likely due to a
         *            bad login).
         */
        public void mainPageLoaded(boolean result)
        {
            try
            {
                if (result)
                {
                    progressBar.setProgress(10);
                    scraperInstance.retrieveCourses("Spring 2013");
                }
                else
                {
                    setCancelable(true);
                }
            }
            catch (WrongLoginException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        public void coursesLoaded()
        {
            progressBar.setProgress(30);
            courses = scraperInstance.getCourses();
            retrieveCourseLinks();
        }


        public void retrieveCourseLinks()
        {
            if (courses == null)
            {
                System.out.println("Courses not loaded yet");
            }
            if (index < courses.size())
            {
                Course course = courses.get(index);
                System.out.println("loading: " + course);
                scraperInstance.retrieveAssignmentPages(course);
                index++;
            }
            else
            {
                /* webview loading is done at this point */
                scraperInstance.logout();
                retrieveAssignments(courses);
            }
        }


        public void retrieveAssignments(List<Course> courses)
        {
            progressBar.setProgress(70);
            new ScholarScraper.AssignmentRetriever().execute(
                courses,
                username,
                password,
                listener,
                context);
        }


        public void updateFinished()
        {
            progressBar.setProgress(100);
            updateNotification.setVisibility(View.GONE);
            updateComplete.setVisibility(View.VISIBLE);
            setCancelable(true);
            System.out.println("updateFinished() called");
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.onUpdateFinished(courses);
                saveCourses();
                saveUsernamePassword();
                System.out.println("courses passed to main activity");
            }
        }

        public void incrementProgress() {

            progressBar.incrementProgressBy(4);

        }
    }
}
