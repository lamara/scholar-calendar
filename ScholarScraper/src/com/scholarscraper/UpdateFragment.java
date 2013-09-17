package com.scholarscraper;

import com.scholarscraper.R;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;

/**
* The update fragment executes the course loading process
*
* @author Alex Lamar
* @version Apr 15, 2013
*/
public class UpdateFragment
    extends DialogFragment
{
    EditText usernameEdit;
    EditText passwordEdit;
    TextView prompt;
    Button launch;

    private List<Course> courses;
    private String username;
    private String password;

    public static final String DEFAULT_PROMPT = "Please enter a valid Scholar username and password";

    final private static String USER_FILE_NAME = "userData";
    final private static String COURSE_FILE_NAME = "courses";

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        final View myFragmentView =
            inflater.inflate(R.layout.fragment, container, false);

        Bundle arguments = getArguments();

        usernameEdit = (EditText) myFragmentView.findViewById(R.id.username);
        passwordEdit = (EditText) myFragmentView.findViewById(R.id.password);
        prompt = (TextView) myFragmentView.findViewById(R.id.prompt);
        launch = (Button) myFragmentView.findViewById(R.id.launch);

        //populate prompt textview
        if (arguments != null) {
            String promptArg = arguments.getString("prompt");
            prompt.setText(promptArg != null ? promptArg : DEFAULT_PROMPT);
        }

        //populate username/password fields, if they exist
        if (recoverUsernamePassword()) {
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }

        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                saveUsernamePassword();
                new ScholarScraper().execute(username, password, getActivity());
                dismiss();
            }
        });

        return myFragmentView;
    }

    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    private boolean saveUsernamePassword() {
        if (username == null || password == null) {
            System.out.println("username/password were not saved (null)");
            return false;
        }
        File file = new File(getActivity().getFilesDir(), USER_FILE_NAME);
        try {
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream stream= new ObjectOutputStream(fout);
            try {

                stream.writeObject(username);
                stream.writeObject(password);
                System.out.println("username/password were saved");
                return true;
            }
            finally {
                stream.close();
            }
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
        File file = new File(getActivity().getFilesDir(), USER_FILE_NAME);
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

    /**
     * returns true if courses are successfully saved to internal storage,
     * false if not
     * @throws IOException
     */
    private boolean saveCourses() {
        File file = new File(getActivity().getFilesDir(), COURSE_FILE_NAME);
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
}