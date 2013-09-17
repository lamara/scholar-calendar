package tests;

import com.scholarscraper.AlarmId;
import junit.framework.TestCase;

// -------------------------------------------------------------------------
/**
 * test alarm id
 *
 * @author Paul Yea
 * @version May 5, 2013
 */
public class AlarmIdTest
    extends TestCase
{
    private AlarmId test;


    // ----------------------------------------------------------
    /**
     * Create a new AlarmIdTest object.
     */
    public AlarmIdTest()
    {
        // intentionaly left blank
    }


    public void setUp()
    {
        test = new AlarmId(45);
    }


    // ----------------------------------------------------------
    /**
     * test getAlarmId
     */
    public void testGetAlarmId()
    {
        assertEquals(45, test.getAlarmId());
    }


    // ----------------------------------------------------------
    /**
     * test setId
     */
    public void testSetId()
    {
        test.setId(34);
        assertEquals(34, test.getAlarmId());
    }

}
