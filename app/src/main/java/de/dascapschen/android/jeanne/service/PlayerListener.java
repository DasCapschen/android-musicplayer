package de.dascapschen.android.jeanne.service;

import android.support.v4.media.session.PlaybackStateCompat;

public abstract class PlayerListener
{
    public abstract void onPlayerStateChange(PlaybackStateCompat state);

    public abstract void onPlaybackCompleted();
}
