package com.scholarscraper;

import com.scholarscraper.model.Course;
import java.util.List;
import android.content.Context;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import java.text.ParseException;

/**
// -------------------------------------------------------------------------
/**
 *  This implementation makes a dramatically smaller amount of http calls when
 *  polling Scholar as it makes use of already stored data retrieved from the
 *  regular ScholarScraper implementation. This implentation's downside, however,
 *  is that it will not detect any changes to the user's courselist that have happened
 *  since the last time the regular ScholarScraper was run. Because of this, it is
 *  important to run the regular ScholarScraper when speed is not
 *  an issue.
 *
 *  Call the light version of the ScholarScraper with the params (String username,
 *  String password, List<Course> courseList, Context context).
 *
 *  @author Alex
 *  @version Nov 19, 2013
 */
public class LightScholarScraper extends ScholarScraper
{
    //TODO there isn't a timeout currently, if the user's internet is slow or
    //something causes the update to hang then the task may not finish updating.
    //add a timeout somewhere between 30-60 seconds
    //TODO error handling is weird at the moment, use a system that makes more sense
    @Override
    protected Integer doInBackground(Object... params)
    {
        String username = (String)params[0];
        String password = (String)params[1];
        courses = (List<Course>)params[2];
        context = (Context)params[3];
        try
        {
            loginToScholar(username, password);
            retrieveTasks(courses);
            if (isCancelled()) {
                return CANCELLED;
            }
        }
        catch (WrongLoginException e)
        {
            System.out.println("login failed");
            exception = e; //exception gets passed through onCancelled
            cancel(true); // calls onCancelled, a cleaner way to exit
            return WRONG_LOGIN;
        }
        catch (IOException e)
        {
            exception = e;
            cancel(true);
            return IO_ERROR;
        }
        catch (ParseException e)
        {
            exception = e;
            cancel(true);
            return ERROR;
        }
        //This will call onPostExecute() from the base class, which handles
        //saving the courselist state.
        return SUCCESSFUL;
    }

    @Override
    public void retrieveTasks(List<Course> courses) throws IOException, ParseException
    {
        for (Course course : courses) {
            String quizPortletUrl = course.getQuizPortletUrl();
            if (quizPortletUrl != null) {
                getQuizzesFromPortlet(course, quizPortletUrl);
            }
            String assignmentPortletUrl = course.getAssignmentPortletUrl();
            if (assignmentPortletUrl != null) {
                getAssignmentsFromPortlet(course, assignmentPortletUrl);
            }
        }
    }
}
