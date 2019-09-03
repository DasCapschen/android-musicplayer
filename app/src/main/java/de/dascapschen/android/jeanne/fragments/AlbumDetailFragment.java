package de.dascapschen.android.jeanne.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SongRecycler;
import de.dascapschen.android.jeanne.data.QueryHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment implements OnItemClickListener
{
    SongRecycler adapter;

    public AlbumDetailFragment()
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
                MainActivity activity = (MainActivity) getActivity();
                if( activity != null )
                {
                    //set queue somehow (only have get, add and remove single item... for loop?)
                }
            }
        });

        int albumID = getArguments().getInt("albumID");
        MediaMetadataCompat metadata = QueryHelper.getAlbumMetadataFromID(getContext(), albumID);
        if(metadata == null)
        {
            Toast.makeText(getContext(), "Error loading Album!", Toast.LENGTH_SHORT).show();
            return;
        }

        getActivity().setTitle( metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) );

        ArrayList<Integer> albumSongIDs = QueryHelper.getSongIDsForAlbum(getContext(), albumID);

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        adapter = new SongRecycler(getContext(), this, albumSongIDs, true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        MainActivity activity = (MainActivity) getActivity();
        if( activity != null )
        {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(activity);
            if(controller != null)
            {
                int id = adapter.getIDAtPos(position);
                Uri songUri = Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id );
                controller.getTransportControls().playFromUri(songUri, null);
            }
        }
    }
}
