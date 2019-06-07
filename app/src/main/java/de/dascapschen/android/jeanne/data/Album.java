package de.dascapschen.android.jeanne.data;

import android.net.Uri;
import java.util.List;

public class Album
{
    private int id;
    private String albumTitle;
    private int artistID;
    private List<Integer> titles;
    private Uri albumArt;

    public int getId() { return id; }
    public String getAlbumTitle() { return albumTitle; }
    public int getArtistID() { return artistID; }
    public List<Integer> getTitles() { return titles; }
    public Uri getAlbumArt() { return albumArt; }
}
