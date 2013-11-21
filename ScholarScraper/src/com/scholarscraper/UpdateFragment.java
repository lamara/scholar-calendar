package com.scholarscraper;

import java.util.AbstractMap.SimpleEntry;
import com.scholarscraper.update.DataManager;
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
                       MainActivity mainActivity = (MainActivity) context;
                       username = usernameEdit.getText().toString();
                       password = passwordEdit.getText().toString();
                       mainActivity.saveUsernamePassword(username, password);
                       mainActivity.update();
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

    /**
     * Retrieves the user's username and password from internal storage
     */
    private boolean recoverUsernamePassword()
    {
        SimpleEntry<String, String> usernamePassword = DataManager.recoverUsernamePassword(context);
        if (usernamePassword != null) {
            this.username = usernamePassword.getKey();
            this.password = usernamePassword.getValue();
            return true;
        }
        else {
            this.username = null;
            this.password = null;
            return false;
        }
    }
}
