package tests;

import com.example.scholarscraper.Assignment;
import com.example.scholarscraper.Course;
import com.example.scholarscraper.ImageAdapter;
import java.util.GregorianCalendar;
import android.app.Activity;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Unit tests for ImageAdapter. Ensures that ImageAdapter's methods work as
 * expected and create anticipated output.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version May 4, 2013
 */
public class ImageAdapterTest
{
    private ImageAdapter adapter;
    private List<Course> courses;
    private Course       c1 = new Course("TestCourse", null);
    private Assignment   a1 = new Assignment(
                                "Test Assignment 1",
                                "I'm a description",
                                new GregorianCalendar(2013, 4, 15));
    private Assignment   a2 = new Assignment(
                                "Test Assignment 2",
                                "I'm a description",
                                new GregorianCalendar(2012, 4, 15));
    private Assignment   a3 = new Assignment(
                                "Test Assignment 3",
                                "I'm a description",
                                new GregorianCalendar(2013, 0, 15));
    Activity             a  = new Activity();


    // ----------------------------------------------------------
    /**
     * Create a new ImageAdapterTest object.
     */
    public ImageAdapterTest()
    {
        // Empty constructor
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    public void setUp()
    {
        c1.addTask(a1);
        c1.addTask(a2);
        c1.addTask(a3);
        courses.add(c1);
        adapter = new ImageAdapter(a.getBaseContext(), 4, 2013, courses);
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    public void testView()
    {
        assertEquals(35, adapter.getCount());
        assertEquals("1", adapter.getDayString(3));
        assertEquals(2, adapter.getItem(4));

    }
}
