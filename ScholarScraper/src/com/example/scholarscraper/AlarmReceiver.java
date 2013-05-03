package com.example.scholarscraper;

import com.example.scholarscraper.R;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AlarmReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager =
            (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence title = "Alarm!!";
        int icon = R.drawable.ic_launcher;
        CharSequence text = "";
        NotificationCompat.Builder notification =
            new NotificationCompat.Builder(context).setSmallIcon(icon)
                .setContentTitle(title).setContentText(text);
        PendingIntent contentIntent =
            PendingIntent.getActivity(context, 0, new Intent(), 0);
        notification.setContentIntent(contentIntent);
        notificationManager.notify(0, notification.build());
    }
}
