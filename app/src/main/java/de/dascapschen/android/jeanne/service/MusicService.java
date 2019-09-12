package de.dascapschen.android.jeanne.service;

import android.app.Notification;
import android.content.ContentUris;
import android.content.Intent;
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
import java.util.Collections;
import java.util.List;

import de.dascapschen.android.jeanne.data.QueryHelper;

public class MusicService extends MediaBrowserServiceCompat
{
    private static final String ROOT_ID = "media_root_id";
    private static final String EMPTY_ROOT_ID = "empty_root_id";

    public static final String CUSTOM_ACTION_CLEAR_QUEUE = "clear_play_queue";
    public static final String CUSTOM_ACTION_APPEND_QUEUE = "append_play_queue";
    public static final String CUSTOM_ACTION_SET_QUEUE = "set_play_queue";
    public static final String CUSTOM_ACTION_DATA_KEY = "play_queue_data";

    private MediaSessionCompat mediaSession;
    private MusicSessionCallbacks callbacks;
    private PlaybackStateCompat.Builder stateBuilder;

    private MusicNotification notification;
    private MusicPlayer player;

    private boolean serviceStarted = false;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MUSIC_SERVICE");

        callbacks = new MusicSessionCallbacks();
        mediaSession.setCallback( callbacks );

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
        );

        //not sure if this is necessary...
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PAUSE);

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
        return new BrowserRoot(EMPTY_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaID, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        if(parentMediaID.equals(EMPTY_ROOT_ID))
        {
            result.sendResult(null);
        }
        else
        {
            List<MediaBrowserCompat.MediaItem> items = new ArrayList<>(); //TODO
            result.sendResult(items);
        }
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
            Log.e("CALLBACK", "Playback complete!");
            callbacks.playbackCompleted();
        }
    } // End Callbacks

    class MusicSessionCallbacks extends MediaSessionCompat.Callback
    {
        private List<Integer> playlist = new ArrayList<>();
        private List<Integer> unshuffled = new ArrayList<>();
        private int queueIndex = -1;
        private MediaMetadataCompat preparedMedia;

        private int repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
        private int shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description)
        {
            //playlist.add( new MediaSessionCompat.QueueItem(description, description.hashCode()) );
            //if(queueIndex == -1) queueIndex = 0;
            //mediaSession.setQueue(playlist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description)
        {
            //playlist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            //if(playlist.isEmpty()) queueIndex = -1;
            //mediaSession.setQueue(playlist);
        }

        void playbackCompleted()
        {
            preparedMedia = null;
            if(repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE)
            {
                //if repeat one, play current media again
                onPlay();
            }
            else if( repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE && queueIndex == playlist.size()-1 )
            {
                //if end of queue and no repeat, stop
                onStop();
            }
            else
            {
                //play next song
                onSkipToNext();
            }
        }



        @Override
        public void onPrepare()
        {
            if(queueIndex < 0 || playlist.isEmpty()) return;

            preparedMedia = QueryHelper.getSongMetadataFromID(MusicService.this, playlist.get(queueIndex));

            mediaSession.setMetadata(preparedMedia);
            if(!mediaSession.isActive()) mediaSession.setActive(true);
        }

        @Override
        public void onPlay()
        {
            Log.i("CALLBACK", "PLAY");
            if(playlist.isEmpty()) return;

            if(preparedMedia == null) onPrepare();

            player.playFromMedia(preparedMedia);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras)
        {
            //empty playlist
            playlist = new ArrayList<>();
            queueIndex = 0;

            //add new playlist item
            int id = (int)ContentUris.parseId(uri);
            playlist.add(id);
            updateMediasessionQueue();

            onPrepare();

            //play it
            player.playFromMedia(preparedMedia);
        }

        @Override
        public void onPause()
        {
            player.pause();
            Log.i("CALLBACK", "PAUSE");
        }

        @Override
        public void onStop()
        {
            player.stop();
            mediaSession.setActive(false);
        }

        @Override
        public void onSkipToNext()//plays current media again
        {
            Log.i("CALLBACK", "NEXT");

            if(playlist.isEmpty()) return;

            queueIndex = ++queueIndex % playlist.size();
            preparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToPrevious()
        {
            Log.i("CALLBACK", "PREV");

            if(playlist.isEmpty()) return;

            if(queueIndex > 0) queueIndex--;
            else queueIndex = playlist.size() - 1;
            preparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToQueueItem(long id)
        {
            if(id < 0 || id > playlist.size())
            {
                Log.w("SKIP_QUEUE", "Out of Range");
                return;
            }
            queueIndex = (int)id;
            preparedMedia = null;
            onPlay();
        }

        @Override
        public void onSetRepeatMode(int repeatMode)
        {
            Log.i("REPEAT", "Setting Mode");

            mediaSession.setRepeatMode(repeatMode);
            this.repeatMode = repeatMode;
        }

        @Override
        public void onSetShuffleMode(int shuffleMode)
        {
            Log.i("SHUFFLE", "Setting Mode");

            mediaSession.setShuffleMode(shuffleMode);
            this.shuffleMode = shuffleMode;

            if(playlist.isEmpty()) return;

            if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL)
            {
                unshuffled = new ArrayList<>(playlist); //remember original order
                int songID = playlist.get(queueIndex);

                Collections.shuffle(playlist);
                queueIndex = playlist.indexOf(songID);

                updateMediasessionQueue();
            }
            else //shuffleMode == SHUFFLE_MODE_NONE
            {
                int songID = playlist.get(queueIndex);
                playlist = new ArrayList<>(unshuffled);
                queueIndex = playlist.indexOf(songID);

                updateMediasessionQueue();
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras)
        {
            ArrayList<Integer> songIDs = extras.getIntegerArrayList(CUSTOM_ACTION_DATA_KEY);

            switch (action)
            {
                case CUSTOM_ACTION_CLEAR_QUEUE:
                    playlist = new ArrayList<>();
                    unshuffled = new ArrayList<>();
                    break;
                case CUSTOM_ACTION_APPEND_QUEUE:
                    if(songIDs == null || songIDs.isEmpty()) return;

                    if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    {
                        unshuffled.addAll(songIDs);
                        Collections.shuffle(songIDs);
                    }

                    playlist.addAll(songIDs);
                    break;
                case CUSTOM_ACTION_SET_QUEUE:
                    if(songIDs == null|| songIDs.isEmpty())
                    {
                        Log.w("CUSTOM_ACTION", "Use CLEAR_QUEUE instead of setting NULL/Empty!");
                        return;
                    }
                    playlist = songIDs;
                    if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    {
                        unshuffled = new ArrayList<>(songIDs);
                        Collections.shuffle(playlist);
                    }
                    break;
                default:
                    break;
            }

            queueIndex = 0;
            preparedMedia = null;
            updateMediasessionQueue();
        }

        //FIXME: I'M EXTREMELY SLOW
        void updateMediasessionQueue()
        {
            ArrayList<MediaSessionCompat.QueueItem> queue = new ArrayList<>();

            for( int id : playlist )
            {
                /*
                MediaMetadataCompat metadata = QueryHelper.getSongMetadataFromID(MusicService.this, id);

                if(metadata == null)
                {
                    Log.e("QUEUE", String.format("Failed to get Metadata for Song with ID %d", id));
                    continue;
                }
                */

                /*FIXME: getting content description is slow, lets just pass our ID
                         and the reclycler view showing this will get the content as needed
                         (this is faster since we only query a few items at a time)
                 */
                MediaDescriptionCompat desc = new MediaDescriptionCompat.Builder().setMediaId(String.valueOf(id)).build();

                queue.add( new MediaSessionCompat.QueueItem(
                        desc,
                        id
                ));
            }

            mediaSession.setQueue(queue);
        }

    } // End Callbacks
} // End MusicService
