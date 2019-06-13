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
import android.widget.Button;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.SongController;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.data.Playlist;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllPlaylists;
import de.dascapschen.android.jeanne.singletons.AllSongs;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistDetailFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{

    ArrayList<Song> playlistSongs;

    public PlaylistDetailFragment()
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

        Button playAll = view.findViewById(R.id.detail_play_all_btn);
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SongController sc = (SongController) getActivity();
                sc.setPlaylist(playlistSongs);
                sc.startNewSong(playlistSongs.get(0));
            }
        });

        int playlistID = getArguments().getInt("playlistID");
        Playlist thisPlaylist = AllPlaylists.instance().getByKey(playlistID);

        getActivity().setTitle( thisPlaylist.getName() );

        AllSongs allSongs = AllSongs.instance();

        playlistSongs = new ArrayList<>();
        for (int id : thisPlaylist.getSongIDs())
        {
            playlistSongs.add( allSongs.getByKey(id) );
        }

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        RecyclerAdapter<Song> adapter
                = new RecyclerAdapter<>(getContext(), this, playlistSongs);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        SongController sc = (SongController) getActivity();
        sc.startNewSong( playlistSongs.get(position) );
    }
}
