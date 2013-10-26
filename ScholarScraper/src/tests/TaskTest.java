package tests;

import static org.junit.Assert.*;
import com.scholarscraper.Assignment;
import com.scholarscraper.Quiz;
import com.scholarscraper.Task;
import java.text.ParseException;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Unit tests for the Task class
 * 
 * @author Alex Lamar
 * @version May 5, 2013
 */
public class TaskTest
{
    Task assignment1;
    Task assignment2;
    Task assignment3;
    Task quiz1;
    Task quiz2;
    Task quiz3;


    /**
     * Sets up test cases, creates some assignments and quizes to be used later
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
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Tests two tasks for equality, should return true as they have the same
     * due date, name, and description
     */
    @Test
    public void testEquals()
    {
        assertEquals(quiz1, quiz2);
        assertEquals(assignment1, assignment2);
    }


    /**
     * Tests two tasks for equality, should return false as they have different
     * due dates
     */
    @Test
    public void testEquals2()
    {
        assertFalse(quiz1.equals(quiz3));
        assertFalse(assignment1.equals(assignment3));
        assertFalse(quiz1.equals(assignment1));
    }


    /**
     * Tests mutating a calendar object that was returned from a task, making
     * sure that doing that doesn't affect the calendar object field inside the
     * object
     */
    @Test
    public void testGetCalendar()
    {
        Calendar c = assignment1.getDueDate();
        c.set(Calendar.HOUR, 3);
        c.set(Calendar.DATE, 16);
        assertEquals(assignment1, assignment2);
    }
}
