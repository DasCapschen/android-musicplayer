package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SectionedAdapter;
import de.dascapschen.android.jeanne.data.QueryHelper;
import de.dascapschen.android.jeanne.service.MusicService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment implements OnItemClickListener, SectionedAdapter.NestedItemClickListener
{
    SectionedAdapter adapter;
    int artistID;

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

        artistID = getArguments().getInt("artistID");
        //FIXME: wasting 35ms just for artist name
        MediaMetadataCompat metadata = QueryHelper.getArtistMetadataFromID(getContext(), artistID);
        if(metadata == null) return;

        String artistName = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);

        if(artistName.equals("<unknown>"))
        {
            artistName = getString(R.string.unknown_replacement);
        }

        getActivity().setTitle( artistName );

        //this just takes 26ms
        ArrayList<Integer> albumIDs = QueryHelper.getAlbumIDsForArtist(getContext(), artistID);
        if(albumIDs == null)
        {
            Toast.makeText(getContext(), "Error loading Artists Albums!", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        adapter = new SectionedAdapter(getContext(), this, this, albumIDs, artistID, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Section Click Listener (Albums)
    @Override
    public void onItemClicked(int position)
    {
        MainActivity activity = (MainActivity) getActivity();
        if( activity != null )
        {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(activity);
            if(controller != null)
            {
                //clear current queue
                controller.getTransportControls()
                        .sendCustomAction(MusicService.CUSTOM_ACTION_CLEAR_QUEUE, null);

                //add all songs from all albums to the queue
                ArrayList<Integer> albums = QueryHelper.getAlbumIDsForArtist(getContext(), artistID);
                for(int album : albums)
                {
                    Bundle data = new Bundle();
                    data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY,
                            QueryHelper.getSongIDsForAlbumArtist(getContext(), album, artistID));
                    controller.getTransportControls()
                            .sendCustomAction(MusicService.CUSTOM_ACTION_APPEND_QUEUE, data);
                }

                //start playing anywhere
                controller.getTransportControls().play();
            }
        }
    }

    //Nested Click Listener (Songs)
    @Override
    public void onItemClicked(int position, int section)
    {
        MainActivity activity = (MainActivity) getActivity();
        if( activity != null )
        {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(activity);
            if(controller != null)
            {
                controller.getTransportControls()
                        .sendCustomAction(MusicService.CUSTOM_ACTION_CLEAR_QUEUE, null);

                int newSongIndex = 0;

                ArrayList<Integer> albums = QueryHelper.getAlbumIDsForArtist(getContext(), artistID);
                for(int i = 0; i < albums.size(); i++)
                {
                    ArrayList<Integer> songs = QueryHelper.getSongIDsForAlbumArtist(getContext(), albums.get(i), artistID);
                    if(i < section) newSongIndex += songs.size();

                    Bundle data = new Bundle();
                    data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY, songs);

                    controller.getTransportControls()
                            .sendCustomAction(MusicService.CUSTOM_ACTION_APPEND_QUEUE, data);
                }

                newSongIndex += position;

                //changes to item at index and plays it
                controller.getTransportControls().skipToQueueItem( newSongIndex );
            }
        }
    }
}
