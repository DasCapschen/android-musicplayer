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
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.singletons.AllArtists;


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

        AllArtists artists = AllArtists.instance(getContext());

        for(Artist a : artists.data() )
        {
            titles.add( a.getArtistName() );
            subtitles.add( ""+a.getAlbumIDs().size()+" Albums" );
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
