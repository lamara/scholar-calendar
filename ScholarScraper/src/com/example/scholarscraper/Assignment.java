package com.example.scholarscraper;

import java.util.Date;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 6, 2013
 */
public class Assignment
{
    private String name;
    private String description;
    private String dateAssigned;
    private String dateDue;

// ----------------------------------------------------------
    /**
     * Create a new Assignment object.
     *
     * @param name
     *            name of the assignment
     * @param description
     *            description of the assignment
     * @param dateAssigned
     *            the date assignment was assigned
     * @param dateDue
     *            the date assignent is due
     */
    public Assignment(
        String name,
        String description,
        String dateAssigned,
        String dateDue)
    {
        this.name = name;
        this.description = description;
        this.dateAssigned = dateAssigned;
        this.dateDue = dateDue;
    }

    /**
     * Returns a Date object based on a String date of the
     * format "Jan 31, 2013 1:45 pm"
     */
    @SuppressWarnings("deprecation")
    private static Date parse(String date) {

        new Date(4, 4, 4);

        return null;

    }


    // ----------------------------------------------------------
    /**
     * returns the name of the assignment
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }


    // ----------------------------------------------------------
    /**
     * returns the description fo the assignment
     *
     * @return description
     */
    public String getDescription()
    {
        return description;
    }


    // ----------------------------------------------------------
    /**
     * returns the date and time of when assignment was assigned
     *
     * @return dateAssigned
     */
    public String getDateAssigned()
    {
        return dateAssigned;
    }


    // ----------------------------------------------------------
    /**
     * returns the date and time of when assignment is due
     *
     * @return dateDue
     */
    public String getDateDue()
    {
        return dateDue;
    }


// ----------------------------------------------------------
    /**
     * setter method for name
     *
     * @param newName
     *            new value of name
     */
    public void setName(String newName)
    {
        name = newName;
    }


    // ----------------------------------------------------------
    /**
     * setter method for description
     *
     * @param newDescription
     *            new value of description
     */
    public void setDescription(String newDescription)
    {
        description = newDescription;
    }


    // ----------------------------------------------------------
    /**
     * setter method for dateAssigned
     *
     * @param newTime
     *            new value of dateAssignedF
     */
    public void setDateAssigned(String newTime)
    {
        dateAssigned = newTime;
    }


    // ----------------------------------------------------------
    /**
     * setter method for dateDue
     *
     * @param newTime
     *            new value of dateDue
     */
    public void setDateDue(String newTime)
    {
        dateDue = newTime;
    }

    /**
     * Compares two assignment objects for equality
     */
    @Override
    public boolean equals(Object assignment) {
        if (assignment instanceof Assignment) {
            Assignment cmpr = (Assignment) assignment;
            if (this.getDateDue().equals(cmpr.getDateDue()) &&
                this.getName().equals(cmpr.getName())      &&
                this.getDescription().equals(cmpr.getDescription())) {
                return true;
            }
        }
        return false;
    }
}
