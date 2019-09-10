package de.dascapschen.android.jeanne.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;

public class MusicNotification
{
    public static final String CHANNEL_ID = "de.dascapschen.android.jeanne.notifchannel";
    public static final int NOTIFICATION_ID = 1337;
    public static final int REQUEST_CODE = 42;

    private MusicService mService;
    private NotificationManager notificationManager;

    private NotificationCompat.Action playAction;
    private NotificationCompat.Action pauseAction;
    private NotificationCompat.Action prevAction;
    private NotificationCompat.Action nextAction;


    public MusicNotification(MusicService service)
    {
        mService = service;
        notificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        playAction = new NotificationCompat.Action(
                R.drawable.ic_play,
                mService.getString(R.string.label_play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_PLAY));

        pauseAction = new NotificationCompat.Action(
                R.drawable.ic_pause,
                mService.getString(R.string.label_pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_PAUSE));

        nextAction = new NotificationCompat.Action(
                R.drawable.ic_next,
                mService.getString(R.string.label_next),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        prevAction = new NotificationCompat.Action(
                R.drawable.ic_prev,
                mService.getString(R.string.label_prev),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        //in case the mService was killed and restarted by the system
        notificationManager.cancelAll();
    }

    public NotificationManager getNotificationManager()
    {
        return notificationManager;
    }

    public Notification getNotification(MediaMetadataCompat metadata, PlaybackStateCompat state, MediaSessionCompat.Token token)
    {
        return buildNotification(metadata, state, token).build();
    }

    private NotificationCompat.Builder buildNotification(MediaMetadataCompat metadata, PlaybackStateCompat state, MediaSessionCompat.Token token)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, CHANNEL_ID);


        //FIXME: cancel button does not show up!
        builder.setStyle(
                new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0,1,2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_STOP)
                        ));

        if(metadata == null)
        {
            builder.setContentTitle( "SONG NAME" )
                    .setContentText( "ARTIST NAME" );
        }
        else
        {
            Bitmap thumbnail = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
            if(thumbnail != null)
            {
                builder.setLargeIcon(thumbnail);
            }

            builder.setContentTitle( metadata.getDescription().getTitle() )
                .setContentText( metadata.getDescription().getSubtitle() );
        }

        builder.setSmallIcon( R.drawable.ic_music_note )
                .setDeleteIntent( MediaButtonReceiver.buildMediaButtonPendingIntent(mService, PlaybackStateCompat.ACTION_STOP) )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        //open UI when tapping on the notification
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent);

        builder.addAction( prevAction );

        if( state.getState() == PlaybackStateCompat.STATE_PLAYING )
        {
            builder.addAction( pauseAction );
        }
        else
        {
            builder.addAction( playAction );
        }

        builder.addAction( nextAction );

        return builder;
    }

    public void onDestroy()
    {

    }

    //we need to create a notification channel on newer android versions
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel()
    {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null)
        {
            // The user-visible name of the channel.
            CharSequence name = "Jeanne";
            // The user-visible description of the channel.
            String description = "Music Player";

            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }
}
