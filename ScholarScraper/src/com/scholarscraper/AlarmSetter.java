package com.scholarscraper;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

// -------------------------------------------------------------------------
/**
 * Recreate all the alarms when the system reboots
 *
 * @author Paul Yea
 * @version May 5, 2013
 */
public class AlarmSetter
    extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent localIntent = new Intent(context, AlarmService.class);
        localIntent.setAction("POPULATE");
        context.startService(localIntent);
    }
}
