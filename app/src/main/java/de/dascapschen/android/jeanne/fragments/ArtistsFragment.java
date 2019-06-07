package de.dascapschen.android.jeanne.fragments;

import android.content.Context;
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


public class ArtistsFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{
    public ArtistsFragment()
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

        //Query the ARTISTS!!!
        Uri mediaUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        //                              ^~~~~~~

        String[] projection = {
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        };

        String sort = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor cursor = getContext().getContentResolver()
                .query( mediaUri, projection, null, null, null);

        if( cursor != null && cursor.moveToFirst() )
        {
            int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST );
            int numIndex = cursor.getColumnIndex( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS );

            do {
                String artistName = cursor.getString( artistIndex );
                int albums = cursor.getInt( numIndex );

                titles.add(artistName);
                subtitles.add( String.format(Locale.getDefault(), "%d Albums", albums) );
                //images.add( artUri );

            } while( cursor.moveToNext() );

            cursor.close();
        }

        RecyclerView artistView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerAdapter artistAdapter = new RecyclerAdapter(getContext(), this, titles, subtitles, images);

        artistView.setAdapter(artistAdapter);
        artistView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Artist %d", position), Toast.LENGTH_SHORT).show();
    }
}
