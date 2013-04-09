package com.example.scholarscraper;

import java.util.List;
import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 *  Holds information about various classes, including their name, Scholar main
 *  URL, and Scholar Assignment/Quiz URLs
 *
 *  Updating assignment and quiz URLS as well as updating the assignment list
 *  is done by outside mutator methods in the ScholarScraper class
 *
 *  @author Alex Lamar
 *  @version Apr 8, 2013
 */

public class Course
{
    private final String name;
    private final String mainURL;
    private String assignmentURL;
    private String quizURL;
    private boolean hasLoaded;

    private List<Object> assignments;

    /**
     * Create a new Class object.
     * @param name
     * @param mainURL
     */
    public Course(String name, String mainURL) {
        this.name = name;
        this.mainURL = mainURL;
        this.setAssignmentURL(null);
        this.setQuizURL(null);

        hasLoaded = false;

        assignments = new ArrayList<Object>();
    }
    /**
     * Create a new Class object.
     * @param name
     * @param mainURL
     * @param aURL
     * @param qURL
     */
    public Course (String name, String mainURL, String aURL, String qURL) {
        this.name = name;
        this.mainURL = mainURL;
        this.setAssignmentURL(aURL);
        this.setQuizURL(qURL);

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
    public String getMainURL()
    {
        return mainURL;
    }
    // ----------------------------------------------------------
    /**
     * @return the assignmentURL
     */
    public String getAssignmentURL()
    {
        return assignmentURL;
    }
    // ----------------------------------------------------------
    /**
     * @param assignmentURL the assignmentURL to set
     */
    public void setAssignmentURL(String assignmentURL)
    {
        this.assignmentURL = assignmentURL;
    }
    // ----------------------------------------------------------
    /**
     * @return the quizURL
     */
    public String getQuizURL()
    {
        return quizURL;
    }
    // ----------------------------------------------------------
    /**
     * @param quizURL the quizURL to set
     */
    public void setQuizURL(String quizURL)
    {
        this.quizURL = quizURL;
    }
    /**
     * Sees if the classes assignment or quiz URLs have been searched for yet
     * @return
     */
    public boolean hasLoaded() {
        return hasLoaded();
    }
    /**
     * Indicates that the current object has loaded its assignment or quiz URLs,
     * even if none are present
     */
    public void setLoaded() {
        hasLoaded = true;
    }

    /**
     * Returns a string representation of the course
     */
    public String toString() {
        return name;
    }

}
