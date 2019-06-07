package de.dascapschen.android.jeanne.data;

import android.net.Uri;
import android.provider.MediaStore;

public class Song
{
    private int id;
    private String songTitle;
    private int artistID;
    private int albumID;

    public Song(int id, String title, int artist, int album)
    {
        this.id = id;
        this.songTitle = title;
        this.artistID = artist;
        this.albumID = album;
    }

    public int getId() { return id; }
    public String getSongTitle() { return songTitle; }
    public int getArtistID() { return artistID; }
    public int getAlbumID() { return albumID; }

    public Uri getUri(){
        return Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id );
    }

}
