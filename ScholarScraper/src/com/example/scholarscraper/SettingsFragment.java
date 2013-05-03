package com.example.scholarscraper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.DialogFragment;
import android.app.ActionBar;
import android.view.Menu;
import android.app.Activity;

public class SettingsFragment extends DialogFragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View fragmentView = inflater.inflate(R.layout.settings, container, false);
        return fragmentView;
    }
}
