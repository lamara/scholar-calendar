package com.scholarscraper.update;

import com.scholarscraper.MainActivity;
import java.io.ObjectInput;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import android.content.Context;
import com.scholarscraper.model.Course;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataManager
{
    private static final String  USER_FILE_NAME    = "userData";
    private static final String  COURSE_FILE_NAME  = "courses";

    /**
     * returns true if courses are successfully saved to internal storage, false
     * if not
     *
     * @throws IOException
     */
    public static synchronized boolean saveCourses(List<Course> courses, Context context) {
        File file = new File(context.getFilesDir(), COURSE_FILE_NAME);
        try
        {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(fout);
            try
            {
                if (courses != null)
                {
                    objectStream.writeObject(courses);
                    System.out.println("courses were saved");
                    return true;
                }
                else
                {
                    System.out.println("courses were not saved");
                    return false;
                }
            }
            finally
            {
                objectStream.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    public static synchronized boolean saveUsernamePassword(String username, String password, Context context)
    {
        if (username == null || password == null)
        {
            System.out.println("username/password were not saved (null)");
            return false;
        }
        File file = new File(context.getFilesDir(), USER_FILE_NAME);
        try
        {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream stream = new ObjectOutputStream(fout);
            try
            {

                stream.writeObject(username);
                stream.writeObject(password);
                System.out.println("username/password were saved");
                return true;
            }
            finally
            {
                stream.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * tries to retrieve the courselist from internal storage, returns true if
     * successful, false if not
     */
    public static synchronized List<Course> recoverCourses(Context context)
    {
        File file = new File(context.getFilesDir(), COURSE_FILE_NAME);
        try
        {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInput input = new ObjectInputStream(buffer);
            try
            {
                List<Course> recoveredCourses =
                    (List<Course>)input.readObject();
                System.out.println("courses retrieved");

                for (Course course : recoveredCourses)
                {
                    System.out.println(course);
                }
                return recoveredCourses;
            }
            finally
            {
                input.close();
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the user's username and password from internal storage
     */
    public static synchronized SimpleEntry<String, String> recoverUsernamePassword(Context context)
    {
        File file = new File(context.getFilesDir(), USER_FILE_NAME);
        try
        {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInputStream input = new ObjectInputStream(buffer);
            try
            {
                String username = (String)input.readObject();
                String password = (String)input.readObject();
                if (username != null && password != null)
                {
                    System.out.println("Username/password retrieved");
                    return new AbstractMap.SimpleEntry<String, String>(username, password);
                }
                System.out.println("Username/password not retrieved");
                return null;
            }
            finally
            {
                input.close();
            }
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Destroys internal data state, effectively resetting the state of the app
     */
    public static synchronized void destroyData(Context context)
    {
        File courseFile = new File(context.getFilesDir(), COURSE_FILE_NAME);
        File userFile = new File(context.getFilesDir(), USER_FILE_NAME);
        courseFile.delete();
        userFile.delete();
    }
}
