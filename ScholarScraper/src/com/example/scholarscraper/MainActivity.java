package com.example.scholarscraper;

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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        DialogFragment fragment = new UpdateFragment(null);
        fragment.show(transaction, "update");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
