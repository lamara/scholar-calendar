package com.scholarscraper;

/**
 * // -------------------------------------------------------------------------
/**
 *  Stores the id for the intents/notifications
 *
 *  @author Paul Yea
 *  @version May 5, 2013
 */
public class AlarmId
{
    int _id;


    // ----------------------------------------------------------
    /**
     * Create a new AlarmId object.
     */
    public AlarmId()
    {
        // left empty on purporse
    }


    // ----------------------------------------------------------
    /**
     * Create a new AlarmId object.
     * @param id id of notification/intent
     */
    public AlarmId(int id)
    {
        this._id = id;
    }


    // ----------------------------------------------------------
    /**
     * getter method for alarm
     * @return the id
     */
    public int getAlarmId()
    {
        return this._id;
    }


    // ----------------------------------------------------------
    /**
     * setter method for alarm
     * @param id the new value of the id
     */
    public void setId(int id)
    {
        this._id = id;
    }

}
