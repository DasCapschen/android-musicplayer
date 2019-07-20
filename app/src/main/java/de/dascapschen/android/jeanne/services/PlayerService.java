package de.dascapschen.android.jeanne.services;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

//I actually read the docs now... should use a service to play music in BG.
//activity only to handle UI
public class PlayerService extends MediaBrowserServiceCompat
{
    MediaSessionCompat mediaSession;
    MediaPlayer mediaPlayer;

    public static final String EMPTY_ROOT_ID = "chickens";

    @Override
    public void onCreate()
    {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "SERVICE_PLAYER");

        //enable mediabutton and transport(?) callbacks
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        //make initial state with ACTION_PLAY (???)
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(new PlayerCallbacks(this));

        //set session token so we can communicate with it (?)
        setSessionToken(mediaSession.getSessionToken());
    }


    //controls client access to service
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle)
    {
        //we will just not have browsing here, because it's less code :p
        //see https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice

        return new BrowserRoot(EMPTY_ROOT_ID, null);

        //return null; //null would refuse connection!
    }

    //allows client to build menu of this services content hierarchy
    //(that mean show a playlist or so?)
    @Override
    public void onLoadChildren(@NonNull String s, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        //browsing not allowed
        if(s.equals(EMPTY_ROOT_ID))
        {
            result.sendResult(null);
            return;
        }

        //else... die in a fire
    }

    //TODO: continue this another day...
}
