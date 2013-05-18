package com.example.scholarscraper;

import android.app.FragmentTransaction;
import java.io.File;
import android.content.Context;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
/**
 * // -------------------------------------------------------------------------
/**
 *  A dialog popup that will prompt the user whether or not they want to change
 *  the information they use to log in to Scholar. Called from the action bar.
 *
 *  @author Alex Lamar, Paul Yea, Brianna Beitzel
 *  @version May 5, 2013
 */
public class ChangeFragment extends DialogFragment
{
    private static final String COURSE_FILE = "courses";
    private static final String USER_FILE = "userData";

    private Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View fragmentView = inflater.inflate(R.layout.change_user, container, false);
        context = getActivity();

        Button positive = (Button) fragmentView.findViewById(R.id.change_positive);
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                destroyData();
                dismiss();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                UpdateFragment newFragment = new UpdateFragment();
                newFragment.show(ft, "update");
            }
        });

        Button negative = (Button) fragmentView.findViewById(R.id.change_negative);
        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return fragmentView;
    }


    /**
     * Destroys internal data state, effectively resetting the state of the app
     */
    private void destroyData() {
        File courseFile = new File(context.getFilesDir(), COURSE_FILE);
        File userFile = new File(context.getFilesDir(), USER_FILE);
        courseFile.delete();
        userFile.delete();
    }
}
