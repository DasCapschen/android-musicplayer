package de.dascapschen.android.jeanne.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;

public class NotificationService extends Service
{
    public class Constants {
        public static final String ACTION_MAIN = "de.dascapschen.android.jeanne.notification.action.main";
        public static final String ACTION_START = "de.dascapschen.android.jeanne.notification.action.start";
        public static final String ACTION_STOP = "de.dascapschen.android.jeanne.notification.action.stop";
        public static final String ACTION_PLAY = "de.dascapschen.android.jeanne.notification.action.play";
        public static final String ACTION_PREV = "de.dascapschen.android.jeanne.notification.action.prev";
        public static final String ACTION_NEXT = "de.dascapschen.android.jeanne.notification.action.next";

        static final int NOTIFICATION_ID = 1337;
    }

    Notification status;

    public NotificationService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent != null)
        switch( intent.getAction() )
        {
            case Constants.ACTION_START:
                showNotification();
                break;
            case Constants.ACTION_STOP:
                stopForeground(true);
                stopSelf();
                break;
            case Constants.ACTION_PLAY:
                break;
            case Constants.ACTION_PREV:
                break;
            case Constants.ACTION_NEXT:
                break;
        }

        return START_STICKY_COMPATIBILITY;
    }

    private void showNotification()
    {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews largeView = new RemoteViews(getPackageName(), R.layout.notification_large);

        /*
        Intent playIntent = new Intent(this, MainActivity.class);
        playIntent.setAction(Constants.ACTION_PLAY);
        PendingIntent pPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlayIntent);
        */

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Constants.ACTION_MAIN);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        status = new Notification.Builder(this).build();
        status.contentView = view; /*deprecated?*/
        status.bigContentView = largeView;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher_round;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID, status);
    }
}
