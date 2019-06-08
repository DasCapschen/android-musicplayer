package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Artist extends MusicalData
{
    private List<Integer> albumIDs;

    public Artist(int id, String name, List<Integer> albumIDs)
    {
        super(id, name);

        this.albumIDs = albumIDs;
    }

    public List<Integer> getAlbumIDs() { return albumIDs; }

    @Override
    public String getDescriptionTitle()
    {
        return name;
    }

    @Override
    public String getDescriptionSubtitle()
    {
        return String.format("%d Albums", albumIDs.size());
    }
}
