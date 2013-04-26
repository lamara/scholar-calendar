package com.example.scholarscraper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    extends Fragment
{

    WebView                  webView;
    Context                  context;
    ScholarScraper           scraperInstance;
    String                   username;
    String                   password;
    EditText                 usernameEdit;
    EditText                 passwordEdit;
    TextView                 textField;
    Button                   launch;
    Button                   assignment;

    /*
     * index is used for keeping track of which courses are to be processed
     * during the update process
     */
    private int              index;
    private List<Course>     courses;
    private PageLoadListener listener;


    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        View myFragmentView =
            inflater.inflate(R.layout.fragment, container, false);
        webView = (WebView)myFragmentView.findViewById(R.id.webView1);
        usernameEdit = (EditText)myFragmentView.findViewById(R.id.username);
        passwordEdit = (EditText)myFragmentView.findViewById(R.id.password);
        textField = (TextView)myFragmentView.findViewById(R.id.textField);
        launch = (Button)myFragmentView.findViewById(R.id.launch);
        assignment = (Button)myFragmentView.findViewById(R.id.assignment);
        listener = new PageLoadListener();

        index = 0;
        courses = null;

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
                    scraperInstance.retrieveCourses("Spring 2013");
                }
                else
                {
                    return;
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
            new ScholarScraper.AssignmentRetriever().execute(
                courses,
                username,
                password,
                listener,
                context);
        }


        public void updateFinished()
        {
            // do stuff on update finish, like saving the update fragment's
            // courselist
            System.out.println("updateFinished() called");
        }
    }
}
