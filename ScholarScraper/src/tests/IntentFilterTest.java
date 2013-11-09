package tests;

import static org.junit.Assert.*;
import com.scholarscraper.model.Task;
import java.net.URISyntaxException;
import java.net.URI;
import org.junit.Before;
import org.junit.Test;


public class IntentFilterTest
{
    URI uri;
    String uniqueId;

    @Before
    public void setUp() throws URISyntaxException
    {
        System.out.println("setting up URI");
        uniqueId = Long.toString(System.currentTimeMillis());
        uri = new URI("android.resource://com.scholarscraper/alarmId/?" + uniqueId);
    }

    @Test
    public void testUri() {
        System.out.println("Hello");
        assertEquals(1, 1);
        System.out.println(uri);
        System.out.println(uri.getQuery());
        System.out.println(Task.class.getName());
    }
}
