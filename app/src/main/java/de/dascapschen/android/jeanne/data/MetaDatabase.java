package de.dascapschen.android.jeanne.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;

public class MetaDatabase
{
    SQLiteDatabase db;
    private static MetaDatabase instance = null;
    private Context context;
    boolean ready = false;

    static final String TABLE_SONGS = "tSongs";
    static final String SONG_ID = "id_song";
    static final String SONG_TITLE = "title_song";
    static final String SONG_DURATION = "duration_song";
    static final String SONG_TRACK = "tracknum_song";
    static final String SONG_URI = "uri_song";

    static final String TABLE_ALBUMS = "tAlbums";
    static final String ALBUM_ID = "id_album";
    static final String ALBUM_TITLE = "title_album";
    static final String ALBUM_ART_URI = "uri_album_art";

    static final String TABLE_ARTISTS = "tArtists";
    static final String ARTIST_ID = "id_artist";
    static final String ARTIST_NAME = "name_artist";

    static final String TABLE_PLAYLISTS = "tPlaylists";
    static final String PLAYLIST_ID = "id_playlist";
    static final String PLAYLIST_TITLE = "title_playlist";

    static final String TABLE_PLAYLIST_SONGS= "tPlaylistSongs";
    static final String TABLE_ARTIST_ALBUMS= "tArtistAlbums";

    private MetaDatabase(Context context)
    {
        this.context = context;
    }

    public static void init(Context context)
    {
        if(instance == null)
        {
            instance = new MetaDatabase(context);
        }
    }

    public static MetaDatabase getInstance()
    {
        return instance;
    }

    private void open()
    {
        db = context.openOrCreateDatabase("MusicMetadata", Context.MODE_PRIVATE, null);
    }

    public boolean exists()
    {
        if( db == null || !db.isOpen() )
        {
            open();
        }

        //TODO: REMOVE ME!! ONLY FOR TESTING BECAUSE CODE NOT COMPLETE!!
        //dropTables();

        //returns 1 if exists, else empty set
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type = ? AND name = ?",
                new String[]{"table", TABLE_SONGS});


        ready = (cursor != null) && cursor.moveToFirst();
        if(ready) cursor.close();
        return ready;
    }

    public void recreate()
    {
        ready = false;
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                dropTables();
                create();
                fill();
                return null;
            }
        }.execute();
    }

    public void create()
    {
        if(db == null || !db.isOpen())
            open();

        createAlbumTable();
        createArtistTable();
        createSongTable();
        createPlaylistTable();
    }

    /* this might be an extremely gigantic query... should we do 1 query per song? */
    public void fill()
    {
        if(db == null || !db.isOpen())
            open();

        ArrayList<AlbumData> albums = QueryHelper.getAllAlbumMetadata(context);
        String albumQuery = "INSERT INTO "+TABLE_ALBUMS+"("+ALBUM_ID+","+ALBUM_TITLE+","+ALBUM_ART_URI+") VALUES (?,?,?);";
        for(AlbumData album : albums)
        {
            Object[] bindings = new Object[3];
            bindings[0] = album.albumID;
            bindings[1] = album.title;
            bindings[2] = album.artUri;

            db.execSQL(albumQuery, bindings);
        }
        Log.i("METADATA", "Added All Albums!");

        ArrayList<ArtistData> artists = QueryHelper.getAllArtistMetadata(context);
        String artistQuery = "INSERT INTO "+TABLE_ARTISTS+"("+ARTIST_ID+","+ARTIST_NAME+") VALUES (?,?);";
        String artistAlbumQuery = "INSERT INTO "+TABLE_ARTIST_ALBUMS+"("+ARTIST_ID+","+ALBUM_ID+") VALUES (?,?);";
        for(ArtistData artist : artists)
        {
            //add this artist to the database
            Object[] bindings = new Object[2];
            bindings[0] = artist.artistID;
            bindings[1] = artist.name;
            db.execSQL(artistQuery, bindings);

            //add all albums belonging to this artist to the database
            for(int album : artist.albums)
            {
                Object[] albumBindings = new Object[2];
                albumBindings[0] = artist.artistID;
                albumBindings[1] = album;
                db.execSQL(artistAlbumQuery, albumBindings);
            }
        }
        Log.i("METADATA", "Added All Artists!");

        ArrayList<SongData> songs = QueryHelper.getAllSongMetadata(context);
        String songQuery = "INSERT INTO "+TABLE_SONGS+"("+ SONG_ID +","+ ALBUM_ID+","+ARTIST_ID+","+SONG_TITLE+","+SONG_URI+","+SONG_DURATION+","+SONG_TRACK+") VALUES (?,?,?,?,?,?,?);";
        for(SongData song : songs)
        {
            Object[] bindings = new Object[7];

            bindings[0] = song.songID;
            bindings[1] = song.albumID;
            bindings[2] = song.artistID;
            bindings[3] = song.title;
            bindings[4] = song.uri;
            bindings[5] = song.duration;
            bindings[6] = song.trackNumber;

            db.execSQL(songQuery, bindings);
        }
        Log.i("METADATA", "Added All Songs!");

        ready = true;
    }

    private void dropTables()
    {
        if(db == null || !db.isOpen())
            open();

        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_PLAYLIST_SONGS +";" );
        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_ARTIST_ALBUMS+";" );
        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_PLAYLISTS +";" );
        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_SONGS +";" );
        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_ALBUMS +";" );
        db.execSQL( "DROP TABLE IF EXISTS "+ TABLE_ARTISTS +";" );
    }

    private void createSongTable()
    {
        db.execSQL("CREATE TABLE " + TABLE_SONGS + " (" +
                        SONG_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        ALBUM_ID + " INTEGER NOT NULL," +
                        ARTIST_ID + " INTEGER NOT NULL," +
                        SONG_TITLE + " TEXT," +
                        SONG_URI + " TEXT NOT NULL," +
                        SONG_DURATION + " INTEGER," +
                        SONG_TRACK + " INTEGER,"+
                        "FOREIGN KEY("+ALBUM_ID+") REFERENCES " + TABLE_ALBUMS +"("+ ALBUM_ID +")," +
                        "FOREIGN KEY("+ARTIST_ID+") REFERENCES " + TABLE_ARTISTS +"("+ ARTIST_ID +")" +
                    ");"
        );
    }

    private void createAlbumTable()
    {
        db.execSQL("CREATE TABLE " + TABLE_ALBUMS + "(" +
                        ALBUM_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        ALBUM_TITLE + " TEXT," +
                        ALBUM_ART_URI + " TEXT" +
                    ");"
        );
    }

    private void createArtistTable()
    {
        db.execSQL("CREATE TABLE "+  TABLE_ARTISTS +"(" +
                        ARTIST_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        ARTIST_NAME + " TEXT" +
                    ");"
        );

        db.execSQL("CREATE TABLE " + TABLE_ARTIST_ALBUMS + "(" +
                ARTIST_ID + " INTEGER NOT NULL," +
                ALBUM_ID + " INTEGER NOT NULL," +
                "PRIMARY KEY ("+ARTIST_ID+","+ALBUM_ID+"),"+
                "FOREIGN KEY("+ARTIST_ID+") REFERENCES "+TABLE_ARTISTS+"("+ARTIST_ID+"),"+
                "FOREIGN KEY("+ALBUM_ID+") REFERENCES "+TABLE_ALBUMS+"("+ALBUM_ID+")"+
                ");"
        );
    }

    private void createPlaylistTable()
    {
        db.execSQL("CREATE TABLE " + TABLE_PLAYLISTS + "(" +
                        PLAYLIST_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        PLAYLIST_TITLE + " TEXT" +
                    ");"
        );

        db.execSQL("CREATE TABLE " + TABLE_PLAYLIST_SONGS + " (" +
                        PLAYLIST_ID + " INTEGER NOT NULL," +
                        SONG_ID + " INTEGER NOT NULL," +
                        "PRIMARY KEY (" + PLAYLIST_ID +", "+SONG_ID+" )," +
                        "FOREIGN KEY (" + PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + PLAYLIST_ID + ")," +
                        "FOREIGN KEY (" + SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + SONG_ID + ")" +
                    ");"
        );
    }

    static class SongData
    {
        int songID;
        int albumID;
        int artistID;
        String title;
        String uri;
        long duration;
        int trackNumber;

        SongData(int songid, int albumid, int artistid, String title, String uri, long duration, int trackNumber)
        {
            this.songID = songid;
            this.albumID = albumid;
            this.artistID = artistid;
            this.title = title;
            this.uri = uri;
            this.duration = duration;
            this.trackNumber = trackNumber;
        }
    }

    static class AlbumData
    {
        int albumID;
        String title;
        String artUri;

        AlbumData(int albumID, String title, String artUri)
        {
            this.albumID = albumID;
            this.title = title;
            this.artUri = artUri;
        }
    }

    static class ArtistData
    {
        int artistID;
        String name;
        ArrayList<Integer> albums;

        ArtistData(int artistID, String artistName, ArrayList<Integer> albums)
        {
            this.artistID = artistID;
            this.name = artistName;
            this.albums = albums;
        }
    }

    /* QUERY FUNCTIONS */
    ArrayList<Integer> getAllSongIDs()
    {
        String[] projection = { SONG_ID, SONG_TITLE };
        String sort = SONG_TITLE + " ASC";

        Cursor c = db.query(TABLE_SONGS, projection, null, null, null, null, sort);

        ArrayList<Integer> songIDs = null;

        try
        {
            songIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(SONG_ID);
            while(c.moveToNext())
            {
                songIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return songIDs;
    }

    ArrayList<Integer> getSongIDsForAlbum(int albumID)
    {
        String[] projection = { SONG_ID, ALBUM_ID, SONG_TRACK };
        String selection = ALBUM_ID +" = ?";
        String[] selectionArgs = { String.valueOf(albumID) };
        String sort = SONG_TRACK + " ASC"; //idk why, but this sorts albums correctly...

        Cursor c = db.query(TABLE_SONGS, projection, selection, selectionArgs, null, null, sort);

        ArrayList<Integer> songIDs = null;

        try
        {
            songIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(SONG_ID);
            while(c.moveToNext())
            {
                songIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return songIDs;
    }

    ArrayList<Integer> getSongIDsForAlbumArtist(int albumID, int artistID)
    {
        String[] projection = {
                SONG_ID,
                ALBUM_ID,
                ARTIST_ID,
                SONG_TRACK
        };
        String selection = ALBUM_ID +" = ? AND "+ARTIST_ID + " = ?";
        String[] selectionArgs = { String.valueOf(albumID), String.valueOf(artistID) };
        String sort = SONG_TRACK + " ASC"; //idk why, but this sorts albums correctly...

        Cursor c = db.query(TABLE_SONGS, projection, selection, selectionArgs, null, null, sort);

        ArrayList<Integer> songIDs = null;

        try
        {
            songIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(SONG_ID);
            while(c.moveToNext())
            {
                songIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return songIDs;
    }

    ArrayList<Integer> getAllArtistIDs()
    {
        String[] projection = { ARTIST_ID, ARTIST_NAME };
        String sort = ARTIST_NAME + " ASC";

        Cursor c = db.query(TABLE_ARTISTS, projection, null, null, null, null, sort);

        ArrayList<Integer> artistIDs = null;

        try
        {
            artistIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(ARTIST_ID);
            while(c.moveToNext())
            {
                artistIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return artistIDs;
    }

    ArrayList<Integer> getAllAlbumIDs()
    {
        String[] projection = { ALBUM_ID, ALBUM_TITLE };
        String sort = ALBUM_TITLE + " ASC";

        Cursor c = db.query(TABLE_ALBUMS, projection, null, null, null, null, sort);

        ArrayList<Integer> albumIDs = null;

        try
        {
            albumIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(ALBUM_ID);
            while(c.moveToNext())
            {
                albumIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return albumIDs;
    }

    ArrayList<Integer> getAlbumIDsForArtist(int artistID)
    {
        String[] projection = { ARTIST_ID, ALBUM_ID };
        //String sort = MetaDatabase.ALBUM_TITLE + " ASC"; //FIXME: No Sort Anymore!

        String selection = ARTIST_ID + "= ?";
        String[] selectionArgs = { String.valueOf(artistID) };

        Cursor c = db.query(TABLE_ARTIST_ALBUMS, projection, selection, selectionArgs, null, null, null);

        ArrayList<Integer> albumIDs = null;

        try
        {
            albumIDs = new ArrayList<>();
            int idIndex = c.getColumnIndex(ALBUM_ID);
            while(c.moveToNext())
            {
                albumIDs.add( c.getInt(idIndex) );
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        c.close();
        return albumIDs;
    }

    MediaMetadataCompat getSongMetadataFromID(int songID)
    {
        String sqlQuery = "SELECT " +
                SONG_TITLE + "," +
                ALBUM_TITLE + "," +
                ALBUM_ART_URI+","+
                ARTIST_NAME + "," +
                SONG_URI + "," +
                SONG_DURATION +
                " FROM " + TABLE_SONGS +
                " JOIN " + TABLE_ALBUMS +
                " ON " + TABLE_SONGS + "." + ALBUM_ID + " = " + TABLE_ALBUMS + "." + ALBUM_ID +
                " JOIN " + TABLE_ARTISTS +
                " ON " + TABLE_SONGS + "." + ARTIST_ID + " = " + TABLE_ARTISTS + "." + ARTIST_ID +
                " WHERE " + SONG_ID + " = ?;";
        String[] selectionArgs = {String.valueOf(songID)};

        Cursor c = db.rawQuery(sqlQuery, selectionArgs);

        if(c == null || !c.moveToFirst())
        {
            Log.e("QUERY SONG METADATA", "Failed to get Cursor");
            return null;
        }

        String songTitle = c.getString( c.getColumnIndex(SONG_TITLE) );
        String albumTitle = c.getString( c.getColumnIndex(ALBUM_TITLE) );
        String artistName = c.getString( c.getColumnIndex(ARTIST_NAME) );
        String uri = c.getString( c.getColumnIndex(SONG_URI) );
        long duration = c.getLong( c.getColumnIndex(SONG_DURATION) );

        String art = c.getString(c.getColumnIndex(ALBUM_ART_URI));

        Bitmap thumbnail  = BitmapFactory.decodeFile(art);

        if(thumbnail == null)
            Log.e("THUMBNAIL", "Could not be loaded");

        c.close();
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(songID))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, uri)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, thumbnail)
                .build();
    }

    MediaMetadataCompat getAlbumMetadataFromID(int albumID)
    {
        String sqlQuery = "SELECT " +
                ALBUM_TITLE+","+
                ARTIST_NAME+","+
                ALBUM_ART_URI+","+
                " COUNT(*) AS numSongs"+
                " FROM " + TABLE_ARTIST_ALBUMS +
                " JOIN " + TABLE_ALBUMS +
                " ON "   + TABLE_ARTIST_ALBUMS+"."+ALBUM_ID+"="+TABLE_ALBUMS+"."+ALBUM_ID+
                " JOIN " + TABLE_ARTISTS +
                " ON "   + TABLE_ARTIST_ALBUMS+"."+ARTIST_ID+"="+TABLE_ARTISTS+"."+ARTIST_ID+
                " JOIN " + TABLE_SONGS +
                " ON "   + TABLE_SONGS+"."+ALBUM_ID+"="+TABLE_ARTIST_ALBUMS+"."+ALBUM_ID+
                " WHERE " + TABLE_ARTIST_ALBUMS+"."+ALBUM_ID + " = ?"+
                " GROUP BY "+ TABLE_ARTIST_ALBUMS+"."+ALBUM_ID+";";
        String[] selectionArgs = { String.valueOf(albumID) };

        Cursor c = db.rawQuery(sqlQuery, selectionArgs);

        if( c == null || !c.moveToFirst() )
        {
            Log.e("QUERY ALBUM METADATA", "Could not get Cursor!");
            return null;
        }

        String albumTitle = c.getString( c.getColumnIndex(ALBUM_TITLE) );
        String artistName = c.getString( c.getColumnIndex(ARTIST_NAME) );
        String albumArt = c.getString( c.getColumnIndex(ALBUM_ART_URI) );
        long count = c.getLong( c.getColumnIndex("numSongs") );

        Bitmap thumbnail  = BitmapFactory.decodeFile(albumArt);

        if(thumbnail == null)
            Log.e("THUMBNAIL", "Could not be loaded");

        c.close();
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(albumID))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, thumbnail)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, count)
                .build();
    }

    MediaMetadataCompat getArtistMetadataFromID(int artistID)
    {
        String sqlQuery = "SELECT "+
                ARTIST_NAME+","+
                ALBUM_ART_URI+","+
                "COUNT(*) AS numAlbums"+
                " FROM " + TABLE_ARTISTS+
                " JOIN " + TABLE_ARTIST_ALBUMS+
                " ON "   + TABLE_ARTISTS+"."+ARTIST_ID+"="+TABLE_ARTIST_ALBUMS+"."+ARTIST_ID+
                " JOIN " + TABLE_ALBUMS+
                " ON "   + TABLE_ARTIST_ALBUMS+"."+ALBUM_ID+"="+TABLE_ALBUMS+"."+ALBUM_ID+
                " WHERE "+ TABLE_ARTISTS+"."+ARTIST_ID+"=?"+
                " GROUP BY " + TABLE_ARTISTS+"."+ARTIST_ID + ";";
        String[] selectionArgs = { String.valueOf(artistID) };

        Cursor c = db.rawQuery(sqlQuery, selectionArgs);

        if( c ==  null || !c.moveToFirst() )
        {
            Log.e("QUERY ARTIST METADATA", "Could not get Cursor!");
            return null;
        }

        String artistName = c.getString( c.getColumnIndex(ARTIST_NAME) );
        long albumCount = c.getLong( c.getColumnIndex("numAlbums") );
        String albumArt = c.getString( c.getColumnIndex(ALBUM_ART_URI));

        Bitmap thumbnail  = BitmapFactory.decodeFile(albumArt);

        if(thumbnail == null)
            Log.e("THUMBNAIL", "Could not be loaded");

        c.close();
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(artistID))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, albumCount)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, thumbnail)
                .build();
    }
}
