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
import android.widget.Button;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllAlbums;
import de.dascapschen.android.jeanne.singletons.AllSongs;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment implements OnItemClickListener
{
    ArrayList<Song> albumSongs;

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
                    MediaControllerCompat.getMediaController(activity);
                }
            }
        });

        int albumID = getArguments().getInt("albumID");
        Album thisAlbum = AllAlbums.instance().getByKey(albumID);

        getActivity().setTitle( thisAlbum.getName() );

        AllSongs allSongs = AllSongs.instance();

        albumSongs = new ArrayList<>();
        for (int id : thisAlbum.getSongIds())
        {
            albumSongs.add( allSongs.getByKey(id) );
        }

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        RecyclerAdapter<Song> adapter
                = new RecyclerAdapter<>(getContext(), this, albumSongs, true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        MainActivity activity = (MainActivity) getActivity();
        if( activity != null )
        {
            MediaControllerCompat.getMediaController(activity)
                    .getTransportControls()
                    .playFromUri(albumSongs.get(position).getUri(), null);
        }
    }
}
