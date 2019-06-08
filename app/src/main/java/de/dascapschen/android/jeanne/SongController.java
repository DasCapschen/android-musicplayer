package de.dascapschen.android.jeanne;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.data.Song;

public interface SongController
{
    void startNewSong( Song song );
    void setPlaylist( ArrayList<Song> songs );
    void setPlaylist(Artist artist);
    void setPlaylist(Album album);

    void playSong();
    void pauseSong();
    void nextSong();
    void prevSong();
}
