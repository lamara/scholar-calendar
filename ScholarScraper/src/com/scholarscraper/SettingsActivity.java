package com.scholarscraper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

// -------------------------------------------------------------------------
/**
 * Setting for the application
 * 
 * @author Paul Yea
 * @version May 5, 2013
 */
public class SettingsActivity
    extends PreferenceActivity
    implements OnSharedPreferenceChangeListener
{
    public static final String KEY_PREF_SYNC_CONN = "pref_notificationDelay";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment()).commit();
    }


    public void onSharedPreferenceChanged(
        SharedPreferences preference,
        String key)
    {
        if (key.equals(KEY_PREF_SYNC_CONN))
        {
            Intent localIntent = new Intent(this, AlarmService.class);
            localIntent.setAction("CANCEL");
            this.startService(localIntent);
            Intent localIntent2 = new Intent(this, AlarmService.class);
            localIntent.setAction("POPULATE");
            this.startService(localIntent2);
        }
    }
}
