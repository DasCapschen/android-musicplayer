package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dascapschen.android.jeanne.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment
{
    public AlbumDetailFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_detail, container, false);
    }
}