package com.scholarscraper;

import com.scholarscraper.model.Course;
import android.widget.Toast;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    EditText                    usernameEdit;
    EditText                    passwordEdit;
    TextView                    prompt;

    private List<Course>        courses;
    private String              username;
    private String              password;

    public static final String  DEFAULT_PROMPT   =
                                                     "";

    final private static String USER_FILE_NAME   = "userData";
    final private static String COURSE_FILE_NAME = "courses";

    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment, null);

        Bundle arguments = getArguments();

        usernameEdit = (EditText)view.findViewById(R.id.username);
        passwordEdit = (EditText)view.findViewById(R.id.password);
        prompt = (TextView)view.findViewById(R.id.prompt);

        // populate prompt textview
        if (arguments != null)
        {
            String promptArg = arguments.getString("prompt");
            prompt.setText(promptArg != null ? promptArg : DEFAULT_PROMPT);
        }
        // populate username/password fields, if they exist
        if (recoverUsernamePassword())
        {
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }

        TextView title = new TextView(context);
        title.setText("Log in to Scholar");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setCustomTitle(title)
               .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       username = usernameEdit.getText().toString();
                       password = passwordEdit.getText().toString();
                       saveUsernamePassword();
                       new ScholarScraper().execute(username, password, getActivity());
                       Toast.makeText(context, "Updating...", Toast.LENGTH_LONG).show();
                       dismiss();
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.setCustomTitle(title);
        builder.setView(view);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /*
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        final View myFragmentView =
            inflater.inflate(R.layout.fragment, container, false);

        Bundle arguments = getArguments();

        usernameEdit = (EditText)myFragmentView.findViewById(R.id.username);
        passwordEdit = (EditText)myFragmentView.findViewById(R.id.password);
        prompt = (TextView)myFragmentView.findViewById(R.id.prompt);

        // populate prompt textview
        if (arguments != null)
        {
            String promptArg = arguments.getString("prompt");
            prompt.setText(promptArg != null ? promptArg : DEFAULT_PROMPT);
        }

        // populate username/password fields, if they exist
        if (recoverUsernamePassword())
        {
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }

        return myFragmentView;
    }
    */


    /**
     * Saves the user's username and password to internal storage, currently
     * stores in plain text, this needs to change before distribution
     */
    private boolean saveUsernamePassword()
    {
        if (username == null || password == null)
        {
            System.out.println("username/password were not saved (null)");
            return false;
        }
        File file = new File(getActivity().getFilesDir(), USER_FILE_NAME);
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
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword()
    {
        File file = new File(getActivity().getFilesDir(), USER_FILE_NAME);
        try
        {
            InputStream inputStream = new FileInputStream(file);
            InputStream buffer = new BufferedInputStream(inputStream);
            ObjectInputStream input = new ObjectInputStream(buffer);
            try
            {
                username = (String)input.readObject();
                password = (String)input.readObject();
                if (username != null && password != null)
                {
                    System.out.println("Username/password retrieved");
                    return true;
                }
                System.out.println("Username/password not retrieved");
                return false;
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
            return false;
        }
        catch (IOException e)
        {
            System.out.println("Username/password not retrieved");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * returns true if courses are successfully saved to internal storage, false
     * if not
     *
     * @throws IOException
     */
    private boolean saveCourses()
    {
        File file = new File(getActivity().getFilesDir(), COURSE_FILE_NAME);
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
}
