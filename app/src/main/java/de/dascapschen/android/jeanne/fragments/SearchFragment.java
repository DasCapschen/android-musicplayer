package de.dascapschen.android.jeanne.fragments;


import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.NavigationRequest;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.AlbumRecycler;
import de.dascapschen.android.jeanne.adapters.SearchAdapter;
import de.dascapschen.android.jeanne.adapters.SectionedAdapter;
import de.dascapschen.android.jeanne.data.QueryHelper;
import de.dascapschen.android.jeanne.service.MusicService;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SectionedAdapter.NestedItemClickListener
{
    SearchAdapter adapter;

    public SearchFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String query = getArguments().getString( "query" );

        Log.e("SEARCH QUERY", query);

        ArrayList<Integer> songs = QueryHelper.getSongIDsByName(getContext(), query);
        ArrayList<Integer> albums = QueryHelper.getAlbumIDsByName(getContext(), query);
        ArrayList<Integer> artists = QueryHelper.getArtistIDsByName(getContext(), query);

        RecyclerView searchView = view.findViewById(R.id.recyclerView);

        adapter = new SearchAdapter(getContext(), this, songs, albums, artists);

        searchView.setAdapter(adapter);
        searchView.setLayoutManager( new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(int position, int section)
    {
        int id = adapter.getIDAtPos(position, section);

        if(section == 0)    //songs
        {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());

            Bundle data = new Bundle();
            data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY, adapter.getSongs());

            controller.getTransportControls().sendCustomAction(MusicService.CUSTOM_ACTION_SET_QUEUE, data);
            controller.getTransportControls().skipToQueueItem( position );
        }
        else if(section == 1)   //albums
        {
            Bundle args = new Bundle();
            args.putInt("albumID", id);

            //TODO: USE INTENTS
            ((NavigationRequest)getActivity()).navigate(R.id.action_search_to_album, args);
        }
        else if(section == 2)   //artists
        {
            Bundle args = new Bundle();
            args.putInt("artistID", id);

            //TODO: USE INTENTS
            ((NavigationRequest)getActivity()).navigate(R.id.action_search_to_artist, args);
        }
    }
}
