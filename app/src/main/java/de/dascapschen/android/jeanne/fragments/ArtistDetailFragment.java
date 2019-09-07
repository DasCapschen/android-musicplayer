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
        //FIXME: wasting 35ms just for artist name
        MediaMetadataCompat metadata = QueryHelper.getArtistMetadataFromID(getContext(), artistID);
        if(metadata == null) return;

        String artistName = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);

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
                //put all the album songIDs into our play queue
                Bundle data = new Bundle();
                data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY,
                        QueryHelper.getSongIDsForAlbum(getContext(), adapter.getIDAtPos(position)));

                controller.getTransportControls()
                        .sendCustomAction(MusicService.CUSTOM_ACTION_SET_QUEUE, data);

                //changes to item at index and plays it
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
                //put all the album songIDs into our play queue
                Bundle data = new Bundle();
                data.putIntegerArrayList(MusicService.CUSTOM_ACTION_DATA_KEY,
                        QueryHelper.getSongIDsForAlbum(getContext(), adapter.getIDAtPos(section)));

                controller.getTransportControls()
                        .sendCustomAction(MusicService.CUSTOM_ACTION_SET_QUEUE, data);

                //changes to item at index and plays it
                controller.getTransportControls().skipToQueueItem( position );
            }
        }
    }
}
