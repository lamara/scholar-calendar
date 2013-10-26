package com.scholarscraper;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * // -------------------------------------------------------------------------
 * /** Launches the settings fragment, used to set the interval before an alarm
 * will trigger with respect to a task's due date.
 * 
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version May 5, 2013
 */
public class SettingsFragment
    extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
