package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Playlist extends MusicalData
{
    List<Song> songs;

    public Playlist(int id, String name, List<Song> songs)
    {
        super(id, name);

        this.songs = songs;
    }

    @Override
    public String getDescriptionTitle()
    {
        return name;
    }

    @Override
    public String getDescriptionSubtitle()
    {
        return String.format("%d Songs", songs.size());
    }
}
