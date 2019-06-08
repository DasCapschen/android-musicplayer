package de.dascapschen.android.jeanne.singletons;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import de.dascapschen.android.jeanne.data.Song;

public class AllSongs extends SingletonBase<Song>
{
    private static AllSongs mInstance = null;

    private AllSongs(Context context) {
        queryAll(context);
    }

    public static AllSongs instance()
    {
        return mInstance;
    }

    public static void initialize(Context context)
    {
        if( mInstance == null )
        {
            mInstance = new AllSongs(context);
        }
    }

    @Override
    protected final void queryAll(Context context)
    {
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String sort = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = context.getContentResolver()
                .query( mediaUri, projection, null, null, sort );

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
            int titleIndex = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST_ID );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID );

            do {
                Song s = new Song(
                    cursor.getInt( idIndex ),
                    cursor.getString( titleIndex ),
                    cursor.getInt( artistIndex ),
                    cursor.getInt( albumIndex )
                );

                put(s.getId(), s);
            } while( cursor.moveToNext() );

            cursor.close();
        }
    }
}
