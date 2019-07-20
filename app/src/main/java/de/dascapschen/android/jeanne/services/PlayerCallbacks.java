package de.dascapschen.android.jeanne.services;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import de.dascapschen.android.jeanne.R;

import static de.dascapschen.android.jeanne.App.CHANNEL_ID;

public class PlayerCallbacks extends MediaSessionCompat.Callback
{
    private PlayerService service;
    private NotificationCompat.Builder notifBuilder;

    public PlayerCallbacks(PlayerService service)
    {
        super();

        this.service = service;

        notifBuilder = new NotificationCompat.Builder(service, CHANNEL_ID);
    }

    @Override
    public void onPlay()
    {
        super.onPlay();

        Context context = service;
        MediaSessionCompat mediaSession = service.mediaSession;

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat metadata = controller.getMetadata();
        MediaDescriptionCompat description = metadata.getDescription();

        //metadata of currently playing track
        notifBuilder.setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                //allow launching activity when pressing on notif
                .setContentIntent(controller.getSessionActivity())
                //stop service when notif dies
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setSmallIcon(R.drawable.ic_music_note)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                //add buttons
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_prev, "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_pause, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_next, "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                ))
                //set media style
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0,1,2)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(PlaybackStateCompat.ACTION_STOP))
                );

        service.startForeground(1, notifBuilder.build());

        service.mediaPlayer.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        service.mediaPlayer.pause();
    }

    @Override
    public void onSkipToNext()
    {
        super.onSkipToNext();
    }

    @Override
    public void onSkipToPrevious()
    {
        super.onSkipToPrevious();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        service.stopSelf();
        service.mediaSession.setActive(false);
        service.mediaPlayer.stop();
        service.stopForeground(false);
    }
}
