package de.dascapschen.android.jeanne.data;

import java.util.List;

public class Album extends MusicalData
{
    private int artistID;
    private List<Integer> songIds;

    public Album(int id, String title, int artistID, List<Integer> titles)
    {
        super(id, title); //construct parent

        this.artistID = artistID;
        this.songIds = titles;
    }

    /*TODO:
        private Uri albumArt;
        public Uri getAlbumArt() { return albumArt; }
    */

    public int getArtistID() { return artistID; }
    public List<Integer> getSongIds() { return songIds; }


    @Override
    public String getDescriptionTitle()
    {
        return name;
    }

    @Override
    public String getDescriptionSubtitle()
    {
        return String.format("%d Songs", songIds.size());
    }
}
