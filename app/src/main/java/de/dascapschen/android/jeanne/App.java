package de.dascapschen.android.jeanne;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application
{
    public static final String CHANNEL_ID = "channel";

    @Override
    public void onCreate()
    {
        super.onCreate();

        /* create Notification channel */
        //only on oreo and above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Controls",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Media Controls");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
}
