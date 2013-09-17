package com.scholarscraper;

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

    private List<Task>   assignments;

    public static final int REPLACED = -1;
    public static final int NOT_ADDED = 0;
    public static final int ADDED = 1;


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
     * Place a description of your method here.
     * @return assignment list
     */
    public List<Task> getAssignments()
    {
        return assignments;
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
    public int addTask(Task task)
    {
        for (int i = 0; i < assignments.size(); i++)
        {
            Task cmpr = assignments.get(i);
            if (cmpr.getName().equals(task.getName())
                && !cmpr.equals(task))
            {
                assignments.set(i, task);
                System.out.println(task.getName() + " replaced and added to " + this);
                return REPLACED;
            }
            else if (cmpr.equals(task))
            {
                System.out.println(task.getName() + " not added, already present");
                return NOT_ADDED;
            }
        }
        assignments.add(task);
        System.out.println(task.getName() + " added to " + this);
        return ADDED;
    }

}
