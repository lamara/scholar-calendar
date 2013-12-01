package com.scholarscraper.listview;

import com.scholarscraper.separators.GenericSeparator;
import com.scholarscraper.model.Task;
import com.scholarscraper.R;
import com.scholarscraper.R.id;
import com.scholarscraper.R.layout;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.scholarscraper.separators.DateSeparator;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alex on 9/22/13.
 */
public class AssignmentAdapter
    extends ArrayAdapter<Listable>
{

    private Context          context;
    private Listable[]       values;

    /* length that assignment strings should be truncated to */
    final static private int ASSIGNMENT_TEXT_LENGTH = 29;

    final static String GRAY = "#909090";


    public AssignmentAdapter(Context context, Listable[] values)
    {
        super(context, R.layout.list_row, values);

        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Listable element = values[position];

        View view = null;
        if (element instanceof DateSeparator)
        {
            DateSeparator separator = (DateSeparator)element;
            view = populateSeparatorView(separator, inflater, parent);
        }

        else if (element instanceof Task)
        {
            Task task = (Task)element;
            view = populateAssignmentView(task, inflater, parent);
        }
        return view;
    }


    private View populateSeparatorView(
        DateSeparator separator,
        LayoutInflater inflater,
        ViewGroup parent)
    {
        View separatorView =
            inflater.inflate(R.layout.separator_row, parent, false);

        TextView separatorText =
            (TextView)separatorView.findViewById(R.id.separatorText);
        separatorText.setText(separator.toString());

        return separatorView;
    }


    private View populateAssignmentView(
        Task task,
        LayoutInflater inflater,
        ViewGroup parent)
    {
        long currentTime = System.currentTimeMillis();
        View assignmentView =
            inflater.inflate(R.layout.list_row, parent, false);

        TextView assignmentName =
            (TextView)assignmentView.findViewById(R.id.assignmentName);
        TextView courseName =
            (TextView)assignmentView.findViewById(R.id.courseName);
        TextView timeDue = (TextView)assignmentView.findViewById(R.id.dueDate);

        String assignmentText = getFormattedAssignmentText(task);
        assignmentName.setText(assignmentText);

        courseName.setText(task.getCourseName());
        String dueDate = getFormattedStringFromCalendar(task.getDueDate());
        timeDue.setText(dueDate);
        if (task.getDueDate() != null
            && task.getDueDate().getTimeInMillis() < currentTime)
        {
            grayOutAssignmentView(assignmentName, courseName, timeDue);
        }

        return assignmentView;
    }

    /**
     * If an assignment is past its due date then we want to gray its elements
     * out
     */
    private void grayOutAssignmentView(
        TextView assignmentName,
        TextView courseName,
        TextView timeDue)
    {
        int gray = Color.parseColor(GRAY);
        assignmentName.setTextColor(gray);
        courseName.setTextColor(gray);
        timeDue.setTextColor(gray);
    }


    private String getFormattedAssignmentText(Task task)
    {
        String originalText = task.getName();
        if (originalText.length() <= ASSIGNMENT_TEXT_LENGTH)
        {
            return originalText;
        }
        String truncatedText =
            originalText.substring(0, ASSIGNMENT_TEXT_LENGTH);
        return truncatedText + "...";
    }


    private String getFormattedStringFromCalendar(Calendar calendar)
    {
        if (calendar == null)
        {
            return "n/a";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d");
        String date = formatter.format(calendar.getTime());
        formatter = new SimpleDateFormat("h:mm a");
        String time = formatter.format(calendar.getTime());
        return date + "\n" + time;
    }

}
