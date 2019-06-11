package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Playlist extends MusicalData
{
    List<Integer> songIDs;

    public Playlist(int id, String name, List<Integer> songs)
    {
        super(id, name);

        this.songIDs = songs;
    }

    @Override
    public String getDescriptionTitle()
    {
        return name;
    }

    @Override
    public String getDescriptionSubtitle()
    {
        return String.format("%d Songs", songIDs.size());
    }

    public List<Integer> getSongIDs()
    {
        return songIDs;
    }
}
