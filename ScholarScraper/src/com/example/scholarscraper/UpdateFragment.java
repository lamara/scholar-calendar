package com.example.scholarscraper;

import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.SystemClock;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import android.webkit.WebView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


/**
 *  The update fragment executes the course loading process
 *
 *  @author Alex Lamar
 *  @version Apr 9, 2013
 */
public class UpdateFragment extends Fragment {

    WebView webView;
    Context context;
    ScholarScraper scraperInstance;
    EditText usernameEdit;
    EditText passwordEdit;
    TextView textField;
    Button launch;

    /* index is used for keeping track of which courses are to be processed
     * during the update process */
    private int index;
    private List<Course> courses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment, container, false);
        webView = (WebView)myFragmentView.findViewById(R.id.webView1);
        usernameEdit = (EditText) myFragmentView.findViewById(R.id.username);
        passwordEdit = (EditText) myFragmentView.findViewById(R.id.password);
        textField = (TextView) myFragmentView.findViewById(R.id.textField);
        launch = (Button) myFragmentView.findViewById(R.id.launch);

        index = 0;
        courses = null;

        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch.setEnabled(false);
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                try
                {
                    scraperInstance = new ScholarScraper(username, password, null,
                                                        webView, new PageLoadListener());
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
        });


        context = getActivity();
        return myFragmentView;
    }

    public class PageLoadListener {

        public void mainPageLoaded() {
            try {
                scraperInstance.retrieveCourses("Spring 2013");
            }
            catch (WrongLoginException e) {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        public void coursesLoaded() {
            courses = scraperInstance.getCourses();
            String courseString = "";
            for (Course course : courses) {
                courseString += course.toString() + "\n";
                System.out.println(course);
            }
            textField.setText(courseString);
            retrieveCourseLinks();
        }
        public void retrieveCourseLinks() {
            if (courses == null) {
                System.out.println("Courses not loaded yet");
            }
            if (index < courses.size()) {

                Course course = courses.get(index);
                System.out.println("loading: " + course);
                scraperInstance.retrieveAssignmentPages(course);
                index++;
            }
            else {
                for (Course course : courses) {
                    System.out.println(course + ": ");
                    System.out.println("main: " + course.getMainUrl());
                    System.out.println("assign: " + course.getAssignmentUrl());
                    System.out.println("quiz: " + course.getQuizUrl());
                }
            }
        }
    }
}


