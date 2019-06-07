package de.dascapschen.android.jeanne;

import android.net.Uri;

import java.util.ArrayList;

public interface SongController
{
    void startNewSong(Uri songUri);
    void setPlaylist( ArrayList<Uri> songs );

    void playSong();
    void pauseSong();
    void nextSong();
    void prevSong();
}
