package com.scholarscraper;

import java.util.Calendar;
import java.util.Comparator;

public class TaskListComparator implements Comparator<Listable>
{
    public int compare(Listable arg0, Listable arg1)
    {
        //Elements in the listview are ordered by the natural ordering of their due dates
        Calendar dueDate = arg0.getDueDate();
        Calendar cmprDueDate = arg1.getDueDate();
        return dueDate.compareTo(cmprDueDate);
    }
}
