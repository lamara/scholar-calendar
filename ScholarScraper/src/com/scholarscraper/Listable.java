package com.scholarscraper;

import java.util.Calendar;
import java.util.Map;

/**
 *  Used to populate the central ListView with attributes. Necessary as objects of
 *  different types can make their way into the list view, such as date separators.
 *
 *  @author Alex
 *  @version Aug 10, 2013
 */

public interface Listable
{
    public Map<String, String> getAttributes();

    public Calendar getDueDate();
}
