package com.example.scholarscraper;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity
    extends Activity
{
    List<Course> courses;

    private static final String COURSE_FILE_NAME = "courses";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!recoverCourses()) {
            System.out.println("courses not retrieved");
            courses = null;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new UpdateFragment(courses);
        fragment.show(transaction, "update");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCourses();
    }

    /**
     * Callback by the update fragment, passes a list of courses with fully
     * updated assignment info.
     */
    public void onUpdateFinished(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * returns true if courses are successfully saved to internal storage,
     * false if not
     * @throws IOException
     */
    private boolean saveCourses() {
        File file = new File(getFilesDir(), COURSE_FILE_NAME);
        try {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream objectStream= new ObjectOutputStream(fout);
            try {
                if (courses != null) {
                    objectStream.writeObject(courses);
                    System.out.println("courses were saved");
                    return true;
                }
                else {
                    System.out.println("courses were not saved");
                    return false;
                }
            }
            finally {
                objectStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * tries to retrieve the courselist from internal storage, returns true
     * if successful, false if not
     */
    private boolean recoverCourses() {
        File file = new File(getFilesDir(), COURSE_FILE_NAME);
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInput input = new ObjectInputStream (buffer);
            try {
                List<Course> recoveredCourses = (List<Course>)input.readObject();
                System.out.println("courses retrieved");

                for(Course course : recoveredCourses) {
                    System.out.println(course);
                }

                this.courses = recoveredCourses;

                return true;

            }
            finally {
                input.close();
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
