package com.scholarscraper;

import android.view.Gravity;
import android.graphics.Color;
import android.widget.TextView;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;

/**
 * // -------------------------------------------------------------------------
 * /** A dialog popup that will prompt the user whether or not they want to
 * change the information they use to log in to Scholar. Called from the action
 * bar.
 *
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version May 5, 2013
 */
public class ChangeFragment
    extends DialogFragment
{
    private static final String COURSE_FILE = "courses";
    private static final String USER_FILE   = "userData";

    private Context             context;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(context);
        title.setText("Log out of Scholar?");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setCustomTitle(title)
               .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       MainActivity mainActivity = (MainActivity) context;
                       mainActivity.logOut();
                       mainActivity.cancelCurrentUpdate();
                       dismiss();
                       launchLoginDialog(UpdateFragment.DEFAULT_PROMPT);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.setCustomTitle(title);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Launches the update popup dialog
     */
    public void launchLoginDialog(String prompt)
    {
        FragmentTransaction transaction =
            getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putString("prompt", prompt);

        DialogFragment fragment = new UpdateFragment();
        fragment.setArguments(bundle);
        fragment.show(transaction, "update");
    }
}
