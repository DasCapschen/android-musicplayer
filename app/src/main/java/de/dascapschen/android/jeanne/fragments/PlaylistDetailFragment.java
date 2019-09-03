package de.dascapschen.android.jeanne.fragments;


import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SongRecycler;
import de.dascapschen.android.jeanne.data.QueryHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistDetailFragment extends Fragment implements OnItemClickListener
{
    SongRecycler adapter;

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
                Toast.makeText(getContext(), "Playing All...", Toast.LENGTH_SHORT).show();
            }
        });

        int playlistID = getArguments().getInt("playlistID");
        MediaMetadataCompat metadata = QueryHelper.getPlaylistMetadataFromID(getContext(), playlistID);
        if(metadata == null)
        {
            Toast.makeText(getContext(), "Error loading Playlist", Toast.LENGTH_SHORT).show();
            return;
        }

        getActivity().setTitle( metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) );

        ArrayList<Integer> songIds = QueryHelper.getSongIDsForPlaylist(getContext(), playlistID);

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        adapter = new SongRecycler(getContext(), this, songIds, true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );

    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format("Playing song %d", position), Toast.LENGTH_SHORT).show();
    }
}
