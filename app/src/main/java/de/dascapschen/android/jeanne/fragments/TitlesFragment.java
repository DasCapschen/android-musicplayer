package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SongRecycler;
import de.dascapschen.android.jeanne.data.QueryHelper;
import de.dascapschen.android.jeanne.service.MusicService;


/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends Fragment implements OnItemClickListener
{
    SongRecycler adapter;

    public TitlesFragment()
    {

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

        ArrayList<Integer> songs = QueryHelper.getAllSongIDs(getContext());

        RecyclerView titleView = view.findViewById(R.id.recyclerView);

        adapter = new SongRecycler(getContext(), this, songs, true);

        titleView.setAdapter(adapter);
        titleView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Playing Title %d", position), Toast.LENGTH_SHORT).show();

        if( getActivity() != null )
        {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if(controller != null)
            {
                //put ALL songs into play query
                Bundle data = new Bundle();
                data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY, QueryHelper.getAllSongIDs(getContext()));

                //set the query
                controller.getTransportControls()
                        .sendCustomAction(MusicService.CUSTOM_ACTION_SET_QUEUE, data);

                //changes to item at index and plays it (we use the id as an index instead)
                controller.getTransportControls().skipToQueueItem( position );
            }
        }
    }
}
