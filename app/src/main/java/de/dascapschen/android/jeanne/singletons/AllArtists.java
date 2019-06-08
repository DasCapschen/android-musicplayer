package de.dascapschen.android.jeanne.singletons;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.data.Artist;

public class AllArtists extends SingletonBase<Artist>
{
    private static AllArtists mInstance = null;

    private AllArtists(Context context) {
        queryAll(context);
    }

    public static AllArtists instance()
    {
        return mInstance;
    }

    public static void initialize(Context context)
    {
        if( mInstance == null )
        {
            mInstance = new AllArtists(context);
        }
    }

    @Override
    protected void queryAll(Context context)
    {
        //Query the ARTISTS!!!
        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        //                              ^~~~~~~

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        };

        String sort = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor cursor = context.getContentResolver()
                .query( mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Artists._ID );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST );
            int numIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS );

            do {
                String artistname = cursor.getString( artistIndex );

                List<Integer> albumIDs = getAlbumIDList(context, artistname);

                Artist a = new Artist(
                        cursor.getInt(idIndex),
                        cursor.getString( artistIndex ),
                        albumIDs
                );

                put(a.getId(), a);
            } while( cursor.moveToNext() );

            cursor.close();
        }
    }

    private List<Integer> getAlbumIDList(Context context, String artistName)
    {
        String[] p = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST
        };
        String s = MediaStore.Audio.Albums.ARTIST +"=\""+ artistName+"\"";

        Cursor albumsCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                p, s, null, null);

        List<Integer> albumsIDs = new ArrayList<>();

        if( albumsCursor != null && albumsCursor.moveToFirst() )
        {
            do
            {
                albumsIDs.add( albumsCursor.getInt( albumsCursor.getColumnIndex(MediaStore.Audio.Albums._ID) ) );
            } while( albumsCursor.moveToNext() );

            albumsCursor.close();
        }

        return albumsIDs;
    }
}
