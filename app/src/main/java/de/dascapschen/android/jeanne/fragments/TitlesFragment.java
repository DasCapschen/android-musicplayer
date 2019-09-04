package de.dascapschen.android.jeanne.fragments;


import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SongRecycler;
import de.dascapschen.android.jeanne.data.QueryHelper;


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
                int id = adapter.getIDAtPos(position);
                Uri songUri = Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id );
                //controller.addQueueItem( QueryHelper.getSongMetadataFromID(getContext(), id).getDescription());


                /*TODO: this is horrible
                //clear playlist
                List<MediaSessionCompat.QueueItem> currentQueue = controller.getQueue();
                if(currentQueue != null && !currentQueue.isEmpty())
                {
                    for( MediaSessionCompat.QueueItem item : currentQueue )
                    {
                        controller.removeQueueItem(item.getDescription());
                    }
                }

                //fill playlist with *ALL* songs
                ArrayList<Integer> songIDs = QueryHelper.getAllSongIDs(getContext());
                for( int songID : songIDs )
                {

                    controller.addQueueItem( QueryHelper.getSongMetadataFromID(getContext(), songID).getDescription() );
                }

                controller.getTransportControls().skipToQueueItem(position);
                */

                controller.getTransportControls().playFromUri(songUri, null);
            }
        }
    }
}
