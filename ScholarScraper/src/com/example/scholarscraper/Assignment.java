package com.example.scholarscraper;

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
}
