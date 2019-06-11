package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.singletons.AllArtists;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment
{
    public ArtistDetailFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        int artistID = getArguments().getInt("artistID");
        Artist thisArtist = AllArtists.instance().getByKey(artistID);

        getActivity().setTitle( thisArtist.getName() );
    }
}
