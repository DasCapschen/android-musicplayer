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
public class TitlesFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{


    public TitlesFragment()
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

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = getContext().getContentResolver()
                .query( mediaUri, projection, null, null, null );

        if( cursor != null && cursor.moveToFirst() )
        {
            int titleIndex = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST );
            //int albumArtIndex = cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM_ART );

            do {
                String songTitle = cursor.getString( titleIndex );
                String artistName = cursor.getString( artistIndex );
                //String artPath = cursor.getString( albumArtIndex );

                //Uri artUri = Uri.parse(artPath);

                titles.add(songTitle);
                subtitles.add( artistName );
                //images.add( artUri );

            } while( cursor.moveToNext() );

            cursor.close();
        }

        RecyclerView titleView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerAdapter titleAdapter = new RecyclerAdapter(getContext(), this, titles, subtitles, images);

        titleView.setAdapter(titleAdapter);
        titleView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Title %d", position), Toast.LENGTH_SHORT).show();
    }
}