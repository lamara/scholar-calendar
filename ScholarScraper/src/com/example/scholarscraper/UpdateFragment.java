package com.example.scholarscraper;

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
    String username;
    String password;
    Button launch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment, container, false);
        webView = (WebView)myFragmentView.findViewById(R.id.webView1);
        usernameEdit = (EditText) myFragmentView.findViewById(R.id.username);
        passwordEdit = (EditText) myFragmentView.findViewById(R.id.password);
        launch = (Button) myFragmentView.findViewById(R.id.launch);
        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
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

    @Override
    public void onStart() {
            /* usernames and passwords will be handled externally, but in the
             * meantime they need to be entered manually here */
            super.onStart();
    }

    public void onLaunch() {

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
            List<Course> courses = scraperInstance.getCourses();
            for (Course course : courses) {
                System.out.println(course);
            }
            //scraperInstance.retrieveAssignmentPages(courses.get(0));
        }
    }
}


