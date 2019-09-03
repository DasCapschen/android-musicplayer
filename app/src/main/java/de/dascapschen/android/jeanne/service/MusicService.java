package de.dascapschen.android.jeanne.service;

import android.app.Notification;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.service.MusicNotification;
import de.dascapschen.android.jeanne.singletons.AllSongs;

public class MusicService extends MediaBrowserServiceCompat
{
    private static final String ROOT_ID = "media_root_id";
    private static final String EMPTY_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    private MusicNotification notification;
    private MusicPlayer player;

    private boolean serviceStarted = false;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MUSIC_SERVICE");

        mediaSession.setCallback( new MusicSessionCallbacks() );

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
        );

        //not sure if this is necessary...
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        setSessionToken(mediaSession.getSessionToken());

        notification = new MusicNotification(this);

        player = new MusicPlayer(this, new MediaPlayerListener());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        notification.onDestroy();
        player.stop();
        mediaSession.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPkgName, int clientUid, @Nullable Bundle rootHints)
    {
        return new BrowserRoot(ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaID, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        if(parentMediaID.equals(EMPTY_ROOT_ID))
        {
            result.sendResult(null);
        }

        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>(); //TODO
        AllSongs songs = AllSongs.instance();
        for(Song s : songs.data())
        {
            MediaDescriptionCompat desc = new MediaDescriptionCompat.Builder()
                    .setTitle(s.getDescriptionTitle())
                    .setSubtitle(s.getDescriptionSubtitle())
                    .setMediaUri(s.getUri())
                    .setMediaId(String.format("%d", s.getId()))
                    .build();
            MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
        }

        result.sendResult(items);
    }


    public class MediaPlayerListener extends PlayerListener
    {
        @Override
        public void onPlayerStateChange(PlaybackStateCompat state)
        {
            mediaSession.setPlaybackState(state);

            Notification notif = notification.getNotification(player.getCurrentMedia(), state, getSessionToken());

            switch(state.getState())
            {
                case PlaybackStateCompat.STATE_PLAYING:
                    if(!serviceStarted)
                    {
                        ContextCompat.startForegroundService(MusicService.this,
                                new Intent(MusicService.this, MusicService.class));
                        serviceStarted = true;
                    }

                    startForeground(MusicNotification.NOTIFICATION_ID, notif);
                    break;

                case PlaybackStateCompat.STATE_PAUSED:
                    stopForeground(false);
                    notification.getNotificationManager().notify(notification.NOTIFICATION_ID, notif);
                    break;

                case PlaybackStateCompat.STATE_STOPPED:
                    stopForeground(true);
                    stopSelf();
                    serviceStarted = false;
                    break;
            }
        }

        @Override
        public void onPlaybackCompleted()
        {

        }
    }

    class MusicSessionCallbacks extends MediaSessionCompat.Callback
    {
        private List<MediaSessionCompat.QueueItem> playlist = new ArrayList<>();
        private int queueIndex = -1;
        private MediaMetadataCompat preparedMedia;


        @Override
        public void onAddQueueItem(MediaDescriptionCompat description)
        {
            playlist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            if(queueIndex == -1) queueIndex = 0;
            mediaSession.setQueue(playlist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description)
        {
            playlist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            if(playlist.isEmpty()) queueIndex = -1;
            mediaSession.setQueue(playlist);
        }

        @Override
        public void onPrepare()
        {
            if(queueIndex < 0 && playlist.isEmpty()) return;

            String mediaID = playlist.get(queueIndex).getDescription().getMediaId();
            //TODO: preparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaID);

            mediaSession.setMetadata(preparedMedia);
            if(!mediaSession.isActive()) mediaSession.setActive(true);
        }

        @Override
        public void onPlay()
        {
            Log.e("CALLBACK", "PLAY");
            if(playlist.isEmpty()) return;

            if(preparedMedia == null) onPrepare();

            player.playFromMedia(preparedMedia);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras)
        {
            player.playFromUri(uri);
        }

        @Override
        public void onPause()
        {
            player.pause();
            Log.e("CALLBACK", "PAUSE");
        }

        @Override
        public void onStop()
        {
            player.stop();
            mediaSession.setActive(false);
        }

        @Override
        public void onSkipToNext()
        {
            Log.e("CALLBACK", "NEXT");
            queueIndex = ++queueIndex % playlist.size();
            preparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToPrevious()
        {
            Log.e("CALLBACK", "PREV");
            if(queueIndex > 0) queueIndex--;
            else queueIndex = playlist.size() - 1;
            preparedMedia = null;
            onPlay();
        }
    }
}
