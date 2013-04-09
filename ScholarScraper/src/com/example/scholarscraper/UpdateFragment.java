package com.example.scholarscraper;

import android.content.Context;
import android.os.SystemClock;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import android.webkit.WebView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.Fragment;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment, container, false);
        webView = new WebView(getActivity());
        context = getActivity();
        return myFragmentView;
    }

    @Override
    public void onStart() {
            /* usernames and passwords will be handled externally, but in the
             * meantime they need to be entered manually here */
            super.onStart();
            String username = "";
            String password = "";
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
            for (Course course : scraperInstance.getCourses()) {
                System.out.println(course);
            }
        }
    }
}


