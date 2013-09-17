package tests;

import static org.junit.Assert.*;
import com.scholarscraper.Quiz;
import java.text.ParseException;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 *  Unit tests for the Quiz class
 *
 *  @author Alex Lamar, Paul Yea, Brianna Beitzel
 *  @version Apr 20, 2013
 */

public class QuizTest
{
    public QuizTest() {
        //Empty test constructor
    }

    @Before
    public void setUp() {
        //Empty test setup
    }

    /**
     * Tests parsing a date of the format "2013-Jan-27 04:48 PM"
     * @throws ParseException
     */
    @Test
    public void testParseDate() throws ParseException {
        String date = "2013-Jan-27 04:48 PM";
        Calendar c = new Quiz(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 48);
        assertEquals(c.get(Calendar.HOUR), 4);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 16);
        assertEquals(c.get(Calendar.DATE), 27);
        assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }
    /**
     * Tests parsing a date of the format "2011-Mar-08 12:32 PM"
     * @throws ParseException
     */
    @Test
    public void testParseDate2() throws ParseException {
        String date = "2011-Mar-08 12:32 PM";
        Calendar c = new Quiz(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 32);
        assertEquals(c.get(Calendar.HOUR), 0);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 12);
        assertEquals(c.get(Calendar.DATE), 8);
        assertEquals(c.get(Calendar.MONTH), Calendar.MARCH);
        assertEquals(c.get(Calendar.YEAR), 2011);
    }
    /**
     * Tests parsing a date of the format "2013-Feb-17 09:14 PM"
     * @throws ParseException
     */
    @Test
    public void testParseDate3() throws ParseException {
        String date = "2009-Feb-17 09:14 PM";
        Calendar c = new Quiz(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 14);
        assertEquals(c.get(Calendar.HOUR), 9);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 21);
        assertEquals(c.get(Calendar.DATE), 17);
        assertEquals(c.get(Calendar.MONTH), Calendar.FEBRUARY);
        assertEquals(c.get(Calendar.YEAR), 2009);
    }
    /**
     * Tests parsing a date of the format "2013-Dec-01 12:00 AM"
     * @throws ParseException
     */
    @Test
    public void testParseDate4() throws ParseException {
        String date = "2013-Dec-01 12:00 AM";
        Calendar c = new Quiz(null, null, date).getDueDate();
        assertEquals(c.get(Calendar.MINUTE), 0);
        assertEquals(c.get(Calendar.HOUR), 0);
        assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(c.get(Calendar.DATE), 1);
        assertEquals(c.get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(c.get(Calendar.YEAR), 2013);
    }
}
