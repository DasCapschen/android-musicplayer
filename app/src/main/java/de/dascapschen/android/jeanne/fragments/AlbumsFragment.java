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

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{


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

        //Query the ALBUMS!!!
        Uri mediaUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        //                             ^~~~~~~

        String[] projection = {
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
        };

        Cursor cursor = getContext().getContentResolver()
                .query( mediaUri, projection, null, null, null);   //query artist table, instead of media

        if( cursor != null && cursor.moveToFirst() )
        {
            int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ARTIST );
            int artIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM_ART );

            do {
                String albumName = cursor.getString( albumIndex );
                String artistName = cursor.getString( artistIndex );
                String artPath = cursor.getString( artIndex );

                if( artPath != null )
                {
                    Uri artUri = Uri.parse(artPath);
                    images.add( artUri );
                }

                titles.add(albumName);
                subtitles.add(artistName);


            } while( cursor.moveToNext() );

            cursor.close();
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
    }
}
