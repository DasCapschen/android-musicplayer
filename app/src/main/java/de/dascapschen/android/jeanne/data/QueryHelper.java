package de.dascapschen.android.jeanne.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

public class QueryHelper
{
    public static ArrayList<Integer> getAllSongIDs(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE //needed for sort
        };

        String sort = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            ArrayList<Integer> songIds = new ArrayList<>();

            do {
                songIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );

            cursor.close();
            return songIds;
        }
        return null;
    }

    public static ArrayList<Integer> getSongIDsForAlbum(Context ctx, int albumID)
    {
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, //needed for sort
                MediaStore.Audio.Media.ALBUM_ID //needed for projection
        };

        String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
        String[] selectionArgs = { String.valueOf(albumID) };

        String sort = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            ArrayList<Integer> songIds = new ArrayList<>();

            do {
                songIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );

            cursor.close();
            return songIds;
        }
        return null;
    }

    public static ArrayList<Integer> getAllArtistIDs(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST //needed for sort
        };

        String sort = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);

            ArrayList<Integer> artistIds = new ArrayList<>();

            do {
                artistIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );

            cursor.close();
            return artistIds;
        }
        return null;
    }

    public static ArrayList<Integer> getAllAlbumIDs(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        //TODO: there's _ID and ALBUM_ID, which to use!?
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM //needed for sort
        };

        String sort = MediaStore.Audio.Albums.ALBUM + " ASC";

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            ArrayList<Integer> albumIds = new ArrayList<>();

            do {
                albumIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );

            cursor.close();
            return albumIds;
        }
        return null;
    }


    public static ArrayList<Integer> getAlbumIDsForArtist(Context ctx, int artistID)
    {
        Uri mediaUri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistID);

        String[] projection = {
                "_id",  //ALBUM_ID as provided by MediaStore does not work... use _ID (which doesn't exist in MediaStore)
                MediaStore.Audio.Artists.Albums.ALBUM //needed for sort
        };

        String sort = MediaStore.Audio.Artists.Albums.ALBUM + " ASC";

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort);

        if( cursor != null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex("_id");

            ArrayList<Integer> albumIds = new ArrayList<>();

            do {
                albumIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );

            cursor.close();
            return albumIds;
        }
        return null;
    }

    public static MediaMetadataCompat getSongMetadataFromID(Context ctx, int id)
    {
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null);

        if( cursor!=null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
            int titleIndex = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM );
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            String songUri = Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id ).toString();

            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(idIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(titleIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(albumIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUri)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, cursor.getLong(durationIndex))
                    .build();
        }

        return null;
    }

    public static MediaMetadataCompat getAlbumMetadataFromID(Context ctx, int id)
    {
        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.ALBUM_ART
        };
        String selection = MediaStore.Audio.Albums._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null);

        if( cursor!=null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Albums._ID );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ARTIST );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM );
            int numSongsIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            //TODO: ALBUM ART
            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(idIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(albumIndex))
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, cursor.getLong(numSongsIndex))
                    .build();
        }
        return null;
    }

    public static MediaMetadataCompat getArtistMetadataFromID(Context ctx, int id)
    {
        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        };
        String selection = MediaStore.Audio.Artists._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null);

        if( cursor!=null && cursor.moveToFirst() )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Artists._ID );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST );
            int numAlbumsIndex = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, cursor.getString(idIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, cursor.getLong(numAlbumsIndex))
                    .build();
        }
        return null;
    }

    public static ArrayList<Integer> getAllPlaylistIDs(Context ctx)
    {
        return null;
    }

    public static MediaMetadataCompat getPlaylistMetadataFromID(Context ctx, int playlistID)
    {
        return null;
    }

    public static ArrayList<Integer> getSongIDsForPlaylist(Context ctx, int playlistID)
    {
        return null;
    }

}