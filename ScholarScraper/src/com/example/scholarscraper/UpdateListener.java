package com.example.scholarscraper;

import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *
 *
 *  @author Alex
 *  @version Apr 28, 2013
 */

public abstract class UpdateListener
{
    public abstract void mainPageLoaded(boolean result);

    public abstract void coursesLoaded();

    public abstract void retrieveCourseLinks();

    public abstract void retrieveAssignments(List<Course> courses);

    public abstract void updateFinished();

    public abstract void incrementProgress();
}
