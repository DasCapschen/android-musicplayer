package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Artist
{
    private int id;
    private String artistName;
    private List<Integer> albums;

    public int getId() { return id; }
    public String getArtistName() { return artistName; }
    public List<Integer> getAlbums() { return albums; }
}
