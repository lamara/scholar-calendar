package com.scholarscraper.listview;

import java.util.Calendar;

/**
 * Used to populate the central ListView with attributes. Necessary as objects
 * of different types can make their way into the list view, such as date
 * separators.
 * 
 * @author Alex
 * @version Aug 10, 2013
 */

public interface Listable
{
    public Calendar getDueDate();
}
