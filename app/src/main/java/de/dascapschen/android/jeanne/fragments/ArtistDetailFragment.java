package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.SongController;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.adapters.RecyclerSection;
import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllAlbums;
import de.dascapschen.android.jeanne.singletons.AllArtists;
import de.dascapschen.android.jeanne.singletons.AllSongs;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{
    private List<Album> albumList;
    private List<Song> songList;

    public ArtistDetailFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        int artistID = getArguments().getInt("artistID");
        Artist thisArtist = AllArtists.instance().getByKey(artistID);

        getActivity().setTitle( thisArtist.getName() );

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        AllAlbums allAlbums = AllAlbums.instance();
        AllSongs allSongs = AllSongs.instance();

        for( int albumID : thisArtist.getAlbumIDs() )
        {
            Album album = allAlbums.getByKey(albumID);
            albumList.add(album);

            ArrayList<Song> albumSongs = new ArrayList<>();
            for(int songID : album.getSongIds())
            {
                Song song = allSongs.getByKey(songID);
                albumSongs.add(song);
                sectionAdapter.addSection(new RecyclerSection(albumSongs, this));
            }
            songList.addAll(albumSongs);
        }


        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        recyclerView.setAdapter(sectionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(int position)
    {
    }
}
