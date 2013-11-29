package com.scholarscraper.update;

import java.util.Map.Entry;
import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

/**
* // -------------------------------------------------------------------------
/**
* A service used to run the Scholar update process
* Can be run in the background, is generally set to run every few hours
* by an alarm manager.
*
* @author Alex Lamar
*/
public class UpdateService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Entry<String, String> usernamePassword = DataManager.recoverUsernamePassword(this);
        if (usernamePassword == null) {
            System.out.println("Username/password not present for background update");
            return START_NOT_STICKY;
        }
        System.out.println("Executing background update");
        new ScholarScraper().execute(usernamePassword.getKey(), usernamePassword.getValue(), this);
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

}