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
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SectionedAdapter;
import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllAlbums;
import de.dascapschen.android.jeanne.singletons.AllArtists;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment implements OnItemClickListener
{
    private ArrayList<Album> albumList = new ArrayList<>();
    private ArrayList<Song> songList = new ArrayList<>();

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

        AllAlbums allAlbums = AllAlbums.instance();
        for(int albumID : thisArtist.getAlbumIDs())
        {
            albumList.add( allAlbums.getByKey(albumID) );
        }

        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);
        SectionedAdapter adapter = new SectionedAdapter(getContext(), this, albumList, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format("Clicked on Album %d", position), Toast.LENGTH_SHORT).show();
    }
}
