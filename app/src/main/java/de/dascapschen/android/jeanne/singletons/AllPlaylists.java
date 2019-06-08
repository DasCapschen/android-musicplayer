package de.dascapschen.android.jeanne.singletons;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.data.Playlist;
import de.dascapschen.android.jeanne.data.Song;

public class AllPlaylists extends SingletonBase<Playlist>
{
    private static AllPlaylists mInstance = null;

    private AllPlaylists(Context context){
        queryAll(context);
    }

    public static AllPlaylists instance() {
        return mInstance;
    }

    public static void initialize(Context context)
    {
        if( mInstance == null ){
            mInstance = new AllPlaylists(context);
        }
    }

    @Override
    protected void queryAll(Context context)
    {
//Query the PLAYLISTS!!!
        Uri mediaUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        //                             ^~~~~~~

        String[] projection = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };

        String sort = MediaStore.Audio.Playlists.NAME + " ASC";

        Cursor cursor = context.getContentResolver()
                .query( mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID );
            int nameIndex = cursor.getColumnIndex( MediaStore.Audio.Playlists.NAME );

            do {

                Playlist p = new Playlist(
                        cursor.getInt( idIndex ),
                        cursor.getString( nameIndex ),
                        new ArrayList<Song>() //TODO
                );

                put(p.getId(), p);

            } while( cursor.moveToNext() );

            cursor.close();
        }

    }
}
