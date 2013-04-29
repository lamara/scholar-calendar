package com.example.scholarscraper;

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

    WebView                  webView;
    Context                  context;
    ScholarScraper           scraperInstance;
    EditText                 usernameEdit;
    EditText                 passwordEdit;
    TextView                 textField;
    TextView                 updateNotification;
    Button                   launch;
    Button                   assignment;
    ProgressBar              progressBar;
    /*
     * index is used for keeping track of which courses are to be processed
     * during the update process
     */
    private int              index;
    private List<Course>     courses;
    private PageLoadListener listener;
    private String username;
    private String password;

    public UpdateFragment(List<Course> courses) {
        this.username = username;
        this.password = password;
        this.courses = courses;
    }

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
        textField =           (TextView) myFragmentView.findViewById(R.id.textField);
        updateNotification =  (TextView) myFragmentView.findViewById(R.id.updateNotification);
        launch =                (Button) myFragmentView.findViewById(R.id.launch);
        assignment =            (Button) myFragmentView.findViewById(R.id.assignment);
        progressBar =      (ProgressBar) myFragmentView.findViewById(R.id.progressBar);

        listener = new PageLoadListener();

        updateNotification.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        //final View progressView = inflater.inflate(R.layout.progress_layout, container,
        //                                           false);

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
                    usernameEdit.setVisibility(View.GONE);
                    passwordEdit.setVisibility(View.GONE);
                    textField.setVisibility(View.GONE);

                    launch.setVisibility(View.GONE);
                    assignment.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    updateNotification.setVisibility(View.VISIBLE);
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

        assignment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                assignment.setEnabled(false);
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                new ScholarScraper.AssignmentRetriever().execute(
                    courses,
                    username,
                    password,
                    listener,
                    context);
            }
        });

        context = getActivity();

        return myFragmentView;
    }


    /**
     * Used as a listener to process the onPagedFinished callbacks in the
     * ScholarScraper class. This is necessary to keep the main thread
     * from hanging for too long, and is useful for organizing the update
     * process as a whole.
     */
    public class PageLoadListener
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
            String courseString = "";
            for (Course course : courses)
            {
                courseString += course.toString() + "\n";
                System.out.println(course);
            }
            textField.setText(courseString);
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
            setCancelable(true);
            System.out.println("updateFinished() called");
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.onUpdateFinished(courses);
                System.out.println("courses passed to main activity");
            }
        }

        public void incrementProgress() {

            progressBar.incrementProgressBy(4);

        }
    }
}
