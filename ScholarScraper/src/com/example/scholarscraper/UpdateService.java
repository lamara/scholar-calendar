package com.example.scholarscraper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.content.Context;
import java.util.List;
import android.os.IBinder;
import android.content.Intent;
import android.app.Service;

public class UpdateService extends Service  {
    private List<Course> courses;
    private Context context;
    private UpdateListener listener;
    private String username;
    private String password;
    private final String COURSE_FILE_NAME = "courses";
    private final String USER_FILE_NAME = "userData";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recoverCourses();
        recoverUsernamePassword();
        listener = new ServiceUpdateListener();
        context = this;

        if (courses == null || username == null || password == null) {
            System.out.println("service not started (null data)");
            return Service.START_NOT_STICKY;
        }
        new ScholarScraper.AssignmentRetriever().execute(
            courses,
            username,
            password,
            listener,
            context);
        return Service.START_NOT_STICKY;
    }


    /**
     * service update listener will call this when update process is finished
     */
    public void onUpdateFinished() {
        saveCourses();
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

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean recoverCourses() {
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

    /**
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword() {
        System.out.println(getFilesDir() + " update service");
        File file = new File(getFilesDir(), USER_FILE_NAME);
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInputStream input = new ObjectInputStream (buffer);
            try {
                username = (String) input.readObject();
                password = (String) input.readObject();
                if (username != null && password != null) {
                    System.out.println("Username/password retrieved");
                    return true;
                }
                System.out.println("Username/password not retrieved");
                return false;
            }
            finally {
                input.close();
            }
        }
        catch (ClassNotFoundException e) {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
    }

    // -------------------------------------------------------------------------
    /**
     *  Used to execute the second portion of the update process (retrieving
     *  assignments from an already retrieved course list)
     *
     *  @author Alex Lamar
     *  @version Apr 28, 2013
     */
    public class ServiceUpdateListener extends UpdateListener {

        @Override
        public void mainPageLoaded(boolean result) {
            //Empty as we don't need the main page for this portion of the update
        }

        @Override
        public void coursesLoaded() {
            //Empty as course list is already loaded
        }

        @Override
        public void retrieveCourseLinks() {
            //Empty as course links should already be retrieved
        }

        @Override
        public void retrieveAssignments(List<Course> courses) {
            //Empty, unneccessary
        }

        /**
         * Callback from the scholar scraper, signifies end of update process
         */
        @Override
        public void updateFinished()
        {
            onUpdateFinished();
        }

        @Override
        public void incrementProgress()
        {
            //Empty as service does not implement the progress bar
        }
    }
  }