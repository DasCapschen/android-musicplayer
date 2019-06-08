package de.dascapschen.android.jeanne.data;

import android.net.Uri;
import android.provider.MediaStore;

import de.dascapschen.android.jeanne.singletons.AllArtists;

public class Song extends MusicalData
{
    private int artistID;
    private int albumID;

    public Song(int id, String title, int artist, int album)
    {
        super(id, title);

        this.artistID = artist;
        this.albumID = album;
    }

    public int getArtistID() { return artistID; }
    public int getAlbumID() { return albumID; }

    public Uri getUri(){
        return Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id );
    }

    @Override
    public String getDescriptionTitle()
    {
        return name;
    }

    @Override
    public String getDescriptionSubtitle()
    {
        AllArtists artists = AllArtists.instance();
        if( artists != null)
            return artists.getByKey(artistID).getName();
        return "";
    }
}
