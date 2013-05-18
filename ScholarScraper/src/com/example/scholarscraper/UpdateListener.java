package com.example.scholarscraper;

import com.example.casexample.exceptions.WrongLoginException;
import java.io.IOException;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *  A listener used by the ScholarScraper class. Implementations of this interface
 *  should be embedded in the activity or service class that requires its
 *  functionality, providing a bridge from the ScholarScraper class
 *  to that specific activity or service
 *
 *  @author Alex Lamar
 *  @version Apr 28, 2013
 */

public interface UpdateListener
{
    public abstract void mainPageLoaded(boolean result);

    public abstract void coursesLoaded();

    public abstract void retrieveCourseLinks();

    public abstract void retrieveAssignments(List<Course> courses);

    public abstract void updateFinished();

    public abstract void incrementProgress();
}
