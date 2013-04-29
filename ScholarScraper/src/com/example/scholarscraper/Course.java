package com.example.scholarscraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * Holds information about various classes, including their name, Scholar main
 * URL, and Scholar Assignment/Quiz URLs Updating assignment and quiz URLS as
 * well as updating the assignment list is done by outside mutator methods in
 * the ScholarScraper class
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 8, 2013
 */

public class Course implements Serializable
{
    private final String name;
    private final String mainUrl;
    private String       assignmentUrl;
    private String       quizUrl;
    private boolean      hasLoaded;

    private List<Task>   assignments;


    /**
     * Create a new Class object.
     *
     * @param name
     * @param mainURL
     */
    public Course(String name, String mainURL)
    {
        this.name = name;
        this.mainUrl = mainURL;
        this.setAssignmentUrl(null);
        this.setQuizUrl(null);

        hasLoaded = false;

        assignments = new ArrayList<Task>();
    }


    /**
     * Create a new Class object.
     *
     * @param name
     * @param mainURL
     * @param aURL
     * @param qURL
     */
    public Course(String name, String mainUrl, String aUrl, String qUrl)
    {
        this.name = name;
        this.mainUrl = mainUrl;
        this.setAssignmentUrl(aUrl);
        this.setQuizUrl(qUrl);

        hasLoaded = true;
    }


    // ----------------------------------------------------------
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * @return the mainURL
     */
    public String getMainUrl()
    {
        return mainUrl;
    }


    // ----------------------------------------------------------
    /**
     * @return the assignmentURL
     */
    public String getAssignmentUrl()
    {
        return assignmentUrl;
    }


    // ----------------------------------------------------------
    /**
     * @param assignmentUrl
     *            the assignmentUrl to set
     */
    public void setAssignmentUrl(String assignmentUrl)
    {
        this.assignmentUrl = assignmentUrl;
    }


    // ----------------------------------------------------------
    /**
     * @return the quizURL
     */
    public String getQuizUrl()
    {
        return quizUrl;
    }


    // ----------------------------------------------------------
    /**
     * @param quizURL
     *            the quizURL to set
     */
    public void setQuizUrl(String quizUrl)
    {
        this.quizUrl = quizUrl;
    }


    /**
     * Sees if the classes assignment or quiz URLs have been searched for yet
     *
     * @return
     */
    public boolean hasLoaded()
    {
        return hasLoaded();
    }


    /**
     * Indicates that the current object has loaded its assignment or quiz URLs,
     * even if none are present
     */
    public void setLoaded()
    {
        hasLoaded = true;
    }


    /**
     * Returns a string representation of the course
     */
    @Override
    public String toString()
    {
        return name;
    }


    /**
     * Adds an task to the internal assignment list. Because tasks
     * can possibly change in either status or due date, addTask will
     * check if similar tasks (similar being two tasks that share
     * the same name) have different due dates or status, and if so, will
     * replace the old task with the new, updated task.
     *
     * @param task
     *            The task to add to the task list
     * @return true if the task was added, false if it was rejected (due
     *         to duplication)
     */
    public boolean addTask(Task task)
    {
        for (int i = 0; i < assignments.size(); i++)
        {
            Task cmpr = assignments.get(i);
            if (cmpr.getName().equals(task.getName())
                && !cmpr.equals(task))
            {
                assignments.set(i, task);
                return true;
            }
            else if (cmpr.equals(task))
            {
                return false;
            }
        }
        assignments.add(task);
        System.out.println(task.getName() + " added to " + this);
        return true;
    }

}
