package de.dascapschen.android.jeanne.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class MusicPlayer implements AudioManager.OnAudioFocusChangeListener
{
    private MediaPlayer mediaPlayer;
    private MediaMetadataCompat currentMedia;
    private Uri currentURI;
    private int state;
    private boolean currentMediaCompleted;

    private PlayerListener listener;

    private Context appContext;
    private AudioManager audioManager;

    private boolean playOnAudioFocus = false;
    private final float VOLUME_DEFAULT = 1.0f;
    private final float VOLUME_DUCK = 0.2f;

    //noisy audio, intent is sent when for example headphones are disconnected
    //app should in that case stop playback
    private boolean noisyRegistered = false;
    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
            {
                if (isPlaying())
                {
                    pause();
                }
            }
        }
    };

    MusicPlayer(@NotNull Context context, PlayerListener listener)
    {
        appContext = context.getApplicationContext();
        audioManager = (AudioManager)appContext.getSystemService(Context.AUDIO_SERVICE);
        this.listener = listener;
    }

    private void initializeMediaPlayer()
    {
        if (mediaPlayer == null)
        {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    listener.onPlaybackCompleted();
                }
            });
        }
    }

    MediaMetadataCompat getCurrentMedia()
    {
        return currentMedia;
    }

    private void play()
    {
        if(requestAudioFocus())
        {
            registerAudioNoisyReceiver();
            onPlay();
        }
    }

    void playFromMedia(@NotNull MediaMetadataCompat media)
    {
        currentMedia = media;
        Uri mediaUri = media.getDescription().getMediaUri();

        playFromUri( mediaUri );
    }

    private void playFromUri(@NotNull Uri uri)
    {
        boolean mediaChanged = !uri.equals(currentURI);

        if(!mediaChanged)
        {
            if(!isPlaying()) play();
            return;
        }

        if(mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        currentURI = uri;

        //int mediaID = (int)ContentUris.parseId(uri);
        //currentMedia = QueryHelper.getSongMetadataFromID(appContext, mediaID);

        initializeMediaPlayer();

        try
        {
            mediaPlayer.setDataSource(appContext, uri);
            mediaPlayer.prepare();
            play();
        }
        catch(Exception e)
        {
            Toast.makeText(appContext, "Failed to Play from Uri", Toast.LENGTH_SHORT).show();
            Log.e("PLAY FROM URI", "FAILED");
            e.printStackTrace();
        }
    }

    void pause()
    {
        unregisterAudioNoisyReceiver();
        onPause();
    }
    void stop()
    {
        audioManager.abandonAudioFocus(this);
        unregisterAudioNoisyReceiver();
        onStop();
    }

    private boolean isPlaying()
    {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void setVolume(float volume)
    {
        if(mediaPlayer != null)
            mediaPlayer.setVolume(volume, volume);
    }

    private void onPlay()
    {
        if(mediaPlayer != null && !mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }
    private void onPause()
    {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }
    private void onStop()
    {
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        if(mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setNewState(int newState)
    {
        String stateString;
        switch (newState)
        {
            case PlaybackStateCompat.STATE_PLAYING:
                stateString = "playing";
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                stateString="pause";
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                stateString="stop";
                break;
            default:
                stateString="unknown";
                break;
        }

        Log.e("STATE", String.format("setting to %s", stateString));

        state = newState;

        long position = mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition();

        PlaybackStateCompat pbState = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions())
                .setState(state, position, 1.0f, SystemClock.elapsedRealtime())
                .build();

        listener.onPlayerStateChange(pbState);
    }

    //tell the system that we would like the control over audio
    private boolean requestAudioFocus()
    {
        final int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void registerAudioNoisyReceiver()
    {
        if (!noisyRegistered)
        {
            appContext.registerReceiver(noisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            noisyRegistered = true;
        }
    }
    private void unregisterAudioNoisyReceiver()
    {
        if (noisyRegistered)
        {
            appContext.unregisterReceiver(noisyReceiver);
            noisyRegistered = false;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
            //if we gain audio focus, start playing or set volume normal
            case AudioManager.AUDIOFOCUS_GAIN:
                if (playOnAudioFocus && !isPlaying())
                {
                    play();
                }
                else if (isPlaying())
                {
                    setVolume(VOLUME_DEFAULT);
                }
                playOnAudioFocus = false;
                break;
            //if we temporarily lose audio focus, lessen volume
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                setVolume(VOLUME_DUCK);
                break;
            //pause if we temporarily lose audio focus completely
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying())
                {
                    playOnAudioFocus = true;
                    pause();
                }
                break;
            //stop playing if we lose audio focus
            case AudioManager.AUDIOFOCUS_LOSS:
                audioManager.abandonAudioFocus(this);
                playOnAudioFocus = false;
                stop();
                break;
        }
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

        switch (state)
        {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        |  PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        |  PlaybackStateCompat.ACTION_PAUSE
                        |  PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        |  PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        |  PlaybackStateCompat.ACTION_PLAY_PAUSE
                        |  PlaybackStateCompat.ACTION_STOP
                        |  PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

}
