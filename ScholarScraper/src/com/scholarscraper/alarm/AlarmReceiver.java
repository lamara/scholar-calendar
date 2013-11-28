package com.scholarscraper.alarm;

import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import com.scholarscraper.model.*;
import android.net.Uri;
import android.media.RingtoneManager;
import com.scholarscraper.R;
import android.app.Notification;
import android.app.PendingIntent;
import com.scholarscraper.MainActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class AlarmReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("alarm intent recieved");
        String dueDate = intent.getStringExtra(Task.class.getName() + "." + AlarmSetter.DUE_DATE_EXTRA);
        String assignmentName = intent.getStringExtra(Task.class.getName() + "." + AlarmSetter.ASSIGNMENT_NAME_EXTRA);
        String courseName = intent.getStringExtra(Task.class.getName() + "." + AlarmSetter.COURSE_NAME_EXTRA);

        //Every time an alarm is triggered we are going to push a notification onto the device.
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Create the pending intent from this stack to prevent duplicate
        //activity navigation issues
        intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification n  = new NotificationCompat.Builder(context)
            .setContentTitle(assignmentName + " is due soon!")
            .setContentText(assignmentName + " due " + dueDate)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .setSound(alarmSound)
            .setStyle(new NotificationCompat.BigTextStyle()).build();
        notificationManager.notify(0, n);

        AlarmSetter.setNextAlarm(context);
    }

}
