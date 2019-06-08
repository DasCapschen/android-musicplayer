package de.dascapschen.android.jeanne.singletons;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.data.Album;

public class AllAlbums extends SingletonBase<Album>
{
    private static AllAlbums mInstance = null;

    private AllAlbums(Context context) {
        queryAll(context);
    }

    public static AllAlbums instance()
    {
        return mInstance;
    }

    public static void initialize(Context context)
    {
        if( mInstance == null )
        {
            mInstance = new AllAlbums(context);
        }
    }

    @Override
    protected void queryAll(Context context)
    {
        //Query the ALBUMS!!!
        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        //                             ^~~~~~~

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
        };

        String sort = MediaStore.Audio.Albums.ALBUM +" ASC";

        Cursor cursor = context.getContentResolver()
                .query( mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Albums._ID );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ARTIST );


            do {
                String artistName = cursor.getString( artistIndex );
                int albumID = cursor.getInt(idIndex);

                int artistID = getArtistID(context, artistName);
                List<Integer> songIds = getSongIDList(context, albumID);

                Album a = new Album(
                        albumID,
                        cursor.getString( albumIndex ),
                        artistID,
                        songIds
                );

                put(a.getId(), a);
            } while( cursor.moveToNext() );

            cursor.close();
        }
    }

    private int getArtistID(Context context, String artistName)
    {
        String[] p = {
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists._ID
        };

        String s = MediaStore.Audio.Artists.ARTIST + "=\"" + artistName+"\"";

        Cursor artistCursor = context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                p, s, null, null);

        int id = 0;

        if( artistCursor != null && artistCursor.moveToFirst() )
        {
            id = artistCursor.getInt( artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID) );
            artistCursor.close();
        }
        return id;
    }

    private List<Integer> getSongIDList(Context context, int albumID)
    {
        String[] p = {
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media._ID
        };

        String s = MediaStore.Audio.Media.ALBUM_ID + "=" + albumID;

        Cursor songCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                p, s, null, null);

        List<Integer> songIDs = new ArrayList<>();

        if(songCursor!=null && songCursor.moveToFirst())
        {
            do {
                songIDs.add(songCursor.getInt( songCursor.getColumnIndex(MediaStore.Audio.Media._ID) ));
            } while(songCursor.moveToNext());
            songCursor.close();
        }

        return songIDs;
    }

}
