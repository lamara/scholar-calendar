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
 *  @author Paul Yea
 *  @author Brianna Beitzel
 *  @version Apr 8, 2013
 */

public class Course
{
    private final String name;
    private final String mainUrl;
    private String assignmentUrl;
    private String quizUrl;
    private boolean hasLoaded;

    private List<Assignment> assignments;


    /**
     * Create a new Class object.
     * @param name
     * @param mainURL
     */
    public Course(String name, String mainURL) {
        this.name = name;
        this.mainUrl = mainURL;
        this.setAssignmentUrl(null);
        this.setQuizUrl(null);

        hasLoaded = false;

        assignments = new ArrayList<Assignment>();
    }
    /**
     * Create a new Class object.
     * @param name
     * @param mainURL
     * @param aURL
     * @param qURL
     */
    public Course (String name, String mainUrl, String aUrl, String qUrl) {
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
     * @param assignmentUrl the assignmentUrl to set
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
     * @param quizURL the quizURL to set
     */
    public void setQuizUrl(String quizUrl)
    {
        this.quizUrl = quizUrl;
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
    /**
     * adds an assignment to the internal assignment list
     */
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

}
