package de.dascapschen.android.jeanne.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import de.dascapschen.android.jeanne.NavigationRequest;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.SongController;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.singletons.AllAlbums;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{

    ArrayList<Integer> albumIds = new ArrayList<>();

    public AlbumsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> subtitles = new ArrayList<>();
        ArrayList<Uri> images = new ArrayList<>();

        AllAlbums albums = AllAlbums.instance(getContext());

        for(Album a : albums.data())
        {
            titles.add( a.getAlbumTitle() );
            subtitles.add( ""+a.getTitles().size()+" Songs" );
        }

        RecyclerView albumsView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerAdapter albumsAdapter = new RecyclerAdapter(getContext(), this, titles, subtitles, images);

        albumsView.setAdapter(albumsAdapter);
        albumsView.setLayoutManager( new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Album %d", position), Toast.LENGTH_SHORT).show();

        ((NavigationRequest)getActivity()).navigate(R.id.action_to_album);

        /*
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media._ID
        };

        String selection = MediaStore.Audio.Media.ALBUM_ID + "=" + albumIds.get(position);

        Cursor c = getContext().getContentResolver().query(mediaUri,
                projection, selection, null, null);

        if(c != null && c.moveToFirst())
        {
            ArrayList<Uri> playlist = new ArrayList<>();

            do {
                int songId = c.getInt( c.getColumnIndex(MediaStore.Audio.Media._ID) );
                Uri uri = Uri.withAppendedPath(mediaUri, ""+songId);
                playlist.add(uri);
            } while( c.moveToNext() );

            SongController sc = (SongController)getActivity();
            sc.setPlaylist(playlist);
            sc.startNewSong(playlist.get(0));
        }
        */
    }
}
