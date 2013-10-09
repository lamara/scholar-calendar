package com.scholarscraper;

import java.util.Calendar;
import java.util.Comparator;

/**
 * // -------------------------------------------------------------------------
/**
 *  Used to compare two elements in a list view by due date.
 *  Due date checks are null safe (null due dates will always be
 *  greater than non-null due dates).
 *
 *  @author Alex
 *  @version Sep 29, 2013
 */
public class TaskListComparator implements Comparator<Listable>
{
    public int compare(Listable arg0, Listable arg1)
    {
        //Elements in the listview are ordered by the natural ordering of their due dates
        Calendar dueDate = arg0.getDueDate();
        Calendar cmprDueDate = arg1.getDueDate();
        if (dueDate == null && cmprDueDate != null) {
            return 1;
        }
        else if (dueDate != null && cmprDueDate == null) {
            return -1;
        }
        else if (dueDate == null && cmprDueDate == null) {
            return 0;
        }
        return dueDate.compareTo(cmprDueDate);
    }
}
