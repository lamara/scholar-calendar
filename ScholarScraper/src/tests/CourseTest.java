package tests;

import static org.junit.Assert.*;
import com.scholarscraper.model.Assignment;
import com.scholarscraper.model.Course;
import com.scholarscraper.model.Quiz;
import com.scholarscraper.model.Task;
import java.text.ParseException;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Unit tests for the Course class
 *
 * @author alawi
 * @version May 5, 2013
 */

public class CourseTest
{
    Task   assignment1;
    Task   assignment2;
    Task   assignment3;
    Task   quiz1;
    Task   quiz2;
    Task   quiz3;
    Course course;


    /**
     * Sets up the test case, adds a few tasks that will be used later
     */
    @Before
    public void setUp()
    {
        try
        {
            quiz1 = new Quiz("quiz 10", "EngE 1024", "2013-Jan-27 04:48 PM");
            quiz2 = new Quiz("quiz 10", "EngE 1024", "2013-Jan-27 04:48 PM");
            quiz3 = new Quiz("quiz 10", "EngE 1024", "2013-Feb-27 05:50 PM");
            assignment1 =
                new Assignment("HW 10", "EngE 1024", "Jul 4, 2013 12:30 am");
            assignment2 =
                new Assignment("HW 10", "EngE 1024", "Jul 4, 2013 12:30 am");
            assignment3 =
                new Assignment("HW 10", "EngE 1024", "Jul 5, 2013 12:30 am");
            course = new Course("EngE 1024", null);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }


    /**
     * Tests adding tasks to a course, should return Task.NOT_ADDED (0) as
     * duplicate tasks are being added, meaning the task has not been added
     */
    @Test
    public void testAddTask()
    {
        course.addTask(quiz1);
        assertEquals(course.addTask(quiz2), Course.NOT_ADDED);
        course.addTask(assignment1);
        assertEquals(course.addTask(assignment2), Course.NOT_ADDED);
    }


    /**
     * Tests adding tasks to a course, should return Task.REPLACED (-1) as the
     * same task is added but with a different due date
     */
    @Test
    public void testAddTask2()
    {
        //replaced logic changed, it now returns the old task's unique ID when a
        //task gets replaced, we can still test this if we TODO add a way to inject
        //a task with our own unique ID (right now it is based off of the system clock).
        /*
        course.addTask(quiz1);
        assertEquals(course.addTask(quiz3), Course.REPLACED);
        course.addTask(assignment1);
        assertEquals(course.addTask(assignment3), Course.REPLACED);
        */
    }

}
