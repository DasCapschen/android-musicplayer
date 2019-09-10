package de.dascapschen.android.jeanne.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

public class QueryHelper
{
    public static ArrayList<Integer> getAllSongIDs(Context ctx)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();

        if(metaDB.ready)
        {
            return metaDB.getAllSongIDs();
        }

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE //needed for sort
        };

        String sort = MediaStore.Audio.Media.TITLE + " ASC";

        ArrayList<Integer> songIds = null;

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort)  )
        {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            songIds = new ArrayList<>();

            do {
                songIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return songIds;
    }

    public static ArrayList<Integer> getSongIDsForAlbum(Context ctx, int albumID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getSongIDsForAlbum(albumID);
        }


        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID, //needed for selection
                MediaStore.Audio.Media.TRACK //sort by track number
        };

        String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
        String[] selectionArgs = { String.valueOf(albumID) };

        String sort = MediaStore.Audio.Media.TRACK + " ASC";

        ArrayList<Integer> songIds = null;

        try ( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, sort) )
        {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            songIds = new ArrayList<>();

            do {
                songIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return songIds;
    }

    public static ArrayList<Integer> getSongIDsForAlbumArtist(Context ctx, int albumID, int artistID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getSongIDsForAlbumArtist(albumID, artistID);
        }

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TRACK, //needed for sort
                MediaStore.Audio.Media.ALBUM_ID, //needed for selection
                MediaStore.Audio.Media.ARTIST_ID
        };

        String selection = MediaStore.Audio.Media.ALBUM_ID + "=?"
                +" AND " + MediaStore.Audio.Media.ARTIST_ID + "=?";
        String[] selectionArgs = { String.valueOf(albumID), String.valueOf(artistID) };

        String sort = MediaStore.Audio.Media.TRACK + " ASC";

        ArrayList<Integer> songIds = null;

        try ( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, sort) )
        {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            songIds = new ArrayList<>();

            do {
                songIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return songIds;

    }

    public static ArrayList<Integer> getAllArtistIDs(Context ctx)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getAllArtistIDs();
        }

        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST //needed for sort
        };

        String sort = MediaStore.Audio.Artists.ARTIST + " ASC";

        ArrayList<Integer> artistIds = null;

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort) )
        {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);

            artistIds = new ArrayList<>();

            do
            {
                artistIds.add(cursor.getInt(idIndex));
            } while (cursor.moveToNext());
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return artistIds;
    }

    public static ArrayList<Integer> getAllAlbumIDs(Context ctx)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getAllAlbumIDs();
        }

        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM //needed for sort
        };

        String sort = MediaStore.Audio.Albums.ALBUM + " ASC";

        ArrayList<Integer> albumIds = null;

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort) )
        {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            albumIds = new ArrayList<>();

            do {
                albumIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return albumIds;
    }

    public static ArrayList<Integer> getAlbumIDsForArtist(Context ctx, int artistID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getAlbumIDsForArtist(artistID);
        }

        Uri mediaUri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistID);

        //FIXME: "Invalid Column DISTINCT _id"
        String[] projection = {
                /*"DISTINCT " +*/ "_id",  //ALBUM_ID as provided by MediaStore does not work... use _ID (which doesn't exist in MediaStore)
                MediaStore.Audio.Artists.Albums.ALBUM //needed for sort
        };

        String sort = MediaStore.Audio.Artists.Albums.ALBUM + " ASC";

        ArrayList<Integer> albumIds = null;

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, sort) )
        {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex("_id");

            albumIds = new ArrayList<>();

            do {
                albumIds.add( cursor.getInt(idIndex) );
            } while(cursor.moveToNext() );
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return albumIds;
    }

    public static MediaMetadataCompat getSongMetadataFromID(Context ctx, int songID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getSongMetadataFromID(songID);
        }

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media._ID + "=?";
        String[] selectionArgs = { String.valueOf(songID) };

        try ( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null) )
        {
            cursor.moveToFirst();

            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            String songUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + songID).toString();

            //TODO: ALBUM ART
            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(songID))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(titleIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(albumIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUri)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, cursor.getLong(durationIndex))
                    .build();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static MediaMetadataCompat getAlbumMetadataFromID(Context ctx, int albumID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getAlbumMetadataFromID(albumID);
        }

        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.ALBUM_ART
        };
        String selection = MediaStore.Audio.Albums._ID + "=?";
        String[] selectionArgs = { String.valueOf(albumID) };


        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null) )
        {
            cursor.moveToFirst();

            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ARTIST );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM );
            int numSongsIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            int albumArtIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(albumID))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(albumIndex))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cursor.getString(albumArtIndex))
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, cursor.getLong(numSongsIndex))
                    .build();
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static MediaMetadataCompat getArtistMetadataFromID(Context ctx, int artistID)
    {
        MetaDatabase metaDB = MetaDatabase.getInstance();
        if(metaDB.ready)
        {
            return metaDB.getArtistMetadataFromID(artistID);
        }

        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        };
        String selection = MediaStore.Audio.Artists._ID + "=?";
        String[] selectionArgs = { String.valueOf(artistID) };

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, selection, selectionArgs, null)  )
        {
            cursor.moveToFirst();

            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST );
            int numAlbumsIndex = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(artistID))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(artistIndex))
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, cursor.getLong(numAlbumsIndex))
                    .build();
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /* ONLY FOR MetaDatabase!! */
    static ArrayList<MetaDatabase.AlbumData> getAllAlbumMetadata(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART
        };

        ArrayList<MetaDatabase.AlbumData> albumData = new ArrayList<>();

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, null) )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Albums._ID );
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM );
            int artIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM_ART );

            while( cursor.moveToNext() )
            {
                int id = cursor.getInt(idIndex);
                String title = cursor.getString(albumIndex);
                String art = cursor.getString(artIndex);

                albumData.add( new MetaDatabase.AlbumData(id, title, art));
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return albumData;
    }

    /* ONLY FOR MetaDatabase!! */
    static ArrayList<MetaDatabase.ArtistData> getAllArtistMetadata(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
        };

        ArrayList<MetaDatabase.ArtistData> artistData = new ArrayList<>();

        try( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, null)  )
        {
            int idIndex = cursor.getColumnIndex( MediaStore.Audio.Artists._ID );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST );

            while( cursor.moveToNext() )
            {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(artistIndex);
                ArrayList<Integer> albums = getAlbumIDsForArtist(ctx, id);

                artistData.add( new MetaDatabase.ArtistData(id, name, albums));
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return artistData;
    }

    /* USE THIS ONLY FOR MetaDatabase */
    static ArrayList<MetaDatabase.SongData> getAllSongMetadata(Context ctx)
    {
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                "DISTINCT " + MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };

        ArrayList<MetaDatabase.SongData> metadata = new ArrayList<>();

        try ( Cursor cursor = ctx.getContentResolver()
                .query(mediaUri, projection, null, null, null) )
        {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int trackIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            while(cursor.moveToNext())
            {
                int songID = cursor.getInt(idIndex);
                int artistID = cursor.getInt(artistIndex);
                int albumID = cursor.getInt(albumIndex);
                String title = cursor.getString(titleIndex);
                String songUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + songID).toString();
                long duration = cursor.getLong(durationIndex);
                int track = cursor.getInt(trackIndex);

                metadata.add( new MetaDatabase.SongData(songID, albumID, artistID, title, songUri, duration, track) );
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return metadata;
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
