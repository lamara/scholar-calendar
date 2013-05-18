package com.example.scholarscraper;

import android.R;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// -------------------------------------------------------------------------
/**
 * receives intent from alarm service and create a notification to appear on the
 * status bar
 *
 * @author Paul Yea
 * @version May 5, 2013
 */
public class AlarmReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager =
            (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int id = (int)intent.getLongExtra("id", -1L);
        CharSequence title = "Assignment Due";
        int icon = R.drawable.ic_dialog_alert;
        CharSequence text = intent.getStringExtra("name");
        NotificationCompat.Builder notification =
            new NotificationCompat.Builder(context).setSmallIcon(icon)
                .setContentTitle(title).setContentText(text);
        PendingIntent contentIntent =
            PendingIntent.getActivity(context, 0, new Intent(), 0);
        notification.setContentIntent(contentIntent);
        notificationManager.notify(id, notification.build());
    }
}
