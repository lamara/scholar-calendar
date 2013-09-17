package tests;

import static org.junit.Assert.*;
import com.scholarscraper.Assignment;
import java.text.ParseException;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Unit tests for the Assignment class
 *
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version Apr 20, 2013
 */

public class AssignmentTest
{
    public AssignmentTest()
    {
        // Empty test constructor
    }


    @Before
    public void setUp()
    {
        // Empty test set up
    }


    /**
     * Tests parsing a date of the format "Apr 23, 2013 11:55 pm" into a
     * calendar object
     *
     * @throws ParseException
     */
    @Test
    public void testParseDate()
        throws ParseException
    {
        String date = "Apr 23, 2013 11:55 pm";
        Calendar c = new Assignment(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 55);
        assertEquals(c.get(Calendar.HOUR), 11);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 23);
        assertEquals(c.get(Calendar.DATE), 23);
        assertEquals(c.get(Calendar.MONTH), Calendar.APRIL);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }


    /**
     * Tests parsing a date of the format "Jan 25, 2013 6:00 pm" into a calendar
     * object
     *
     * @throws ParseException
     */
    @Test
    public void testParseDate2()
        throws ParseException
    {
        String date = "Jan 25, 2013 6:00 pm";
        Calendar c = new Assignment(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 0);
        assertEquals(c.get(Calendar.HOUR), 6);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 18);
        assertEquals(c.get(Calendar.DATE), 25);
        assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }
    /**
     * Tests parsing a date of the format "Aug 25, 2013 12:00 pm" into a calendar
     * object
     *
     * @throws ParseException
     */
    @Test
    public void testParseDate3()
        throws ParseException
    {
        String date = "Aug 25, 2013 12:00 pm";
        Calendar c = new Assignment(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 0);
        assertEquals(c.get(Calendar.HOUR), 0);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 12);
        assertEquals(c.get(Calendar.DATE), 25);
        assertEquals(c.get(Calendar.MONTH), Calendar.AUGUST);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }
    /**
     * Tests parsing a date of the format "Jul 4, 2013 12:30 am" into a calendar
     * object
     *
     * @throws ParseException
     */
    @Test
    public void testParseDate4()
        throws ParseException
    {
        String date = "Jul 4, 2013 12:30 am";
        Calendar c = new Assignment(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 30);
        assertEquals(c.get(Calendar.HOUR), 0);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(c.get(Calendar.DATE), 4);
        assertEquals(c.get(Calendar.MONTH), Calendar.JULY);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }
    /**
     * Tests parsing a date of the format "Apr 18, 2013 2:00 am" into a calendar
     * object
     *
     * @throws ParseException
     */
    @Test
    public void testParseDate5()
        throws ParseException
    {
        String date = "Apr 18, 2013 2:00 am";
        Calendar c = new Assignment(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 0);
        assertEquals(c.get(Calendar.HOUR), 2);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 2);
        assertEquals(c.get(Calendar.DATE), 18);
        assertEquals(c.get(Calendar.MONTH), Calendar.APRIL);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }


}
