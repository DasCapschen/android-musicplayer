package de.dascapschen.android.jeanne.fragments;


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
import de.dascapschen.android.jeanne.singletons.AllArtists;
import de.dascapschen.android.jeanne.singletons.AllSongs;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.SongController;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.singletons.SingletonBase;


/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{

    final Uri mediaUri;
    ArrayList<Integer> titleIDs;

    public TitlesFragment()
    {
        mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        titleIDs = new ArrayList<>();
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

        //does this work?
        AllSongs songs = (AllSongs)AllSongs.instance(getContext());
        AllArtists artists = (AllArtists)AllArtists.instance(getContext());

        for( Song song : songs.data() )
        {
            titles.add( song.getSongTitle() );
            subtitles.add( artists.get(song.getArtistID()).getArtistName() );
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
                "Playing Title %d", position), Toast.LENGTH_SHORT).show();

        SongController sc = (SongController)getActivity();
        sc.startNewSong( Uri.withAppendedPath(mediaUri, ""+titleIDs.get(position)) );
    }
}
