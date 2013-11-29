package com.scholarscraper.update;

import com.scholarscraper.MainActivity;
import java.util.Calendar;
import android.app.PendingIntent;
import android.app.AlarmManager;
import com.scholarscraper.alarm.AlarmSetter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * // -------------------------------------------------------------------------
 * /** This class gets run every time the device launches. It is used to set a
 * repeating update service that will launch a scholar update every
 * six hours.
 *
 * @author Alex Lamar
 */
public class UpdateAtBootReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            AlarmSetter.setNextAlarm(context);

            Intent service = new Intent(context, UpdateService.class);

            PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar cal = Calendar.getInstance();

            alarm.cancel(pendingIntent); //remove already existing alarms (if they exist)
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR * MainActivity.UPDATE_INTERVAL, pendingIntent);
        }
    }
}
