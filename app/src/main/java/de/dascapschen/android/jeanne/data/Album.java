package de.dascapschen.android.jeanne.data;

import android.net.Uri;
import java.util.List;

public class Album
{
    private int id;
    private String albumTitle;
    private int artistID;
    private List<Integer> titles;

    public Album(int id, String title, int artistID, List<Integer> titles)
    {
        this.id = id;
        this.albumTitle = title;
        this.artistID = artistID;
        this.titles = titles;
    }

    /*TODO:
        private Uri albumArt;
        public Uri getAlbumArt() { return albumArt; }
    */

    public int getId() { return id; }
    public String getAlbumTitle() { return albumTitle; }
    public int getArtistID() { return artistID; }
    public List<Integer> getTitles() { return titles; }
}
