package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Artist
{
    private int id;
    private String artistName;
    private List<Integer> albumIDs;

    public Artist(int id, String name, List<Integer> albumIDs)
    {
        this.id = id;
        this.artistName = name;
        this.albumIDs = albumIDs;
    }

    public int getId() { return id; }
    public String getArtistName() { return artistName; }
    public List<Integer> getAlbumIDs() { return albumIDs; }
}
